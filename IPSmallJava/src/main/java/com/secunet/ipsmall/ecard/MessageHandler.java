package com.secunet.ipsmall.ecard;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class MessageHandler {
    
    private Document xmlDocument;
    private XPath xPath;
    private String rawMessage;
    
    public MessageHandler(final String msg) throws SAXException, IOException, ParserConfigurationException {
        rawMessage = msg;
        xmlDocument = DocumentBuilderFactory.newInstance().newDocumentBuilder()
                .parse(new InputSource(new ByteArrayInputStream(msg.getBytes("utf-8"))));
        
        xPath = XPathFactory.newInstance().newXPath();
    }
    
    /** on error can return null */
    public Object read(String expression, QName returnType) {
        try {
            XPathExpression xPathExpression = xPath.compile(expression);
            return xPathExpression.evaluate(xmlDocument, returnType);
        } catch (XPathExpressionException ex) {
            ex.printStackTrace();
            return null;
        }
    }
    
    public boolean isStartPaos() {
        Object test = read("/Envelope/Body/StartPAOS", XPathConstants.NODE);
        
        return (test != null);
    }
    
    public StartPAOS getStartPAOS() {
        return new StartPAOS(this);
    }
    
    public boolean isInitializeResponse() {
        Object test = read("/Envelope/Body/InitializeFrameworkResponse", XPathConstants.NODE);
        
        return (test != null);
    }
    
    public InitializeFrameworkResponse getInitializeResponse() {
        return new InitializeFrameworkResponse(this);
    }
    
    public boolean isDIDAuthenticate1Response() {
        Object test = read("/Envelope/Body/DIDAuthenticateResponse/AuthenticationProtocolData/EFCardAccess",
                XPathConstants.NODE);
        
        return (test != null);
    }
    
    public DIDAuthenticate1Response getDIDAuthenticate1Response() {
        return new DIDAuthenticate1Response(this);
    }
    
    public boolean isDIDAuthenticate2Response(boolean threeMessageMode) {
        
        if (threeMessageMode) {
            Object test = read("/Envelope/Body/DIDAuthenticateResponse/AuthenticationProtocolData/Challenge",
                    XPathConstants.NODE);
            
            return (test != null);
        } else // if only two messages are used for EAC, check for auth. token
            return isDIDAuthenticate3Response();
    }
    
    public DIDAuthenticate2Response getDIDAuthenticate2Response() {
        return new DIDAuthenticate2Response(this);
    }
    
    public boolean isDIDAuthenticate3Response() {
        Object test = read("/Envelope/Body/DIDAuthenticateResponse/AuthenticationProtocolData/AuthenticationToken",
                XPathConstants.NODE);
        
        return (test != null);
    }
    
    public DIDAuthenticate3Response getDIDAuthenticate3Response() {
        return new DIDAuthenticate3Response(this);
    }
    
    public boolean isTransmitResponse() {
        Object test = read("/Envelope/Body/TransmitResponse", XPathConstants.NODE);
        
        return (test != null);
    }
    
    public TransmitResponse getTransmitResponse() {
        return new TransmitResponse(this);
    }
    
    @Override
    public String toString() {
        try {
            Transformer t = TransformerFactory.newInstance().newTransformer();
            t.setOutputProperty(OutputKeys.INDENT, "yes");
            t.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
            
            ByteArrayOutputStream s = new ByteArrayOutputStream();
            
            t.transform(new DOMSource(xmlDocument), new StreamResult(s));
            
            return new String(s.toByteArray());
            
        } catch (TransformerConfigurationException e) {
            e.printStackTrace();
        } catch (TransformerFactoryConfigurationError e) {
            e.printStackTrace();
        } catch (TransformerException e) {
            e.printStackTrace();
        }
        
        return rawMessage;
    }
    
    /** If authentication fails */
    public boolean isErrorResponse() {
        String test = (String) read("/Envelope/Body/TransmitResponse/DIDAuthenticateResponse/Result/ResultMajor", XPathConstants.STRING);
        return "http://www.bsi.bund.de/ecard/api/1.1/resultmajor#error".equals(test);
    }
    
}
