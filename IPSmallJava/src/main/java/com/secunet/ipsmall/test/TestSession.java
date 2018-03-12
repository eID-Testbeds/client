package com.secunet.ipsmall.test;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.SecureRandom;
import java.security.interfaces.ECPublicKey;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.secunet.ipsmall.eac.CardAccess;
import com.secunet.ipsmall.eac.TerminalAuthenticationSignature;
import com.secunet.ipsmall.eac.cv.UtilPublicKey;
import com.secunet.ipsmall.eac.sm.TestAPDUHandler;
import com.secunet.ipsmall.eac.sm.TestAPDUHandler.TestApdu;
import com.secunet.ipsmall.ecard.DIDAuthenticate1Response;
import com.secunet.ipsmall.ecard.DIDAuthenticate2Response;
import com.secunet.ipsmall.ecard.DIDAuthenticate3Response;
import com.secunet.ipsmall.ecard.InitializeFrameworkResponse;
import com.secunet.ipsmall.ecard.StartPAOS;
import com.secunet.ipsmall.ecard.TransmitResponse;
import com.secunet.testbedutils.utilities.Base64Util;
import com.secunet.testbedutils.utilities.CommonUtil;
import com.secunet.testbedutils.utilities.VariableParser;
import com.sun.xml.internal.ws.util.ByteArrayBuffer;

public class TestSession implements ITestSession {
    String m_sessionID;
    byte[] m_pskkey = new byte[5];
    ITestData m_testData;
    
    StartPAOS m_startPAOS;
    InitializeFrameworkResponse m_initializeResponse;
    DIDAuthenticate1Response m_didAuthenticate1Response;
    DIDAuthenticate2Response m_didAuthenticate2Response;
    DIDAuthenticate3Response m_didAuthenticate3Response;
    
    // TODO maybe we should use a list here
    List<TransmitResponse> m_transmitResponses;
    
    TestAPDUHandler m_apduHandler;
    
    KeyPair m_ephemeralKeyPair = null;
    
    static SecureRandom s_rand = new SecureRandom();
    
    public TestSession(ITestData testData) {
        m_testData = testData;
        
        m_sessionID = Base64Util.encodeHEX(CommonUtil.convertUUID(UUID.randomUUID()));
        // m_sessionID = "1374737645e268bad3411eb1e2f5";
        s_rand.nextBytes(m_pskkey);
        
        m_pskkey = CommonUtil.concatArrays(m_pskkey, Base64Util.decodeHEX("7bc5d27c580dd8a6ec24fc2f049a735a0c9e0bcbbb54b7f0464133d7257adf87"));
    }
    
    @Override
    public String getSessionID() {
        return m_sessionID;
    }
    
    @Override
    public byte[] getPSKKey() {
        
        return m_pskkey;
    }
    
    @Override
    public ITestData getTestData() {
        
        return m_testData;
    }
    
    @Override
    public String getTCToken() throws Exception {
        VariableParser parser = new VariableParser(new VariableSessionParameter(this));
        String tokenTemplate = m_testData.getEServiceTokenTemplate();
        return (tokenTemplate != null) ? parser.format(tokenTemplate) : null;
    }
    
    @Override
    public String getTCTokenValue(final String key) throws Exception {
        if (key == null) {
            return null;
        }
        String tcToken = getTCToken();
        if (tcToken == null) {
            return null;
        } else {
            String value = CommonUtil.getSubstringBefore(CommonUtil.getSubstringAfter(tcToken, "<" + key + ">", false), "</" + key + ">", false);
            return ((value != null) && (value.length() > 0)) ? value : null;
        }
    }
    
    @Override
    public void setStartPaos(StartPAOS startPAOS) {
        m_startPAOS = startPAOS;
    }
    
    @Override
    public StartPAOS getStartPaos() {
        return m_startPAOS;
    }
    
    @Override
    public String getInitializeFramework() throws Exception {
        VariableParser parser = new VariableParser(new VariableSessionParameter(this));
        return parser.format(m_testData.getECardInitializeFrameworkTemplate());
    }
    
    @Override
    public void setInitializeResponse(InitializeFrameworkResponse initializeResponse) {
        m_initializeResponse = initializeResponse;
    }
    
    @Override
    public InitializeFrameworkResponse getInitializeResponse() {
        return m_initializeResponse;
    }
    
    @Override
    public String getDIDAuthenticate1() throws Exception {
        VariableParser parser = new VariableParser(new VariableSessionParameter(this));
        return parser.format(m_testData.getECardDIDAuthenticate1Template());
    }
    
