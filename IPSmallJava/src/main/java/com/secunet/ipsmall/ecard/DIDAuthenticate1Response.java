package com.secunet.ipsmall.ecard;

import java.io.IOException;

import javax.xml.xpath.XPathConstants;

import com.secunet.ipsmall.eac.CardAccess;
import com.secunet.ipsmall.eac.EIDException;
import com.secunet.ipsmall.test.ITestSession;
import com.secunet.ipsmall.util.Base64Util;

public class DIDAuthenticate1Response extends Message {
    CardAccess m_cardAccess = null;
    
    public DIDAuthenticate1Response(MessageHandler messageHandler) {
        super(messageHandler);
    }
    
    public boolean isResultOK() {
        String value = (String) m_handler.read("/Envelope/Body/DIDAuthenticateResponse/Result/ResultMajor",
                XPathConstants.STRING);
        
        return "http://www.bsi.bund.de/ecard/api/1.1/resultmajor#ok".equals(value);
    }
    
    public final CardAccess getEFCardAccess(ITestSession testSession) throws IOException, EIDException {
        if (m_cardAccess == null) {
            String hexCard = (String) m_handler.read(
                    "/Envelope/Body/DIDAuthenticateResponse/AuthenticationProtocolData/EFCardAccess",
                    XPathConstants.STRING);
            
            m_cardAccess = new CardAccess();
            m_cardAccess.fromAsn1(Base64Util.decodeHEX(hexCard), testSession.getTestData().getDefaultCAKeyID());
        }
        
        return m_cardAccess;
    }
    
    public final byte[] getIDPicc() {
        String hexID = (String) m_handler.read(
                "/Envelope/Body/DIDAuthenticateResponse/AuthenticationProtocolData/IDPICC", XPathConstants.STRING);
        // System.out.println("--idPicc: "+ hexID);
        
        return Base64Util.decodeHEX(hexID);
    }
    
    public byte[] getChallenge() {
        String hexCard = (String) m_handler.read(
                "/Envelope/Body/DIDAuthenticateResponse/AuthenticationProtocolData/Challenge", XPathConstants.STRING);
        return Base64Util.decodeHEX(hexCard);
    }
}
