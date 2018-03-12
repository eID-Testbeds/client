package com.secunet.ipsmall.tls;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Vector;

import org.bouncycastle.crypto.agreement.DHStandardGroups;
import org.bouncycastle.crypto.params.AsymmetricKeyParameter;
import org.bouncycastle.crypto.params.DHParameters;
import org.bouncycastle.jce.provider.X509CertificateObject;
import org.bouncycastle.crypto.tls.Certificate;
import org.bouncycastle.crypto.tls.CipherSuite;
import org.bouncycastle.crypto.tls.DefaultTlsEncryptionCredentials;
import org.bouncycastle.crypto.tls.DefaultTlsSignerCredentials;
import org.bouncycastle.crypto.tls.ProtocolVersion;
import org.bouncycastle.crypto.tls.SignatureAlgorithm;
import org.bouncycastle.crypto.tls.SignatureAndHashAlgorithm;
import org.bouncycastle.crypto.tls.TlsECDHEKeyExchange;
import org.bouncycastle.crypto.tls.TlsEncryptionCredentials;
import org.bouncycastle.crypto.tls.TlsKeyExchange;
import org.bouncycastle.crypto.tls.TlsSignerCredentials;

import com.secunet.ipsmall.log.IModuleLogger.LogLevel;
import com.secunet.ipsmall.log.Logger;
import com.secunet.testbedutils.utilities.BouncyCastleTlsHelper;

public class BouncyCastleTlsServer extends BouncyCastleNotifyingTlsServer {
    
    private AsymmetricKeyParameter serverPrivateKey;
    private Certificate serverTlsCertificate;
    
    private ProtocolVersion protocolMinimumVersion = ProtocolVersion.TLSv10;
    private ProtocolVersion protocolMaximumVersion = ProtocolVersion.TLSv12;
    
    private DHParameters dhParameters = DHStandardGroups.rfc5114_2048_224;
    
    private int forcedCurveForECDHEKeyExchange = -1; // -1 = no curve is forced
    private SignatureAndHashAlgorithm forcedSignatureAndHashAlgorithm = null; // null = no SignatureAndHashAlgorithm is forced

    private int[] enabledCipherSuites = new int[]
        {
            CipherSuite.TLS_ECDHE_RSA_WITH_AES_256_GCM_SHA384,
            CipherSuite.TLS_ECDHE_RSA_WITH_AES_128_GCM_SHA256,
            CipherSuite.TLS_ECDHE_RSA_WITH_AES_256_CBC_SHA384,
            CipherSuite.TLS_ECDHE_RSA_WITH_AES_128_CBC_SHA256,
            CipherSuite.TLS_RSA_WITH_AES_256_GCM_SHA384,
            CipherSuite.TLS_RSA_WITH_AES_128_GCM_SHA256,
            CipherSuite.TLS_RSA_WITH_AES_256_CBC_SHA256,
            CipherSuite.TLS_RSA_WITH_AES_128_CBC_SHA256
        };

    public BouncyCastleTlsServer(AsymmetricKeyParameter serverPrivateKey, Certificate serverCertificateChain) {
        super();
        this.serverPrivateKey = serverPrivateKey;
        this.serverTlsCertificate = serverCertificateChain;
        
        // dump certificate to log
        try {
            X509CertificateObject certObject = new X509CertificateObject(serverTlsCertificate.getCertificateAt(0));
            Logger.TLS.logState(certObject.toString(), LogLevel.Debug);
        } catch (Exception ignore) {
        }
    }
    
    public void setAllowedProtocolVersions(ProtocolVersion protocolMinimumVersion,
            ProtocolVersion protocolMaximumVersion) {
        if(protocolMinimumVersion.isEqualOrEarlierVersionOf(protocolMaximumVersion)) {
            this.protocolMinimumVersion = protocolMinimumVersion;
            this.protocolMaximumVersion = protocolMaximumVersion;
        }
    }

