package com.secunet.ipsmall.util;

import java.util.HashMap;
import java.util.Map;

import com.secunet.ipsmall.http.NanoHTTPD.HTTPSession;
import com.secunet.ipsmall.rmi.IBrowserSimulator;
import com.secunet.ipsmall.rmi.RmiHttpResponse;
import com.secunet.ipsmall.test.ITestProtocolCallback.SourceComponent;
import com.secunet.ipsmall.tls.BouncyCastleSocket;

import com.secunet.testbedutils.utilities.CommonUtil;

public class HttpUtils {
    
    /** ECARDCONF-235 */
    public static String getHeader(final HTTPSession session)
    {
        Map<String, String> headers = session.getHeaders();
        StringBuilder headLogEntries = new StringBuilder();
        headLogEntries.append("Header:\r\n");
        for (String key : headers.keySet())
        {
            headLogEntries.append(key + ":\t" + headers.get(key) + "\r\n");
        }
        
        return headLogEntries.toString();
    }
    
    public static SourceComponent getSourceFromHttpHeaders(Map<String, String> headers) {
        String userAgent = headers.get(IBrowserSimulator.HEADER_KEY_USER_AGENT);
        
        if (userAgent == null) {
        	// workaround for clients which do not have a userAgent set
        	return SourceComponent.EID_CLIENT;
        } else if (userAgent.equals(IBrowserSimulator.HEADER_VALUE_USER_AGENT)
                || userAgent.startsWith("Apache-HttpClient")) {
            // startWith 'Apache-HttpClient' inserted too as passing user-agent
            // manually in BrowserSimulator is not applied as expected
            return SourceComponent.BROWSER_SIMULATOR;
        } else if (userAgent.contains("Mozilla") || userAgent.contains("Dalvik")) {
            // almost any genuine browser sends Mozilla...
            return SourceComponent.BROWSER;
        } else {
        	// assume we deal with an eID-client
        	return SourceComponent.EID_CLIENT;
        }
    }
    
    /**
     * Parses URL parameters as key-value-pairs where the passed String can be
     * any URL or a part of it containing the params, e.g.
     * <ul>
     * <li>ampersand-separated key-value-pairs only, e.g.:
     * 'key1=value1&key2=value2'</li>
     * <li>a URL, e.g. 'http://host:port/path?key1=value1&key2=value2'</li>
     * </ul>
     * 
     * @param s
     * @return a Map of key-value pairs
     */
    public static Map<String, String> parseUrlParams(String s) {       
        Map<String, String> result = new HashMap<String, String>();
        
        if (s != null && s.indexOf("=") >= 0 && !s.trim().isEmpty()) {
            int qmark = s.indexOf("?");
            if (qmark >= 0) {
                s = s.substring(qmark + 1);
            }
            String[] keyValues = s.split("&");
            for (String keyValue : keyValues) {
                String[] splitted = keyValue.split("=");
                String value = "";
                if (splitted.length == 2) {
                	value = splitted[1];
                }
                result.put(splitted[0], value);
            }
        }
        
        return result;
    }
    
    public static String getRedirectUrl(final RmiHttpResponse response) {
        return (response != null && response.headers != null ? CommonUtil.getIgnoreCase(response.headers, "Location") : null);
    }
    
    public static String getRedirectUrl(final HTTPSession session) {
        if (session == null) {
            return null;
        }
        
        String url = "";
        
        // get protocol
        if (session.getSocket() != null) {
            if (session.getSocket() instanceof BouncyCastleSocket) {
                url += "https://";
            } else {
                url += "http://";
            }
        } else {
            return null;
        }
        
        // get host
        if (session.getHeaders() != null) {
            String host = CommonUtil.getIgnoreCase(session.getHeaders(), "host");
            if (host != null) {
                url += host;
            } else {
                return null;
            }
        } else {
            return null;
        }
        
        // get path
        if (session.getUri() != null) {
            url += session.getUri();
        } else {
            url += "/";
        }
        
        // get params
        if (session.getParms() != null) {
            String params = CommonUtil.getIgnoreCase(session.getParms(), "NanoHttpd.QUERY_STRING");
            if (params != null) {
                url += "?" + params;
            }
        }
        
        return url;
    }
}
