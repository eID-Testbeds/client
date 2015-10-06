package com.secunet.ipsmall;

import com.secunet.ipsmall.http.NanoHTTPD;
import com.secunet.ipsmall.http.NanoHTTPD.Response.Status;
import com.secunet.ipsmall.log.Logger;
import com.secunet.ipsmall.test.ITestData;
import com.secunet.ipsmall.test.ITestData.Type;
import com.secunet.ipsmall.test.ITestProtocolCallback.SourceComponent;
import com.secunet.ipsmall.test.ITestProtocolCallback.TestStep;
import com.secunet.ipsmall.util.HttpUtils;

/**
 * Server handling communication error address request (which was part of {@link EService} before).
 * This needs to handle HTTPS (in most use-cases), but also HTTP.
 * 
 * @author kersten.benjamin
 *
 */
public class CommErrorAddressServer extends NanoHTTPD {

	public CommErrorAddressServer(ITestData testData) {
		
        super(testData.getCommErrorAddressServerHost(), testData.getCommErrorAddressServerPort(), null, testData.getCommErrorAddressServerTLSVersion(), testData.getCommErrorAddressServerTLSCipherSuites());
        
        this.logger = Logger.eIDServer;
        this.testData = testData;
        
       
        // in future releases, https might be added to this comm-error-address-server. At present, https is handled
        // by eService-server        
        // https
        //externalServerSocketFactory = new Java7NanoHTTPSocketFactory(testData.getEServiceCertificate(), testData.getEServerPrivateKey(), false);            
                
        // http
        // when socket factory is null, NanoHTTPD will be started with default http socket
        externalServerSocketFactory = null;            
            
        
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

}
