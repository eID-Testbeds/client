package com.secunet.ipsmall.http;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.List;

public interface ExternalServerSocketFactory {
    
    /**
     * 
     * @param hostname
     * @param port
     * @param protocols
     *            allowed protocols (as defined in *.tls.version, values: sslv3, tls10, tls11 and tls12). May be null.
     * @param cipherSuites
     *            allowed cipher suites (as defined in *.tls.ciphersuites). May be null.
     * @return
     * @throws IOException
     * @throws Exception
     */
    public ServerSocket create(String hostname, int port, List<String> protocols, List<String> cipherSuites) throws IOException, Exception;
    
    public HashMap<String, Object> getAdditionalSocketInfo(Socket socket);
}