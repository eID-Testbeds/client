package com.secunet.testbedutils.cvc.cvcertificate;

import java.math.BigInteger;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PublicKey;
import java.security.Signature;
import java.security.SignatureException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.RSAPublicKeySpec;

import org.bouncycastle.jce.spec.ECParameterSpec;
import org.bouncycastle.jce.spec.ECPublicKeySpec;
import org.bouncycastle.math.ec.ECCurve;
import org.bouncycastle.math.ec.ECPoint;

import com.secunet.testbedutils.cvc.cvcertificate.exception.CVBufferNotEmptyException;
import com.secunet.testbedutils.cvc.cvcertificate.exception.CVDecodeErrorException;
import com.secunet.testbedutils.cvc.cvcertificate.exception.CVInvalidECPointLengthException;
import com.secunet.testbedutils.cvc.cvcertificate.exception.CVInvalidKeySourceException;
import com.secunet.testbedutils.cvc.cvcertificate.exception.CVInvalidOidException;
import com.secunet.testbedutils.cvc.cvcertificate.exception.CVKeyTypeNotSupportedException;
import com.secunet.testbedutils.cvc.cvcertificate.exception.CVMissingKeyException;
import com.secunet.testbedutils.cvc.cvcertificate.exception.CVSignOpKeyMismatchException;
import com.secunet.testbedutils.cvc.cvcertificate.exception.CVTagNotFoundException;
import com.secunet.testbedutils.cvc.cvcertificate.exception.CVUnknownAlgorithmException;
import com.secunet.testbedutils.cvc.cvcertificate.exception.CVUnknownCryptoProviderException;

/**
 * @class CCVPubKeyHolder
 * @brief This class load, store and generate the public key for a CV
 *        certificate
 * 
 * @author meier.marcus
 * @version 1.0
 * @created 27-Aug-2009 14:18:57
 */
public class CVPubKeyHolder {
    
    /**
     * < this member controls the including of the ECDSA domain parameters in
     * the public key
     */
    protected boolean m_bIncludeECDSADomainParam;
    /**
     * < this member stores the public point of a ECDSA key
     */
    protected ECPubPoint m_ECPubPoint;
    /**
     * < this member stores the domain parameter of a ECDSA key
     */
    protected ECParameterSpec m_ECDomain;
    /**
     * < this member stores the RSA key
     */
    protected RSAPublicKeySpec m_RSAKey;
    /**
     * < This member holds the algorithm type for the signature generation
     */
    protected TAAlgorithm m_algorithmType;
    /**
     * < This member stores a pointer to an KeySource object
     */
    protected IPublicKeySource m_KeySource = null;
    
    /**
     * 
     * @param rLog
     */
    public CVPubKeyHolder() {
        m_bIncludeECDSADomainParam = false;
        m_ECPubPoint = null;
        m_ECDomain = null;
        m_RSAKey = null;
        m_algorithmType = TAAlgorithm.UNDEFINED;
    }
    
