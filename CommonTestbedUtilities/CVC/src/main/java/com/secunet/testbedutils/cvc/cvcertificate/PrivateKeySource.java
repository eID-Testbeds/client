package com.secunet.testbedutils.cvc.cvcertificate;

import java.math.BigInteger;
import java.security.PrivateKey;
import java.security.Signature;

import org.bouncycastle.asn1.ASN1InputStream;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;

import com.secunet.testbedutils.cvc.cvcertificate.exception.CVKeyTypeNotSupportedException;
import com.secunet.testbedutils.cvc.cvcertificate.exception.CVUnknownAlgorithmException;

/**
 * 
 * 
 * @author meier.marcus
 */
public class PrivateKeySource extends IPrivateKeySource {
    
    protected PrivateKey m_key = null;
    
    /**
     * 
     * @param key
     *            consigns the private key which will used to sign the data
     * @brief constructor
     * 
     * @param keyStore
     *            This parameter consigns a handle to a initialized keystore
     *            object
     * @param keyIdentifier
     *            This parameter consigns the key name
     * @param provider
     *            This parameter consigns the provider name of the key
     * @throws CVKeyTypeNotSupportedException
     */
    public PrivateKeySource(PrivateKey key) {
        
        m_key = key;
        
    }
    
    @Override
    public KeyType getKeyType() throws CVKeyTypeNotSupportedException {
        String strAlgo = m_key.getAlgorithm();
        
        if ("RSA".equals(strAlgo)) {
            return KeyType.KEY_RSA;
        } else if ("EC".equals(strAlgo) || "ECC".equals(strAlgo) || "ECDSA".equals(strAlgo)) {
            return KeyType.KEY_ECDSA;
        }
        
        throw new CVKeyTypeNotSupportedException();
    }
    
    @Override
    public void signContent(DataBuffer rContent, TAAlgorithm signGenOp, DataBuffer rSignature)
            throws CVKeyTypeNotSupportedException {
        Signature signature = null;
        try {
            
            switch (signGenOp) {
                case RSA_v1_5_SHA_1:
                    signature = Signature.getInstance("SHA1WithRSA", "BC");
                    break;
                case RSA_v1_5_SHA_256:
                    signature = Signature.getInstance("SHA256WithRSA", "BC");
                    break;
                case RSA_PSS_SHA_1:
                    signature = Signature.getInstance("SHA1withRSA/PSS", "BC");
                    break;
                case RSA_PSS_SHA_256:
                    signature = Signature.getInstance("SHA256withRSA/PSS", "BC");
                    break;
                case ECDSA_SHA_1:
                    signature = Signature.getInstance("SHA1withCVC-ECDSA", "BC");
                    break;
                case ECDSA_SHA_224:
                    signature = Signature.getInstance("SHA224withCVC-ECDSA", "BC");
                    break;
                case ECDSA_SHA_256:
                    signature = Signature.getInstance("SHA256withCVC-ECDSA", "BC");
                    break;
                case ECDSA_NONE:
                    signature = Signature.getInstance("NONEwithECDSA", "BC");
                    break;
                default:
                    // m_rLog << "unknown algorithm type" << std::endl;
                    throw new CVUnknownAlgorithmException();
            }
            
            signature.initSign(m_key);
            signature.update(rContent.toByteArray());
            byte[] sign = signature.sign();
            
            switch (signGenOp) {
                case ECDSA_SHA_1:
                case ECDSA_SHA_224:
                case ECDSA_SHA_256:
                case ECDSA_NONE:
                    DataBuffer plainSign = convertToPlainBC(sign);
                    rSignature.assign(plainSign);
                    break;
                default:
                    rSignature.assign(sign);
                    break;
            }
        } catch (Exception e) {
            throw new CVKeyTypeNotSupportedException(e);
        }
    }
    
    protected static DataBuffer convertToPlainBC(byte[] sign) {
        
        ASN1InputStream dIn = new ASN1InputStream(sign);
        ASN1Primitive obj;
        try {
            
            obj = dIn.readObject();
            
            if (obj instanceof ASN1Sequence) {
                ASN1Sequence seq = (ASN1Sequence) obj;
                if (seq.size() == 2) {
                    ASN1Integer r = (ASN1Integer) seq.getObjectAt(0);
                    ASN1Integer s = (ASN1Integer) seq.getObjectAt(1);
                    byte[] res;
                    byte[] byteR = makeUnsigned(r.getValue());
                    byte[] byteS = makeUnsigned(s.getValue());
                    
                    if (byteR.length > byteS.length) {
                        res = new byte[byteR.length * 2];
                    } else {
                        res = new byte[byteS.length * 2];
                    }
                    
                    System.arraycopy(byteR, 0, res, res.length / 2 - byteR.length, byteR.length);
                    System.arraycopy(byteS, 0, res, res.length - byteS.length, byteS.length);
                    
                    return new DataBuffer(res);
                }
            }
        } catch (Exception e) {
        } finally {
            try {
                dIn.close();
            } catch (Exception e) {
                // ???
            }
        }
        return new DataBuffer(sign);
    }
    
    private static byte[] makeUnsigned(BigInteger val) {
        byte[] res = val.toByteArray();
        
        if (res[0] == 0) {
            byte[] tmp = new byte[res.length - 1];
            
            System.arraycopy(res, 1, tmp, 0, tmp.length);
            
            return tmp;
        }
        
        return res;
    }
}
