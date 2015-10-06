package com.secunet.ipsmall;

import java.io.File;

import com.secunet.ipsmall.http.Java7NanoHTTPSocketFactory;
import com.secunet.ipsmall.http.NanoHTTPD;
import com.secunet.ipsmall.http.NanoHTTPD.Response.Status;
import com.secunet.ipsmall.log.IModuleLogger.LogLevel;
import com.secunet.ipsmall.log.Logger;
import com.secunet.ipsmall.test.FileBasedTestData;
import com.secunet.ipsmall.test.ITestData;
import com.secunet.ipsmall.test.ITestProtocolCallback.SourceComponent;
import com.secunet.ipsmall.test.ITestProtocolCallback.TestStep;
import com.secunet.ipsmall.test.ITestSession;
import com.secunet.ipsmall.util.HttpUtils;

public class TCTokenProvider extends NanoHTTPD {

	/**
     * Mime type for TCToken
     */
    public static final String c_TCTOKEN_MIME_TYPE = "text/xml; charset=utf-8";
	
    
    public TCTokenProvider(ITestData testData) {
        super(testData.getTCTokenProviderHost(), testData.getTCTokenProviderPort(), null, testData.getTCTokenProviderTLSVersion(), testData
                .getTCTokenProviderTLSCipherSuites());
        this.logger = Logger.TCTokenProv;
        this.testData = testData;
        
        for (java.security.cert.X509Certificate cert : testData.getTCTokenProviderCertificate()) {
            logger.logState("Loading X509Cert: " + cert.toString(), LogLevel.Debug);
        }
        
        try {
            // use a precompiled openssl implementation to test whether the client accepts short elliptic curves
            if (testData.useModifiedSSL()) {
                String path = ((FileBasedTestData) testData).getDefaultConfigPath();
                Runtime.getRuntime().exec(
                        "cmd /c start " + path + File.separator + "e08.bat " + path + " " + ((FileBasedTestData) testData).getRelativeTestObjectFolder());
            } else {
                externalServerSocketFactory = new Java7NanoHTTPSocketFactory(testData.getTCTokenProviderCertificate(), testData.getTCTokenProviderPrivateKey(),
                        false);
                // externalServerSocketFactory = new IAIKNanoHTTPDSocketFactory(testData.getTCTokenProviderCertificate(),
                // testData.getTCTokenProviderPrivateKey(), false);
            }
        } catch (Exception e) {
            if (testData.useModifiedSSL()) {
                logger.logState("Error starting OpenSSL server: " + e.getMessage(), LogLevel.Error);
            } else {
                logger.logState("Error creating java7 socket factory: " + e.getMessage(), LogLevel.Error);
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
        logger.logState("TCTokenProvider: serving HTTPSession URI: " + hostname + ":" + myPort + uri + optionalParams);
        // TODO this is redundant. safety net for NanoHTTPD parsing errors until the logging is rewritten. the entire header will be logged in future releases
        logger.logState("Used the request string: " + httpReq.getRawHeader().substring(0, httpReq.getRawHeader().indexOf(System.getProperty("line.separator"))));
        Response resp_message = null;
        try {
            SourceComponent sourceComponent = HttpUtils.getSourceFromHttpHeaders(httpReq.getHeaders());
            
            if (uri.equals("/" + testData.getTCTokenProviderTCTokenURL())) {
                ITestSession session = testData.getNewSession();
                String tcToken = session.getTCToken();
                if (tcToken != null) {
                    resp_message = new Response(tcToken, c_TCTOKEN_MIME_TYPE, testData.chunkedTransfer());
                    logger.logState("Client fetched Token: session - " + session.getSessionID());
                } else {
                    resp_message = new Response(Status.NOT_FOUND, "Error 404", testData.chunkedTransfer());
                    logger.logState("Client found no Token: session - " + session.getSessionID());
                }
                testData.sendMessageToCallbacks(TestStep.TC_TOKEN, tcToken, sourceComponent, this);
            } else if (uri.equals("/favicon.ico")) {
                resp_message = new Response(Status.NOT_FOUND, "Error 404", testData.chunkedTransfer());
            }
            
        } catch (Exception e) {
            logger.logException(e);
            resp_message = new Response(Status.INTERNAL_ERROR, e.getMessage(), testData.chunkedTransfer());
        }
        
        if (resp_message == null) {
            
            StringBuilder sb = new StringBuilder();
            sb.append("<html>");
            sb.append("<head><title>Debug Server - TC Token Provider</title></head>");
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
    
}
