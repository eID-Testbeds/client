package com.secunet.ipsmall.tls;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

public class BouncyCastleServerSocket extends ServerSocket {
    
    private BouncyCastlePSKTlsServer pskTlsServer;
    private BouncyCastleTlsServer tlsServer;
    
    private ServerSocket serverSocket;
    private Object closeLock = new Object();
    
    private BouncyCastleServerSocket(int port, InetAddress bindAddr) throws IOException {
        this.serverSocket = new ServerSocket(port,50,bindAddr);
    }

    public BouncyCastleServerSocket(BouncyCastlePSKTlsServer pskTlsServer, int port) throws IOException {
        this(pskTlsServer, port, null);
    }
    
    public BouncyCastleServerSocket(BouncyCastlePSKTlsServer pskTlsServer, int port, InetAddress bindAddr) throws IOException {
        this(port,bindAddr);
        this.pskTlsServer = pskTlsServer;
    }

    public BouncyCastleServerSocket(BouncyCastleTlsServer tlsServer, int port) throws IOException {
        this(tlsServer, port, null);
    }
    
    public BouncyCastleServerSocket(BouncyCastleTlsServer tlsServer, int port, InetAddress bindAddr) throws IOException {
        this(port,bindAddr);
        this.tlsServer = tlsServer;
    }

    @Override
    public Socket accept() throws IOException {
        Socket socket = serverSocket.accept();
        BouncyCastleSocket bcSocket = new BouncyCastleSocket(socket);
        if(null != pskTlsServer) {
            bcSocket.initTLS(pskTlsServer);
        }
        else {
            bcSocket.initTLS(tlsServer);
        }
        return bcSocket;
    }

    @Override
    public void close() throws IOException {
        synchronized(closeLock) {
            serverSocket.close();
        }
    }

    @Override
    public boolean isClosed() {
        synchronized(closeLock) {
            return serverSocket.isClosed();
        }
    }

    @Override
    public int getLocalPort() {
        return serverSocket.getLocalPort();
    }

    public byte[] addPSKCredential(String ident, byte[] key) {
        return pskTlsServer.addPSKCredential(ident, key);
    }

}
