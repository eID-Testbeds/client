package com.secunet.ipsmall.cardsimulation;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.Security;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.secunet.testbedutils.cvc.cvcertificate.*;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

import com.secunet.ipsmall.log.IModuleLogger.ProtocolDirection;
import com.secunet.ipsmall.log.Logger;
import com.secunet.ipsmall.log.IModuleLogger.LogLevel;
import com.secunet.ipsmall.log.Protocols;

/**
 * This class controls the HJP Global Tester to simulate an eID-Card.
 * 
 * @author neunkirchen.bernd
 * 
 */
public class GTCardSimCtrl {
    private boolean remoteGT = false;
    // connection settings
    private String hostGT = null;
    private int portGTService = 6789;
    private int portGTResult = 6788;
    private int portGTAPDU = 9876;
    private RemotePCSCCtrl pcscCtrl = null;
    
    private File gtWorkspace = null;
    
    /**
     * Creates a a new control object to run HJP GT testcases remotely and without Hardware.
     * 
     * @param gtWorkspacePath
     *            Path HJP testcase scripts.
     * @param hostGT
     *            Host name of machine running Global Tester.
     * @param portGTService
     *            Port number for server commands.
     * @param portGTResult
     *            Port number for result commands.
     * @param portGTAPDU
     *            Port number of APDU socket.
     * @param hostPCSC
     *            Host name of remote PC/SC device.
     * @param portPCSC
     *            Port number of remote PS/SC device.
     */
    public GTCardSimCtrl(String gtWorkspacePath, String hostGT, int portGTService, int portGTResult, int portGTAPDU, String hostPCSC, int portPCSC) {
        this.remoteGT = true;
        
        gtWorkspace = new File(gtWorkspacePath);
        if (!gtWorkspace.isDirectory() || !gtWorkspace.exists())
            gtWorkspace = null;
        
        this.hostGT = hostGT;
        this.portGTService = portGTService;
        this.portGTResult = portGTResult;
        this.portGTAPDU = portGTAPDU;
        
        Security.addProvider(new BouncyCastleProvider());
        
        pcscCtrl = new RemotePCSCCtrl(hostPCSC, portPCSC);
        try {
            pcscCtrl.init((Inet4Address) InetAddress.getByName(this.hostGT), this.portGTAPDU);
        } catch (UnknownHostException e) {
            Logger.CardSim.logState("Unable to connect to remote PCSC device:" + e.getMessage(), LogLevel.Error);
        }
    }
    
    /**
     * Creates a a new control object to use HJP GT without Hardware.
     * 
     * @param hostGT
     *            Host name of machine running Global Tester.
     * @param portGTAPDU
     *            Port number of APDU socket.
     * @param hostPCSC
     *            Host name of remote PC/SC device.
     * @param portPCSC
     *            Port number of remote PS/SC device.
     */
    public GTCardSimCtrl(String hostGT, int portGTAPDU, String hostPCSC, int portPCSC) {
        this.hostGT = hostGT;
        this.portGTAPDU = portGTAPDU;
        
        Security.addProvider(new BouncyCastleProvider());
        
        pcscCtrl = new RemotePCSCCtrl(hostPCSC, portPCSC);
        try {
            pcscCtrl.init((Inet4Address) InetAddress.getByName(this.hostGT), this.portGTAPDU);
        } catch (UnknownHostException e) {
            Logger.CardSim.logState("Unable to connect to remote PCSC device:" + e.getMessage(), LogLevel.Error);
        }
    }
    
    /**
     * Creates a a new control object to run HJP GT testcases remotely.
     * 
     * @param gtWorkspacePath
     *            Path HJP testcase scripts.
     * @param host
     *            Host name of machine running Global Tester.
     * @param portService
     *            Port number for server commands.
     * @param portResult
     *            Port number for result commands.
     */
    public GTCardSimCtrl(String gtWorkspacePath, String host, int portService, int portResult) {
        this.remoteGT = true;
        
        gtWorkspace = new File(gtWorkspacePath);
        if (!gtWorkspace.isDirectory() || !gtWorkspace.exists())
            gtWorkspace = null;
        
        this.hostGT = host;
        this.portGTService = portService;
        this.portGTResult = portResult;
        
        Security.addProvider(new BouncyCastleProvider());
    }
    
    /**
     * Creates a a new control object to personalize HJP GT card simulation.
     * 
     * @param gtWorkspacePath
     *            Path HJP testcase scripts.
     */
    public GTCardSimCtrl(String gtWorkspacePath) {
        gtWorkspace = new File(gtWorkspacePath);
        if (!gtWorkspace.isDirectory() || !gtWorkspace.exists())
            gtWorkspace = null;
        
        Security.addProvider(new BouncyCastleProvider());
    }
    
