package com.secunet.ipsmall;

import org.bouncycastle.crypto.tls.AlertDescription;
import org.bouncycastle.crypto.tls.AlertLevel;
import org.bouncycastle.crypto.tls.Certificate;
import org.bouncycastle.crypto.tls.ProtocolVersion;
import org.bouncycastle.crypto.tls.SignatureAndHashAlgorithm;

import com.secunet.ipsmall.http.NanoHTTPD;
import com.secunet.ipsmall.http.NanoHTTPD.Response.Status;
import com.secunet.ipsmall.log.IModuleLogger;
import com.secunet.ipsmall.log.IModuleLogger.LogLevel;
import com.secunet.ipsmall.log.Logger;
import com.secunet.ipsmall.test.ITestData;
import com.secunet.ipsmall.test.ITestProtocolCallback.SourceComponent;
import com.secunet.ipsmall.test.ITestProtocolCallback.TestStep;
import com.secunet.ipsmall.test.ITestSession;
import com.secunet.ipsmall.tls.BouncyCastleNanoHTTPDSocketFactory;
import com.secunet.ipsmall.tls.BouncyCastleTlsIcsMatcher;
import com.secunet.ipsmall.tls.BouncyCastleTlsNotificationListener;
import com.secunet.ipsmall.tobuilder.ics.TLSVersionType;
import com.secunet.testbedutils.utilities.BouncyCastleTlsHelper;
import com.secunet.testbedutils.utilities.CommonUtil;
import com.secunet.ipsmall.util.BouncyCastleTlsUtils;
import com.secunet.ipsmall.util.HttpUtils;

import org.bouncycastle.crypto.params.DHParameters;

public class Redirector extends NanoHTTPD implements BouncyCastleTlsNotificationListener {
    
    private boolean hasFatalErrors = false;
    
    private BouncyCastleTlsIcsMatcher matcher;
    
    /**
     * Type of redirector
     */
    public static enum Type {
        TCToken,
        RefreshAddress,
        Other
    }
    
    Type type;
    String[] params;
    
    public Redirector(ITestData testData, Type type, String[] params) {
        super(CommonUtil.getSubstringBefore(params[0], ":", true), Integer.parseInt(CommonUtil.getSubstringAfter(
                CommonUtil.getSubstringBefore(params[0], "/", false), ":", true)));
        
        this.logger = Logger.Redirector;
        this.testData = testData;
        this.type = type;
        this.params = params;
        
        matcher = new BouncyCastleTlsIcsMatcher(IPSmallManager.getInstance().getIcs());
        
        try {            
            BouncyCastleNanoHTTPDSocketFactory factory = new BouncyCastleNanoHTTPDSocketFactory(this, testData.readCertificate(params[2]), testData.readPrivateKey(params[3]));
                if(testData.getEServiceTLSdhParameters() != null && !testData.getEServiceTLSdhParameters().isEmpty()) {
                    factory.setDHParameters(testData.getEServiceTLSdhParameters());
                }
                if(testData.getEServiceTLSecCurve() != null && !testData.getEServiceTLSecCurve().isEmpty()) {
                    factory.setForcedCurveForECDHEKeyExchange(testData.getEServiceTLSecCurve());
                }
                if(testData.getEServiceTLSSignatureAlgorithm() != null && !testData.getEServiceTLSSignatureAlgorithm().isEmpty()) {
                    factory.setForcedSignatureAlgorithm(testData.getEServiceTLSSignatureAlgorithm());
                }
                externalServerSocketFactory = factory;
        } catch (Exception e) {
            logger.logState("Error creating BC socket factory: " + e.getMessage(), LogLevel.Error);
        }
    }
    
