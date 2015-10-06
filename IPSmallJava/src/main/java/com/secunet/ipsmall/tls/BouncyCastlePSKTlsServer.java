package com.secunet.ipsmall.tls;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.bouncycastle.crypto.agreement.DHStandardGroups;
import org.bouncycastle.crypto.params.AsymmetricKeyParameter;
import org.bouncycastle.crypto.params.DHParameters;

import com.secunet.bouncycastle.crypto.tls.Certificate;
import com.secunet.bouncycastle.crypto.tls.CipherSuite;
import com.secunet.bouncycastle.crypto.tls.DefaultTlsEncryptionCredentials;
import com.secunet.bouncycastle.crypto.tls.ProtocolVersion;
import com.secunet.bouncycastle.crypto.tls.TlsEncryptionCredentials;
import com.secunet.ipsmall.log.IModuleLogger.LogLevel;
import com.secunet.ipsmall.log.Logger;

public class BouncyCastlePSKTlsServer extends BouncyCastleNotifyingPSKTlsServer {
    
    private AsymmetricKeyParameter serverPrivateKey;
    private Certificate serverCertificate;
    
    private ProtocolVersion protocolMinimumVersion = ProtocolVersion.TLSv11;
    private ProtocolVersion protocolMaximumVersion = ProtocolVersion.TLSv12;

    private DHParameters dhParameters = DHStandardGroups.rfc5114_2048_224;
    
    private int[] enabledCipherSuites = new int[] { CipherSuite.TLS_RSA_PSK_WITH_AES_256_CBC_SHA };
    
    public BouncyCastlePSKTlsServer(BouncyCastleTlsPSKIdentityManager pskIdentityManager, AsymmetricKeyParameter serverPrivateKey, Certificate serverCertificateChain) {
        super(pskIdentityManager);
        this.serverPrivateKey = serverPrivateKey;
        this.serverCertificate = serverCertificateChain;
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

    @Override
    protected TlsEncryptionCredentials getRSAEncryptionCredentials() throws IOException {
        return new DefaultTlsEncryptionCredentials(context, serverCertificate, serverPrivateKey);
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

    public byte[] addPSKCredential(String ident, byte[] key) {
        return ((BouncyCastleTlsPSKIdentityManager)pskIdentityManager).addPSKCredential(ident, key);
    }
    
}
