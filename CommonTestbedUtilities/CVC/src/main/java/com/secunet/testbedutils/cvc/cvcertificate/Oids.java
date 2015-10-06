package com.secunet.testbedutils.cvc.cvcertificate;

import java.io.IOException;

import org.bouncycastle.asn1.ASN1InputStream;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;

/**
 * This class defines all OIDs which are used by the TR03111 specification
 * 
 * @author meier.marcus
 * 
 */
public class Oids {
    /**
     * BSI base OID
     */
    public static final byte OID_BSI_DE[] = { 0x04, 0x00, 0x7f, 0x00, 0x07 };
    /**
     * Base for the terminal authentication OID id-TA
     */
    public static final byte OID_TA[] = { 0x02, 0x02, 0x02 };
    /**
     * Terminal authentication signature algorithm
     * 
     * RSA v1.5 SHA 1 OID
     */
    public static final byte OID_RSA_v1_5_SHA_1[] = { 0x01, 0x01 };
    /**
     * Terminal authentication signature algorithm RSA v1.5 SHA 256 OID
     */
    public static final byte OID_RSA_v1_5_SHA_256[] = { 0x01, 0x02 };
    /**
     * Terminal authentication signature algorithm RSA PSS SHA 1 OID
     */
    public static final byte OID_RSA_PSS_SHA_1[] = { 0x01, 0x03 };
    /**
     * Terminal authentication signature algorithm RSA PSS SHA 256 OID
     */
    public static final byte OID_RSA_PSS_SHA_256[] = { 0x01, 0x04 };
    /**
     * Terminal authentication signature algorithm ECC SHA1 OID
     */
    public static final byte OID_ECDSA_SHA_1[] = { 0x02, 0x01 };
    /**
     * Terminal authentication signature algorithm ECC SHA224 OID
     */
    public static final byte OID_ECDSA_SHA_224[] = { 0x02, 0x02 };
    /**
     * Terminal authentication signature algorithm ECC SHA256 OID
     */
    public static final byte OID_ECDSA_SHA_256[] = { 0x02, 0x03 };
    
    /**
     * OID for the certificate holder terminal role id-roles
     */
    public static final byte OID_TERM_ROLE[] = { 0x03, 0x01, 0x02 };
    /**
     * OID for the inspection system
     */
    public static final byte OID_IS_TERMINAL[] = { 0x01 };
    /**
     * OID for the authentication terminal
     */
    public static final byte OID_AT_TERMINAL[] = { 0x02 };
    /**
     * OID for the signature terminal
     */
    public static final byte OID_ST_TERMINAL[] = { 0x03 };
    /**
     * Base OId for the certificate extension
     */
    public static final byte OID_BASE_EXTENSION[] = { 0x03, 0x01, 0x03 };
    /**
     * OID part for the certificate description extension
     */
    public static final byte OID_EXT_DESCRIPTION[] = { 0x01 };
    /**
     * OID part for the terminal sector extension
     */
    public static final byte OID_EXT_SECTOR[] = { 0x02 };
    /**
     * OID part for the plain certificate description format
     */
    public static final byte OID_EXT_DESC_PLAIN[] = { 0x01 };
    /**
     * OID part for the HTML certificate description format
     */
    public static final byte OID_EXT_DESC_HTML[] = { 0x02 };
    /**
     * OID part for the PDF certificate description format
     */
    public static final byte OID_EXT_DESC_PDF[] = { 0x03 };
    /**
     * OID base for the restricted identification algorithms
     */
    public static final byte OID_RI_BASE[] = { 0x02, 0x02, 0x05 };
    /**
     * RI Algorithm Diffie-Hellman with SHA1
     */
    public static final byte OID_RI_DH_SHA1[] = { 0x01, 0x01 };
    /**
     * RI Algorithm Diffie-Hellman with SHA224
     */
    public static final byte OID_RI_DH_SHA224[] = { 0x01, 0x02 };
    /**
     * RI Algorithm Diffie-Hellman with SHA256
     */
    public static final byte OID_RI_DH_SHA256[] = { 0x01, 0x03 };
    /**
     * RI Algorithm Diffie-Hellman with SHA384
     */
    public static final byte OID_RI_DH_SHA384[] = { 0x01, 0x04 };
    /**
     * RI Algorithm Diffie-Hellman with SHA512
     */
    public static final byte OID_RI_DH_SHA512[] = { 0x01, 0x05 };
    /**
     * RI Algorithm Diffie-Hellman with EC keys and SHA1
     */
    public static final byte OID_RI_ECDH_SHA1[] = { 0x02, 0x01 };
    /**
     * RI Algorithm Diffie-Hellman with EC keys and SHA224
     */
    public static final byte OID_RI_ECDH_SHA224[] = { 0x02, 0x02 };
    /**
     * RI Algorithm Diffie-Hellman with EC keys and SHA256
     */
    public static final byte OID_RI_ECDH_SHA256[] = { 0x02, 0x03 };
    /**
     * RI Algorithm Diffie-Hellman with EC keys and SHA384
     */
    public static final byte OID_RI_ECDH_SHA384[] = { 0x02, 0x04 };
    /**
     * RI Algorithm Diffie-Hellman with EC keys and SHA512
     */
    public static final byte OID_RI_ECDH_SHA512[] = { 0x02, 0x05 };
    
    /**
     * @brief concatenation of byte arrays
     * 
     * @param part1
     * @param part2
     * @return returns the concatenation as DataBuffer
     */
    public static DataBuffer concat(byte[] part1, byte[] part2) {
        DataBuffer out = new DataBuffer();
        out.append(part1);
        out.append(part2);
        return out;
    }
    
    /**
     * @brief concatenation of byte arrays
     * 
     * @param part1
     * @param part2
     * @param part3
     * @return returns the concatenation as DataBuffer
     */
    public static DataBuffer concat(byte[] part1, byte[] part2, byte[] part3) {
        DataBuffer out = new DataBuffer();
        out.append(part1);
        out.append(part2);
        out.append(part3);
        return out;
    }
    
    /**
     * @brief concatenation of byte arrays
     * 
     * @param part1
     * @param part2
     * @param part3
     * @param part4
     * @return returns the concatenation as DataBuffer
     */
    public static DataBuffer concat(byte[] part1, byte[] part2, byte[] part3, byte[] part4) {
        DataBuffer out = new DataBuffer();
        out.append(part1);
        out.append(part2);
        out.append(part3);
        out.append(part4);
        return out;
    }
    
    public static String getStringOid(DataBuffer buffer) {
        // terrible code, due to changes in BC's API
        // buffer should only contain an OID value, i.e. 0x06 tag and length
        // field are missing
        byte[] tagBuf = { (byte) 0x06 };
        byte[] sizeBuf = new byte[] { (byte) buffer.size() };
        DataBuffer tmp = concat(tagBuf, sizeBuf, buffer.toByteArray());
        ASN1InputStream aIn = new ASN1InputStream(tmp.toByteArray());
        ASN1ObjectIdentifier o = null;
        try {
            o = (ASN1ObjectIdentifier) aIn.readObject();
        } catch (IOException e) {
        } finally {
            try {
                aIn.close();
            } catch (Exception e) {
                // ???
            }
        }
        
        return o.getId();
    }
}