    /**
     * This function generate a DataBuffer with the public key and the
     * information about the signature algorithm
     * 
     * @return the certificate as raw buffer
     * @throws CVSignOpKeyMismatchException
     * @throws CVInvalidKeySourceException
     * @throws CVMissingKeyException
     * @throws CVKeyTypeNotSupportedException
     */
    public DataBuffer generateCertPubKey() throws CVSignOpKeyMismatchException, CVInvalidKeySourceException,
            CVMissingKeyException, CVKeyTypeNotSupportedException {
        DataBuffer out = new DataBuffer();
        
        // insert terminal authentication signature algorithm (HASH) OID
        switch (m_algorithmType) {
            case RSA_v1_5_SHA_1:
                TLV.append(out, CVCertificate.s_CvOIDTag,
                        Oids.concat(Oids.OID_BSI_DE, Oids.OID_TA, Oids.OID_RSA_v1_5_SHA_1));
                break;
            case RSA_v1_5_SHA_256:
                TLV.append(out, CVCertificate.s_CvOIDTag,
                        Oids.concat(Oids.OID_BSI_DE, Oids.OID_TA, Oids.OID_RSA_v1_5_SHA_256));
                break;
            case RSA_PSS_SHA_1:
                TLV.append(out, CVCertificate.s_CvOIDTag,
                        Oids.concat(Oids.OID_BSI_DE, Oids.OID_TA, Oids.OID_RSA_PSS_SHA_1));
                break;
            case RSA_PSS_SHA_256:
                TLV.append(out, CVCertificate.s_CvOIDTag,
                        Oids.concat(Oids.OID_BSI_DE, Oids.OID_TA, Oids.OID_RSA_PSS_SHA_256));
                break;
            case ECDSA_SHA_1:
                TLV.append(out, CVCertificate.s_CvOIDTag,
                        Oids.concat(Oids.OID_BSI_DE, Oids.OID_TA, Oids.OID_ECDSA_SHA_1));
                break;
            case ECDSA_SHA_224:
                TLV.append(out, CVCertificate.s_CvOIDTag,
                        Oids.concat(Oids.OID_BSI_DE, Oids.OID_TA, Oids.OID_ECDSA_SHA_224));
                break;
            case ECDSA_SHA_256:
                TLV.append(out, CVCertificate.s_CvOIDTag,
                        Oids.concat(Oids.OID_BSI_DE, Oids.OID_TA, Oids.OID_ECDSA_SHA_256));
                break;
            default:
                // Do nothing; return empty buffer
                break;
        }
        
        // now include the key depend on his type
        if (m_algorithmType == TAAlgorithm.RSA_PSS_SHA_1 || m_algorithmType == TAAlgorithm.RSA_PSS_SHA_256
                || m_algorithmType == TAAlgorithm.RSA_v1_5_SHA_1 || m_algorithmType == TAAlgorithm.RSA_v1_5_SHA_256) {
            // check whether this key is loaded
            if (m_RSAKey == null && m_ECPubPoint == null) {
                loadKeyFromKeySource();
            }
            // check whether this chosen sign algorithm and the loaded key
            // matches
            if (m_RSAKey == null) {
                throw new CVSignOpKeyMismatchException();
            }
            
            out.append(generateCertPubKeyRSA());
        } else if (m_algorithmType == TAAlgorithm.ECDSA_SHA_1 || m_algorithmType == TAAlgorithm.ECDSA_SHA_224
                || m_algorithmType == TAAlgorithm.ECDSA_SHA_256) {
            // check whether this key is loaded
            if (m_RSAKey == null && m_ECPubPoint == null) {
                loadKeyFromKeySource();
            }
            // check whether this chosen sign algorithm and the loaded key
            // matches
            if (m_ECPubPoint == null) {
                throw new CVSignOpKeyMismatchException();
            }
            
            out.append(generateCertPubKeyECDSA());
        }
        
        return out;
    }
    
    /**
     * @brief This function set the member variable m_bIncludeECDSADomainParam
     * 
     * @param value
     */
    public void setIncludeDomainParam(boolean value) {
        m_bIncludeECDSADomainParam = value;
    }
    
    /**
     * @brief This function get the member variable m_bIncludeECDSADomainParam
     * 
     * @return returns whether this object knows the domain parameter
     */
    public boolean getIncludeDomainParam() {
        return m_bIncludeECDSADomainParam;
    }
    
