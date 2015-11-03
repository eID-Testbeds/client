package com.secunet.ipsmall.ecard;

import javax.xml.xpath.XPathConstants;

public class TransmitResponse extends Message {
    
    public TransmitResponse(MessageHandler handler) {
        super(handler);
    }
    
    public boolean isResultOK() {
        String value = (String) m_handler.read("/Envelope/Body/TransmitResponse/Result/ResultMajor",
                XPathConstants.STRING);
        
        return "http://www.bsi.bund.de/ecard/api/1.1/resultmajor#ok".equals(value);
    }
    
}
