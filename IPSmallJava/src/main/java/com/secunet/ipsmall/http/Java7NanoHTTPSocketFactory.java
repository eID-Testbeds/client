package com.secunet.ipsmall.http;

import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.PrivateKey;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.util.HashMap;
import java.util.List;

import javax.net.ssl.KeyManager;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLParameters;
import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLServerSocketFactory;
import javax.net.ssl.TrustManager;

import com.secunet.ipsmall.test.ITestData.PROTOCOLS;
import com.secunet.testbedutils.utilities.CommonUtil;

public class Java7NanoHTTPSocketFactory implements ExternalServerSocketFactory {
    
    PrivateKey m_serverKey = null;
    X509Certificate[] m_serverCertificateChain = null;
    
    boolean m_debug = false;
    
    public Java7NanoHTTPSocketFactory(X509Certificate[] certificate, PrivateKey serverKey, boolean debug) {
        m_serverCertificateChain = certificate;
        m_serverKey = serverKey;
        
        m_debug = debug;
        
        if (m_debug) {
            System.setProperty("javax.net.debug", "all");
        }
    }
    
    @Override
    public ServerSocket create(String hostname, int port, List<String> protocols, List<String> cipherSuites) throws Exception {
        // if an explicit protocol is passed we choose it (the first one) for default init. If nothing passed, use TLSv1.2:
        String protocol = ( protocols != null && protocols.size() > 0 ) ? toJava7SocketProtocol(protocols)[0] : "TLSv1.2";
        SSLContext sslContext = SSLContext.getInstance(protocol);
        sslContext.init(new KeyManager[] { new MemoryKeyManager(m_serverKey, m_serverCertificateChain) }, new TrustManager[] { new MemoryTrustManager() },
                new SecureRandom());
        
        SSLServerSocketFactory serverSocketFactory = sslContext.getServerSocketFactory();
        
        SSLServerSocket serverSocket;
        
        if (hostname != null) {
            serverSocket = (SSLServerSocket) serverSocketFactory.createServerSocket(port, 10, InetAddress.getByName(hostname));
            
        } else {
            serverSocket = (SSLServerSocket) serverSocketFactory.createServerSocket(port, 10);
            
        }
        serverSocket.setEnabledCipherSuites(serverSocketFactory.getDefaultCipherSuites());
        
        SSLParameters params = sslContext.getDefaultSSLParameters();
        
        if (protocols != null && protocols.size() > 0) {
            params.setProtocols(toJava7SocketProtocol(protocols));
        }
        
        if (cipherSuites != null && cipherSuites.size() > 0) {
            String[] array = new String[cipherSuites.size()];
            params.setCipherSuites(cipherSuites.toArray(array));
        }
        
        serverSocket.setSSLParameters(params);
        
        return serverSocket;
    }
    
    @Override
    public HashMap<String, Object> getAdditionalSocketInfo(Socket socket) {
        HashMap<String, Object> info = new HashMap<String, Object>();
        //
        // if (socket instanceof SSLSocket) {
        // SSLSocket sock = (SSLSocket) socket;
        // // TODO
        //
        // }
        
        return info;
    }
    
    /**
     * Converts the passed protocols as defined in config.properties (e.g. 'tls10') to the appropriate String-representations expected by Java7 (e.g.
     * 'TLSv1.0').
     * 
     * See {@link http://docs.oracle.com/javase/7/docs/technotes/guides/security/StandardNames.html#SSLContext}
     * 
     * @param protocol
     * @return
     */
    private String[] toJava7SocketProtocol(List<String> protocols) {
        String[] result = new String[protocols.size()];
        
        for (int i = 0; i < protocols.size(); i++) {
            if (protocols.get(i).equalsIgnoreCase(PROTOCOLS.sslv2.toString())) {
                result[i] = "SSLv2";
            } else if (protocols.get(i).equalsIgnoreCase(PROTOCOLS.sslv3.toString())) {
                result[i] = "SSLv3";
            } else if (protocols.get(i).equalsIgnoreCase(PROTOCOLS.tls10.toString())) {
                result[i] = "TLSv1";
            } else if (protocols.get(i).equalsIgnoreCase(PROTOCOLS.tls11.toString())) {
                result[i] = "TLSv1.1";
            } else if (protocols.get(i).equalsIgnoreCase(PROTOCOLS.tls12.toString())) {
                result[i] = "TLSv1.2";
            } else {
                throw new RuntimeException("Defined protocols contain invalid protocol for iaik adatper: " + CommonUtil.listToCommaSeparatedString(protocols));
            }
        }
        
        return result;
        
    }
    
    
    public static String[] toProtocolEnumNames(String[] protocols) {
        String[] result = new String[protocols.length];
        
        for (int i = 0; i < protocols.length; i++) {
            
            switch (protocols[i]) {
                case "SSLv2":
                    result[i] = PROTOCOLS.sslv2.toString();
                    break;
                case "SSLv3":
                    result[i] = PROTOCOLS.sslv3.toString();
                    break;
                case "TLSv1":
                    result[i] = PROTOCOLS.tls10.toString();
                    break;
                case "TLSv1.1":
                    result[i] = PROTOCOLS.tls11.toString();
                    break;
                case "TLSv1.2":
                    result[i] = PROTOCOLS.tls12.toString();
                    break;
                
                default:
                    result[i] = "unknown protocol";
            }
        }
        return result;
        
    }
    
}
