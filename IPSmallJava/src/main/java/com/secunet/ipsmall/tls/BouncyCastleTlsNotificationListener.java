package com.secunet.ipsmall.tls;

import org.bouncycastle.crypto.params.DHParameters;

import org.bouncycastle.crypto.tls.Certificate;
import org.bouncycastle.crypto.tls.ProtocolVersion;
import org.bouncycastle.crypto.tls.SignatureAndHashAlgorithm;

public interface BouncyCastleTlsNotificationListener {
    
    public boolean hasFatalErrors();

    public void notifyAlertRaised(final short alertLevel, final short alertDescription, final String message, final Throwable cause);

    public void notifyAlertReceived(final short alertLevel, final short alertDescription);
    
    public void notifyClientVersion(final ProtocolVersion clientVersion);

    public void notifyFallback(final boolean isFallback);

    public void notifyOfferedCipherSuites(final int[] offeredCipherSuites);

    public void notifyOfferedCompressionMethods(final short[] offeredCompressionMethods);

    public void notifyClientCertificate(final Certificate clientCertificate);

    public void notifySecureRenegotiation(final boolean secureRenegotiation);

    public void notifyHandshakeComplete();

    public void notifyEncryptThenMACExtension(final boolean hasEncryptThenMACExtension);
    
    public void notifySignatureAlgorithmsExtension(final SignatureAndHashAlgorithm[] signatureAlgorithms);

    public void notifySupportedEllipticCurvesExtension(final int[] namedCurves);

    public void notifySupportedPointFormatsExtension(final short[] supportedECPointFormats);

    public void notifySelectedVersion(final ProtocolVersion clientVersion);

    public void notifySelectedCipherSuite(final int cipherSuite);

    public void notifyEnabledCipherSuites(final int[] enabledCipherSuites);

    public void notifyEnabledMinimumVersion(final ProtocolVersion minimumVersion);

    public void notifyEnabledMaximumVersion(final ProtocolVersion maximumVersion);
    
    public void notifySelectedDHParameters(final DHParameters dhParameters);
    
}