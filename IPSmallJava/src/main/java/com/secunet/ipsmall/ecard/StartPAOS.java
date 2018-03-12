package com.secunet.ipsmall.ecard;

import java.io.StringWriter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPathConstants;

import org.w3c.dom.Node;

public class StartPAOS extends Message {
    
    public StartPAOS(MessageHandler handler) {
        super(handler);
    }
    
    public String getMessageID() {
        return (String) m_handler.read("/Envelope/Header/MessageID", XPathConstants.STRING);
    }
    
    public String getSessionID() {
        return (String) m_handler.read("/Envelope/Body/StartPAOS/SessionIdentifier", XPathConstants.STRING);
    }
    
    public String getContextHandle() {
        return (String) m_handler.read("/Envelope/Body/StartPAOS/ConnectionHandle/ContextHandle", XPathConstants.STRING);
    }
    
    public String getSlotHandle() {
        return (String) m_handler.read("/Envelope/Body/StartPAOS/ConnectionHandle/SlotHandle", XPathConstants.STRING);
    }
    
    public String getConnectionHandle() {
        
        Node node = (Node)m_handler.read("/Envelope/Body/StartPAOS/ConnectionHandle", XPathConstants.NODE);
        
        String xml = "";
        
        StringWriter writer = new StringWriter();
        Transformer transformer;
        try {
            transformer = TransformerFactory.newInstance().newTransformer();
            transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.transform(new DOMSource(node), new StreamResult(writer));
        } catch (TransformerFactoryConfigurationError | TransformerException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        xml = writer.toString();
        
        // replace namespace prefix
        String regex = "<(/?)[a-z0-9]*(:?)";
        String newPrefix = "iso:"; // "iso:"
        
        Matcher matcher = Pattern.compile(regex).matcher(xml);
        StringBuffer sb = new StringBuffer(xml.length());
        while (matcher.find())
          matcher.appendReplacement(sb, "<$1" + newPrefix);
        matcher.appendTail(sb);
        xml = sb.toString();
        
        // replace attributes
        xml = xml.replaceAll("<iso:ConnectionHandle.*>", "<iso:ConnectionHandle xsi:type=\"iso:ConnectionHandleType\">");
        
        return xml;
    }
    
}
