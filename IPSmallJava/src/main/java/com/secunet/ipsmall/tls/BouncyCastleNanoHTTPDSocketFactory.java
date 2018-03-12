package com.secunet.ipsmall.tls;

import static com.secunet.testbedutils.utilities.CommonUtil.containsIgnoreCase;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.bouncycastle.crypto.params.AsymmetricKeyParameter;

import org.bouncycastle.crypto.tls.Certificate;
import org.bouncycastle.crypto.tls.CipherSuite;
import org.bouncycastle.crypto.tls.ProtocolVersion;
import com.secunet.ipsmall.http.ExternalServerSocketFactory;
import com.secunet.ipsmall.log.Logger;
import com.secunet.ipsmall.test.ITestData.PROTOCOLS;
import com.secunet.testbedutils.utilities.CommonUtil;

public class BouncyCastleNanoHTTPDSocketFactory implements ExternalServerSocketFactory {
    
    boolean usePSK = false;
    
    AsymmetricKeyParameter m_serverKey = null;
    Certificate m_serverCertificateChain = null;
    BouncyCastleTlsNotificationListener m_listener = null;
    
    ArrayList<String> m_cipherSuites = null;
    
    String m_dhParameters = null;
    
    String m_forcedCurve = null;
    String m_forcedSignatureAndHashAlgorithm = null;
    
    public BouncyCastleNanoHTTPDSocketFactory(BouncyCastleTlsNotificationListener listener, Certificate certificate, AsymmetricKeyParameter serverKey) {
        m_serverCertificateChain = certificate;
        m_serverKey = serverKey;
        m_listener = listener;
        
        m_cipherSuites = new ArrayList<String>();
        m_cipherSuites.add("TLS_ECDHE_RSA_WITH_AES_128_CBC_SHA256");
    }
    
    public void enablePSK() {
        usePSK = true;
        m_cipherSuites.clear();
        m_cipherSuites.add("TLS_RSA_PSK_WITH_AES_256_CBC_SHA");
    }
    
    public void enableCiphers(List<String> cipherSuites) {
        m_cipherSuites.clear();
        m_cipherSuites.addAll(cipherSuites);
    }
    
    public void setDHParameters(String params) {
        m_dhParameters = params;
    }

    public void setForcedCurveForECDHEKeyExchange(String curve) {
        m_forcedCurve = curve;
    }
    
    public void setForcedSignatureAlgorithm(String signatureAndHashAlgorithm) throws IllegalArgumentException {
        m_forcedSignatureAndHashAlgorithm = signatureAndHashAlgorithm;
    }

    private ProtocolVersion getProtocolMinimumVersion(List<String> protocols) {
        // SSLv2 not supported by BouncyCastle
        // TLS1.2 apparently not fully supported by BouncyCastle
        if (containsIgnoreCase(protocols, PROTOCOLS.sslv3.toString())) {
            return ProtocolVersion.SSLv3;
        } else if (containsIgnoreCase(protocols, PROTOCOLS.tls10.toString())) {
            return ProtocolVersion.TLSv10;
        } else if (containsIgnoreCase(protocols, PROTOCOLS.tls11.toString())) {
            return ProtocolVersion.TLSv11;
        } else if (containsIgnoreCase(protocols, PROTOCOLS.tls12.toString())) {
            return ProtocolVersion.TLSv12;
        }
        
        throw new RuntimeException("Defined protocols contain invalid protocol for BouncyCastle: " + CommonUtil.listToCommaSeparatedString(protocols));
    }
    
    private ProtocolVersion getProtocolMaximumVersion(List<String> protocols) {
        // SSLv2 not supported by BouncyCastle
        // TLS1.2 apparently not fully supported by BouncyCastle
        if (containsIgnoreCase(protocols, PROTOCOLS.tls12.toString())) {
            return ProtocolVersion.TLSv12;
        } else if (containsIgnoreCase(protocols, PROTOCOLS.tls11.toString())) {
            return ProtocolVersion.TLSv11;
        } else if (containsIgnoreCase(protocols, PROTOCOLS.tls10.toString())) {
            return ProtocolVersion.TLSv10;
        } else if (containsIgnoreCase(protocols, PROTOCOLS.sslv3.toString())) {
            return ProtocolVersion.SSLv3;
        }
        
        throw new RuntimeException("Defined protocols contain invalid protocol for BouncyCastle: " + CommonUtil.listToCommaSeparatedString(protocols));
    }
    
