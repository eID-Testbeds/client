package com.secunet.ipsmall.tls;

import org.bouncycastle.crypto.params.DHParameters;

import org.bouncycastle.crypto.tls.Certificate;
import org.bouncycastle.crypto.tls.ProtocolVersion;
import org.bouncycastle.crypto.tls.SignatureAndHashAlgorithm;

public interface BouncyCastleTlsNotificationProducer {
    
    public boolean addNotificationListener(BouncyCastleTlsNotificationListener listener);
    
    public boolean removeNotificationListener(BouncyCastleTlsNotificationListener listener);

    public void sendNotificationAlertRaised(final short alertLevel, final short alertDescription, final String message, final Throwable cause);

    public void sendNotificationAlertReceived(final short alertLevel, final short alertDescription);
    
    public void sendNotificationClientVersion(final ProtocolVersion clientVersion);

    public void sendNotificationFallback(final boolean isFallback);

    public void sendNotificationOfferedCipherSuites(final int[] offeredCipherSuites);

    public void sendNotificationOfferedCompressionMethods(final short[] offeredCompressionMethods);

    public void sendNotificationClientCertificate(final Certificate clientCertificate);

    public void sendNotificationSecureRenegotiation(final boolean secureRenegotiation);

    public void sendNotificationHandshakeComplete();

    public void sendNotificationEncryptThenMACExtension(final boolean hasEncryptThenMACExtension);
    
    public void sendNotificationSignatureAlgorithmsExtension(final SignatureAndHashAlgorithm[] signatureAlgorithms);

    public void sendNotificationSupportedEllipticCurvesExtension(final int[] namedCurves);

    public void sendNotificationSupportedPointFormatsExtension(final short[] supportedECPointFormats);

    public void sendNotificationSelectedVersion(final ProtocolVersion clientVersion);

    public void sendNotificationSelectedCipherSuite(final int cipherSuite);

    public void sendNotificationEnabledCipherSuites(final int[] enabledCipherSuites);

    public void sendNotificationEnabledMinimumVersion(final ProtocolVersion minimumVersion);

    public void sendNotificationEnabledMaximumVersion(final ProtocolVersion maximumVersion);

    public void sendNotificationSelectedDHParameters(final DHParameters dhParameters);

}