    /**
     * This function decode a public key data stream of a certificate
     * 
     * @param buffer
     * @throws CVDecodeErrorException
     * @throws CVTagNotFoundException
     * @throws CVInvalidOidException
     * @throws CVBufferNotEmptyException
     * @throws CVInvalidECPointLengthException
     */
    public void parseRawKey(DataBuffer buffer) throws CVDecodeErrorException, CVTagNotFoundException,
            CVInvalidOidException, CVBufferNotEmptyException, CVInvalidECPointLengthException {
        
        DataBuffer data = new DataBuffer(buffer);
        
        // decode the OID
        TLV extrOID = TLV.extract(data);
        
        if (extrOID.getTag() != CVCertificate.s_CvOIDTag) {
            // m_rLog << "0x06 OID Tag not found" << std::endl;
            throw new CVTagNotFoundException("0x06");
        }
        
        // Test the OID
        // decode the right signature algorithm
        if (extrOID.getValue().equals(Oids.concat(Oids.OID_BSI_DE, Oids.OID_TA, Oids.OID_RSA_v1_5_SHA_1))) {
            m_algorithmType = TAAlgorithm.RSA_v1_5_SHA_1;
        } else if (extrOID.getValue().equals(Oids.concat(Oids.OID_BSI_DE, Oids.OID_TA, Oids.OID_RSA_v1_5_SHA_256))) {
            m_algorithmType = TAAlgorithm.RSA_v1_5_SHA_256;
        } else if (extrOID.getValue().equals(Oids.concat(Oids.OID_BSI_DE, Oids.OID_TA, Oids.OID_RSA_PSS_SHA_1))) {
            m_algorithmType = TAAlgorithm.RSA_PSS_SHA_1;
        } else if (extrOID.getValue().equals(Oids.concat(Oids.OID_BSI_DE, Oids.OID_TA, Oids.OID_RSA_PSS_SHA_256))) {
            m_algorithmType = TAAlgorithm.RSA_PSS_SHA_256;
        } else if (extrOID.getValue().equals(Oids.concat(Oids.OID_BSI_DE, Oids.OID_TA, Oids.OID_ECDSA_SHA_1))) {
            m_algorithmType = TAAlgorithm.ECDSA_SHA_1;
        } else if (extrOID.getValue().equals(Oids.concat(Oids.OID_BSI_DE, Oids.OID_TA, Oids.OID_ECDSA_SHA_224))) {
            m_algorithmType = TAAlgorithm.ECDSA_SHA_224;
        } else if (extrOID.getValue().equals(Oids.concat(Oids.OID_BSI_DE, Oids.OID_TA, Oids.OID_ECDSA_SHA_256))) {
            m_algorithmType = TAAlgorithm.ECDSA_SHA_256;
        } else {
            // m_rLog << "Error: Defined signature algorithm is invalid" <<
            // std::endl;
            throw new CVInvalidOidException();
        }
        
        // decode the key depend on the signature algorithm
        if (m_algorithmType == TAAlgorithm.RSA_PSS_SHA_1 || m_algorithmType == TAAlgorithm.RSA_PSS_SHA_256
                || m_algorithmType == TAAlgorithm.RSA_v1_5_SHA_1 || m_algorithmType == TAAlgorithm.RSA_v1_5_SHA_256) { // RSA
                                                                                                                       // KEy
            
            // Modulus
            TLV extrMod = TLV.extract(data);
            
            if (extrMod.getTag() != CVCertificate.s_CvRSAModulusTag) {
                // m_rLog << "Error: Tag 0x81 not found. Found tag "<< std::hex
                // << std::showbase << tag << std::endl;
                throw new CVTagNotFoundException("0x81");
            }
            
            BigInteger modulus = new BigInteger(1, extrMod.getValue().toByteArray());
            
            // Exponent
            TLV extrExpo = TLV.extract(data);
            if (extrExpo.getTag() != CVCertificate.s_CvRSAExponentTag) {
                // m_rLog << "Error: Tag 0x82 not found. Found tag "<< std::hex
                // << std::showbase << tag << std::endl;
                throw new CVTagNotFoundException("0x82");
            }
            
            BigInteger exponent = new BigInteger(1, extrExpo.getValue().toByteArray());
            // store the key
            m_RSAKey = new RSAPublicKeySpec(modulus, exponent);
            
            // Delete the EC key if available
            if (m_ECDomain != null) {
                m_ECDomain = null;
            }
            if (m_ECPubPoint != null) {
                m_ECPubPoint = null;
            }
        } else if (m_algorithmType == TAAlgorithm.ECDSA_SHA_1 || m_algorithmType == TAAlgorithm.ECDSA_SHA_224
                || m_algorithmType == TAAlgorithm.ECDSA_SHA_256) { // ECDSA Key
        
            TLV extrPrime = TLV.extract(data);
            // check whether or not the EC domain parameter is available
            if (extrPrime.getTag() == CVCertificate.s_CvECPrimeModTag) {
                // m_rLog << "Domain parameterspresent" << std::endl;
                m_bIncludeECDSADomainParam = true;
                
                // parse prime modulus
                BigInteger p = new BigInteger(1, extrPrime.getValue().toByteArray());
                
                // parse first coefficient
                TLV extrFirstCo = TLV.extract(data);
                
                if (extrFirstCo.getTag() != CVCertificate.s_CvECFirstCoeffTag) {
                    
                    // m_rLog << "Error: Tag 0x82 not found. Found tag "<<
                    // std::hex << std::showbase << tag << std::endl;
                    throw new CVTagNotFoundException("0x82");
                }
                
                BigInteger a = new BigInteger(1, extrFirstCo.getValue().toByteArray());
                
                // parse second coefficient
                TLV extrSecCo = TLV.extract(data);
                if (extrSecCo.getTag() != CVCertificate.s_CvECSecondCoeffTag) {
                    // m_rLog << "Error: Tag 0x83 not found. Found tag "<<
                    // std::hex << std::showbase << tag << std::endl;
                    throw new CVTagNotFoundException("0x83");
                }
                
                BigInteger b = new BigInteger(1, extrSecCo.getValue().toByteArray());
                
                // parse base point G
                TLV extrBasePoint = TLV.extract(data);
                if (extrBasePoint.getTag() != CVCertificate.s_CvECBasePointTag) {
                    // m_rLog << "Error: Tag 0x84 not found. Found tag "<<
                    // std::hex << std::showbase << tag << std::endl;
                    throw new CVTagNotFoundException("0x84");
                }
                // tag for uncompressed ec point
                if (extrBasePoint.getValue().get(0) != CVCertificate.s_CvUnCompressedTag) {
                    
                    // m_rLog <<
                    // "Error: Tag 0x04 not found. Elliptic point G not uncompressed"
                    // << std::endl;
                    throw new CVTagNotFoundException("0x04");
                }
                // delete tag
                extrBasePoint.getValue().erase(0, 1);
                
                if (extrBasePoint.getValue().size() % 2 != 0) {
                    // m_rLog << "Error: The point length is odd" << std::endl;
                    throw new CVInvalidECPointLengthException();
                }
                
                BigInteger Gx = new BigInteger(1, extrBasePoint.getValue()
                        .substr(0, extrBasePoint.getValue().size() / 2).toByteArray());
                BigInteger Gy = new BigInteger(1, extrBasePoint.getValue().substr(extrBasePoint.getValue().size() / 2)
                        .toByteArray());
                
                // parse order of the base point r
                TLV extrOrder = TLV.extract(data);
                
                if (extrOrder.getTag() != CVCertificate.s_CvECOrderBasePointTag) {
                    
                    // m_rLog << "Error: Tag 0x85 not found. Found tag "<<
                    // std::hex << std::showbase << tag << std::endl;
                    throw new CVTagNotFoundException("0x85");
                }
                
                BigInteger r = new BigInteger(1, extrOrder.getValue().toByteArray());
                
                // parse public point
                TLV extrPubPoint = TLV.extract(data);
                if (extrPubPoint.getTag() != CVCertificate.s_CvECPubPointTag) {
                    
                    // m_rLog << "Error: Tag 0x86 not found. Found tag "<<
                    // std::hex << std::showbase << tag << std::endl;
                    throw new CVTagNotFoundException("0x86");
                }
                // tag for uncompressed ec point
                if (extrPubPoint.getValue().get(0) != CVCertificate.s_CvUnCompressedTag) {
                    
                    // m_rLog <<
                    // "Error: Tag 0x04 not found. Elliptic point G not uncompressed"
                    // << std::endl;
                    throw new CVTagNotFoundException("0x04");
                }
                // delete tag
                extrPubPoint.getValue().erase(0, 1);
                
                if (extrPubPoint.getValue().size() % 2 != 0) {
                    // m_rLog << "Error: The point length is odd" << std::endl;
                    throw new CVInvalidECPointLengthException();
                }
                // split the byte chain at the half
                // The first half is the x coordinate and the second half is the
                // y coordinate of the public point
                BigInteger Yx = new BigInteger(1, extrPubPoint.getValue().substr(0, extrPubPoint.getValue().size() / 2)
                        .toByteArray());
                BigInteger Yy = new BigInteger(1, extrPubPoint.getValue().substr(extrPubPoint.getValue().size() / 2)
                        .toByteArray());
                
                // parse cofactor
                TLV extrCofactor = TLV.extract(data);
                
                if (extrCofactor.getTag() != CVCertificate.s_CvECCofactorTag) {
                    // m_rLog << "Error: Tag 0x87 not found. Found tag "<<
                    // std::hex << std::showbase << tag << std::endl;
                    throw new CVTagNotFoundException("0x87");
                }
                BigInteger f = new BigInteger(1, extrCofactor.getValue().toByteArray());
                
                // Now store it
                ECCurve curve = new ECCurve.Fp(p, a, b);
                m_ECDomain = new ECParameterSpec(curve, curve.createPoint(Gx, Gy), r, f);
                m_ECPubPoint = new ECPubPoint(Yx, Yy);
                
                // Delete the RSA key if available
                m_RSAKey = null;
                
            } else if (extrPrime.getTag() == CVCertificate.s_CvECPubPointTag) {
                // Only the public point is present
                // m_rLog << "Only public point present" << std::endl;
                m_bIncludeECDSADomainParam = false;
                // Check whether this key is uncompressed
                if (extrPrime.getValue().get(0) != CVCertificate.s_CvUnCompressedTag) {
                    // m_rLog <<
                    // "Error: Tag 0x04 not found. Elliptic point G not uncompressed or tag unknown"
                    // << std::endl;
                    throw new CVTagNotFoundException("0x04");
                }
                // delete tag
                extrPrime.getValue().erase(0, 1);
                // check whether or not the byte length has a even value
                if (extrPrime.getValue().size() % 2 != 0) {
                    // m_rLog << "Error: The point length is odd" << std::endl;
                    throw new CVInvalidECPointLengthException();
                }
                // split the byte chain at the half
                // The first half is the x coordinate and the second half is the
                // y coordinate of the public point
                BigInteger Yx = new BigInteger(1, extrPrime.getValue().substr(0, extrPrime.getValue().size() / 2)
                        .toByteArray());
                BigInteger Yy = new BigInteger(1, extrPrime.getValue().substr(extrPrime.getValue().size() / 2)
                        .toByteArray());
                
                // Store the public point
                
                m_ECPubPoint = new ECPubPoint(Yx, Yy);
                
                // cleanup all other information
                
                m_ECDomain = null;
                m_RSAKey = null;
            } else {
                // m_rLog << "Tag 0x81 or 0x86 not found. Found tag "<< std::hex
                // << std::showbase << tag << std::endl;
                throw new CVTagNotFoundException("0x81 or 0x86");
            }
            
        }
        
        if (data.size() > 0) {
            // m_rLog << "Error: " << (unsigned int)data.size() <<
            // " bytes left in buffer" << std::endl;
            throw new CVBufferNotEmptyException();
        }
    }
    