    @Override
    public Response serve(HTTPSession httpReq) {
        // if the request contained parameters, append them to the log
        String optionalParams = "";
        if (httpReq.getParms().size() > 0) {
            optionalParams = " Parameters:";
            for (String param : httpReq.getParms().keySet()) {
                if (!param.equals(NanoHTTPD.QUERY_STRING_PARAMETER)) {
                    optionalParams += (" " + param + "=" + httpReq.getParms().get(param));
                }
            }
        }
        String uri = httpReq.getUri();
        logger.logState("Redirector: serving HTTPSession URI: " + uri + optionalParams);
        // TODO this is redundant. safety net for NanoHTTPD parsing errors until the logging is rewritten. the entire header will be logged in future releases
        logger.logState("Used the request string: " + httpReq.getRawHeader().substring(0, httpReq.getRawHeader().indexOf(System.getProperty("line.separator"))));
        Response resp_message = null;
        Status status = null;
        try {
            SourceComponent sourceComponent = HttpUtils.getSourceFromHttpHeaders(httpReq.getHeaders());
            
            boolean haveToRedirect = false;
            
            Integer redirectorNumber = testData.getEServiceRedirectorTCTokenNumber();
            
            if ((Type.TCToken.equals(type)) && (redirectorNumber != null) && (uri.equals("/" + testData.getRedirectorTCTokenURL(redirectorNumber)))) {
                // "Redirector" can also respond with TCToken (e.g. in A2_06)
                ITestSession session = testData.getNewSession();
                logger.logState("Check for Redirector TC Token URL: passed\t " + testData.getRedirectorTCTokenURL(redirectorNumber));
                logger.logState("Client fetched Token: session - " + session.getSessionID());
                String tcToken = session.getTCToken();
                resp_message = new Response(tcToken, c_SOAP_MIME_TYPE, testData.chunkedTransfer());
                testData.sendMessageToCallbacks(TestStep.TC_TOKEN, tcToken, sourceComponent, this);
            } else if ((Type.RefreshAddress.equals(type)) && (uri.equals("/" + CommonUtil.getSubstringAfter(params[0], "/", false)))) {
                // Redirection
                testData.sendMessageToCallbacks(TestStep.REFRESH_ADDRESS_REDIRECT, params[1], sourceComponent, this);
                haveToRedirect = true;
            }
            
            if (haveToRedirect) {
                int statusCode = Integer.parseInt(params[4]);
                switch (statusCode) {
                    case 301:
                        status = Status.REDIRECT; // Moved Permanently
                        break;
                    case 302:
                        status = Status.FOUND;
                        break;
                    case 303:
                        status = Status.SEE_OTHER;
                        break;
                    // case 304:
                    // status = Status.NOT_MODIFIED;
                    // // No "Location" header
                    // break;
                    case 307:
                        status = Status.TEMPORARY_REDIRECT;
                        break;
                    default:
                        status = Status.NOT_FOUND;
                        break;
                }
                String locationInfo = "";
                String targetInfo = "";
                if (!Status.NOT_FOUND.equals(status)) {
                    resp_message = new Response(status, status.getDescription(), testData.chunkedTransfer());
                    String location = "https://" + params[1];
                    if (testData.getEServiceRedirectLocation() != null) {
                        location = testData.getEServiceRedirectLocation();
                        locationInfo = " (Location changed to '" + location + "')";
                    }
                    targetInfo = "to '" + location + "' ";
                    
                    resp_message.addHeader("Location", location);
                }
                logger.logState("Redirector " + targetInfo + "with StatusCode: " + statusCode + " " + status + locationInfo);
            }
            
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        if (resp_message == null) {
            resp_message = new Response(Status.NOT_FOUND, Status.NOT_FOUND.getDescription(), testData.chunkedTransfer());
        }
        
        return resp_message;
    }
    
    public Type getType() {
        return type;
    }
    
    public void setType(Type type) {
        this.type = type;
    }
    
    public String[] getParams() {
        return params;
    }
    
    public void setParams(String[] params) {
        this.params = params;
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

        if(!testData.getSkipNextICSCheck()) {
            ProtocolVersion expectedProtocolVersion = BouncyCastleTlsUtils.convertProtocolVersionFromEnumToObject(testData.getEServiceTLSExpectedClientVersion());
            if( expectedProtocolVersion.equals(clientVersion) ) {
                logger.logConformity(IModuleLogger.ConformityResult.passed, "Check that client offered " + testData.getEServiceTLSExpectedClientVersion() + " passed.");
            }
            else {
                hasFatalErrors = true;
                logger.logConformity(IModuleLogger.ConformityResult.failed, "Check that client offered " + testData.getEServiceTLSExpectedClientVersion() + " failed.");
            }
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

        if(!testData.getSkipNextICSCheck()) {
            TLSVersionType expectedProtocolVersion = TLSVersionType.fromValue(testData.getEServiceTLSExpectedClientVersion());
            if( matcher.matchCipherSuites(true, expectedProtocolVersion, offeredCipherSuites) ) {
                logger.logConformity(IModuleLogger.ConformityResult.passed, "Check cipher suites against ICS passed.");
            }
            else {
                hasFatalErrors = true;
                logger.logConformity(IModuleLogger.ConformityResult.failed, "Check cipher suites against ICS failed.");
            }
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

        if(!testData.getSkipNextICSCheck()) {
            TLSVersionType expectedProtocolVersion = TLSVersionType.fromValue(testData.getEServiceTLSExpectedClientVersion());
            if( matcher.matchSignatureAndHashAlgorithms(true, expectedProtocolVersion, signatureAlgorithms) ) {
                logger.logConformity(IModuleLogger.ConformityResult.passed, "Check SignatureAlgorithms extension against ICS passed.");
            }
            else {
                hasFatalErrors = true;
                logger.logConformity(IModuleLogger.ConformityResult.failed, "Check SignatureAlgorithms extension against ICS failed.");
            }
        }
    }

    @Override
    public void notifySupportedEllipticCurvesExtension(int[] namedCurves) {
        String curves = "";
        for (int entry : namedCurves) {
            curves += " " + BouncyCastleTlsHelper.convertNamedCurveIntToString(entry);
        }
        logger.logState("TLS client sent SupportedEllipticCurves extension:" + curves);

        if(!testData.getSkipNextICSCheck()) {
            TLSVersionType expectedProtocolVersion = TLSVersionType.fromValue(testData.getEServiceTLSExpectedClientVersion());
            if( matcher.matchEllipticCurves(true, expectedProtocolVersion, namedCurves) ) {
                logger.logConformity(IModuleLogger.ConformityResult.passed, "Check SupportedEllipticCurves extension against ICS passed.");
            }
            else {
                hasFatalErrors = true;
                logger.logConformity(IModuleLogger.ConformityResult.failed, "Check SupportedEllipticCurves extension against ICS failed.");
            }
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