    public void setCipherSuites(int[] cipherSuites) {
        enabledCipherSuites = cipherSuites;
    }

    /**
     * This method enables cipher suites given as a list of strings.
     * If a cipher suite is not recognized, it will be ignored. 
     * @param cipherSuites list of cipher suites given as list of strings
     */
    public void setCipherSuites(List<String> cipherSuites) {
        enabledCipherSuites = convertCipherSuiteList(cipherSuites);
    }
    
    public void setDHParameters(DHParameters dhParams) {
        dhParameters = dhParams;
    }
    
    /**
     * This method sets DHParameters for DH-based key exchanges given as a string from DHStandardGroups names.
     * If DHParameters are not recognized, they will not be set. 
     * @param dhParams name of DHParameters as given in class DHStandardGroups
     */
    public void setDHParameters(String dhParams) throws IllegalArgumentException {
        DHParameters tmp = BouncyCastleTlsHelper.convertDHStandardGroupsStringToDHParametersObject(dhParams);
        if(null != tmp) {
            dhParameters = tmp;
        }
        else {
            throw new IllegalArgumentException("Could not convert string '" + dhParams + "' to a corresponding standardized DHParameters object");
        }
    }
    
    public void setForcedCurveForECDHEKeyExchange(int forcedCurve) {
        forcedCurveForECDHEKeyExchange = forcedCurve;
    }

    /**
     * This method forces use of the given named curve during ECDHEKeyExchange.
     * The SupportedEllipticCurve extension will be ignored.
     * @param namedCurve named curve given as string
     */
    public void setForcedCurveForECDHEKeyExchange(String namedCurve) throws IllegalArgumentException {
        if(namedCurve == null) {
            forcedCurveForECDHEKeyExchange = -1; // no curve is forced
        }
        else {
            forcedCurveForECDHEKeyExchange = BouncyCastleTlsHelper.convertNamedCurveStringToInt(namedCurve);
        }
    }

    /**
     * This method forces use of the given SignatureAndHashAlgorithm.
     * The SupportedSignatureAndHashAlgorithm extension will be ignored.
     * @param signatureAndHashAlgorithm signature and hash algorithm given as string, e.g. SHA256withRSA
     */
    public void setForcedSignatureAlgorithm(String signatureAndHashAlgorithm) throws IllegalArgumentException {
        if(signatureAndHashAlgorithm == null) {
            forcedSignatureAndHashAlgorithm = null; // no SignatureAndHashAlgorithm is forced
        }
        else {
            SignatureAndHashAlgorithm tmp = BouncyCastleTlsHelper.convertSignatureAndHashAlgorithmStringToClass(signatureAndHashAlgorithm);
            if(null != tmp) {
                forcedSignatureAndHashAlgorithm = tmp;
            }
            else {
                throw new IllegalArgumentException("Could not convert string '" + signatureAndHashAlgorithm + "' to a corresponding object from class SignatureAndHashAlgorithm");
            }
        }
    }

    @Override
    protected TlsEncryptionCredentials getRSAEncryptionCredentials() throws IOException {
        return new DefaultTlsEncryptionCredentials(context, serverTlsCertificate, serverPrivateKey);
    }
    
    @Override
    protected TlsSignerCredentials getRSASignerCredentials() throws IOException {
        return provideSignerCredentials(SignatureAlgorithm.rsa);
    }
    
    @Override
    protected TlsSignerCredentials getDSASignerCredentials() throws IOException {
        return provideSignerCredentials(SignatureAlgorithm.dsa);
    }

    @Override
    protected TlsSignerCredentials getECDSASignerCredentials() throws IOException {
        return provideSignerCredentials(SignatureAlgorithm.ecdsa);
    }

    @Override
    protected int[] getCipherSuites() {
        sendNotificationEnabledCipherSuites(enabledCipherSuites);
        return enabledCipherSuites;
    }

    @Override
    protected DHParameters getDHParameters() {
        sendNotificationSelectedDHParameters(dhParameters);
        return dhParameters;
    }