    /**
     * This function returns the ec public point of this object
     * 
     * @return returns the EC public point
     * @throws CVInvalidKeySourceException
     * @throws CVMissingKeyException
     * @throws CVKeyTypeNotSupportedException
     */
    public ECPubPoint getECPublicPoint() throws CVInvalidKeySourceException, CVMissingKeyException,
            CVKeyTypeNotSupportedException {
        // load the key from key source if a key is not available
        if (m_RSAKey == null && m_ECPubPoint == null) {
            loadKeyFromKeySource();
        }
        
        if (m_ECPubPoint == null) {
            throw new CVMissingKeyException();
        }
        return m_ECPubPoint;
    }
    
    /**
     * This function set only the ec public point for this key
     * 
     * @param point
     */
    public void setECPublicPoint(ECPubPoint point) {
        m_ECPubPoint = point;
        m_RSAKey = null;
    }
    
    /**
     * This function returns whether or not the EC domain param is present
     * 
     * @return returns whether this class knows the domain parameter
     * @throws CVInvalidKeySourceException
     * @throws CVKeyTypeNotSupportedException
     */
    public boolean isDomainParamPresent() throws CVInvalidKeySourceException, CVKeyTypeNotSupportedException {
        // load the key from key source if a key is not available
        if (m_RSAKey == null && m_ECPubPoint == null && m_ECDomain == null) {
            loadKeyFromKeySource();
        }
        
        if (m_ECDomain != null) {
            return true;
        }
        
        return false;
    }
    
