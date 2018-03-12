package com.secunet.ipsmall.tls;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.SocketException;
import java.security.SecureRandom;

import org.bouncycastle.util.Strings;

import org.bouncycastle.crypto.tls.TlsServer;

public class BouncyCastleSocket extends Socket {
    
    private Socket socket;
    private BouncyCastleTlsServerProtocol tlsServerProtocol;
    private TlsServer tlsServer;
    private boolean closed = false;
    private boolean handshakeDone = false;
    
    BouncyCastleSocket(Socket socket) {
        super();
        this.socket = socket;
    }
    
    void initTLS(TlsServer tlsServer) throws IOException {
        this. tlsServer = tlsServer;
        tlsServerProtocol = new BouncyCastleTlsServerProtocol(socket, new SecureRandom());
    }
    
    public void startHandshake() throws IOException {
        tlsServerProtocol.accept(tlsServer);
        handshakeDone = true;
    }

    @Override
    public InputStream getInputStream() throws IOException {
        if(!handshakeDone) {
            startHandshake();
        }
        socket.getInputStream(); // check socket
        return tlsServerProtocol.getInputStream();
    }

    @Override
    public OutputStream getOutputStream() throws IOException {
        if(!handshakeDone) {
            startHandshake();
        }
        socket.getOutputStream(); // check socket
        return tlsServerProtocol.getOutputStream();
    }

    @Override
    public synchronized int getSoTimeout() throws SocketException {
        return socket.getSoTimeout();
    }

    @Override
    public synchronized void setSoTimeout(int timeout) throws SocketException {
        socket.setSoTimeout(timeout);
    }

    @Override
    public synchronized void close() throws IOException {
        tlsServerProtocol.close();
        socket.close();
        closed = true;
    }

    @Override
    public boolean isClosed() {
        return closed;
    }

    @Override
    public SocketAddress getRemoteSocketAddress() {
        return socket.getRemoteSocketAddress();
    }

    @Override
    public SocketAddress getLocalSocketAddress() {
        return socket.getLocalSocketAddress();
    }

    @Override
    public InetAddress getLocalAddress() {
        return socket.getLocalAddress();
    }

    @Override
    public int getLocalPort() {
        return socket.getLocalPort();
    }

    public int getActiveCipherSuite() throws IOException {
        return tlsServerProtocol.getActiveCipherSuite();
    }

    public String getPSKIdentity() throws IOException {
        return Strings.fromUTF8ByteArray(tlsServerProtocol.getPSKIdentity());
    }

}
