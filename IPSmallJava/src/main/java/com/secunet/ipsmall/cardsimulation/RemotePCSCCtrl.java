package com.secunet.ipsmall.cardsimulation;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

import com.secunet.ipsmall.log.Logger;
import com.secunet.ipsmall.log.IModuleLogger.LogLevel;

/**
 * This class implements the remote control of the virtual PC/SC device.
 * 
 * @author neunkirchen.bernd
 * 
 */
public class RemotePCSCCtrl {
    private String host = "localhost";
    private int port = 12345;
    
    /**
     * Creates a new control object for remote PC/SC device.
     * 
     * @param host
     *            Host name of remote PC/SC device.
     * @param port
     *            Port number of remote PC/SC device.
     */
    public RemotePCSCCtrl(String host, int port) {
        this.host = host;
        this.port = port;
    }
    
    /**
     * Initializes remote PC/SC device.
     * 
     * @param address
     *            IPv4 address of APDU simulation server (GlobalTester).
     * @param port
     *            Port number of APDU socket.
     */
    public void init(Inet4Address address, int port) {
        Logger.CardSim.logState("Connecting to Remote PCSC ...");
        
        byte[] data = new byte[7];
        
        data[0] = 0x01; // = GlobalTester
        
        data[1] = address.getAddress()[0];
        data[2] = address.getAddress()[1];
        data[3] = address.getAddress()[2];
        data[4] = address.getAddress()[3];
        
        data[5] = (byte) ((port >> 8) & 0xFF);
        data[6] = (byte) (port & 0xFF);
        
        int result = submit(0, data);
        if (result != 42 || result != 11)
            Logger.CardSim.logState("RemotePCSC error code: " + result, LogLevel.Error);
    }
    
    /**
     * Connects virtual card remotely.
     */
    public void connect() {
        int result = submit(1, null);
        if (result != 42)
            Logger.CardSim.logState("RemotePCSC error code: " + result, LogLevel.Error);
    }
    
    /**
     * Disconnects virtual card remotely.
     */
    public void disconnect() {
        int result = submit(2, null);
        if (result != 42)
            Logger.CardSim.logState("RemotePCSC error code: " + result, LogLevel.Error);
    }
    
    /**
     * Submits command to remote PC/SC device.
     * 
     * @param command
     *            The command.
     * @param data
     *            Data for the command (null if no data available).
     * @return Status code.
     */
    private int submit(int command, byte[] data) {
        int result = -1;
        
        Socket server = null;
        
        try {
            server = new Socket(host, port);
        } catch (Exception e) {
            Logger.CardSim.logState("Unable to connect to socket: " + e.getMessage(), LogLevel.Error);
            return -1;
        } 
        
        DataInputStream in = null;
        DataOutputStream out = null;
        try {
            out = new DataOutputStream(server.getOutputStream());
            in = new DataInputStream(server.getInputStream());
        } catch (IOException e) {
            Logger.CardSim.logState("Unable to access socket: " + e.getMessage(), LogLevel.Error);
        }
        
        if (out != null && in != null) {
            try {
                out.writeByte(command);
                
                if (data != null) {
                    out.writeInt(data.length);
                    out.write(data, 0, data.length);
                }
                
                result = in.readByte();
            } catch (IOException e) {
                Logger.CardSim.logState("Unable to write/read socket: " + e.getMessage(), LogLevel.Error);
            }
        }
        
        try {
            server.close();
        } catch (IOException e) {
            Logger.CardSim.logState("Unable to close socket: " + e.getMessage(), LogLevel.Error);
        }
        
        return result;
    }
    
    public static void main(String[] args) {
        
        RemotePCSCCtrl pcscCtrl = new RemotePCSCCtrl("pcscemulator.secunet.de", 12345);
        try {
            pcscCtrl.init((Inet4Address) InetAddress.getByName("gt-simulator.secunet.de"), 9876);
        } catch (UnknownHostException e) {
            System.out.println("Unable to connect to remote PCSC device.");
        }
        
        System.out.println("Connected to RemotePCSCCtrl.");
        
        pcscCtrl.connect();
        
        try {
            System.out.println("Card connected. Press enter to stop ...");
            System.in.read();
        } catch (Throwable ignored) {
        }
        
        pcscCtrl.disconnect();
        System.out.println("Card disconnected.");
    }
}
