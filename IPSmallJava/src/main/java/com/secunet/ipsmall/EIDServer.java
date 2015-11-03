package com.secunet.ipsmall;

import java.io.UnsupportedEncodingException;
import org.bouncycastle.crypto.params.DHParameters;

import com.secunet.bouncycastle.crypto.tls.AlertDescription;
import com.secunet.bouncycastle.crypto.tls.AlertLevel;
import com.secunet.bouncycastle.crypto.tls.Certificate;
import com.secunet.bouncycastle.crypto.tls.ProtocolVersion;
import com.secunet.bouncycastle.crypto.tls.SignatureAndHashAlgorithm;
import com.secunet.ipsmall.http.NanoHTTPD;
import com.secunet.ipsmall.log.IModuleLogger.ConformityResult;
import com.secunet.ipsmall.log.Logger;
import com.secunet.ipsmall.test.IPublishPSK;
import com.secunet.ipsmall.test.ITestData;
import com.secunet.ipsmall.tls.BouncyCastleNanoHTTPDSocketFactory;
import com.secunet.ipsmall.tls.BouncyCastleServerSocket;
import com.secunet.ipsmall.tls.BouncyCastleTlsHelper;
import com.secunet.ipsmall.tls.BouncyCastleTlsIcsMatcher;
import com.secunet.ipsmall.tls.BouncyCastleTlsNotificationListener;
import com.secunet.ipsmall.tobuilder.ics.TLSVersionType;

public class EIDServer extends NanoHTTPD implements IPublishPSK, BouncyCastleTlsNotificationListener {
    
    private final EIDServerWebServer eIDServer;

    private BouncyCastleTlsIcsMatcher matcher;
    
    private boolean hasFatalErrors = false;
    
    public EIDServer(ITestData testData) {
        super(testData.getEIDServiceHost(), testData.getEIDServicePort(), null, testData.getEIDServiceTLSVersion(), testData.getEIDServiceTLSCipherSuites());
        
        this.logger = Logger.eIDServer;
        this.testData = testData;
        
        eIDServer = new EIDServerWebServer(testData, false, this);

        matcher = new BouncyCastleTlsIcsMatcher(IPSmallManager.getInstance().getIcs());

        BouncyCastleNanoHTTPDSocketFactory factory = new BouncyCastleNanoHTTPDSocketFactory(this, testData.getEIDServiceCertificate(), testData.getEIDServerPrivateKey());
        externalServerSocketFactory = factory;
        
        // If testcase defines to usepsk or useecdh, this is enabled
        // here for the socket object (which effectively preselects a
        // static cipherSuite).
        // This is a default for many usecases.
        // Note that cipherSuites can also be set explicitly via eidservice.tls.ciphersuites.
        // If this is defined, the default psd/ecdh-ciphersuites set here will
        // later be overridden with cipherSuites from eidservice.tls.ciphersuites (when
        // the server is started).
        if (testData.useEIDServiceTLSPSK()) {
            factory.enablePSK();
        }
    }
    
    @Override
    public void addPSK(String ident, byte[] key) throws UnsupportedEncodingException, IllegalArgumentException {
        if (myServerSocket instanceof BouncyCastleServerSocket) {
            ((BouncyCastleServerSocket) myServerSocket).addPSKCredential(ident, key);
        }
    }
    
    @Override
    public Response serve(HTTPSession httpReq) {
        return eIDServer.serve(httpReq, logger);
    }

    // BEGIN implementation of BouncyCastleTlsNotificationListener
    @Override
    public void notifyAlertRaised(short alertLevel, short alertDescription, String message, Throwable cause)
    {
        String logMessage = "TLS server raised alert: " + AlertLevel.getText(alertLevel) + ", " + AlertDescription.getText(alertDescription);
        if (message != null)
        {
            logMessage += " > " + message;
        }
        if (cause != null)
        {
            logMessage += " " + cause.toString();
        }
        logger.logState(logMessage);
        
        //TODO hack to suppress error TLS messages
        if(alertLevel == AlertLevel.fatal && alertDescription == AlertDescription.internal_error && "Failed to read record".equals(message) && cause instanceof java.io.EOFException) {
            doSuppressTLSErrors = true;
        }

        //close HTTP connections if TLS channel is closed
        if(alertLevel == AlertLevel.warning && alertDescription == AlertDescription.close_notify) {
            closeAllConnections();
        }
    }

    @Override
    public void notifyAlertReceived(short alertLevel, short alertDescription) {
        logger.logState("TLS server received alert: " + AlertLevel.getText(alertLevel) + ", "
                + AlertDescription.getText(alertDescription));
    }
    
    @Override
    public void notifyClientVersion(ProtocolVersion clientVersion) {
        logger.logState("TLS client offered version: " + clientVersion.toString());
        ProtocolVersion expectedProtocolVersion = BouncyCastleTlsHelper.convertProtocolVersionFromEnumToObject(testData.getEIDServiceTLSExpectedClientVersion());
        if( expectedProtocolVersion.equals(clientVersion) ) {
            logger.logConformity(ConformityResult.passed, "Check that client offered " + testData.getEIDServiceTLSExpectedClientVersion() + " passed.");
        }
        else {
            hasFatalErrors = true;
            logger.logConformity(ConformityResult.failed, "Check that client offered " + testData.getEIDServiceTLSExpectedClientVersion() + " failed.");
        }
    }

    @Override
    public void notifyFallback(boolean isFallback) {
        logger.logState("notifyFallback: " + isFallback);
    }

