package com.secunet.ipsmall.tls;

import java.io.IOException;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.Vector;

import org.bouncycastle.crypto.params.DHParameters;

import org.bouncycastle.crypto.tls.Certificate;
import org.bouncycastle.crypto.tls.PSKTlsServer;
import org.bouncycastle.crypto.tls.ProtocolVersion;
import org.bouncycastle.crypto.tls.SignatureAndHashAlgorithm;
import org.bouncycastle.crypto.tls.TlsECCUtils;
import org.bouncycastle.crypto.tls.TlsExtensionsUtils;
import org.bouncycastle.crypto.tls.TlsPSKIdentityManager;
import org.bouncycastle.crypto.tls.TlsUtils;

/**
 * This class maps BouncyCastle TLS events from class DefaultTlsServer to an external notification listener interface.
 * @author schiel.patrick
 */
public class BouncyCastleNotifyingPSKTlsServer extends PSKTlsServer implements BouncyCastleTlsNotificationProducer {
    
    public BouncyCastleNotifyingPSKTlsServer(TlsPSKIdentityManager pskIdentityManager) {
        super(pskIdentityManager);
    }

    private LinkedList<BouncyCastleTlsNotificationListener> listeners = new LinkedList<BouncyCastleTlsNotificationListener>();
    
    @Override
    public void notifyAlertRaised(short alertLevel, short alertDescription, String message, Throwable cause)
    {
        sendNotificationAlertRaised(alertLevel, alertDescription, message, cause);
        super.notifyAlertRaised(alertLevel, alertDescription, message, cause);
    }

    @Override
    public void notifyAlertReceived(short alertLevel, short alertDescription) {
        sendNotificationAlertReceived(alertLevel, alertDescription);
        super.notifyAlertReceived(alertLevel, alertDescription);
    }
    
    @Override
    public void notifyClientVersion(ProtocolVersion clientVersion) throws IOException {
        sendNotificationClientVersion(clientVersion);
        super.notifyClientVersion(clientVersion);
    }

    @Override
    public void notifyFallback(boolean isFallback) throws IOException {
        sendNotificationFallback(isFallback);
        super.notifyFallback(isFallback);
    }

    @Override
    public void notifyOfferedCipherSuites(int[] offeredCipherSuites) throws IOException {
        sendNotificationOfferedCipherSuites(offeredCipherSuites);
        super.notifyOfferedCipherSuites(offeredCipherSuites);
    }

    @Override
    public void notifyOfferedCompressionMethods(short[] offeredCompressionMethods) throws IOException {
        sendNotificationOfferedCompressionMethods(offeredCompressionMethods);
        super.notifyOfferedCompressionMethods(offeredCompressionMethods);
    }

    @Override
    public void notifyClientCertificate(Certificate clientCertificate) throws IOException {
        sendNotificationClientCertificate(clientCertificate);
        super.notifyClientCertificate(clientCertificate);
    }

    @Override
    public void notifySecureRenegotiation(boolean secureRenegotiation) throws IOException {
        sendNotificationSecureRenegotiation(secureRenegotiation);
        super.notifySecureRenegotiation(secureRenegotiation);
    }

    @Override
    public void notifyHandshakeComplete() throws IOException {
        sendNotificationHandshakeComplete();
        super.notifyHandshakeComplete();
    }

    @SuppressWarnings("rawtypes")
    @Override
    public void processClientExtensions(Hashtable clientExtensions) throws IOException {
        if (clientExtensions != null)
        {
            sendNotificationEncryptThenMACExtension(TlsExtensionsUtils.hasEncryptThenMACExtension(clientExtensions));

            Vector clientSupportedSignatureAlgorithms = TlsUtils.getSignatureAlgorithmsExtension(clientExtensions);
            if (clientSupportedSignatureAlgorithms != null) {
                SignatureAndHashAlgorithm[] signatureAlgorithms = new SignatureAndHashAlgorithm[clientSupportedSignatureAlgorithms.size()];
                int i = 0;
                for (Object entry : clientSupportedSignatureAlgorithms) {
                    signatureAlgorithms[i++] = (SignatureAndHashAlgorithm) entry;
                }
                sendNotificationSignatureAlgorithmsExtension(signatureAlgorithms);
            }

            int[] clientNamedCurves = TlsECCUtils.getSupportedEllipticCurvesExtension(clientExtensions);
            if (clientNamedCurves != null) {
                sendNotificationSupportedEllipticCurvesExtension(clientNamedCurves);
            }

            short[] clientSupportedECPointFormats = TlsECCUtils.getSupportedPointFormatsExtension(clientExtensions);
            if (clientSupportedECPointFormats != null) {
                sendNotificationSupportedPointFormatsExtension(clientSupportedECPointFormats);
            }
        }
        super.processClientExtensions(clientExtensions);
    }

    @Override
    public ProtocolVersion getServerVersion() throws IOException {
        ProtocolVersion version = super.getServerVersion();
        sendNotificationSelectedVersion(version);
        return version;
    }

    @Override
    public int getSelectedCipherSuite() throws IOException {
        int cipherSuite = super.getSelectedCipherSuite();
        sendNotificationSelectedCipherSuite(cipherSuite);
        return cipherSuite;
    }

