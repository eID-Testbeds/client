package com.secunet.ipsmall.tls;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.security.SecureRandom;

import com.secunet.bouncycastle.crypto.tls.TlsServer;
import com.secunet.bouncycastle.crypto.tls.TlsServerProtocol;
import com.secunet.ipsmall.log.Logger;

public class BouncyCastleTlsServerProtocol extends TlsServerProtocol {

    private Socket socket = null;

    public BouncyCastleTlsServerProtocol(InputStream input, OutputStream output, SecureRandom secureRandom) {
        super(input, output, secureRandom);
    }

    public BouncyCastleTlsServerProtocol(Socket socket, SecureRandom secureRandom) throws IOException {
        this(socket.getInputStream(), socket.getOutputStream(), secureRandom);
        this.socket = socket;
    }

    public byte[] getPSKIdentity() {
        return securityParameters.getPSKIdentity();
    }

    public int getActiveCipherSuite() {
        return securityParameters.getCipherSuite();
    }

    @Override
    public void accept(TlsServer tlsServer) throws IOException {
        Logger.TLS.logState("TLS handshake starting!");
        super.accept(tlsServer);
    }

    @Override
    protected int readApplicationData(byte[] buf, int offset, int len) throws IOException {
        if(socket != null) {
            try {
                socket.getInputStream();
            } catch (IOException e) {
                this.close();
            }
            finally {
                
            }
        }
        return super.readApplicationData(buf, offset, len);
    }

}