    /**
     * This function returns the length of the loaded key
     * 
     * @return returns the key length
     * @throws CVInvalidKeySourceException
     * @throws CVKeyTypeNotSupportedException
     */
    public int getKeyLength() throws CVInvalidKeySourceException, CVKeyTypeNotSupportedException {
        // load the key from key source if a key is not available
        if (m_RSAKey == null && m_ECPubPoint == null) {
            loadKeyFromKeySource();
        }
        // calculate the bit length
        if (m_RSAKey != null) {
            return m_RSAKey.getModulus().bitLength();
        } else if (m_ECPubPoint != null) {
            return m_ECPubPoint.getX().bitLength();
        }
        
        return 0;
    }
    
    /**
     * This function returns the ecdsa domain param
     * 
     * @return the domain parameter of this object
     * @throws CVInvalidKeySourceException
     * @throws CVMissingKeyException
     * @throws CVKeyTypeNotSupportedException
     */
    public ECParameterSpec getDomainParam() throws CVInvalidKeySourceException, CVMissingKeyException,
            CVKeyTypeNotSupportedException {
        // load the domain parameter from key source if a key is not available
        if (m_RSAKey == null && m_ECPubPoint == null && m_ECDomain == null) {
            loadKeyFromKeySource();
        }
        
        if (m_ECDomain == null) {
            // m_rLog << "this object doesn't know the domain param" <<
            // std::endl;
            throw new CVMissingKeyException();
        }
        return m_ECDomain;
    }
    
    /**
     * This function set the domain param
     * 
     * @param dp
     */
    public void setDomainParam(ECParameterSpec dp) {
        m_ECDomain = dp;
    }
    