    // BEGIN implementation of BouncyCastleTlsNotificationProducer
    @Override
    public boolean addNotificationListener(BouncyCastleTlsNotificationListener listener) {
        return listeners.add(listener);
    }

    @Override
    public boolean removeNotificationListener(BouncyCastleTlsNotificationListener listener) {
        return listeners.remove(listener);
    }

    @Override
    public void sendNotificationAlertRaised(short alertLevel, short alertDescription, String message, Throwable cause) {
        for(BouncyCastleTlsNotificationListener listener : listeners) {
            listener.notifyAlertRaised(alertLevel, alertDescription, message, cause);
        }
    }

    @Override
    public void sendNotificationAlertReceived(short alertLevel, short alertDescription) {
        for(BouncyCastleTlsNotificationListener listener : listeners) {
            listener.notifyAlertReceived(alertLevel, alertDescription);
        }
    }

    @Override
    public void sendNotificationClientVersion(ProtocolVersion clientVersion) {
        for(BouncyCastleTlsNotificationListener listener : listeners) {
            listener.notifyClientVersion(clientVersion);
        }
    }

    @Override
    public void sendNotificationFallback(boolean isFallback) {
        for(BouncyCastleTlsNotificationListener listener : listeners) {
            listener.notifyFallback(isFallback);
        }
    }

    @Override
    public void sendNotificationOfferedCipherSuites(int[] offeredCipherSuites) {
        for(BouncyCastleTlsNotificationListener listener : listeners) {
            listener.notifyOfferedCipherSuites(offeredCipherSuites);
        }
    }

    @Override
    public void sendNotificationOfferedCompressionMethods(short[] offeredCompressionMethods) {
        for(BouncyCastleTlsNotificationListener listener : listeners) {
            listener.notifyOfferedCompressionMethods(offeredCompressionMethods);
        }
    }

    @Override
    public void sendNotificationClientCertificate(Certificate clientCertificate) {
        for(BouncyCastleTlsNotificationListener listener : listeners) {
            listener.notifyClientCertificate(clientCertificate);
        }
    }

    @Override
    public void sendNotificationSecureRenegotiation(boolean secureRenegotiation) {
        for(BouncyCastleTlsNotificationListener listener : listeners) {
            listener.notifySecureRenegotiation(secureRenegotiation);
        }
    }

    @Override
    public void sendNotificationHandshakeComplete() {
        for(BouncyCastleTlsNotificationListener listener : listeners) {
            listener.notifyHandshakeComplete();
        }
    }

    @Override
    public void sendNotificationEncryptThenMACExtension(boolean hasEncryptThenMACExtension) {
        for(BouncyCastleTlsNotificationListener listener : listeners) {
            listener.notifyEncryptThenMACExtension(hasEncryptThenMACExtension);
        }
    }

    @Override
    public void sendNotificationSignatureAlgorithmsExtension(SignatureAndHashAlgorithm[] signatureAlgorithms) {
        for(BouncyCastleTlsNotificationListener listener : listeners) {
            listener.notifySignatureAlgorithmsExtension(signatureAlgorithms);
        }
    }

    @Override
    public void sendNotificationSupportedEllipticCurvesExtension(int[] namedCurves) {
        for(BouncyCastleTlsNotificationListener listener : listeners) {
            listener.notifySupportedEllipticCurvesExtension(namedCurves);
        }
    }

    @Override
    public void sendNotificationSupportedPointFormatsExtension(short[] supportedECPointFormats) {
        for(BouncyCastleTlsNotificationListener listener : listeners) {
            listener.notifySupportedPointFormatsExtension(supportedECPointFormats);
        }
    }

    @Override
    public void sendNotificationSelectedVersion(ProtocolVersion clientVersion) {
        for(BouncyCastleTlsNotificationListener listener : listeners) {
            listener.notifySelectedVersion(clientVersion);
        }
    }

    @Override
    public void sendNotificationSelectedCipherSuite(int cipherSuite) {
        for(BouncyCastleTlsNotificationListener listener : listeners) {
            listener.notifySelectedCipherSuite(cipherSuite);
        }
    }

    @Override
    public void sendNotificationEnabledCipherSuites(int[] enabledCipherSuites) {
        for(BouncyCastleTlsNotificationListener listener : listeners) {
            listener.notifyEnabledCipherSuites(enabledCipherSuites);
        }
    }

    @Override
    public void sendNotificationEnabledMinimumVersion(ProtocolVersion minimumVersion) {
        for(BouncyCastleTlsNotificationListener listener : listeners) {
            listener.notifyEnabledMinimumVersion(minimumVersion);
        }
    }

    @Override
    public void sendNotificationEnabledMaximumVersion(ProtocolVersion maximumVersion) {
        for(BouncyCastleTlsNotificationListener listener : listeners) {
            listener.notifyEnabledMaximumVersion(maximumVersion);
        }
    }

    @Override
    public void sendNotificationSelectedDHParameters(final DHParameters dhParameters) {
        for(BouncyCastleTlsNotificationListener listener : listeners) {
            listener.notifySelectedDHParameters(dhParameters);
        }
    }
    // END implementation of BouncyCastleTlsNotificationProducer

}
