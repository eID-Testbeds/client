package com.secunet.ipsmall.eac.sm;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.util.Arrays;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import javax.smartcardio.CommandAPDU;

import com.secunet.ipsmall.eac.CADomainParameter;
import com.secunet.ipsmall.eac.CardSecurity;
import com.secunet.ipsmall.eac.EIDException;
import com.secunet.ipsmall.eac.cv.TLVObject;
import com.secunet.ipsmall.eac.sm.SecureMessaging.SymmetricCipher;
import com.secunet.ipsmall.log.IModuleLogger.LogLevel;
import com.secunet.ipsmall.log.Logger;
import com.secunet.ipsmall.test.ITestSession;
import com.secunet.ipsmall.util.Base64Util;

public class TestAPDUHandler {
    
    private CommandAPDU c_selectApplicationEID_APDU = new CommandAPDU(0x00, 0xa4, 0x04, 0x0C, new byte[] { (byte) 0xe8, 0x07, 0x04, 0x00, 0x7f, 0x00, 0x07,
            0x03, 0x02 });
    
    private CommandAPDU c_selectApplicationESIGN_APDU = new CommandAPDU(0x00, 0xa4, 0x04, 0x0C, new byte[] { (byte) 0xa0, 0x00, 0x00, 0x01, 0x67, 0x45, 0x53,
            0x49, 0x47, 0x4e });
    
    /** read dg1: document type */
    private CommandAPDU c_readDG1_APDU = new CommandAPDU(0x00, 0xb0, 0x80 | 0x01, 0x00, 0x100);
    /** read dg2: issuing state */
    private CommandAPDU c_readDG2_APDU = new CommandAPDU(0x00, 0xb0, 0x80 | 0x02, 0x00, 0x100);
    /** read dg3: date of expire */
    private CommandAPDU c_readDG3_APDU = new CommandAPDU(0x00, 0xb0, 0x80 | 0x03, 0x00, 0x100);
    /** read dg4: given names */
    private CommandAPDU c_readDG4_APDU = new CommandAPDU(0x00, 0xb0, 0x80 | 0x04, 0x00, 0x100);
    /** read dg5: family names */
    private CommandAPDU c_readDG5_APDU = new CommandAPDU(0x00, 0xb0, 0x80 | 0x05, 0x00, 0x100);
    /** read dg6: artistic name */
    private CommandAPDU c_readDG6_APDU = new CommandAPDU(0x00, 0xb0, 0x80 | 0x06, 0x00, 0x100);
    /** read dg7: academic title */
    private CommandAPDU c_readDG7_APDU = new CommandAPDU(0x00, 0xb0, 0x80 | 0x07, 0x00, 0x100);
    /** read dg8: date of birth */
    private CommandAPDU c_readDG8_APDU = new CommandAPDU(0x00, 0xb0, 0x80 | 0x08, 0x00, 0x100);
    /** read dg9: place of birth */
    private CommandAPDU c_readDG9_APDU = new CommandAPDU(0x00, 0xb0, 0x80 | 0x09, 0x00, 0x100);
    /** read dg10: nationality */
    private CommandAPDU c_readDG10_APDU = new CommandAPDU(0x00, 0xb0, 0x80 | 0x0a, 0x00, 0x100);
    /** read dg11: sex */
    private CommandAPDU c_readDG11_APDU = new CommandAPDU(0x00, 0xb0, 0x80 | 0x0b, 0x00, 0x100);
    /** read dg12: optional data */
    private CommandAPDU c_readDG12_APDU = new CommandAPDU(0x00, 0xb0, 0x80 | 0x0c, 0x00, 0x100);
    /** read dg13: -- */
    private CommandAPDU c_readDG13_APDU = new CommandAPDU(0x00, 0xb0, 0x80 | 0x0d, 0x00, 0x100);
    /** read dg14: -- */
    private CommandAPDU c_readDG14_APDU = new CommandAPDU(0x00, 0xb0, 0x80 | 0x0e, 0x00, 0x100);
    /** read dg15: -- */
    private CommandAPDU c_readDG15_APDU = new CommandAPDU(0x00, 0xb0, 0x80 | 0x0f, 0x00, 0x100);
    /** read dg16: -- */
    private CommandAPDU c_readDG16_APDU = new CommandAPDU(0x00, 0xb0, 0x80 | 0x10, 0x00, 0x100);
    /** read dg17: place of residence */
    private CommandAPDU c_readDG17_APDU = new CommandAPDU(0x00, 0xb0, 0x80 | 0x11, 0x00, 0x100);
    /** read dg18: community id */
    private CommandAPDU c_readDG18_APDU = new CommandAPDU(0x00, 0xb0, 0x80 | 0x12, 0x00, 0x100);
    /** read dg19: residence permit i */
    private CommandAPDU c_readDG19_APDU = new CommandAPDU(0x00, 0xb0, 0x80 | 0x13, 0x00, 0x100);
    /** read dg20: residence permit ii */
    private CommandAPDU c_readDG20_APDU = new CommandAPDU(0x00, 0xb0, 0x80 | 0x14, 0x00, 0x100);
    /** read dg21: optional data */
    private CommandAPDU c_readDG21_APDU = new CommandAPDU(0x00, 0xb0, 0x80 | 0x15, 0x00, 0x100);
    