    /**
     * Initializes card simulation.
     * 
     * @param cvcaCertPath
     *            Path of AT CVCA certificate to personalize card.
     * @param cardDate
     *            Date of card.
     */
    public void initCard(String cvcaCertPath, String cardDate, String pathIS, String pathAT, String fileAdd) {
        DataBuffer cert = null;
        Date date = null;
        
        File cvcaCert = new File(cvcaCertPath);
        if (cvcaCert.exists())
            try {
                cert = DataBuffer.readFromFile(cvcaCert.getAbsolutePath());
            } catch (IOException e) {
                Logger.CardSim.logState("Unable to read CVCA certificate from file:" + e.getMessage(), LogLevel.Error);
            }
        
        DateFormat dateFor = new SimpleDateFormat("yyyyMMdd");
        
        try {
            date = dateFor.parse(cardDate);
        } catch (ParseException e) {
            Logger.CardSim.logState("Invalid date format: " + cardDate, LogLevel.Error);
        }
        
        initCard(cert, date, pathIS, pathAT, fileAdd);
    }
    
    /**
     * Initializes card simulation.
     * 
     * @param rawCVCACert
     *            Raw AT CVCA certificate to personalize card.
     * @param cardDate
     *            Date of card.
     */
    public void initCard(DataBuffer rawCVCACert, Date cardDate, String pathIS, String pathAT, String fileAdd) {
        setupATCVCA(rawCVCACert, pathAT, fileAdd);
        setupCardDate(cardDate, pathIS, fileAdd);
    }
    
    /**
     * Sets up CVCA certificate and public key.
     * 
     * @param rawCert
     */
    private void setupATCVCA(DataBuffer rawCert, String pathAT, String fileAdd) {
        if (gtWorkspace != null) {
            File atCertsDir = new File(gtWorkspace.getAbsolutePath() + "/" + pathAT);
            
            if (atCertsDir.isDirectory() && atCertsDir.exists()) {
                // Save CVCA certificate
                try {
                    rawCert.writeToFile(atCertsDir.getAbsolutePath() + "\\CVCA_Cert_01" + fileAdd + ".cvcert");
                } catch (IOException e) {
                    Logger.CardSim.logState("Unable to save AT CVCA certificate:" + e.getMessage(), LogLevel.Error);
                }
                
                // Generate public key and save
                CVCertificate cert;
                try {
                    cert = new CVCertificate(rawCert);
                    DataBuffer pubKey = new DataBuffer(cert.getPublicKey().getPublicKey().getEncoded());
                    pubKey.writeToFile(atCertsDir.getAbsolutePath() + "\\CVCA_KEY_01" + fileAdd + ".bin");
                } catch (Exception e) {
                    Logger.CardSim.logState("Unable to save AT CVCA certificates public key:" + e.getMessage(), LogLevel.Error);
                }
            } else
                Logger.CardSim.logState("AT certificate directory not found: " + atCertsDir.getAbsolutePath(), LogLevel.Error);
        }
    }
    
    /**
     * Sets up card date.
     * 
     * @param date
     */
    private void setupCardDate(Date date, String pathIS, String fileAdd) {
        if (gtWorkspace != null) {
            File isCertsDir = new File(gtWorkspace.getAbsolutePath() + "/" + pathIS);
            
            if (isCertsDir.isDirectory() && isCertsDir.exists()) {
                // Save CVCA certificate & public key
                try {
                    KeyPairGenerator keyGen = KeyPairGenerator.getInstance("ECDSA", "BC");
                    SecureRandom random = new SecureRandom();
                    keyGen.initialize(ECCCurves.BRAINPOOLP256R1.getECParameter(), random);
                    KeyPair pair = keyGen.generateKeyPair();
                    PrivateKey priv = pair.getPrivate();
                    PublicKey pub = pair.getPublic();
                    
                    PrivateKeySource privkey = new PrivateKeySource(priv);
                    PublicKeySource pubkey = new PublicKeySource(pub);
                    
                    String holderName = "DEDATECERT001";
                    
                    CVCertificate certObj = new CVCertificate();
                    certObj.setProfileId(0);
                    certObj.setCertAuthRef(holderName);
                    certObj.setCertHolderRef(holderName);
                    // the date
                    certObj.getEffDate().setDate(date);
                    certObj.getExpDate().setDate(date);
                    certObj.getCertHolderAuth().setAuth(new CVAuthorizationIS());
                    certObj.getCertHolderAuth().getAuth().setRole(CertHolderRole.CVCA);
                    certObj.getPublicKey().setAlgorithm(TAAlgorithm.ECDSA_SHA_256);
                    certObj.getPublicKey().setIncludeDomainParam(true);
                    certObj.getPublicKey().setKeySource(pubkey);
                    
                    certObj.getSignKey().setAlgorithm(TAAlgorithm.ECDSA_SHA_256);
                    certObj.getSignKey().setKeySource(privkey);
                    
                    DataBuffer rawCert = certObj.generateCert();
                    rawCert.writeToFile(isCertsDir.getAbsolutePath() + "\\CVCA_Cert_01" + fileAdd + ".cvcert");
                    
                    DataBuffer pubKey = new DataBuffer(pub.getEncoded());
                    pubKey.writeToFile(isCertsDir.getAbsolutePath() + "\\CVCA_KEY_01" + fileAdd + ".bin");
                    
                    DataBuffer privKey = new DataBuffer(priv.getEncoded());
                    privKey.writeToFile(isCertsDir.getAbsolutePath() + "\\CVCA_KEY_01" + fileAdd + ".pkcs8");
                } catch (Exception e) {
                    Logger.CardSim.logState("Unable to save IS CVCA certificate:" + e.getMessage(), LogLevel.Error);
                }
            }
        }
    }
    