    @Override
    public byte[] getEphemeralPublicKey(boolean raw) throws Exception {
        if (m_ephemeralKeyPair == null) {
            try {
                CardAccess access = m_didAuthenticate1Response.getEFCardAccess(this);
                KeyPairGenerator generator = KeyPairGenerator.getInstance(access.getCAKeylgorithm(), "BC");
                generator.initialize(access.getCADomainParameters());
                
                m_ephemeralKeyPair = generator.generateKeyPair();
            } catch (Exception e) {
                throw new Exception("cant generate ephemeral key", e);
            }
        }
        
        if (m_ephemeralKeyPair.getPublic() instanceof ECPublicKey) {
            ECPublicKey pub = (ECPublicKey) m_ephemeralKeyPair.getPublic();
            
            ByteArrayBuffer ecPubKeyData = new ByteArrayBuffer();
            if (!raw)
                ecPubKeyData.write(new byte[] { 0x04 });
            
            ecPubKeyData.write(UtilPublicKey.getRawKey(pub));
            ecPubKeyData.close();
            return ecPubKeyData.getRawData();
        }
        
        // TODO fix it for DH
        return m_ephemeralKeyPair.getPublic().getEncoded();
    }
    
    @Override
    public PrivateKey getEphemeralPrivateKey() {
        return m_ephemeralKeyPair.getPrivate();
    }
    
    @Override
    public KeyPair getEphemeralKeyPair() {
        return m_ephemeralKeyPair;
    }
    
    @Override
    public String getDIDAuthenticate2() throws Exception {
        VariableParser parser = new VariableParser(new VariableSessionParameter(this));
        return parser.format(m_testData.getECardDIDAuthenticate2Template());
    }
    
    @Override
    public void setDIDAuthenticate1Response(DIDAuthenticate1Response didAuthenticate1Response) {
        m_didAuthenticate1Response = didAuthenticate1Response;
    }
    
    @Override
    public void setDIDAuthenticate2Response(DIDAuthenticate2Response didAuthenticate2Response) {
        m_didAuthenticate2Response = didAuthenticate2Response;
    }
    
    @Override
    public String getDIDAuthenticate3() throws Exception {
        VariableParser parser = new VariableParser(new VariableSessionParameter(this));
        return parser.format(m_testData.getECardDIDAuthenticate3Template());
    }
    
    @Override
    public byte[] getChallengeSiganture() throws Exception {
        byte[] challenge = null;
        if (m_didAuthenticate2Response != null)
            challenge = m_didAuthenticate2Response.getChallenge();
        else if (m_didAuthenticate1Response != null)
            challenge = m_didAuthenticate1Response.getChallenge();
        
        if (challenge == null)
            return new byte[0];
        
        // prepare compressed form
        byte[] completeKey = getEphemeralPublicKey(true);
        byte[] xCoord = new byte[completeKey.length / 2];
        System.arraycopy(completeKey, 0, xCoord, 0, xCoord.length);
        
        // just sign
        TerminalAuthenticationSignature signer = new TerminalAuthenticationSignature(m_testData.getEIDServiceCV_TERM(), m_testData.getEIDServiceCV_TERM_KEY());
        signer.init(m_didAuthenticate1Response.getIDPicc(), xCoord, m_testData.getEIDServiceAuxData());
        
        return signer.sign(challenge);
    }
    
    @Override
    public void setDIDAuthenticate3Response(DIDAuthenticate3Response didAuthenticate3Response) {
        m_didAuthenticate3Response = didAuthenticate3Response;
    }
    
    @Override
    public List<String> getTransmits() {
        return m_testData.getECardStepTransmitTemplates();
    }
    
    @Override
    public List<TransmitResponse> getTransmitResponses() {
        return m_transmitResponses;
    }
    
    @Override
    public String getTransmit(int number) throws Exception {
        VariableParser parser = new VariableParser(new VariableSessionParameter(this));
        if (m_testData.getECardStepTransmitTemplates() != null) {
            return parser.format(m_testData.getECardStepTransmitTemplates().get(number));
        } else {
            return null;
        }
    }
    
    @Override
    public void addTransmitResponse(TransmitResponse transmitResponse) {
        if (m_transmitResponses == null) {
            m_transmitResponses = new ArrayList<TransmitResponse>();
        }
        m_transmitResponses.add(transmitResponse);
    }
    
    @Override
    public String getStartPAOSResponse() throws Exception {
        VariableParser parser = new VariableParser(new VariableSessionParameter(this));
        return parser.format(m_testData.getECardStartPAOSResponseTemplate());
    }
    
    @Override
    public String getTransmitAPDU(TestApdu test) throws Exception {
        if (m_apduHandler == null) {
            m_apduHandler = new TestAPDUHandler(this);
            m_apduHandler.initialize();
        }
        
        byte[] apdu = m_apduHandler.getTestApdu(test);
        return Base64Util.encodeHEX(apdu);
    }
    
    @Override
    public DIDAuthenticate1Response getDIDAuthenticate1Response() {
        return m_didAuthenticate1Response;
    }
    
    @Override
    public DIDAuthenticate2Response getDIDAuthenticate2Response() {
        return m_didAuthenticate2Response;
    }
    
    @Override
    public DIDAuthenticate3Response getDIDAuthenticate3Response() {
        return m_didAuthenticate3Response;
    }
    
}