    /** verify age */
    private CommandAPDU c_verifyAge_APDU = new CommandAPDU(0x80, 0x20, 0x80, 0x00, new byte[] { 0x06, 0x09, 0x04, 0x00, 0x7F, 0x00, 0x07, 0x03, 0x01, 0x04,
            0x01 });
    /** verify document validity */
    private CommandAPDU c_verifyDocumentValidity_APDU = new CommandAPDU(0x80, 0x20, 0x80, 0x00, new byte[] { 0x06, 0x09, 0x04, 0x00, 0x7F, 0x00, 0x07, 0x03,
            0x01, 0x04, 0x02 });
    /** verify community id */
    private CommandAPDU c_verifyCommunityID_APDU = new CommandAPDU(0x80, 0x20, 0x80, 0x00, new byte[] { 0x06, 0x09, 0x04, 0x00, 0x7F, 0x00, 0x07, 0x03, 0x01,
            0x04, 0x03 });
    
    /**
     * DefaultApdu: Read Binary from elementary file CardAccess; no body; result length 1 byte
     */
    private CommandAPDU c_readBinEFCardAccessAPDU = new CommandAPDU(0x00, 0xB0, 0x9C, 0x00, 0x01);
    
    public enum TestApdu {
        ReadBinEFCardAccess,
        SelectApplicationEID,
        SelectApplicationESIGN,
        ReadDG_1,
        ReadDG_2,
        ReadDG_3,
        ReadDG_4,
        ReadDG_5,
        ReadDG_6,
        ReadDG_7,
        ReadDG_8,
        ReadDG_9,
        ReadDG_10,
        ReadDG_11,
        ReadDG_12,
        ReadDG_13,
        ReadDG_14,
        ReadDG_15,
        ReadDG_16,
        ReadDG_17,
        ReadDG_18,
        ReadDG_19,
        ReadDG_20,
        ReadDG_21,
        UpdateDG_18,
        VerifyAge,
        VerifyDocumentValidity,
        VerifyCommunityID,
        RISetAT,
        RISetAT_AuthOnly,
        RIGeneralAuthenticate,
        GenSigKeys
        ;
        
        public static TestApdu getEnum(String value) {
            if (value == null)
                return null;
            TestApdu result = null;
            for (TestApdu cur : TestApdu.values()) {
                if (cur.name().toLowerCase().equals(value.toLowerCase())) {
                    return cur;
                }
            }
            
            return result;
        }
    }
    
    private ITestSession m_testSession;
    private SecureMessaging m_smContext = null;
    
    /** Handles the different APDUs and ueses cardencryption */
    public TestAPDUHandler(ITestSession testSession) {
        m_testSession = testSession;
    }
    
