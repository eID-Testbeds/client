package com.secunet.ipsmall;

import com.secunet.ipsmall.http.NanoHTTPD;
import com.secunet.ipsmall.http.NanoHTTPD.HTTPSession;
import com.secunet.ipsmall.http.NanoHTTPD.Response;
import com.secunet.ipsmall.http.NanoHTTPD.Response.Status;
import com.secunet.ipsmall.log.Logger;
import com.secunet.ipsmall.test.ITestData;
import com.secunet.ipsmall.test.ITestData.Type;
import com.secunet.ipsmall.test.ITestProtocolCallback.SourceComponent;
import com.secunet.ipsmall.test.ITestProtocolCallback.TestStep;
import com.secunet.ipsmall.test.ITestSession;
import com.secunet.ipsmall.util.HttpUtils;

public class EServiceWebServer {
    
    protected ITestData testData;

    private Object parent = null;
    
    public EServiceWebServer(ITestData testData, Object parent) {
        this.testData = testData;
        this.parent = parent;
    }

    public Response serve(HTTPSession httpReq, Logger logger) {
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
        logger.logState("EServiceWebpage: serving HTTPSession URI: " + uri + optionalParams);
        // TODO this is redundant. safety net for NanoHTTPD parsing errors until the logging is rewritten. the entire header will be logged in future releases
        logger.logState("Used the request string: " + httpReq.getRawHeader().substring(0, httpReq.getRawHeader().indexOf(System.getProperty("line.separator"))));
        Response resp_message = null;
        try {
            
            SourceComponent sourceComponent = HttpUtils.getSourceFromHttpHeaders(httpReq.getHeaders());
            
            Integer redirectorNumber = testData.getEServiceRedirectorTCTokenNumber();
            
            if (uri.equals("/") || uri.equals("/" + testData.getEServiceIndexPageURL())) {
                if(parent instanceof EService) {
                    ((EService)parent).skipFirstICSTest = false;
                }
                else if(parent instanceof AttachedEIDServer) {
                    ((AttachedEIDServer)parent).skipFirstICSTest = false;
                }
                resp_message = new Response(testData.getEServiceIndexPage(), testData.chunkedTransfer());
            } else if (uri.equals("/" + testData.getEServiceTCTokenURL())) {
                ITestSession session = testData.getNewSession();
                String tcToken = session.getTCToken();
                if (tcToken != null) {
                    resp_message = new Response(tcToken, TCTokenProvider.c_TCTOKEN_MIME_TYPE, testData.chunkedTransfer());
                    logger.logState("Client fetched Token: session - " + session.getSessionID());
                } else {
                    resp_message = new Response(Status.NOT_FOUND, "Error 404", testData.chunkedTransfer());
                    logger.logState("Client found no Token: session - " + session.getSessionID());
                }
                testData.sendMessageToCallbacks(TestStep.TC_TOKEN, tcToken, sourceComponent, parent);
            } else if (uri.equals("/" + testData.getEServiceRefreshPageURL())) {
                logger.logState("EServiceWebpage refresh page called");
                resp_message = new Response(testData.getEServiceRefreshPage(), testData.chunkedTransfer());
                
                // If no browsersimulator is used, the final step BROWSER_REDIRECT can not be detected via browsersimulator.
                // Instead, the resulting request from the BROWSER to the REFRESH_ADDRESS must be handled.
                if(testData.getTestType() != null && testData.getTestType() == Type.BROWSER && sourceComponent == SourceComponent.BROWSER) {
                    testData.sendMessageToCallbacks(TestStep.REDIRECT_BROWSER, httpReq, sourceComponent, parent);
                }
                else {
                    testData.sendMessageToCallbacks(TestStep.REFRESH_ADDRESS, httpReq, sourceComponent, parent);
                }
            } else if (uri.equals("/" + testData.getEServiceCommunicationErrorPageURL())) {
                logger.logState("EServiceWebpage communication error page called");
                resp_message = new Response(testData.getEServiceCommunicationErrorPage(), testData.chunkedTransfer());

                // If no browsersimulator is used, the final step BROWSER_REDIRECT can not be detected via browsersimulator.
                // Instead, the resulting request from the BROWSER to the COMMUNICATION_ERROR_ADDRESS must be handled.
                if(testData.getTestType() != null && testData.getTestType() == Type.BROWSER && sourceComponent == SourceComponent.BROWSER) {
                    testData.sendMessageToCallbacks(TestStep.REDIRECT_BROWSER, httpReq, sourceComponent, parent);
                }
                else {
                    testData.sendMessageToCallbacks(TestStep.COMMUNICATION_ERROR_ADDRESS, httpReq, sourceComponent, parent);
                }
            } else if ((redirectorNumber != null) && (uri.equals("/" + testData.getEServiceRedirectURL()))) {
                // Redirect
                // String target = testData.getRedirectorTCTokenHost(redirectorNumber) + ":" + testData.getRedirectorTCTokenPort(redirectorNumber) + "/"
                // + testData.getRedirectorTCTokenURL(redirectorNumber);
                String[] redirectorInfo = testData.getRedirectorsInfoTCToken().get(redirectorNumber);
                String target = redirectorInfo[0];
                
                testData.sendMessageToCallbacks(TestStep.TC_TOKEN_REDIRECT, target, sourceComponent, parent);
                int statusCode = Integer.parseInt(testData.getRedirectorTCTokenStatus(redirectorNumber));
                Status status = null;
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
                if (!Status.NOT_FOUND.equals(status)) {
                    String locationInfo = "";
                    resp_message = new Response(status, status.getDescription(), testData.chunkedTransfer());
                    String location = "https://" + target;
                    if (testData.getEServiceRedirectLocation() != null) {
                        location = testData.getEServiceRedirectLocation();
                        locationInfo = " (Location changed to '" + location + "')";
                    }
                    resp_message.addHeader("Location", location);
                    logger.logState("EServiceWebpage as redirector to '" + location + "' with StatusCode: " + statusCode + " " + status + locationInfo);
                }
            } else if (uri.equals("/sop_redirect_refresh")) {
                // Redirect
                // String target = testData.getRedirectorTCTokenHost(redirectorNumber) + ":" + testData.getRedirectorTCTokenPort(redirectorNumber) + "/"
                // + testData.getRedirectorTCTokenURL(redirectorNumber);
                
                // highly experemental. Set the redirectorNumber to 302 and Location manually.
                int statusCode = 302;
                String target = testData.getEServiceHost();
                target += ":";
                target += Integer.toString(testData.getEServicePort());
                target += "/";
                target += testData.getEServiceRefreshPageURL();
                
                // String target = "eservice-1-idp-test.secunet.de:443/refresh";
                // String[] redirectorInfo = testData.getRedirectorsInfoTCToken().get(redirectorNumber);
                // String target = redirectorInfo[0];
                
                testData.sendMessageToCallbacks(TestStep.REFRESH_ADDRESS_REDIRECT, target, sourceComponent, parent);
                // int statusCode = Integer.parseInt(testData.getRedirectorTCTokenStatus(redirectorNumber));
                Status status = null;
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
                // status = Status.FOUND;
                if (!Status.NOT_FOUND.equals(status)) {
                    String locationInfo = "";
                    resp_message = new Response(status, status.getDescription(), testData.chunkedTransfer());
                    String location = "https://" + target;
                    if (testData.getEServiceRedirectLocation() != null) {
                        location = testData.getEServiceRedirectLocation();
                        locationInfo = " (Location changed to '" + location + "')";
                    }
                    resp_message.addHeader("Location", location);
                    logger.logState("EServiceWebpage as redirector to '" + location + "' with StatusCode: " + statusCode + " " + status + locationInfo);
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

}