    /**
     * This function, examines the signature with the public key of this class
     * 
     * @param content
     *            consigns the plain content
     * @param sign
     *            consigns the signature
     * @return returns true if the signature is valid else false
     * @throws CVInvalidKeySourceException
     * @throws CVMissingKeyException
     * @throws UnknownAlgorithmException
     * @throws UnknownCryptoProviderException
     * @throws NoSuchProviderException
     * @throws NoSuchAlgorithmException
     * @throws InvalidKeyException
     * @throws SignatureException
     * @throws InvalidKeySpecException
     * @throws CVKeyTypeNotSupportedException
     */
    public boolean checkSign(DataBuffer content, DataBuffer sign) throws CVInvalidKeySourceException,
            CVMissingKeyException, CVUnknownAlgorithmException, NoSuchAlgorithmException, NoSuchProviderException,
            CVUnknownCryptoProviderException, InvalidKeyException, SignatureException, InvalidKeySpecException,
            CVKeyTypeNotSupportedException {
        boolean result = false;
        
        Signature sig = null;
        // Initialize the signature check depended on the chosen signature
        // algorithm
        switch (m_algorithmType) {
            case RSA_v1_5_SHA_1:
                sig = Signature.getInstance("SHA1WithRSA", "BC");
                break;
            case RSA_v1_5_SHA_256:
                sig = Signature.getInstance("SHA256WithRSA", "BC");
                break;
            case RSA_PSS_SHA_1:
                sig = Signature.getInstance("SHA1withRSA/PSS", "BC");
                break;
            case RSA_PSS_SHA_256:
                sig = Signature.getInstance("SHA256withRSA/PSS", "BC");
                break;
            case ECDSA_SHA_1:
                sig = Signature.getInstance("SHA1withCVC-ECDSA", "BC");
                break;
            case ECDSA_SHA_224:
                sig = Signature.getInstance("SHA224withCVC-ECDSA", "BC");
                break;
            case ECDSA_SHA_256:
                sig = Signature.getInstance("SHA256withCVC-ECDSA", "BC");
                break;
            default:
                // m_rLog << "unknown algorithm type" << std::endl;
                throw new CVUnknownAlgorithmException();
        }
        
        // Rebuild a keysource for cryptopp as bytequeue
        if (m_algorithmType == TAAlgorithm.ECDSA_SHA_1 || m_algorithmType == TAAlgorithm.ECDSA_SHA_224
                || m_algorithmType == TAAlgorithm.ECDSA_SHA_256) {
            // test whether or not all EC key components are available
            if (!isDomainParamPresent()) {
                // m_rLog << "Domain parameter not present" << std::endl;
                throw new CVMissingKeyException();
            }
            
            if (m_ECPubPoint == null) {
                // m_rLog << "Public Point not present" << std::endl;
                throw new CVMissingKeyException();
            }
            
            ECPoint pubPoint = m_ECDomain.getCurve().createPoint(m_ECPubPoint.getX(), m_ECPubPoint.getY());
            ECPublicKeySpec pubKeySpec = new ECPublicKeySpec(pubPoint, m_ECDomain);
            
            KeyFactory keyFactory = KeyFactory.getInstance("EC", "BC");
            PublicKey pubKey = keyFactory.generatePublic(pubKeySpec);
            
            sig.initVerify(pubKey);
            
        } else {
            // test whether or not all EC key components are available
            if (m_RSAKey == null) {
                throw new CVMissingKeyException();
            }
            
            KeyFactory keyFactory = KeyFactory.getInstance("RSA", "BC");
            PublicKey pubKey = keyFactory.generatePublic(m_RSAKey);
            sig.initVerify(pubKey);
        }
        
        sig.update(content.toByteArray());
        result = sig.verify(sign.toByteArray());
        
        // return the result
        return result;
    }
    
    /**
     * Gets public key.
     * 
     * @return
     * @throws CVInvalidKeySourceException
     * @throws CVKeyTypeNotSupportedException
     * @throws CVMissingKeyException
     * @throws NoSuchAlgorithmException
     * @throws NoSuchProviderException
     * @throws InvalidKeySpecException
     */
    public PublicKey getPublicKey() throws CVInvalidKeySourceException, CVKeyTypeNotSupportedException,
            CVMissingKeyException, NoSuchAlgorithmException, NoSuchProviderException, InvalidKeySpecException {
        PublicKey pubKey = null;
        
        if (m_algorithmType == TAAlgorithm.ECDSA_SHA_1 || m_algorithmType == TAAlgorithm.ECDSA_SHA_224
                || m_algorithmType == TAAlgorithm.ECDSA_SHA_256) {
            // test whether or not all EC key components are available
            if (!isDomainParamPresent()) {
                // m_rLog << "Domain parameter not present" << std::endl;
                throw new CVMissingKeyException();
            }
            
            if (m_ECPubPoint == null) {
                // m_rLog << "Public Point not present" << std::endl;
                throw new CVMissingKeyException();
            }
            
            ECPoint pubPoint = m_ECDomain.getCurve().createPoint(m_ECPubPoint.getX(), m_ECPubPoint.getY());
            ECPublicKeySpec pubKeySpec = new ECPublicKeySpec(pubPoint, m_ECDomain);
            
            KeyFactory keyFactory = KeyFactory.getInstance("EC", "BC");
            pubKey = keyFactory.generatePublic(pubKeySpec);
        } else {
            // test whether or not all EC key components are available
            if (m_RSAKey == null) {
                throw new CVMissingKeyException();
            }
            
            KeyFactory keyFactory = KeyFactory.getInstance("RSA", "BC");
            pubKey = keyFactory.generatePublic(m_RSAKey);
        }
        
        return pubKey;
    }
    