    @Override
    public void notifyOfferedCipherSuites(int[] offeredCipherSuites) {
        String cipherSuites = "";
        for(int cipherSuite : offeredCipherSuites) {
            cipherSuites += " " + BouncyCastleTlsHelper.convertCipherSuiteIntToString(cipherSuite);
        }
        logger.logState("TLS client offered cipher suites:" + cipherSuites);

        TLSVersionType expectedProtocolVersion = TLSVersionType.fromValue(testData.getEIDServiceTLSExpectedClientVersion());
        if( matcher.matchCipherSuites(false, expectedProtocolVersion, offeredCipherSuites) ) {
            logger.logConformity(ConformityResult.passed, "Check cipher suites against ICS passed.");
        }
        else {
            hasFatalErrors = true;
            logger.logConformity(ConformityResult.failed, "Check cipher suites against ICS failed.");
        }
    }

    @Override
    public void notifyOfferedCompressionMethods(short[] offeredCompressionMethods) {
        String methods = "";
        for(short method : offeredCompressionMethods) {
            methods += " " + method;
        }
        logger.logState("TLS client offered compression methods: [" + methods + " ]");
    }

    @Override
    public void notifyClientCertificate(Certificate clientCertificate) {
        logger.logState("notifyClientCertificate: " + clientCertificate);
    }

    @Override
    public void notifySecureRenegotiation(boolean secureRenegotiation) {
        logger.logState("notifySecureRenegotiation: " + secureRenegotiation);
    }

    @Override
    public void notifyHandshakeComplete() {
        logger.logState("TLS handshake complete!");
    }

    @Override
    public void notifyEncryptThenMACExtension(boolean hasEncryptThenMACExtension) {
        logger.logState("TLS client sent EncryptThenMAC extension: " + hasEncryptThenMACExtension);
    }

    @Override
    public void notifySignatureAlgorithmsExtension(SignatureAndHashAlgorithm[] signatureAlgorithms) {
        String algorithms = "";
        for (Object entry : signatureAlgorithms) {
            SignatureAndHashAlgorithm saha = (SignatureAndHashAlgorithm) entry;
            algorithms += " " + BouncyCastleTlsHelper.convertSignatureAndHashAlgorithmObjectToString(saha);
        }
        logger.logState("TLS client sent SignatureAlgorithms extension:" + algorithms);

        TLSVersionType expectedProtocolVersion = TLSVersionType.fromValue(testData.getEIDServiceTLSExpectedClientVersion());
        if( matcher.matchSignatureAndHashAlgorithms(false, expectedProtocolVersion, signatureAlgorithms) ) {
            logger.logConformity(ConformityResult.passed, "Check SignatureAlgorithms extension against ICS passed.");
        }
        else {
            hasFatalErrors = true;
            logger.logConformity(ConformityResult.failed, "Check SignatureAlgorithms extension against ICS failed.");
        }
    }

    @Override
    public void notifySupportedEllipticCurvesExtension(int[] namedCurves) {
        String curves = "";
        for (int entry : namedCurves) {
            curves += " " + BouncyCastleTlsHelper.convertNamedCurveIntToString(entry);
        }
        logger.logState("TLS client sent SupportedEllipticCurves extension:" + curves);

        TLSVersionType expectedProtocolVersion = TLSVersionType.fromValue(testData.getEIDServiceTLSExpectedClientVersion());
        if( matcher.matchEllipticCurves(false, expectedProtocolVersion, namedCurves) ) {
            logger.logConformity(ConformityResult.passed, "Check SupportedEllipticCurves extension against ICS passed.");
        }
        else {
            hasFatalErrors = true;
           logger.logConformity(ConformityResult.failed, "Check SupportedEllipticCurves extension against ICS failed.");
        }
    }

    @Override
    public void notifySupportedPointFormatsExtension(short[] supportedECPointFormats) {
        String formats = "";
        for (short format : supportedECPointFormats) {
            formats += " " + BouncyCastleTlsHelper.convertECPointFormatShortToString(format);
        }
        logger.logState("TLS client sent SupportedPointFormats extension:" + formats);
    }

    @Override
    public void notifySelectedVersion(ProtocolVersion clientVersion) {
        logger.logState("TLS server accepted version: " + clientVersion.toString());
    }

    @Override
    public void notifySelectedCipherSuite(int cipherSuite) {
        logger.logState("TLS server accepted cipher suite: " + BouncyCastleTlsHelper.convertCipherSuiteIntToString(cipherSuite));
    }
    
    @Override
    public void notifyEnabledCipherSuites(int[] enabledCipherSuites) {
        String cipherSuites = "";
        for(int cipherSuite : enabledCipherSuites) {
            cipherSuites += " " + BouncyCastleTlsHelper.convertCipherSuiteIntToString(cipherSuite);
        }
        logger.logState("TLS server enabled cipher suites:" + cipherSuites);
    }

    @Override
    public void notifyEnabledMinimumVersion(ProtocolVersion minimumVersion) {
        logger.logState("TLS server offers minimum version: " + minimumVersion.toString());
    }

    @Override
    public void notifyEnabledMaximumVersion(ProtocolVersion maximumVersion) {
        logger.logState("TLS server offers maximum version: " + maximumVersion.toString());
    }
    
    @Override
    public void notifySelectedDHParameters(DHParameters dhParameters) {
        logger.logState("TLS server selected DH parameters: " + BouncyCastleTlsHelper.convertDHParametersObjectToDHStandardGroupsString(dhParameters));
    }
    
    @Override
    public boolean hasFatalErrors() {
        return hasFatalErrors;
    }
    // END implementation of BouncyCastleTlsNotificationListener

}