    @Override
    protected ProtocolVersion getMaximumVersion() {
        sendNotificationEnabledMaximumVersion(protocolMaximumVersion);
        return protocolMaximumVersion;
    }

    @Override
    protected ProtocolVersion getMinimumVersion() {
        sendNotificationEnabledMinimumVersion(protocolMinimumVersion);
        return protocolMinimumVersion;
    }
    
    /**
     * @see com.secunet.bouncycastle.crypto.tls.DefaultTlsServer#createECDHEKeyExchange(int)
     */
    @Override
    protected TlsKeyExchange createECDHEKeyExchange(int keyExchange) {
        if (-1 == forcedCurveForECDHEKeyExchange) {
            LinkedList<String> allStrings = BouncyCastleTlsHelper.getAllNamedCurveStrings();
            String curves = "";
            for (String entry : allStrings) {
                curves += " " + entry;
            }
            Logger.TLS.logState("TLS server supports named curves for ECDHEKeyExchange: " + curves);
            return super.createECDHEKeyExchange(keyExchange);
        } else {
            Logger.TLS.logState("TLS server forces named curve for ECDHEKeyExchange: " + BouncyCastleTlsHelper.convertNamedCurveIntToString(forcedCurveForECDHEKeyExchange));
            return new TlsECDHEKeyExchange(keyExchange, supportedSignatureAlgorithms, new int[] { forcedCurveForECDHEKeyExchange },
                    clientECPointFormats, serverECPointFormats);
        }
    }
    
    private TlsSignerCredentials provideSignerCredentials(short signatureAlgorithm) throws IOException {
        SignatureAndHashAlgorithm signatureAndHashAlgorithm = null;
        if(forcedSignatureAndHashAlgorithm != null) {
            signatureAndHashAlgorithm = forcedSignatureAndHashAlgorithm;
        }
        else {
            // CODE AND COMMENTS ARE COPIED FROM BOUNCY CASTLE SAMPLE CODE
            // see com.secunet.bouncycastle.crypto.tls.test.MockTlsServer
            
            /*
             * TODO Note that this code fails to provide default value for the client supported
             * algorithms if it wasn't sent.
             * 
             */
            @SuppressWarnings("rawtypes")
            Vector sigAlgs = supportedSignatureAlgorithms;
            if (sigAlgs != null)
            {
                for (int i = 0; i < sigAlgs.size(); ++i)
                {
                    SignatureAndHashAlgorithm sigAlg = (SignatureAndHashAlgorithm)
                        sigAlgs.elementAt(i);
                    if (sigAlg.getSignature() == signatureAlgorithm)
                    {
                        signatureAndHashAlgorithm = sigAlg;
                        break;
                    }
                }
    
                if (signatureAndHashAlgorithm == null)
                {
                    return null;
                }
            }
        }
        
        return new DefaultTlsSignerCredentials(context, serverTlsCertificate, serverPrivateKey, signatureAndHashAlgorithm);
    }

    private int[] convertCipherSuiteList(List<String> cipherSuiteList) {
        ArrayList<Integer> tempList = new ArrayList<Integer>();
        for(String cipherSuite : cipherSuiteList) {
            try {
                tempList.add(BouncyCastleTlsHelper.convertCipherSuiteStringToInt(cipherSuite));
            }
            catch(IllegalArgumentException e) {
                Logger.TLS.logState("Configured cipher suite '" + cipherSuite + "' not recognized and will be ignored. Message: " +e.getMessage(), LogLevel.Warn);
            }
            catch(Exception e) {
                Logger.TLS.logException(e);
            }
            finally {
                
            }
        }
        
        // copy from list to array
        int[] cipherSuiteArray = new int[tempList.size()];
        for(int i=0 ; i < cipherSuiteArray.length ; i++) {
            cipherSuiteArray[i] = tempList.get(i);
        }
        
        return cipherSuiteArray;
    }

}