    /**
     * Starts card simulation.
     * 
     * @return True, if started successfully.
     */
    public boolean start() {
        boolean result = true;
        
        if (remoteGT)
            result = startTestcase("TS_eID_1.2.1", "GT Scripts ePA EAC2 Reader BSI");
        
        if (pcscCtrl != null)
            pcscCtrl.connect();
        
        return result;
    }
    
    /**
     * Stops card simulation.
     * 
     * @return True, if stopped successfully.
     */
    public boolean stop() {
        boolean result = true;
        
        if (pcscCtrl != null)
            pcscCtrl.disconnect();
        
        if (remoteGT)
            result = stopTestcase();
        
        return result;
    }
    
    /**
     * Starts specific testcase-
     * 
     * @param name
     *            Name of Testcase.
     * @param project
     *            Name of proect in GlobalTester.
     * @return True, if started successfully.
     */
    private boolean startTestcase(String name, String project) {
        boolean result = true;
        
        Socket server = null;
        
        try {
            server = new Socket(hostGT, portGTService);
        } catch (Exception e) {
            Logger.CardSim.logState("Unable to connect to socket:" + e.getMessage(), LogLevel.Error);
            return false;
        }
        
        PrintStream out = null;
        BufferedReader in = null;
        try {
            out = new PrintStream(server.getOutputStream());
            in = new BufferedReader(new InputStreamReader(server.getInputStream()));
        } catch (IOException e) {
            Logger.CardSim.logState("Unable to access socket:" + e.getMessage(), LogLevel.Error);
            result = false;
        }
        
        if (out != null && in != null) {
            String command = null;
            String response = null;
            
            try {
                // initialize testmanager
                command = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><setPreferences>"
                        + "<preference qualifier=\"org.globaltester.testmanager\" key=\"PROFILES_SHOW_DIALOG\"> false </preference></setPreferences>";
                
                out.println(command);
                out.flush();
                response = in.readLine();
                processResponse(command, response);
                
                // set project
                command = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><changeProject>" + project + "</changeProject>";
                
                out.println(command);
                out.flush();
                response = in.readLine();
                processResponse(command, response);
                
                // set testcase
                command = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><testcase>" + name + "</testcase>";
                
                out.println(command);
                out.flush();
                response = in.readLine();
                processResponse(command, response);
            } catch (IOException e) {
                Logger.CardSim.logState("Unable to read socket:" + e.getMessage(), LogLevel.Error);
                result = false;
            }
        }
        
        try {
            server.close();
        } catch (IOException e) {
            Logger.CardSim.logState("Unable to close socket:" + e.getMessage(), LogLevel.Error);
        }
        
        return result;
    }
    
    /**
     * Stops current testcase.
     * 
     * @return True, if stopped successfully.
     */
    private boolean stopTestcase() {
        boolean result = true;
        Socket server = null;
        
        try {
            server = new Socket(hostGT, portGTResult);
        } catch (Exception e) {
            Logger.CardSim.logState("Unable to connect to socket:" + e.getMessage(), LogLevel.Error);
            return false;
        }
        
        PrintStream out = null;
        BufferedReader in = null;
        try {
            out = new PrintStream(server.getOutputStream());
            in = new BufferedReader(new InputStreamReader(server.getInputStream()));
        } catch (IOException e) {
            Logger.CardSim.logState("Unable to access socket:" + e.getMessage(), LogLevel.Error);
            result = false;
        }
        
        if (out != null && in != null) {
            String command = null;
            String response = null;
            
            try {
                // send testresult
                command = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><testresult>PASSED</testresult>";
                
                out.println(command);
                out.flush();
                response = in.readLine();
                processResponse(command, response);
            } catch (IOException e) {
                Logger.CardSim.logState("Unable to read socket:" + e.getMessage(), LogLevel.Error);
                result = false;
            }
        }
        
        try {
            server.close();
        } catch (IOException e) {
            Logger.CardSim.logState("Unable to close socket:" + e.getMessage(), LogLevel.Error);
        }
        
        return result;
    }
    