    @Override
    public ServerSocket create(String hostname, int port, List<String> protocols, List<String> cipherSuites) throws IOException {
        
        if(usePSK) {

            BouncyCastleTlsPSKIdentityManager pskIdentityManager = new BouncyCastleTlsPSKIdentityManager();
            
            BouncyCastlePSKTlsServer tlsServer = new BouncyCastlePSKTlsServer(pskIdentityManager, m_serverKey, m_serverCertificateChain);
            tlsServer.addNotificationListener(m_listener);
            
            if (protocols != null && protocols.size() > 0) {
                tlsServer.setAllowedProtocolVersions(getProtocolMinimumVersion(protocols), getProtocolMaximumVersion(protocols));
            }
    
            // if cipherSuites are set explicitly (eidservice.tls.ciphersuites), they
            // are applied here and potentially overwrite cipher suites set before by
            // other testcase params (e.g. eidservice.usepsk)
            if (cipherSuites != null && cipherSuites.size() > 0) {
                enableCiphers(cipherSuites);
            }
            try {
                tlsServer.setCipherSuites(m_cipherSuites);
            } catch (IllegalArgumentException e) {
                Logger.TLS.logException(e);
            }
            
            if (hostname != null) {
                return new BouncyCastleServerSocket(tlsServer, port, InetAddress.getByName(hostname));
            }
            else {
                return new BouncyCastleServerSocket(tlsServer, port);
            }
            
        }
        else {

            BouncyCastleTlsServer tlsServer = new BouncyCastleTlsServer(m_serverKey, m_serverCertificateChain);
            tlsServer.addNotificationListener(m_listener);
            
            if (protocols != null && protocols.size() > 0) {
                tlsServer.setAllowedProtocolVersions(getProtocolMinimumVersion(protocols), getProtocolMaximumVersion(protocols));
            }
            
            if (m_dhParameters != null) {
                try {
                    tlsServer.setDHParameters(m_dhParameters);
                } catch (IllegalArgumentException e) {
                    Logger.TLS.logException(e);
                }
            }
    
            if (m_forcedCurve != null) {
                try {
                    tlsServer.setForcedCurveForECDHEKeyExchange(m_forcedCurve);
                } catch (IllegalArgumentException e) {
                    Logger.TLS.logException(e);
                }
            }
    
            if (m_forcedSignatureAndHashAlgorithm != null) {
                try {
                    tlsServer.setForcedSignatureAlgorithm(m_forcedSignatureAndHashAlgorithm);
                } catch (IllegalArgumentException e) {
                    Logger.TLS.logException(e);
                }
            }
    
            // if cipherSuites are set explicitly (eidservice.tls.ciphersuites), they
            // are applied here and potentially overwrite cipher suites set before by
            // other testcase params (e.g. eidservice.usepsk)
            if (cipherSuites != null && cipherSuites.size() > 0) {
                enableCiphers(cipherSuites);
            }
            try {
                tlsServer.setCipherSuites(m_cipherSuites);
            } catch (IllegalArgumentException e) {
                Logger.TLS.logException(e);
            }
            
            if (hostname != null) {
                return new BouncyCastleServerSocket(tlsServer, port, InetAddress.getByName(hostname));
            }
            else {
                return new BouncyCastleServerSocket(tlsServer, port);
            }

        }
    }
    
    @Override
    public HashMap<String, Object> getAdditionalSocketInfo(Socket socket) {
        HashMap<String, Object> info = new HashMap<String, Object>();
/*        
        if (socket instanceof SSLSocket) {
            SSLSocket sock = (SSLSocket) socket;
            
            if (CipherSuite.TLS_RSA_PSK_WITH_AES_256_CBC_SHA.equals(sock.getActiveCipherSuite())) {
                info.put("PSK_IDENT", sock.getPSKIdentity());
                
            }
            
            //Log.getLogger().log(Level.INFO, "Used CipherSuite: " + sock.getActiveCipherSuite().getName());
            
            // ExtensionList list = sock.getPeerExtensions();
            // Enumeration<Extension> enume = list.listExtensions();
            // while(enume.hasMoreElements())
            // {
            // Extension ext = enume.nextElement();
            //
            // Log.getLogger().log(Level.INFO, "Ext Name: " + ext.getName());
            // Log.getLogger().log(Level.INFO, "Ext Type: " +
            // ext.getExtensionType().getName());
            //
            // if(ext.getType() == 10)
            // {
            // SupportedEllipticCurves curves = (SupportedEllipticCurves) ext;
            //
            // for(SupportedEllipticCurves$NamedCurve t :
            // curves.getEllipticCurveList())
            // {
            // Log.getLogger().log(Level.INFO, "Curve:  " + t.getName());
            // }
            // }
            //
            //
            // }
        }
        //Log.getLogger().log(Level.INFO, "Conn with PSK Ident - " + info.get("PSK_IDENT"));
*/
        if (socket instanceof BouncyCastleSocket) {
            BouncyCastleSocket sock = (BouncyCastleSocket) socket;
            
            try {
                switch (sock.getActiveCipherSuite()) {
                    case CipherSuite.TLS_RSA_PSK_WITH_3DES_EDE_CBC_SHA:
                    case CipherSuite.TLS_RSA_PSK_WITH_AES_128_CBC_SHA:
                    case CipherSuite.TLS_RSA_PSK_WITH_AES_256_CBC_SHA:
                    case CipherSuite.TLS_RSA_PSK_WITH_AES_128_GCM_SHA256:
                    case CipherSuite.TLS_RSA_PSK_WITH_AES_256_GCM_SHA384:
                    case CipherSuite.TLS_RSA_PSK_WITH_AES_128_CBC_SHA256:
                    case CipherSuite.TLS_RSA_PSK_WITH_AES_256_CBC_SHA384:
                        info.put("PSK_IDENT", sock.getPSKIdentity());
                        break;
                    default:
                        break;
                }
            } catch (IOException e) {
                Logger.TLS.logException(e);
            }
            
        }
        
        return info;
    }

}
