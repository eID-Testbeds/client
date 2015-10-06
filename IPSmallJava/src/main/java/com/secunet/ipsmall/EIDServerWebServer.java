package com.secunet.ipsmall;

import java.util.List;

import com.secunet.ipsmall.ecard.MessageHandler;
import com.secunet.ipsmall.eval.EvaluateResult;
import com.secunet.ipsmall.eval.Evaluator;
import com.secunet.ipsmall.http.NanoHTTPD;
import com.secunet.ipsmall.http.NanoHTTPD.HTTPSession;
import com.secunet.ipsmall.http.NanoHTTPD.Response;
import com.secunet.ipsmall.http.NanoHTTPD.Response.Status;
import com.secunet.ipsmall.log.IModuleLogger.ConformityResult;
import com.secunet.ipsmall.log.IModuleLogger.LogLevel;
import com.secunet.ipsmall.log.Logger;
import com.secunet.ipsmall.test.ITestData;
import com.secunet.ipsmall.test.ITestProtocolCallback.SourceComponent;
import com.secunet.ipsmall.test.ITestProtocolCallback.TestStep;
import com.secunet.ipsmall.test.ITestSession;
import com.secunet.ipsmall.util.HttpUtils;

public class EIDServerWebServer {
    
    private ITestData testData = null;
    
    private boolean attached = false;
    
    private Object parent = null;
    
    private String hostname = null;
    private int port = -1;
    
    
    public static final String c_PAOS_MIME_TYPE = "application/vnd.paos+xml; charset=UTF-8";
    
    private final Status paosResponseCode;
    