    /**
     * This function set the rsa key
     * 
     * @param key
     */
    public void setRSAKey(RSAPublicKeySpec key) {
        
        m_RSAKey = key;
        
        // Delete the EC key, if available
        if (m_ECPubPoint != null) {
            m_ECPubPoint = null;
        }
        if (m_ECDomain != null) {
            m_ECDomain = null;
        }
    }
    
    /**
     * This function returns the rsa key
     * 
     * @return returns the RSA key object
     * @throws CVInvalidKeySourceException
     * @throws CVMissingKeyException
     * @throws CVKeyTypeNotSupportedException
     */
    public RSAPublicKeySpec getRSAKey() throws CVInvalidKeySourceException, CVMissingKeyException,
            CVKeyTypeNotSupportedException {
        // load the key from key source if a key is not available
        if (m_RSAKey == null && m_ECPubPoint == null) {
            loadKeyFromKeySource();
        }
        
        if (m_RSAKey == null) {
            throw new CVMissingKeyException();
        }
        return m_RSAKey;
    }
    
    /**
     * This function loads the key from key source object
     * 
     * @throws CVInvalidKeySourceException
     * @throws CVKeyTypeNotSupportedException
     * 
     * 
     */
    protected void loadKeyFromKeySource() throws CVInvalidKeySourceException, CVKeyTypeNotSupportedException {
        
        if (m_KeySource == null) {
            throw new CVInvalidKeySourceException();
        }
        
        if (m_KeySource.getKeyType() == KeyType.KEY_RSA) {
            setRSAKey(m_KeySource.getRSAPublicKey());
        } else if (m_KeySource.getKeyType() == KeyType.KEY_ECDSA) {
            
            setDomainParam(m_KeySource.getECDSADomain());
            setECPublicPoint(m_KeySource.getECDSAPublicPoint());
        }
    }
    
    /**
     * This function generate a DataBuffer with the public RSA key and the
     * information about the signature algorithm
     */
    protected DataBuffer generateCertPubKeyRSA() {
        DataBuffer out = new DataBuffer();
        
        // TAG for the Modulus
        TLV.append(out, CVCertificate.s_CvRSAModulusTag, Util.removeLeadingZeros(m_RSAKey.getModulus().toByteArray()));
        
        // TAG for the exponent
        TLV.append(out, CVCertificate.s_CvRSAExponentTag,
                Util.removeLeadingZeros(m_RSAKey.getPublicExponent().toByteArray()));
        
        return out;
    }
    
    /**
     * This function generate a DataBuffer with the public ECDSA key and the
     * information about the signature algorithm
     * 
     * @throws CVMissingKeyException
     */
    protected DataBuffer generateCertPubKeyECDSA() throws CVMissingKeyException {
        DataBuffer out = new DataBuffer();
        
        if (m_bIncludeECDSADomainParam && m_ECDomain == null) {
            throw new CVMissingKeyException();
        }
        
        if (m_bIncludeECDSADomainParam) {
            
            ECCurve.Fp curve = (ECCurve.Fp) m_ECDomain.getCurve();
            
            // prime modulus
            TLV.append(out, CVCertificate.s_CvECPrimeModTag, Util.removeLeadingZeros(curve.getQ().toByteArray()));
            
            // First Coefficient
            TLV.append(out, CVCertificate.s_CvECFirstCoeffTag,
                    Util.removeLeadingZeros(curve.getA().toBigInteger().toByteArray()));
            
            // Second Coefficient
            TLV.append(out, CVCertificate.s_CvECSecondCoeffTag,
                    Util.removeLeadingZeros(curve.getB().toBigInteger().toByteArray()));
            
            ECPoint G = m_ECDomain.getG().normalize();
            DataBuffer x = new DataBuffer(Util.removeLeadingZeros(G.getXCoord().toBigInteger().toByteArray()));
            DataBuffer y = new DataBuffer(Util.removeLeadingZeros(G.getYCoord().toBigInteger().toByteArray()));
            // correct different byte key sizes with leading null bytes
            if (x.size() != y.size()) {
                if (x.size() < y.size()) {
                    int add = y.size() - x.size();
                    
                    for (int i = 0; i < add; i++)
                        x.insert(0, (byte) 0x00);
                } else if (x.size() > y.size()) {
                    int add = x.size() - y.size();
                    
                    for (int i = 0; i < add; i++)
                        y.insert(0, (byte) 0x00);
                }
            }
            
            // Base Point G
            out.append(TLV.convertTag(CVCertificate.s_CvECBasePointTag));
            out.append(TLV.getEncodedLength(1 + x.size() + y.size()));
            out.append(TLV.convertTag(CVCertificate.s_CvUnCompressedTag));
            out.append(x);
            out.append(y);
            
            // Order of the Base point r
            TLV.append(out, CVCertificate.s_CvECOrderBasePointTag,
                    Util.removeLeadingZeros(m_ECDomain.getN().toByteArray()));
            
        }
        
        // add the public point
        DataBuffer x = new DataBuffer(Util.removeLeadingZeros(m_ECPubPoint.getX().toByteArray()));
        DataBuffer y = new DataBuffer(Util.removeLeadingZeros(m_ECPubPoint.getY().toByteArray()));
        // correct different byte key sizes with leading null bytes
        if (x.size() != y.size()) {
            if (x.size() < y.size()) {
                int add = y.size() - x.size();
                
                for (int i = 0; i < add; i++)
                    x.insert(0, (byte) 0x00);
            } else if (x.size() > y.size()) {
                int add = x.size() - y.size();
                
                for (int i = 0; i < add; i++)
                    y.insert(0, (byte) 0x00);
            }
        }
        // TAG for the ECDSA public point
        out.append(TLV.convertTag(CVCertificate.s_CvECPubPointTag));
        out.append(TLV.getEncodedLength(1 + x.size() + y.size()));
        // not compressed ECDSA Key
        out.append(TLV.convertTag(CVCertificate.s_CvUnCompressedTag));
        // insert key
        out.append(x);
        out.append(y);
        
        if (m_bIncludeECDSADomainParam) {
            // include Domain Parameter 0x87
            // Cofactor
            TLV.append(out, CVCertificate.s_CvECCofactorTag, Util.removeLeadingZeros(m_ECDomain.getH().toByteArray()));
        }
        
        return out;
    }
    
