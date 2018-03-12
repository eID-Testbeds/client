package com.secunet.ipsmall.test;

import java.security.KeyPair;
import java.security.PrivateKey;
import java.util.List;

import com.secunet.ipsmall.eac.sm.TestAPDUHandler.TestApdu;
import com.secunet.ipsmall.ecard.DIDAuthenticate1Response;
import com.secunet.ipsmall.ecard.DIDAuthenticate2Response;
import com.secunet.ipsmall.ecard.DIDAuthenticate3Response;
import com.secunet.ipsmall.ecard.InitializeFrameworkResponse;
import com.secunet.ipsmall.ecard.StartPAOS;
import com.secunet.ipsmall.ecard.TransmitResponse;

public interface ITestSession {
    public String getSessionID();
    
    public byte[] getPSKKey();
    
    public ITestData getTestData();
    
    public byte[] getEphemeralPublicKey(boolean raw) throws Exception;
    
    public PrivateKey getEphemeralPrivateKey();
    
    public KeyPair getEphemeralKeyPair();
    
    public String getTCToken() throws Exception;
    
    public String getTCTokenValue(final String key) throws Exception;
    
    public void setStartPaos(StartPAOS startPAOS);
    
    public StartPAOS getStartPaos();
    
    // Step 0
    public String getInitializeFramework() throws Exception;
    
    public void setInitializeResponse(InitializeFrameworkResponse initializeResponse);
    
    public InitializeFrameworkResponse getInitializeResponse();
    
    // Step 1
    public String getDIDAuthenticate1() throws Exception;
    
    public void setDIDAuthenticate1Response(DIDAuthenticate1Response didAuthenticate1Response);
    
    public DIDAuthenticate1Response getDIDAuthenticate1Response();
    
    // Step 2
    public String getDIDAuthenticate2() throws Exception;
    
    public void setDIDAuthenticate2Response(DIDAuthenticate2Response didAuthenticate2Response);
    
    public DIDAuthenticate2Response getDIDAuthenticate2Response();
    
    // Step 3
    public String getDIDAuthenticate3() throws Exception;
    
    public byte[] getChallengeSiganture() throws Exception;
    
    public void setDIDAuthenticate3Response(DIDAuthenticate3Response didAuthenticate3Response);
    
    public DIDAuthenticate3Response getDIDAuthenticate3Response();
    
    // Step 4 Transmit
    public List<String> getTransmits();
    
    public List<TransmitResponse> getTransmitResponses();
    
    public String getTransmit(int number) throws Exception;
    
    public void addTransmitResponse(TransmitResponse transmitResponse);
    
    public String getStartPAOSResponse() throws Exception;
    
    public String getTransmitAPDU(TestApdu test) throws Exception;
    
}
