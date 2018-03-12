package com.secunet.ipsmall.ecard;

import java.io.IOException;

import javax.xml.xpath.XPathConstants;

import com.secunet.ipsmall.eac.CardSecurity;
import com.secunet.ipsmall.eac.EIDException;
import com.secunet.ipsmall.test.ITestSession;
import com.secunet.testbedutils.utilities.Base64Util;

public class DIDAuthenticate2Response extends Message {
    public DIDAuthenticate2Response(MessageHandler handler) {
        super(handler);
    }
    
    public boolean isResultOK() {
        String value = (String) m_handler.read("/Envelope/Body/DIDAuthenticateResponse/Result/ResultMajor",
                XPathConstants.STRING);
        
        return "http://www.bsi.bund.de/ecard/api/1.1/resultmajor#ok".equals(value);
    }
    
    public byte[] getChallenge() {
        String hexCard = (String) m_handler.read(
                "/Envelope/Body/DIDAuthenticateResponse/AuthenticationProtocolData/Challenge", XPathConstants.STRING);
        return Base64Util.decodeHEX(hexCard);
    }
    
    public byte[] getAuthenticationToken() {
        String hexCard = (String) m_handler.read(
                "/Envelope/Body/DIDAuthenticateResponse/AuthenticationProtocolData/AuthenticationToken",
                XPathConstants.STRING);
        return Base64Util.decodeHEX(hexCard);
    }
    
    public byte[] getNonce() {
        String hexCard = (String) m_handler.read(
                "/Envelope/Body/DIDAuthenticateResponse/AuthenticationProtocolData/Nonce", XPathConstants.STRING);
        return Base64Util.decodeHEX(hexCard);
    }
    
    public CardSecurity getCardSecurity(ITestSession testSession) throws IOException, EIDException {
        String hexCard = (String) m_handler.read(
                "/Envelope/Body/DIDAuthenticateResponse/AuthenticationProtocolData/EFCardSecurity",
                XPathConstants.STRING);
        
        CardSecurity cardSecurity = null;
        if (!hexCard.isEmpty()) {    
            cardSecurity = new CardSecurity();
            cardSecurity.fromAsn1(Base64Util.decodeHEX(hexCard), testSession.getTestData().getDefaultCAKeyID());
        }
        
        return cardSecurity;
    }
}