    private void processResponse(String command, String response) {
        Logger.CardSim.logProtocol(Protocols.ISO7816_APDU.toString(), ProtocolDirection.sent, this.getClass().getSimpleName(), "Card", command);
        Logger.CardSim.logProtocol(Protocols.ISO7816_APDU.toString(), ProtocolDirection.received, "Card", this.getClass().getSimpleName(), response);
    }
    
    public static void main(String[] args) {
        
        String workspace = null;
        
        String subPathIS = null;
        String subPathAT = null;
        
        String fileAdd = "";
        
        String CVCAcertFile = null;
        String cardDate = null;
        
        if (args.length < 1) {
            System.out.println("No arguments ...");
            showHelp();
        } else {
            for (int i = 0; i < args.length; i++) {
                switch (args[i]) {
                case "-?":
                    showHelp();
                    return;
                case "-w":
                    if (i + 1 >= args.length || args[i + 1].startsWith("-")) {
                        System.out.println("No parameter for argument: " + args[i]);
                        showHelp();
                        return;
                    }
                    workspace = args[++i];
                    break;
                case "-is":
                    if (i + 1 >= args.length || args[i + 1].startsWith("-")) {
                        System.out.println("No parameter for argument: " + args[i]);
                        showHelp();
                        return;
                    }
                    subPathIS = args[++i];
                    break;
                case "-at":
                    if (i + 1 >= args.length || args[i + 1].startsWith("-")) {
                        System.out.println("No parameter for argument: " + args[i]);
                        showHelp();
                        return;
                    }
                    subPathAT = args[++i];
                    break;
                case "-fileAdd":
                    if (i + 1 >= args.length || args[i + 1].startsWith("-")) {
                        System.out.println("No parameter for argument: " + args[i]);
                        showHelp();
                        return;
                    }
                    fileAdd = args[++i];
                    break;
                case "-p":
                    if (i + 2 >= args.length || args[i + 1].startsWith("-") || args[i + 2].startsWith("-")) {
                        System.out.println("No parameter for argument: " + args[i]);
                        showHelp();
                        return;
                    }
                    CVCAcertFile = args[++i];
                    cardDate = args[++i];
                    break;
                default:
                    System.out.println("Unknown argument: " + args[i]);
                    showHelp();
                    return;
                }
            }
        }
        
        //GTCardSimCtrl hjpSim = new GTCardSimCtrl(args[0], "gt-simulator.secunet.de", 6789, 6788, 9876, "pcscemulator.secunet.de", 12345);
        //GTCardSimCtrl hjpSim = new GTCardSimCtrl(args[0], "gt-simulator.secunet.de", 6789, 6788);
        
        GTCardSimCtrl hjpSim = new GTCardSimCtrl(workspace);
        
        hjpSim.initCard(CVCAcertFile, cardDate, subPathIS, subPathAT, fileAdd);
        System.out.println("Card personalized.");
        /*hjpSim.start();
        
        try {
            System.out.println("Simulation started. Press enter to stop ...");
            System.in.read();
        } catch (Throwable ignored) {
        }
        
        hjpSim.stop();*/
    }
    
    /**
     * Shows help text.
     */
    private static void showHelp()
    {
        String runCmd = "java -jar " + GTCardSimCtrl.class.getSimpleName();
        
        System.out.println();
        System.out.println("CommandLine Interface for using HJP GT remotly.");
        
        // Usage
        System.out.println();
        System.out.println("Usage:");
        System.out.println(" " + runCmd + " -?");
        System.out.println(" " + runCmd + " -w <path> -is <path> -at <path> [-fileAdd \"a\" -p <file> date]");
        
        // Parameter
        System.out.println();
        System.out.println("-?\tShows this help.");
        System.out.println("-w\tSets workspace of global tester");
        System.out.println("-is\tSets path to IS certificates");
        System.out.println("-at\tSets path to AT certificates");
        System.out.println("-fileAdd\tFilename addition");
        System.out.println("-p\tPersonalises card with cvca certificate file an card date. Format: yyyyMMdd");
        
        System.out.println();
    }
}