    /**
     * This function sets the hash algorithm type
     * 
     * @param type
     */
    public void setAlgorithm(TAAlgorithm type) {
        m_algorithmType = type;
    }
    
    /**
     * This function returns the hash algorithm type
     * 
     * @return returns the currently used signature algorithm
     */
    public TAAlgorithm getAlgorithm() {
        return m_algorithmType;
    }
    
    /**
     * @brief this method returns the used key type depending on the used
     *        signature algorithm
     * 
     * @return returns the key type
     */
    public KeyType getKeyType() {
        if (m_algorithmType == TAAlgorithm.RSA_v1_5_SHA_1 || m_algorithmType == TAAlgorithm.RSA_v1_5_SHA_256
                || m_algorithmType == TAAlgorithm.RSA_PSS_SHA_1 || m_algorithmType == TAAlgorithm.RSA_PSS_SHA_256) {
            return KeyType.KEY_RSA;
        } else if (m_algorithmType == TAAlgorithm.ECDSA_SHA_1 || m_algorithmType == TAAlgorithm.ECDSA_SHA_224
                || m_algorithmType == TAAlgorithm.ECDSA_SHA_256) {
            return KeyType.KEY_ECDSA;
        }
        
        return KeyType.KEY_UNDEFINED;
    }
    
    /**
     * This function set the key source object for this class
     * 
     * @param pSource
     */
    public void setKeySource(IPublicKeySource pSource) {
        m_KeySource = pSource;
    }
    
    /**
     * This function returns the key source object of this class
     * 
     * @return returns the key source object of this class
     */
    public IPublicKeySource getKeySource() {
        return m_KeySource;
    }
    
    @Override
    public boolean equals(Object obj) {
        try {
            if (obj instanceof CVPubKeyHolder) {
                CVPubKeyHolder other = (CVPubKeyHolder) obj;
                
                if (other.getKeyType() == KeyType.KEY_RSA && getKeyType() == KeyType.KEY_RSA) {
                    RSAPublicKeySpec otherKey = other.getRSAKey();
                    RSAPublicKeySpec ourkey = getRSAKey();
                    
                    if (otherKey.getModulus().compareTo(ourkey.getModulus()) != 0
                            || otherKey.getPublicExponent().compareTo(ourkey.getPublicExponent()) != 0) {
                        return false;
                    }
                    
                } else if (other.getKeyType() == KeyType.KEY_ECDSA && getKeyType() == KeyType.KEY_ECDSA) {
                    ECPubPoint otherKey = other.getECPublicPoint();
                    ECPubPoint ourKey = getECPublicPoint();
                    
                    if (otherKey.getX().compareTo(ourKey.getX()) != 0 || otherKey.getY().compareTo(ourKey.getY()) != 0) {
                        return false;
                    }
                } else {
                    return false;
                }
                
                return true;
            }
        } catch (Exception e) {
            
        }
        return false;
    }
    
}