    /** must be called before any other methods */
    public void initialize() throws IOException, EIDException, InvalidKeySpecException, NoSuchAlgorithmException, NoSuchProviderException, InvalidKeyException {
        
        CardSecurity security;
        byte[] piccNonce;
        byte[] piccToken;
        if (m_testSession.getTestData().getECardDIDAuthenticate3Template() == null) {
            security = m_testSession.getDIDAuthenticate2Response().getCardSecurity(m_testSession);
            piccNonce = m_testSession.getDIDAuthenticate2Response().getNonce();
            piccToken = m_testSession.getDIDAuthenticate2Response().getAuthenticationToken();
        } else {
            security = m_testSession.getDIDAuthenticate3Response().getCardSecurity(m_testSession);
            piccNonce = m_testSession.getDIDAuthenticate3Response().getNonce();
            piccToken = m_testSession.getDIDAuthenticate3Response().getAuthenticationToken();
        }
        
        CADomainParameter domainParameter = security.getSecurityInfos().getDefaultCADomainParameter();
        PublicKey pkPICC = security.getPublicCAKey();
        
        FullElementKeyAgreement ka = new FullElementKeyAgreement(security.getCAAlgorithmType());
        ka.init(m_testSession.getEphemeralPrivateKey());
        ka.doPhase(pkPICC);
        
        byte[] sharedSecretK = ka.generateSecret();
        
        KeyDerivationFunction kdf = new KeyDerivationFunction();
        kdf.init(domainParameter.getProtocol(), sharedSecretK);
        
        byte[] encryptionKeyBytes = kdf.perform(1, piccNonce);
        byte[] macKeyBytes = kdf.perform(2, piccNonce);
        
        SecretKey encryptionKey = new SecretKeySpec(encryptionKeyBytes, domainParameter.getSymmetricCipher().toString());
        SecretKey macKey = new SecretKeySpec(macKeyBytes, domainParameter.getSymmetricCipher().toString());
        
        byte[] authToken = AuthenticationToken.calculate(macKey, m_testSession.getEphemeralKeyPair().getPublic(), domainParameter);
        
        
        
        if (!Arrays.equals(authToken, piccToken)) {
            String logMsg = "Auth Token does not match:" + System.lineSeparator();
            logMsg += "PCD Auth Token: " + Base64Util.encodeHEX(authToken) + System.lineSeparator();
            logMsg += "Picc Auth Token: " + Base64Util.encodeHEX(piccToken) + System.lineSeparator();
            Logger.EAC.logState(logMsg, LogLevel.Warn);
        } else {
            Logger.EAC.logState("--- Auth Token is OK");
        }
        
        // TODO hopefully we dont need DES
        m_smContext = new SecureMessaging(SymmetricCipher.AES, encryptionKey, macKey);
        
    }
    
    private byte[] getEncryptedAPDU(CommandAPDU plainapdu) throws EIDCryptoException {
        m_smContext.nextAPDU();
        return m_smContext.encrypt(plainapdu).getBytes();
    }
    
    private byte[] getEncryptedAPDU(CommandAPDU plainapdu, boolean dataBERTLVencoded) throws EIDCryptoException {
        m_smContext.nextAPDU();
        return m_smContext.encrypt(plainapdu, dataBERTLVencoded).getBytes();
    }
    