    public EIDServerWebServer(ITestData testData, boolean isAttached, Object parent) {
        this.testData = testData;
        this.attached = isAttached;
        this.parent = parent;
        
        if(isAttached) {
            this.hostname = testData.getEServiceHost();
            this.port = testData.getEServicePort();
        }
        else {
            this.hostname = testData.getEIDServiceHost();
            this.port = testData.getEIDServicePort();
        }
        
        // default to 202 if something other than 200 was specified ("202 ACCEPTED" is correct, but "200 OK" is commonly used by most real eID servers)
        this.paosResponseCode = (testData.getPaosRequestResponseCode() == 200) ? Status.OK : Status.ACCEPTED;

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
        String uriComplete = hostname + ":" + port + httpReq.getUri() + optionalParams;
        logger.logState("EIDServer: serving HTTPSession URI: " + uriComplete);
        
        // TODO this is redundant. safety net for NanoHTTPD parsing errors until the logging is rewritten. the entire header will be logged in future releases
        logger.logState("Used the request string: " + httpReq.getRawHeader().substring(0, httpReq.getRawHeader().indexOf(System.getProperty("line.separator"))));
        
        Response resp_message = null;
        boolean isErrorResponse = false;
        try {
            SourceComponent sourceComponent = HttpUtils.getSourceFromHttpHeaders(httpReq.getHeaders());
            
            // Respond 404 if required
            if (httpReq.getUri().equals("/respond404")) {
                logger.logState("Respond 404", LogLevel.Error);
                return new Response(Status.NOT_FOUND, "Error 404", testData.chunkedTransfer());
            }
            
            String post_content = httpReq.getPostContent();
            
            MessageHandler handler = new MessageHandler(post_content);
            
            ITestSession sessionData = null;
            
            if(attached) {
                if (handler.isStartPaos()) {
                    String sessionid = handler.getStartPAOS().getSessionID();
                    if (sessionid == null) {
                        logger.logState("StartPAOS without SessionIdentifier (try sessionid from HTTP header)");
                        sessionid = httpReq.getParms().get("sessionid");
                    }
                    
                    logger.logState("Session ID -- " + sessionid);
                    sessionData = testData.getSession(sessionid);
                    
                    if (sessionData == null) {
                        logger.logState("Unknown Session", LogLevel.Error);
                        return new Response(Status.BAD_REQUEST, "Unknown Session", testData.chunkedTransfer());
                    }
                }
            }
            else {
                String sessionid = (String) httpReq.getSocketInfo().get("PSK_IDENT");
                if (sessionid == null) {
                    logger.logState("Unkown PSK Ident (try session id)");
                    sessionid = httpReq.getParms().get("sessionid");
                }
                
                logger.logState("Session ID -- " + sessionid);
                sessionData = testData.getSession(sessionid);
                
                if (sessionData == null) {
                    logger.logState("Unknown Session", LogLevel.Error);
                    return new Response(Status.BAD_REQUEST, "Unknown Session", testData.chunkedTransfer());
                }
            }
            
            if (testData.getEIDServiceCheckURI()) {
                final String key = "ServerAddress";
                String tcTokenServerAddr = sessionData.getTCTokenValue(key);
                if (tcTokenServerAddr != null) {
                    logger.logState(key + " from TCToken: " + tcTokenServerAddr);
                    // kinda hack, cause dunno how to get complete URI via NanoHTTPD
                    if (uriComplete.endsWith("/") && !tcTokenServerAddr.endsWith("/")) {
                        tcTokenServerAddr += "/";
                    }
                    //uriComplete is expected to be the full URI, e.g. https://server:port/path?params
                    uriComplete = "https://" + uriComplete;
                    if (!tcTokenServerAddr.equalsIgnoreCase(uriComplete)) {
                        logger.logConformity(ConformityResult.failed, "URI does not match " + key + " from TCToken", LogLevel.Error);
                        return new Response(Status.BAD_REQUEST, "Invalid URI", testData.chunkedTransfer());
                    }
                } else {
                    logger.logState("Invalid local TCToken template (Missing parameter '" + key + "'?)", LogLevel.Error);
                    return new Response(Status.INTERNAL_ERROR, "Invalid configuration", testData.chunkedTransfer());
                }
            }
            

            if (handler.isStartPaos()) {
                EvaluateResult res = Evaluator.createOrderedOccurenceResult("StartPAOS", testData.getStartPAOSEvaluationConfig(), handler);
                sessionData.setStartPaos(handler.getStartPAOS());
                
                // if no configuration for InitFramework is set, we skip InitFramework and proceed with EAC1 message.
                String nextMessage;
                if (testData.getECardInitializeFrameworkTemplate() != null)
                    nextMessage = sessionData.getInitializeFramework();
                else
                    nextMessage = sessionData.getDIDAuthenticate1();
                
                resp_message = new Response(paosResponseCode, c_PAOS_MIME_TYPE, nextMessage, testData.chunkedTransfer());
                testData.sendMessageToCallbacks(TestStep.START_PAOS, res, sourceComponent, parent);
            } else if (handler.isInitializeResponse()) {
                EvaluateResult res = Evaluator
                        .createOrderedOccurenceResult("InitializeFrameworkResponse", testData.getInitFrameworkEvaluationConfig(), handler);
                sessionData.setInitializeResponse(handler.getInitializeResponse());
                
                resp_message = new Response(paosResponseCode, c_PAOS_MIME_TYPE, sessionData.getDIDAuthenticate1(), testData.chunkedTransfer());
                testData.sendMessageToCallbacks(TestStep.INITIALIZE_FRAMEWORK, res, sourceComponent, parent);
            } else if (handler.isDIDAuthenticate1Response()) {
                EvaluateResult res = Evaluator.createOrderedOccurenceResult("DIDAuthenticateResponse", testData.getAuth1EvaluationConfig(), handler);
                sessionData.setDIDAuthenticate1Response(handler.getDIDAuthenticate1Response());
                
                resp_message = new Response(paosResponseCode, c_PAOS_MIME_TYPE, sessionData.getDIDAuthenticate2(), testData.chunkedTransfer());
                testData.sendMessageToCallbacks(TestStep.EAC1, res, sourceComponent, parent);
            } else if (handler.isDIDAuthenticate2Response(testData.getECardDIDAuthenticate3Template() != null)) {
                EvaluateResult res = Evaluator.createOrderedOccurenceResult("DIDAuthenticateResponse", testData.getAuth2EvaluationConfig(), handler);
                sessionData.setDIDAuthenticate2Response(handler.getDIDAuthenticate2Response());
                
             // if no configuration for EAC3 is set, we use only two messages.
                String nextMessage;
                if (testData.getECardDIDAuthenticate3Template() != null)
                    nextMessage = sessionData.getDIDAuthenticate3();
                else
                    nextMessage = sessionData.getTransmit(0);
                
                resp_message = new Response(paosResponseCode, c_PAOS_MIME_TYPE, nextMessage, testData.chunkedTransfer());
                testData.sendMessageToCallbacks(TestStep.EAC2, res, sourceComponent, parent);
            } else if (handler.isDIDAuthenticate3Response()) {
                EvaluateResult res = Evaluator.createOrderedOccurenceResult("DIDAuthenticateResponse", testData.getAuth3EvaluationConfig(), handler);
                sessionData.setDIDAuthenticate3Response(handler.getDIDAuthenticate3Response());
                
                resp_message = new Response(paosResponseCode, c_PAOS_MIME_TYPE, sessionData.getTransmit(0), testData.chunkedTransfer());
                testData.sendMessageToCallbacks(TestStep.EAC3, res, sourceComponent, parent);
            } else if (handler.isTransmitResponse()) {
                EvaluateResult res = Evaluator.createOrderedOccurenceResult("TransmitResponse", testData.getTransmitEvaluationConfig(), handler);
                sessionData.addTransmitResponse(handler.getTransmitResponse());
                List<String> transmits = sessionData.getTransmits();
                if ((transmits != null) && (sessionData.getTransmitResponses().size() < sessionData.getTransmits().size())) {
                    resp_message = new Response(paosResponseCode, c_PAOS_MIME_TYPE, sessionData.getTransmit(sessionData.getTransmitResponses().size()),
                            testData.chunkedTransfer());
                    testData.sendMessageToCallbacks(TestStep.TRANSMIT, res, sourceComponent, parent);
                } else {
                    resp_message = new Response(Status.OK, NanoHTTPD.c_SOAP_MIME_TYPE, sessionData.getStartPAOSResponse(), testData.chunkedTransfer());
                    testData.sendMessageToCallbacks(TestStep.TRANSMIT, res, sourceComponent, parent);
                    
                    // with last transmit, server send PAOS_RESPONSE to client.
                    // As this is also defined as TestStep, do another
                    // notification here.
                    // TODO: last-transmit+PaosResponse (?)
                    // callback nach hinten wenn transmit mit 9000 zurueck kommt
                    testData.sendMessageToCallbacks(TestStep.START_PAOS_RESPONSE, res, sourceComponent, parent);
                }
            } else {
                isErrorResponse = handler.isErrorResponse();
            }
            
        } catch (Exception e) {
            logger.logException(e);
            resp_message = new Response(Status.INTERNAL_ERROR, e.getMessage(), testData.chunkedTransfer());
        }
        
        if (resp_message == null) {
            if (isErrorResponse)
                logger.logState("Found Authentication Error Client Message, responding with 404");
            else
                logger.logState("Did not recognize message request type!");
            
            // resp_message = new Response(Status.NOT_FOUND, "404");
            resp_message = new Response(Status.BAD_REQUEST, testData.getECardErrorStartPaosResponseTemplate(), testData.chunkedTransfer());
        }
        
        return resp_message;
    }

}
