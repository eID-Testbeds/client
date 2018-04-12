package com.secunet.ipsmall;

import org.bouncycastle.crypto.params.DHParameters;

import com.secunet.bouncycastle.crypto.tls.AlertDescription;
import com.secunet.bouncycastle.crypto.tls.AlertLevel;
import com.secunet.bouncycastle.crypto.tls.Certificate;
import com.secunet.bouncycastle.crypto.tls.ProtocolVersion;
import com.secunet.bouncycastle.crypto.tls.SignatureAndHashAlgorithm;
import com.secunet.ipsmall.http.NanoHTTPD;
import com.secunet.ipsmall.http.NanoHTTPD.Response.Status;
import com.secunet.ipsmall.log.IModuleLogger;
import com.secunet.ipsmall.log.Logger;
import com.secunet.ipsmall.test.ITestData;
import com.secunet.ipsmall.test.ITestData.Type;
import com.secunet.ipsmall.test.ITestProtocolCallback.SourceComponent;
import com.secunet.ipsmall.test.ITestProtocolCallback.TestStep;
import com.secunet.ipsmall.tls.BouncyCastleNanoHTTPDSocketFactory;
import com.secunet.ipsmall.tls.BouncyCastleTlsNotificationListener;
import com.secunet.ipsmall.util.HttpUtils;
import com.secunet.testbedutils.utilities.BouncyCastleTlsHelper;

/**
 * Server handling communication error address request (which was part of {@link EService} before).
 * This needs to handle HTTPS (in most use-cases), but also HTTP.
 * 
 * @author kersten.benjamin
 *
 */
public class CommErrorAddressServer extends NanoHTTPD implements BouncyCastleTlsNotificationListener {
    
        private boolean hasFatalErrors = false;

	public CommErrorAddressServer(ITestData testData) {
		
        super(testData.getCommErrorAddressServerHost(), testData.getCommErrorAddressServerPort(), null, testData.getCommErrorAddressServerTLSVersion(), testData.getCommErrorAddressServerTLSCipherSuites());
        
        this.logger = Logger.CommErrorAddressServer;
        this.testData = testData;
                      
        // http
        // when socket factory is null, NanoHTTPD will be started with default http socket
        externalServerSocketFactory = null;
        if (testData.getCommErrorAddressServerCertificate() != null && testData.getCommErrorAddressServerCertificate().getLength() > 0) {
            try {
                //externalServerSocketFactory = new Java7NanoHTTPSocketFactory(testData.getCommErrorAddressServerCertificate(), testData.getCommErrorAddressServerPrivateKey(), false);
                BouncyCastleNanoHTTPDSocketFactory factory = new BouncyCastleNanoHTTPDSocketFactory(this, testData.getCommErrorAddressServerCertificate(), testData.getCommErrorAddressServerPrivateKey());
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
                logger.logState("Error creating BC socket factory: " + e.getMessage(), IModuleLogger.LogLevel.Error);
            }
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
        logger.logState("CommErrorAddressServer: serving HTTPSession URI: " + hostname + ":" + myPort + uri + optionalParams);
        // TODO this is redundant. safety net for NanoHTTPD parsing errors until the logging is rewritten. the entire header will be logged in future releases
        logger.logState("Used the request string: " + httpReq.getRawHeader().substring(0, httpReq.getRawHeader().indexOf(System.getProperty("line.separator"))));
        Response resp_message = null;
        try {
            
            SourceComponent sourceComponent = HttpUtils.getSourceFromHttpHeaders(httpReq.getHeaders());
            
            if (uri.equals("/" + testData.getCommErrorAddressServerCommunicationErrorPageURL())) {
                logger.logState("CommErrorAddressServer communication error page called");
                resp_message = new Response(testData.getCommErrorAddressServerCommunicationErrorPage(), testData.chunkedTransfer());

                // If no browsersimulator is used, the final step BROWSER_REDIRECT can not be detected via browsersimulator.
                // Instead, the resulting request from the BROWSER to the COMMUNICATION_ERROR_ADDRESS must be handled.
                if(testData.getTestType() != null && testData.getTestType() == Type.BROWSER && sourceComponent == SourceComponent.BROWSER) {
                    testData.sendMessageToCallbacks(TestStep.REDIRECT_BROWSER, httpReq, sourceComponent, this);
                }
                else {
                    testData.sendMessageToCallbacks(TestStep.COMMUNICATION_ERROR_ADDRESS, httpReq, sourceComponent, this);
                }
            } else if (uri.equals("/favicon.ico")) {
                resp_message = new Response(Status.NOT_FOUND, "Error 404", testData.chunkedTransfer());
            }
            
        } catch (Exception e) {
            logger.logException(e);
        }
        
        if (resp_message == null) {
            
            StringBuilder sb = new StringBuilder();
            sb.append("<html>");
            sb.append("<head><title>Debug Server</title></head>");
            sb.append("<body>");
            sb.append("<h1>Response</h1>");
            sb.append("<p><blockquote><b>URI -</b> ").append(uri).append("<br />");
            sb.append("<b>Method -</b> ").append(httpReq.getMethod()).append("</blockquote></p>");
            sb.append("<h3>Headers</h3><p><blockquote>").append(httpReq.getHeaders()).append("</blockquote></p>");
            sb.append("<h3>Parms</h3><p><blockquote>").append(httpReq.getParms()).append("</blockquote></p>");
            
            sb.append("</body>");
            sb.append("</html>");
            resp_message = new Response(sb.toString(), testData.chunkedTransfer());
        }
        
        return resp_message;
        
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
    }

    @Override
    public void notifySupportedEllipticCurvesExtension(int[] namedCurves) {
        String curves = "";
        for (int entry : namedCurves) {
            curves += " " + BouncyCastleTlsHelper.convertNamedCurveIntToString(entry);
        }
        logger.logState("TLS client sent SupportedEllipticCurves extension:" + curves);
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
	public void notifySessionTicketExtension(byte[] sessionTicketData)
	{
        logger.logState("TLS client sent SessionTicket extension: " + (sessionTicketData == null ? "null" : ("length=" + sessionTicketData.length + " " + javax.xml.bind.DatatypeConverter.printHexBinary(sessionTicketData))));
	}

    @Override
    public boolean hasFatalErrors() {
        return hasFatalErrors;
    }
    // END implementation of BouncyCastleTlsNotificationListener    

}