    /**
     * Creates and encryptes an apdu by their identifiers
     * 
     * @throws EIDCryptoException
     *             , IllegalArgumentException if TestApdu type not implemented
     */
    public byte[] getTestApdu(TestApdu test) throws EIDCryptoException {
        if (test == null)
            return null;
        
        byte[] apdu = null;
        boolean riAuthOnly = false;
        switch (test) {            
            case ReadBinEFCardAccess:
                apdu = getEncryptedAPDU(c_readBinEFCardAccessAPDU);
                break;
                
            case SelectApplicationEID:
                apdu = getEncryptedAPDU(c_selectApplicationEID_APDU);
                break;
                
            case SelectApplicationESIGN:
                apdu = getEncryptedAPDU(c_selectApplicationESIGN_APDU);
                break;

            case ReadDG_1:
                apdu = getEncryptedAPDU(c_readDG1_APDU);
                break;
                
            case ReadDG_2:
                apdu = getEncryptedAPDU(c_readDG2_APDU);
                break;
                
            case ReadDG_3:
                apdu = getEncryptedAPDU(c_readDG3_APDU);
                break;
                
            case ReadDG_4:
                apdu = getEncryptedAPDU(c_readDG4_APDU);
                break;
                
            case ReadDG_5:
                apdu = getEncryptedAPDU(c_readDG5_APDU);
                break;
                
            case ReadDG_6:
                apdu = getEncryptedAPDU(c_readDG6_APDU);
                break;
                
            case ReadDG_7:
                apdu = getEncryptedAPDU(c_readDG7_APDU);
                break;
                
            case ReadDG_8:
                apdu = getEncryptedAPDU(c_readDG8_APDU);
                break;
                
            case ReadDG_9:
                apdu = getEncryptedAPDU(c_readDG9_APDU);
                break;
                
            case ReadDG_10:
                apdu = getEncryptedAPDU(c_readDG10_APDU);
                break;
                
            case ReadDG_11:
                apdu = getEncryptedAPDU(c_readDG11_APDU);
                break;
                
            case ReadDG_12:
                apdu = getEncryptedAPDU(c_readDG12_APDU);
                break;
                
            case ReadDG_13:
                apdu = getEncryptedAPDU(c_readDG13_APDU);
                break;
                
            case ReadDG_14:
                apdu = getEncryptedAPDU(c_readDG14_APDU);
                break;
                
            case ReadDG_15:
                apdu = getEncryptedAPDU(c_readDG15_APDU);
                break;
                
            case ReadDG_16:
                apdu = getEncryptedAPDU(c_readDG16_APDU);
                break;
                
            case ReadDG_17:
                apdu = getEncryptedAPDU(c_readDG17_APDU);
                break;
                
            case ReadDG_18:
                apdu = getEncryptedAPDU(c_readDG18_APDU);
                break;
                
            case ReadDG_19:
                apdu = getEncryptedAPDU(c_readDG19_APDU);
                break;
                
            case ReadDG_20:
                apdu = getEncryptedAPDU(c_readDG20_APDU);
                break;
                
            case ReadDG_21:
                apdu = getEncryptedAPDU(c_readDG21_APDU);
                break;
                
            case UpdateDG_18:
                byte[] newDG18 = new byte[] { 0x03, 0x76, 0x11, 0x00, 0x00, 0x00, 0x00}; // new community id
                TLVObject tlv1 = new TLVObject(0x04, newDG18);
                TLVObject tlv2 = new TLVObject(0x72, tlv1.toBytes());
                CommandAPDU updateDG18_APDU = new CommandAPDU(0x00, 0xd6, 0x80 | 0x12, 0x00, tlv2.toBytes());
                apdu = getEncryptedAPDU(updateDG18_APDU);
                break;
                
            case VerifyAge:
                apdu = getEncryptedAPDU(c_verifyAge_APDU);
                break;
                
            case VerifyDocumentValidity:
                apdu = getEncryptedAPDU(c_verifyDocumentValidity_APDU);
                break;
                
            case VerifyCommunityID:
                apdu = getEncryptedAPDU(c_verifyCommunityID_APDU);
                break;
            
            case RISetAT_AuthOnly:
                riAuthOnly = true;
            case RISetAT:                
                byte[] keyReference = null;
                
                // read card security to get key id
                CardSecurity security;
                try {
                    // get card security only from EAC 3 message if 3 messages used
                    if (m_testSession.getTestData().getECardDIDAuthenticate3Template() != null)
                        security = m_testSession.getDIDAuthenticate3Response().getCardSecurity(m_testSession);
                    else
                        security = m_testSession.getDIDAuthenticate2Response().getCardSecurity(m_testSession);
                    
                    int keyID = security.getSecurityInfos().getDefaultRestrictedIdentificationInfo(riAuthOnly).getKeyId();
                    
                    keyReference = new byte[] { (byte)0x80, 0x0A, 0x04, 0x00, 0x7F, 0x00, 0x07, 0x02, 0x02, 0x05, 0x02, 0x03, (byte)0x84, 0x01, (byte)(keyID & 0xFF) };
                } catch (Exception e) {
                    Logger.EAC.logState("Failed perfoming RI: " + e.getMessage(), LogLevel.Error);
                }
                
                CommandAPDU riSetAT_APDU = new CommandAPDU(0x00, 0x22, 0x41, 0xA4, keyReference);
                apdu = getEncryptedAPDU(riSetAT_APDU);
                break;
                
            case RIGeneralAuthenticate:
                TLVObject termSectorPubKey = TLVObject.generateFromBytes(m_testSession.getTestData().getEIDServiceCV_TERM_SECTOR());
                
                // replace tag 0x7F49 with 0xA0 for first key
                TLVObject sectorKey = new TLVObject(0xA0, termSectorPubKey.value);
                
                // Dynamic Authentication data TLV object
                TLVObject dynamicAuthenticationData = new TLVObject(0x7C, sectorKey.toBytes());
                
                CommandAPDU riGeneralAuthenticate_APDU = new CommandAPDU(0x00, 0x86, 0x00, 0x00, dynamicAuthenticationData.toBytes(), 0xFFFF);                
                apdu = getEncryptedAPDU(riGeneralAuthenticate_APDU);
                break;
                
            case GenSigKeys:
                /*byte[] sigKeyRefData = new byte[] { (byte)0x81 };
                TLVObject sigKeyRef = new TLVObject(0x84, sigKeyRefData);
                TLVObject data = new TLVObject(0xb6, sigKeyRef.toBytes());
                byte[] rawData = data.toBytes()*/
                
                byte[] rawData = m_testSession.getTestData().getESignDST();
                
                CommandAPDU siGenAsymKeyPair_APDU = new CommandAPDU(0x00, 0x47, 0x82, 0x00, rawData, 0x10000);
                apdu = getEncryptedAPDU(siGenAsymKeyPair_APDU, true);
                break;
                
            default:
                throw new IllegalArgumentException("Unkown APDU identifier: " + test);
        }
        
        m_smContext.nextAPDU();
        
        return apdu;
    }
    
}
