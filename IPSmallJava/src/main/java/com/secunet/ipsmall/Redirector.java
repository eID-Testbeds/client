package com.secunet.ipsmall;

import java.security.cert.X509Certificate;

import com.secunet.ipsmall.http.Java7NanoHTTPSocketFactory;
import com.secunet.ipsmall.http.NanoHTTPD;
import com.secunet.ipsmall.http.NanoHTTPD.Response.Status;
import com.secunet.ipsmall.log.IModuleLogger.LogLevel;
import com.secunet.ipsmall.log.Logger;
import com.secunet.ipsmall.test.ITestData;
import com.secunet.ipsmall.test.ITestProtocolCallback.SourceComponent;
import com.secunet.ipsmall.test.ITestProtocolCallback.TestStep;
import com.secunet.ipsmall.test.ITestSession;
import com.secunet.ipsmall.util.CommonUtil;
import com.secunet.ipsmall.util.HttpUtils;

public class Redirector extends NanoHTTPD {
    
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
        
        try {
            externalServerSocketFactory = new Java7NanoHTTPSocketFactory(new X509Certificate[] { testData.readCertificate(params[2]) },
                    testData.readPrivateKey(params[3]), false);
        } catch (Exception e) {
            logger.logState("Error creating java7 socket factory: " + e.getMessage(), LogLevel.Error);
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
    
}
