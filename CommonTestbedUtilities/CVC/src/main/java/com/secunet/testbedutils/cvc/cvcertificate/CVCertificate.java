package com.secunet.testbedutils.cvc.cvcertificate;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SignatureException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.RSAPublicKeySpec;
import java.text.SimpleDateFormat;
import java.util.TimeZone;

import org.bouncycastle.jce.spec.ECParameterSpec;
import org.bouncycastle.math.ec.ECCurve;

import com.secunet.testbedutils.cvc.cvcertificate.exception.CVAuthorityRefNotValidException;
import com.secunet.testbedutils.cvc.cvcertificate.exception.CVBufferNotEmptyException;
import com.secunet.testbedutils.cvc.cvcertificate.exception.CVDecodeErrorException;
import com.secunet.testbedutils.cvc.cvcertificate.exception.CVInvalidDateException;
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
 * @class  CVCertificate
 * @brief  This class generate a chip verifiable certificate
 *
 * This is the main class for a complete cv certificate.
 * It can be used to create certificates or to read them.
 *
 * @author meier.marcus
 * @version 1.0
 * @created 27-Aug-2009 14:18:47
 */
public class CVCertificate {


	/**
	 * < tag nests complete certificate
	 */
	public static final int s_CvCertTag = 0x7f21;
	/**
	 * < tag nests certificate body
	 */
	public static final int s_CvBodyTag = 0x7f4e;
	/**
	 * < tag for the version number of the certificate
	 */
	public static final int s_CvProfileIdTag = 0x5f29;
	/**
	 * < tag for the name of the certification authority
	 */
	public static final int s_CvCertAuthRefTag = 0x42;
	/**
	 * < tag for the public key
	 */
	public static final int s_CvPublicKeyTag = 0x7f49;
	/**
	 * < tag for the unique name of the certificate holder
	 */
	public static final int s_CvCertHolderRefTag = 0x5f20;
	/**
	 * < this tag encodes role of the holder and the read access rights to data groups
	 */
	public static final int s_CvCertHolderAuthTag = 0x7f4c;
	/**
	 * < tag for the date of the certificate generation
	 */
	public static final int s_CvCertEffDateTag = 0x5f25;
	/**
	 * < tag for the date after the certificate expires
	 */
	public static final int s_CvCertExpDateTag = 0x5f24;
	/**
	 * < tag for the signature of the certificate
	 */
	public static final int s_CvSignatureTag = 0x5f37;
	/**
	 * < Tag for a object identifier
	 */
	public static final int s_CvOIDTag = 0x06;
	/**
	 * < Tag for the rsa modulus
	 */
	public static final int s_CvRSAModulusTag = 0x81;
	/**
	 * < Tag for the rsa public exponent
	 */
	public static final int s_CvRSAExponentTag = 0x82;
	/**
	 * < Tag for the EC prime modulus
	 */
	public static final int s_CvECPrimeModTag = 0x81;
	/**
	 * < Tag for the first EC coefficient
	 */
	public static final int s_CvECFirstCoeffTag = 0x82;
	/**
	 * < Tag for the second EC coefficient
	 */
	public static final int s_CvECSecondCoeffTag = 0x83;
	/**
	 * < Tag for the EC base point g
	 */
	public static final int s_CvECBasePointTag = 0x84;
	/**
	 * < Tag for the order of the EC base point
	 */
	public static final int s_CvECOrderBasePointTag = 0x85;
	/**
	 * < Tag for the EC public point
	 */
	public static final int s_CvECPubPointTag = 0x86;
	/**
	 * < Tag for the cofactor
	 */
	public static final int s_CvECCofactorTag = 0x87;
	/**
	 * DH Prime Modulus key Tag
	 */
	public static final int s_DHPrimeModulusTag = 0x81;
	/**
	 * DH Order of the subgroup Tag
	 */
	public static final int s_DHOrderSubgroupTag = 0x82;
	/**
	 * DH generator Tag
	 */
	public static final int s_DHGeneratorTag = 0x83;
	/**
	 * DH public value Tag
	 */
	public static final int s_DHPublicValueTag = 0x84;
	/**
	 * < Tag that indicates an uncompressed point
	 */
	public static final int s_CvUnCompressedTag = 0x04;
	/**
	 * < Tag for discretionary data
	 */
	public static final int s_CvDataTag = 0x53;
	/**
	 * < Tag for the certificate extension
	 */
	public static final int s_CvExtensionTag = 0x65;
	/**
	 * < tag of the discretionary data template
	 */
	public static final int s_CvDataTemplateTag = 0x73;
	/**
	 * < tag of the certificate description hash or the hash of the first public
	 * sector key
	 */
	public static final int s_CvExtensionHash1Tag = 0x80;
	/**
	 * < tag of the second public sector key hash
	 */
	public static final int s_CvExtensionHash2Tag = 0x81;

	/**
	 * ASN.1 tag for a printable string
	 */
	public static final int s_CvPrintableStringTag = 0x13;
	/**
	 * < tag for the name of the certification authority
	 */
	private static final int s_CvCertAuthenticationTag = 0x67;
	/**
	 * < this member hold the information about the holder role and access rights
	 */
	private CVHolderAuth m_certHolderAuth = new CVHolderAuth();
	/**
	 * < member for generation date
	 */
	private CVDate m_effDate = new CVDate();
	/**
	 * < member for expiration date
	 */
	private CVDate m_expDate = new CVDate();
	/**
	 * < member for the public key
	 */
	private CVPubKeyHolder m_publicKey = new CVPubKeyHolder();
	/**
	 * < member for the signature key
	 */
	private CVSignKeyHolder m_signKey = new CVSignKeyHolder();
	/**
	 * This member handles the certificate extensions
	 */
	private CVExtension m_extension = new CVExtension();
	/**
	 * < version number of the certificate
	 */
	protected int m_profileId;
	/**
	 * < holds the unique name of the certification authority
	 */
	protected String m_certAuthRef = "";
	/**
	 * < holds the unique name of the certificate holder
	 */
	protected String m_certHolderRef = "";
	/**
	 * < member for the signature key of the outer signature
	 */
	private CVSignKeyHolder m_outerSignKey = new CVSignKeyHolder();
	/**
	 * < holds the unique name of the certification authority for the outer signature
	 */
	protected String m_outerAuthRef = "";
	/**
	 * < stores the outer signature of the request certificate
	 */
	protected DataBuffer m_outerSign = null;
	/**
	 * < stores the encoded outer body
	 */
	protected DataBuffer m_outerBody = null;
	/**
	 * < stores the encoded body
	 */
	protected DataBuffer m_body = null;
	/**
	 * < stores the signature
	 */
	protected DataBuffer m_sign = null;
	/**
	 * < this member indicates whether this certificate is a request certificate
	 */
	protected boolean m_bReqCert = false;


	/**
	 * @brief constructor
	 *
	 * @param createReqCert this parameter consigns whether we want to create a request certificate
	 */
	public CVCertificate(boolean createReqCert)
	{
		m_profileId = 0;
	    m_bReqCert = createReqCert;
	}

	/**
	 * @brief constructor
	 *
	 * @throws CVInvalidECPointLengthException
	 * @throws CVInvalidDateException
	 * @throws CVDecodeErrorException
	 * @throws CVInvalidOidException
	 * @throws CVBufferNotEmptyException
	 * @throws CVTagNotFoundException
	 * @brief constructor
	 *
	 */
	public CVCertificate(DataBuffer rawData) throws CVTagNotFoundException, CVBufferNotEmptyException, CVInvalidOidException, CVDecodeErrorException, CVInvalidDateException, CVInvalidECPointLengthException
	{
		this();
	    parseRaw(rawData);
	}

	/**
	 * @brief default constructor
	 *
	 */
	public CVCertificate()
	{
		m_profileId = 0;
	    m_bReqCert = false;
	}

	/**
	 *
	 * @brief returns the certificate holder authorization object
	 *
	 * @return returns the authorization holder object
	 */
	public CVHolderAuth getCertHolderAuth()
	{
		return m_certHolderAuth;
	}

	/**
	 *
	 * @param newVal consigns a new certificate holder authorization
	 */
	public void setCertHolderAuth(CVHolderAuth newVal)
	{

		m_certHolderAuth = newVal;
	}
	/**
	 *
	 * @brief returns the effective date object
	 *
	 * @return returns CVDate object
	 */
	public CVDate getEffDate()
	{
		return m_effDate;
	}

	/**
	 *
	 * @param newVal
	 */
	public void setEffDate(CVDate newVal){
		m_effDate = newVal;
	}
	/**
	 *
	 * @brief returns the expiration date object
	 *
	 * @return returns a instance of CVDate
	 */
	public CVDate getExpDate(){
		return m_expDate;
	}

	/**
	 *
	 * @param newVal
	 */
	public void setExpDate(CVDate newVal){
		m_expDate = newVal;
	}

	/**
	 * @brief This method returns the public key object of the certificate
	 *
	 * @return returns a CVVPublicKeyHolder instance
	 */
	public CVPubKeyHolder getPublicKey(){
		return m_publicKey;
	}

	/**
	 *
	 * @param newVal
	 */
	public void setPublicKey(CVPubKeyHolder newVal){
		m_publicKey = newVal;
	}

	/**
	 *
	 * @brief This method returns the instance of the Signer object
	 *
	 * @return returns a instance of the CVSignKeyHolder
	 */
	public CVSignKeyHolder getSignKey(){
		return m_signKey;
	}

	/**
	 *
	 * @param newVal
	 */
	public void setSignKey(CVSignKeyHolder newVal){
		m_signKey = newVal;
	}

	/**
	 *
	 * @return returns the extension object
	 */
	public CVExtension getExtension(){
		return m_extension;
	}

	/**
	 *
	 * @brief This method returns the instance of the Signer object for the outer signature
	 *
	 * @return returns a instance of the CVSignKeyHolder
	 */
	public CVSignKeyHolder getOuterSignKey(){
		return m_outerSignKey;
	}

	/**
	 *
	 * @param newVal
	 */
	public void setOuterSignKey(CVSignKeyHolder newVal){
		m_outerSignKey = newVal;
	}
	/**
	 * This function sets the authority reference for the outer signature
	 *
	 * @param authref
	 */
	public void setOuterAuthRef(String authref){
		m_outerAuthRef = authref;
	}

	/**
	 * This function returns the authority reference of the outer signature
	 *
	 * @return returns the outer authority reference
	 */
	public String getOuterAuthRef(){
		return m_outerAuthRef;
	}
	/**
	 *
	 * @param newVal
	 */
	public void setExtension(CVExtension newVal){
		m_extension = newVal;
	}


	/**
	 * This function sets the version number
	 *
	 * @param version
	 */
	public void setProfileId(int version){
		m_profileId = version;
	}

	/**
	 * This function returns the version number
	 * @return returns the profile id of the certificate
	 */
	public int getProfileId(){
		return m_profileId;
	}

	/**
	 * This function sets the authority reference
	 *
	 * @param authref
	 */
	public void setCertAuthRef(String authref){
		m_certAuthRef = authref;
	}

	/**
	 * This function returns the authority reference
	 *
	 * @return returns the name of the issuer certificate
	 */
	public String getCertAuthRef(){
		return m_certAuthRef;
	}

	/**
	 * This function sets the certificate holder reference
	 *
	 * @param holderref
	 */
	public void setCertHolderRef(String holderref){
		m_certHolderRef = holderref;
	}

	/**
	 * This function returns the certificate holder reference
	 *
	 * @return returns the holder name of the certificate
	 */
	public String getCertHolderRef(){
		return m_certHolderRef;
	}

	/**
	 * This function encode and return the complete chip verifiable request
	 * certificate
	 *
	 *
	 * @return returns the complete encoded certificate or request certificate as data buffer
	 * @throws CVAuthorityRefNotValidException
	 * @throws CVMissingKeyException
	 * @throws CVInvalidOidException
	 * @throws CVSignOpKeyMismatchException
	 * @throws CVInvalidKeySourceException
	 * @throws CVKeyTypeNotSupportedException
	 */
	public DataBuffer generateCert() throws CVAuthorityRefNotValidException, CVInvalidKeySourceException, CVSignOpKeyMismatchException, CVInvalidOidException, CVMissingKeyException, CVKeyTypeNotSupportedException{
		DataBuffer out= null;
		if(m_bReqCert && m_outerAuthRef.length() > 0 && m_outerSignKey != null)
		{
			//create the certificate structure
			out = generateInnerCert();

			//add the Certification Authority Reference
			TLV.append(out,s_CvCertAuthRefTag,m_outerAuthRef);
			m_outerBody = out;

			//generates the signature
			DataBuffer sig = m_outerSignKey.signContent(out);
			m_outerSign = sig;
			//add the signature
			TLV.append(out,s_CvSignatureTag,sig);

			//now include the whole length in front of the byte buffer
			int size = out.size();
			out.insert(0,TLV.getEncodedLength(size));
			//at last add the tag for the outer structure
			out.insert(0,TLV.convertTag(s_CvCertAuthenticationTag));
		}
		else
		{
			//generate a initial request
			out = generateInnerCert();
		}

		return out;
	}

	/**
	 * @brief This function encode and return the complete chip verifiable certificate
	 *
	 * @return returns encoded the CV certificate structure
	 * @throws CVAuthorityRefNotValidException
	 * @throws CVSignOpKeyMismatchException
	 * @throws CVInvalidKeySourceException
	 * @throws CVMissingKeyException
	 * @throws CVInvalidOidException
	 * @throws CVKeyTypeNotSupportedException
	 */
	protected DataBuffer generateInnerCert() throws CVAuthorityRefNotValidException, CVInvalidKeySourceException, CVSignOpKeyMismatchException, CVInvalidOidException, CVMissingKeyException, CVKeyTypeNotSupportedException
	{
		DataBuffer out = new DataBuffer();

		//generate CVCert Body
		if(m_body == null)
		{
			DataBuffer body = generateBody();
			//body tag
			TLV.append(out,s_CvBodyTag,body);
			m_body = new DataBuffer(out);
		}
		else
			out.assign(m_body);

		//generate the sign of the body
		if(m_sign == null)
			m_sign = generateSign(out);

		//include the signature
		TLV.append(out,s_CvSignatureTag,m_sign);
		//compute and inlcude the complete size of the certificate
		int size = out.size();
		out.insert(0,TLV.getEncodedLength(size));
		out.insert(0,TLV.convertTag(s_CvCertTag));


		return out;
	}


	/**
	 * This function decode a cv request certificate
	 *
	 * @param cv
	 * @throws CVTagNotFoundException
	 * @throws CVBufferNotEmptyException
	 * @throws CVInvalidOidException
	 * @throws CVDecodeErrorException
	 * @throws CVInvalidECPointLengthException
	 * @throws CVInvalidDateException
	 */
	public void parseRaw(DataBuffer cv) throws CVTagNotFoundException, CVBufferNotEmptyException, CVInvalidOidException, CVDecodeErrorException, CVInvalidDateException, CVInvalidECPointLengthException	{
		DataBuffer data = new DataBuffer(cv);

		TLV extr = TLV.extract(data);

		if(extr.getTag() == s_CvCertTag)
		{
			//without outer structure
			//m_rLog << "Seems to be a request cert without a outer structure or a CV certificate" << std::endl;
			parseRawInnerCV(cv);
		}
		else if(extr.getTag() == s_CvCertAuthenticationTag)
		{
			m_bReqCert = true;
			//m_rLog << "0x67 Request certificate authentication tag found" << std::endl;

			data.assign(extr.getValue());
			TLV extrInner = TLV.extract(data);

			if(extrInner.getTag() != s_CvCertTag)
			{
//				m_rLog << "0x7f21 certificate tag not found" << std::endl;
				throw new CVTagNotFoundException("0x7f21");
			}

			//store it for later signature checking
			m_outerBody = extrInner.getTLV();

			//now parse the inner structure
			parseRawInnerCV(extr.getValue());

			//Outer Authority reference
			TLV extrAuthRef = TLV.extract(data);
			if(extrAuthRef.getTag() != s_CvCertAuthRefTag)
			{
//				m_rLog << "0x42 Certificate authority reference tag not found" << std::endl;
				throw new CVTagNotFoundException("0x42");
			}
			//m_rLog << "0x42 Outer certifiacte authority reference tag found" << std::endl;
			setOuterAuthRef(new String(extrAuthRef.getValue().toByteArray()));

			//store it for later signature checking
			m_outerBody.append(extrAuthRef.getTLV());

			//Outer signature

			TLV extrOutSign = TLV.extract(data);
			if(extrOutSign.getTag() != s_CvSignatureTag)
			{
//				m_rLog << "0x5f37 Certificate signature tag not found" << std::endl;
				throw new CVTagNotFoundException("0x5f37");
			}
			//m_rLog << "0x5F37 Outer certificate signature tag found" << std::endl;
			m_outerSign = extrOutSign.getValue();

			//data should be empty, otherwise the parse of the cert has failed
			if(data.size() > 0)
			{
				// Error message
//				m_rLog << "Error: " << (unsigned int)data.size() << " bytes left in buffer" << std::endl;
				throw new CVBufferNotEmptyException();
			}
		}
		else
		{
//			m_rLog << "0x7f21 certificate tag or 0x67 authentication tag not found" << std::endl;
			throw new CVTagNotFoundException("0x7f21 or 0x67");
		}
	}

	/**
	 * This function decode a cv certificate
	 *
	 * @param cv
	 * @throws CVTagNotFoundException
	 * @throws CVBufferNotEmptyException
	 * @throws CVInvalidOidException
	 * @throws CVDecodeErrorException
	 * @throws CVInvalidDateException
	 * @throws CVInvalidECPointLengthException
	 */
	protected void parseRawInnerCV(DataBuffer cv) throws CVTagNotFoundException, CVBufferNotEmptyException, CVInvalidOidException, CVDecodeErrorException, CVInvalidDateException, CVInvalidECPointLengthException
	{
		DataBuffer data = new DataBuffer(cv);

		TLV extrCert = TLV.extract(data);
		// Certificate tag
		if(extrCert.getTag() != s_CvCertTag)
		{
			//m_rLog << "0x7F21 Certificate tag not found" << std::endl;
			throw new CVTagNotFoundException("0x7f21");
		}

		//m_rLog << "0x7F21 Certificate tag found" << std::endl;
		//extract body
		data = extrCert.getValue();

		//cert body tag
		TLV extrBody = TLV.extract(data);
		if(extrBody.getTag() != s_CvBodyTag)
		{
			//m_rLog << "0x7F4E Certificate body tag not found" << std::endl;
			throw new CVTagNotFoundException("0x7F4E");
		}

		//m_rLog << "0x7F4E Certificate body tag found" << std::endl;
		DataBuffer body = extrBody.getValue();
		m_body = extrBody.getTLV();

		//cert signature tag
		TLV extrSign = TLV.extract(data);
		if(extrSign.getTag() != s_CvSignatureTag)
		{
			//m_rLog << "0x5F37 Certificate signature tag not found" << std::endl;
			throw new CVTagNotFoundException("0x5F37");
		}
		//m_rLog << "0x5F37 Certificate signature tag found" << std::endl;
		m_sign = extrSign.getValue();

		//now we parse the body structure
		data = new DataBuffer(body);
		TLV extrProfilID = TLV.extract(data);
		//cert profile id
		if(extrProfilID.getTag() != s_CvProfileIdTag || extrProfilID.getLength() != 1)
		{
			//m_rLog << "0x5F29 Certificate profile id tag not found" << std::endl;
			throw new CVTagNotFoundException("0x5F29");
		}
		//m_rLog << "0x5F29 Certificate profile id tag found" << std::endl;
		setProfileId(extrProfilID.getValue().get(0));

		//cert authority reference
		TLV extrAuthRef = TLV.extract(data);
		TLV extrPubKey = null;
		if(extrAuthRef.getTag() == s_CvCertAuthRefTag)
		{
			setCertAuthRef(new String(extrAuthRef.getValue().toByteArray()));
			extrPubKey = TLV.extract(data);
		}
		else if(extrAuthRef.getTag() == s_CvPublicKeyTag)
		{
			extrPubKey = extrAuthRef;
		}
		else
		{
			throw new CVTagNotFoundException("0x42 or 0x7f49");
		}

		//public key
		if(extrPubKey.getTag() != s_CvPublicKeyTag)
		{
//			m_rLog << "0x7F49 Certificate public key tag not found" << std::endl;
			throw new CVTagNotFoundException("0x7f49");
		}
		//(m_rLog << "0x7F49 Certificate public key tag found" << std::endl;
		//parse the public key data object
		m_publicKey.parseRawKey(extrPubKey.getValue());

		//cert holder reference tag
		TLV extrCertHolderRef = TLV.extract(data);
		if(extrCertHolderRef.getTag() != s_CvCertHolderRefTag)
		{
//			m_rLog << "0x5F20 Certificate holder reference tag not found" << std::endl;
			throw new CVTagNotFoundException("0x5f20");
		}
		//m_rLog << "0x5F20 Certificate holder reference tag found" << std::endl;
		setCertHolderRef(new String(extrCertHolderRef.getValue().toByteArray()));
		if(data.size() > 0)
		{
			// try to parse cert extension
			TLV extrExt_CHR = TLV.extract(data);

			//parse only if this cert isn't a request cert
			if(extrExt_CHR.getTag() == s_CvCertHolderAuthTag)
			{
				if(m_bReqCert)
				{
					throw new CVDecodeErrorException();
				}
				//cert holder auth tag
				//m_rLog << "0x7F4C Certificate holder authorization tag found" << std::endl;
				//parse the holder authorization
				m_certHolderAuth.parseRawHolderAuth(extrExt_CHR.getValue());

				//effective date object
				TLV extrEffDate = TLV.extract(data);
				if(extrEffDate.getTag() != s_CvCertEffDateTag)
				{
//					m_rLog << "0x5F25 Certificate effective date tag notfound" << std::endl;
					throw new CVTagNotFoundException("0x5f25");
				}
				//m_rLog << "0x5F25 Certificate effective date tag found" << std::endl;
				//parse the date object
				m_effDate.parseRawDate(extrEffDate.getValue());
				//expiration date object
				TLV extrExpDate = TLV.extract(data);
				if(extrExpDate.getTag() != s_CvCertExpDateTag)
				{
//					m_rLog << "0x5F24 Certificate expiration date tag not found" << std::endl;
					throw new CVTagNotFoundException("0x5F24");
				}
				//m_rLog << "0x5F24 Certificate expiration date tag found" << std::endl;
				//parse the date
				m_expDate.parseRawDate(extrExpDate.getValue());

				if(data.size() > 0)
				{
					// try to parse cert extension
					TLV extrExt = TLV.extract(data);
					if(extrExt.getTag() != s_CvExtensionTag)
					{
//						m_rLog << "0x65 Certificate extension tag not found" << std::endl;
						throw new CVTagNotFoundException("0x65");
					}
					m_extension.parseRawExtensions(extrExt.getValue());
				}
			}
			else if(extrExt_CHR.getTag() == s_CvExtensionTag)
			{
				m_bReqCert = true;
				m_extension.parseRawExtensions(extrExt_CHR.getValue());
			}
			else
			{
				throw new CVTagNotFoundException("0x7f4c or 0x65");
			}
		}
		else
		{
			m_bReqCert = true;
		}

		//data should be empty, otherwise the parse of the cert has failed
		if(data.size() > 0)
		{
			// Error message
			throw new CVBufferNotEmptyException();
		}
	}

	/**
	 * This function, examines the signature of this certificate
	 *
	 * @param authCert consigns the issuer certificate
	 * @return returns true if the signature is valid else false
	 * @throws CVMissingKeyException
	 * @throws CVInvalidKeySourceException
	 * @throws UnknownCryptoProviderException
	 * @throws UnknownAlgorithmException
	 * @throws InvalidKeySpecException
	 * @throws SignatureException
	 * @throws NoSuchProviderException
	 * @throws NoSuchAlgorithmException
	 * @throws InvalidKeyException
	 * @throws CVKeyTypeNotSupportedException
	 */
	public boolean checkSign(CVCertificate authCert) throws CVInvalidKeySourceException, CVMissingKeyException, InvalidKeyException, NoSuchAlgorithmException, NoSuchProviderException, SignatureException, InvalidKeySpecException, CVUnknownAlgorithmException, CVUnknownCryptoProviderException, CVKeyTypeNotSupportedException{

		//Do not check the holder reference for request certificates,
		//because these certificates are self signed and they have not a authority reference
		if(!m_bReqCert && authCert.getCertHolderRef().compareTo(getCertAuthRef()) != 0)
		{
			//m_rLog << "The certificate holder reference and the certificate authority reference are not identical" << std::endl;
			return false;
		}

		return authCert.m_publicKey.checkSign(getBody(), m_sign);
	}


	/**
	 * This function, examines the signature of this certificate
	 *
	 * @param authCert consigns the issuer certificate
	 * @param cvRootCert CV certificate which contains the domain parameter
	 * @return returns true if the signature is valid else false
	 * @throws CVMissingKeyException
	 * @throws CVInvalidKeySourceException
	 * @throws UnknownCryptoProviderException
	 * @throws UnknownAlgorithmException
	 * @throws InvalidKeySpecException
	 * @throws SignatureException
	 * @throws NoSuchProviderException
	 * @throws NoSuchAlgorithmException
	 * @throws InvalidKeyException
	 * @throws CVKeyTypeNotSupportedException
	 */
	public boolean checkSign(CVCertificate authCert, CVCertificate cvRootCert) throws CVInvalidKeySourceException, CVMissingKeyException, InvalidKeyException, NoSuchAlgorithmException, NoSuchProviderException, SignatureException, InvalidKeySpecException, CVUnknownAlgorithmException, CVUnknownCryptoProviderException, CVKeyTypeNotSupportedException{

		//Do not check the holder reference for request certificates,
		//because these certificates are self signed and they have not a authority reference
		if(!m_bReqCert && authCert.getCertHolderRef().compareTo(getCertAuthRef()) != 0)
		{
			//m_rLog << "The certificate holder reference and the certificate authority reference are not identical" << std::endl;
			return false;
		}

		// extract domain parameter from the cvRoot certificate
		if (cvRootCert == null ||
			cvRootCert.getPublicKey() == null ||
			!cvRootCert.getPublicKey().isDomainParamPresent())
		{
			throw new CVMissingKeyException();
		}
		if (cvRootCert.getPublicKey().getAlgorithm().compareTo(TAAlgorithm.ECDSA_NONE) == 0 ||
				cvRootCert.getPublicKey().getAlgorithm().compareTo(TAAlgorithm.ECDSA_SHA_1) == 0 ||
				cvRootCert.getPublicKey().getAlgorithm().compareTo(TAAlgorithm.ECDSA_SHA_224) == 0 ||
				cvRootCert.getPublicKey().getAlgorithm().compareTo(TAAlgorithm.ECDSA_SHA_256) == 0)
		{
			CVPubKeyHolder publicKey = authCert.m_publicKey;
			// use domain parameter from the given CV root certificate
			publicKey.setDomainParam(cvRootCert.getPublicKey().getDomainParam());
			// check signature
			boolean bResult = authCert.m_publicKey.checkSign(getBody(), m_sign);

			return bResult;

		}
		else
		{
			return authCert.m_publicKey.checkSign(getBody(), m_sign);
		}
	}


	/**
	 * This function returns the signature
	 * @return returns a data buffer with the signature
	 */
	public DataBuffer getSignature(){
		return m_sign;
	}

	/**
	 * This function returns the body data object
	 * @return returns the encoded body of the certificate
	 */
	public DataBuffer getBody(){

		return m_body;
	}

	/**
	 * This function returns whether this cert is a request certificate
	 * @return returns whether this object is an request certifcate
	 */
	public boolean isReqCert(){
		return m_bReqCert;
	}

	/**
	 * This function sets whether we want to create a request certificate
	 * @param createReqCert consigns whether we want create a request certificate
	 */
	public void setReqCertFlag(boolean createReqCert){
		m_bReqCert = createReqCert;
	}
	/**
	 * This function, examines the signature of the outer structure
	 *
	 * @param authCert consigns the Issuer certificate of the signature
	 * @return true if the signature is valid else false
	 * @throws CVMissingKeyException
	 * @throws CVInvalidKeySourceException
	 * @throws UnknownCryptoProviderException
	 * @throws UnknownAlgorithmException
	 * @throws InvalidKeySpecException
	 * @throws SignatureException
	 * @throws NoSuchProviderException
	 * @throws NoSuchAlgorithmException
	 * @throws InvalidKeyException
	 * @throws CVKeyTypeNotSupportedException
	 */
	public boolean checkOuterSign(CVCertificate authCert) throws CVInvalidKeySourceException, CVMissingKeyException, InvalidKeyException, NoSuchAlgorithmException, NoSuchProviderException, SignatureException, InvalidKeySpecException, CVUnknownAlgorithmException, CVUnknownCryptoProviderException, CVKeyTypeNotSupportedException{
		if( authCert.getCertHolderRef().compareTo(getOuterAuthRef()) != 0)
		{
			//m_rLog << "The certificate holder reference and the outer certificate authority reference are not identical" << std::endl;
			return false;
		}

		return authCert.m_publicKey.checkSign(m_outerBody, m_outerSign);
	}


	/**
	 * This function returns whether this request cert has a outer signature
	 * @return true if a outer signature exist else false
	 */
	public boolean hasOuterSignature(){
		return (m_outerAuthRef.length() != 0 && m_outerBody != null && m_outerSign != null);
	}

	/**
	 * This function generate and returns the certificate body
	 * @throws CVAuthorityRefNotValidException
	 * @throws CVInvalidOidException
	 * @throws CVMissingKeyException
	 * @throws CVInvalidKeySourceException
	 * @throws CVSignOpKeyMismatchException
	 * @throws CVKeyTypeNotSupportedException
	 *
	 *
	 */
	protected DataBuffer generateBody() throws CVAuthorityRefNotValidException, CVInvalidOidException, CVSignOpKeyMismatchException, CVInvalidKeySourceException, CVMissingKeyException, CVKeyTypeNotSupportedException{
		DataBuffer output = new DataBuffer();

		//Profile ID
		output.append(TLV.convertTag(s_CvProfileIdTag));
		output.append((byte)0x01);
		output.append((byte)m_profileId);

		//Certification Authority Reference
		if(m_certAuthRef.length() > 0 && m_certAuthRef.length() <= 16)
		{
			TLV.append(output, s_CvCertAuthRefTag, m_certAuthRef);
		}
		else
		{
			throw new CVAuthorityRefNotValidException();
		}


		//Public Key
		DataBuffer pubKey = m_publicKey.generateCertPubKey();
		TLV.append(output, s_CvPublicKeyTag, pubKey);


		//Certification Holder Reference
		if(m_certHolderRef.length() > 0 && m_certHolderRef.length() <= 16)
		{
			TLV.append(output, s_CvCertHolderRefTag, m_certHolderRef);
		}
		else
		{
			throw new CVAuthorityRefNotValidException();
		}

		if(!m_bReqCert)
		{
			//Certification Holder Authorization
			TLV.append(output, s_CvCertHolderAuthTag, m_certHolderAuth.genHolderAuth());

			//Certificate Effective Date
			TLV.append(output, s_CvCertEffDateTag,m_effDate.generateDate());

			//Certificate Expiration Date
			TLV.append(output, s_CvCertExpDateTag,m_expDate.generateDate());

		}

		DataBuffer extension = m_extension.genRawExtensions();
		if (!extension.isEmpty())
		{
			TLV.append(output, s_CvExtensionTag,extension);
		}

		return output;
	}

	/**
	 * This function returns the signature of the body parameter
	 *
	 * @param body
	 * @throws CVSignOpKeyMismatchException
	 * @throws CVInvalidKeySourceException
	 * @throws CVKeyTypeNotSupportedException
	 */
	protected DataBuffer generateSign(DataBuffer body) throws CVInvalidKeySourceException, CVSignOpKeyMismatchException, CVKeyTypeNotSupportedException{
		return m_signKey.signContent(body);
	}

	@Override
	public String toString()
	{
		StringBuilder out = new StringBuilder(10000);

		out.append("Certificate:\n");
		out.append("\tData:\n");
		out.append("\t\tIssuer: ");
		out.append(m_certAuthRef);
		out.append("\n");

		if (m_effDate.getDate() != null || m_expDate.getDate() != null)
		{
			SimpleDateFormat sdf = new SimpleDateFormat("MMM dd yy");
			sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
			out.append("\t\tValidity:\n");
			if (m_effDate.getDate() != null)
			{
				out.append("\t\t\tNot Before: ");
				out.append(sdf.format(m_effDate.getDate()));
				out.append("\n");
			}
			if (m_expDate.getDate() != null)
			{
				out.append("\t\t\tNot After : ");
				out.append(sdf.format(m_expDate.getDate()));
				out.append("\n");
			}
		}

		out.append("\t\tSubject: ");
		out.append(m_certHolderRef);
		out.append("\n");

		out.append("\t\tSubject Public Key Info:\n");
		out.append("\t\t\tPublic Key Algorithm: ");
		out.append(m_publicKey.getAlgorithm().name());
		out.append("\n");

		try
		{
			if (m_publicKey.m_RSAKey != null)
			{
				out.append(getRSAPublicKeyInfos());
			}
			if (m_publicKey.m_ECPubPoint != null)
			{
				out.append(getECPublicKeyInfos());
			}

			out.append(getCVAutorizationInfos());

			if (getExtension() != null && getExtension().getExtensions() != null)
			{
				out.append("\t\tCV extensions:\n");
				for(CVExtensionData cvExtensionData : getExtension().getExtensions())
				{
					if (CVExtensionType.extDescription.equals(cvExtensionData.getType()))
					{
						out.append("\t\t\tExtended Description:\n");
						out.append(cvExtensionData.getHash1().getHexSplit(":", "\t\t\t\t", 48));
					}
					if (CVExtensionType.extSector.equals(cvExtensionData.getType()))
					{
						out.append("\t\t\tExtended Sector:\n");
						out.append(cvExtensionData.getHash1().getHexSplit(":", "\t\t\t\t", 48));
						if (cvExtensionData.getHash2() != null)
							out.append(cvExtensionData.getHash2().getHexSplit(":", "\t\t\t\t", 48));
					}
				}
			}

			if (getSignature() != null)
			{
				out.append("\tSignature:\n");
				out.append(getSignature().getHexSplit(":", "\t\t", 48));
			}
			
			out.append("\n-----BEGIN CERTIFICATE-----\n");
			if (m_outerBody != null)
				out.append(m_outerBody.getB64encoded(64)+"\n");
			else
				out.append(m_body.getB64encoded(64)+"\n");
			out.append("-----END CERTIFICATE-----");
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}

		return out.toString();
	}

	private String getCVAutorizationInfos()
	{
		StringBuilder out = new StringBuilder(1000);

		if (getCertHolderAuth() == null ||
				getCertHolderAuth().getAuth() == null)
			return "";

		CVAuthorization cvAuthorization = getCertHolderAuth().getAuth();
		out.append("\t\tRole:\n");
		out.append("\t\t\t");
		out.append(cvAuthorization.getRole().name());
		out.append("\n");

		out.append("\t\tTermType:\n");
		out.append("\t\t\t");
		out.append(cvAuthorization.getTermType().name());
		out.append("\n");

		if (TermType.InspectionSystem.equals(cvAuthorization.getTermType()))
		{
			out.append("\t\tAuthorization:\n");
			out.append("\t\t\tCommon:\n");
			out.append(getCVAutorizationAT(cvAuthorization, CVAuthorizationIS.auth_Read_eID));

			out.append("\t\t\tRead:\n");
			out.append("\t\t\t\t");
			out.append(getSingleCVAutorizationIS(cvAuthorization, CVAuthorizationIS.auth_Read_DG3));
			out.append(getSingleCVAutorizationIS(cvAuthorization, CVAuthorizationIS.auth_Read_DG4));
			out.append("\n");
		}
		if (TermType.AuthenticationTerminal.equals(getCertHolderAuth().getAuth().getTermType()))
		{
			out.append("\t\tAuthorization:\n");
			out.append("\t\t\tCommon:\n");
			out.append(getCVAutorizationAT(cvAuthorization, CVAuthorizationAT.auth_AgeVerification));
			out.append(getCVAutorizationAT(cvAuthorization, CVAuthorizationAT.auth_CommunityIDVerification));
			out.append(getCVAutorizationAT(cvAuthorization, CVAuthorizationAT.auth_RestrictedIdentification));
			out.append(getCVAutorizationAT(cvAuthorization, CVAuthorizationAT.auth_CANAllowed));
			out.append(getCVAutorizationAT(cvAuthorization, CVAuthorizationAT.auth_PINManagement));
			out.append(getCVAutorizationAT(cvAuthorization, CVAuthorizationAT.auth_InstallCertificate));
			out.append(getCVAutorizationAT(cvAuthorization, CVAuthorizationAT.auth_InstallQulifiedCertificate));

			out.append("\t\t\tRead:\n");
			out.append("\t\t\t\t");
			out.append(getSingleCVAutorizationAT(cvAuthorization, CVAuthorizationAT.auth_Read_DG1));
			out.append(getSingleCVAutorizationAT(cvAuthorization, CVAuthorizationAT.auth_Read_DG2));
			out.append(getSingleCVAutorizationAT(cvAuthorization, CVAuthorizationAT.auth_Read_DG3));
			out.append(getSingleCVAutorizationAT(cvAuthorization, CVAuthorizationAT.auth_Read_DG4));
			out.append(getSingleCVAutorizationAT(cvAuthorization, CVAuthorizationAT.auth_Read_DG5));
			out.append(getSingleCVAutorizationAT(cvAuthorization, CVAuthorizationAT.auth_Read_DG6));
			out.append(getSingleCVAutorizationAT(cvAuthorization, CVAuthorizationAT.auth_Read_DG7));
			out.append(getSingleCVAutorizationAT(cvAuthorization, CVAuthorizationAT.auth_Read_DG8));
			out.append(getSingleCVAutorizationAT(cvAuthorization, CVAuthorizationAT.auth_Read_DG9));
			out.append(getSingleCVAutorizationAT(cvAuthorization, CVAuthorizationAT.auth_Read_DG10));
			out.append(getSingleCVAutorizationAT(cvAuthorization, CVAuthorizationAT.auth_Read_DG11));
			out.append(getSingleCVAutorizationAT(cvAuthorization, CVAuthorizationAT.auth_Read_DG12));
			out.append(getSingleCVAutorizationAT(cvAuthorization, CVAuthorizationAT.auth_Read_DG13));
			out.append(getSingleCVAutorizationAT(cvAuthorization, CVAuthorizationAT.auth_Read_DG14));
			out.append(getSingleCVAutorizationAT(cvAuthorization, CVAuthorizationAT.auth_Read_DG15));
			out.append(getSingleCVAutorizationAT(cvAuthorization, CVAuthorizationAT.auth_Read_DG16));
			out.append(getSingleCVAutorizationAT(cvAuthorization, CVAuthorizationAT.auth_Read_DG17));
			out.append(getSingleCVAutorizationAT(cvAuthorization, CVAuthorizationAT.auth_Read_DG18));
			out.append(getSingleCVAutorizationAT(cvAuthorization, CVAuthorizationAT.auth_Read_DG19));
			out.append(getSingleCVAutorizationAT(cvAuthorization, CVAuthorizationAT.auth_Read_DG20));
			out.append(getSingleCVAutorizationAT(cvAuthorization, CVAuthorizationAT.auth_Read_DG21));
			out.append("\n");

			out.append("\t\t\tWrite:\n");
			out.append("\t\t\t\t");
			out.append(getSingleCVAutorizationAT(cvAuthorization, CVAuthorizationAT.auth_Read_DG17));
			out.append(getSingleCVAutorizationAT(cvAuthorization, CVAuthorizationAT.auth_Read_DG18));
			out.append(getSingleCVAutorizationAT(cvAuthorization, CVAuthorizationAT.auth_Read_DG19));
			out.append(getSingleCVAutorizationAT(cvAuthorization, CVAuthorizationAT.auth_Read_DG20));
			out.append(getSingleCVAutorizationAT(cvAuthorization, CVAuthorizationAT.auth_Read_DG21));
			out.append("\n");
		}
		if (TermType.SignatureTerminal.equals(getCertHolderAuth().getAuth().getTermType()))
		{
			out.append("\t\tAuthorization:\n");
			out.append("\t\t\tCommon:\n");
			out.append(getCVAutorizationST(cvAuthorization, CVAuthorizationST.auth_GenerateQualifiedSignature));
			out.append(getCVAutorizationST(cvAuthorization, CVAuthorizationST.auth_GenerateSignature));
		}

		return out.toString();
	}

	private String getCVAutorizationAT(CVAuthorization cvAuthorization, int cvAuthorizationAT)
	{
		if (cvAuthorization.getAuth(cvAuthorizationAT))
		{
			StringBuilder out = new StringBuilder(50);
			out.append("\t\t\t\t");
			out.append(CVAuthorizationAT.getText(cvAuthorizationAT));
			out.append("\n");
			return out.toString();
		}
		else
			return "";
	}
	
	private String getCVAutorizationST(CVAuthorization cvAuthorization, int cvAuthorizationST)
	{
		if (cvAuthorization.getAuth(cvAuthorizationST))
		{
			StringBuilder out = new StringBuilder(50);
			out.append("\t\t\t\t");
			out.append(CVAuthorizationST.getText(cvAuthorizationST));
			out.append("\n");
			return out.toString();
		}
		else
			return "";
	}

	private String getSingleCVAutorizationAT(CVAuthorization cvAuthorization, int cvAuthorizationAT)
	{
		if (cvAuthorization.getAuth(cvAuthorizationAT))
			return CVAuthorizationAT.getText(cvAuthorizationAT)+" ";
		else
			return "";
	}
	
	private String getSingleCVAutorizationIS(CVAuthorization cvAuthorization, int cvAuthorizationIS)
	{
		if (cvAuthorization.getAuth(cvAuthorizationIS))
			return CVAuthorizationIS.getText(cvAuthorizationIS)+" ";
		else
			return "";
	}

	private String getECPublicKeyInfos() throws CVInvalidKeySourceException, CVMissingKeyException, CVKeyTypeNotSupportedException
	{
		StringBuilder out = new StringBuilder(2000);

		ECPubPoint ecPublicPoint = m_publicKey.getECPublicPoint();

		ECParameterSpec domainParams = null;
		ECCCurves curve = null;
		if (m_publicKey.isDomainParamPresent())
		{
			domainParams = m_publicKey.getDomainParam();

			curve = ECCCurves.getECCCuveEnum(domainParams);
			out.append("\t\t\tEC Curve: ");
			out.append(curve == null ? "Unknown" : curve.name());
			out.append("\n");
		}

		out.append("\t\t\tEC Public Key:\n");
		if (domainParams != null)
		{
			if (domainParams.getCurve() instanceof ECCurve.Fp)
			{
				out.append("\t\t\t\tP:\n");
				out.append(new DataBuffer(((ECCurve.Fp)domainParams.getCurve()).getQ().toByteArray()).getHexSplit(":", "\t\t\t\t\t", 48));
			}
			out.append("\t\t\t\tA:\n");
			out.append(new DataBuffer(domainParams.getCurve().getA().toBigInteger().toByteArray()).getHexSplit(":", "\t\t\t\t\t", 48));

			out.append("\t\t\t\tB:\n");
			out.append(new DataBuffer(domainParams.getCurve().getB().toBigInteger().toByteArray()).getHexSplit(":", "\t\t\t\t\t", 48));
		}
		
		out.append("\t\t\t\tX:\n");
		out.append(new DataBuffer(ecPublicPoint.getX().toByteArray()).getHexSplit(":", "\t\t\t\t\t", 48));

		out.append("\t\t\t\tY:\n");
		out.append(new DataBuffer(ecPublicPoint.getY().toByteArray()).getHexSplit(":", "\t\t\t\t\t", 48));

		if (domainParams != null)
		{
			out.append("\t\t\t\tQ:\n");
			out.append(new DataBuffer(domainParams.getN().toByteArray()).getHexSplit(":", "\t\t\t\t\t", 48));
			
			out.append("\t\t\t\tGx:\n");
			out.append(new DataBuffer(domainParams.getG().normalize().getXCoord().toBigInteger().toByteArray()).getHexSplit(":", "\t\t\t\t\t", 48));
			
			out.append("\t\t\t\tGy:\n");
			out.append(new DataBuffer(domainParams.getG().normalize().getYCoord().toBigInteger().toByteArray()).getHexSplit(":", "\t\t\t\t\t", 48));
			
			out.append("\t\t\t\tCofactor: ");
			out.append(domainParams.getH());
			out.append(" (0x");
			out.append(Integer.toHexString(domainParams.getH().intValue()));
			out.append(")\n");
		}
		else if (curve != null)
		{
			out.append("\t\t\t\tQ:\n");
			out.append(new DataBuffer(curve.getECParameter().getOrder().toByteArray()).getHexSplit(":", "\t\t\t\t\t", 48));
			
			out.append("\t\t\t\tGx:\n");
			out.append(new DataBuffer(curve.getECParameter().getGenerator().getAffineX().toByteArray()).getHexSplit(":", "\t\t\t\t\t", 48));
			
			out.append("\t\t\t\tGy:\n");
			out.append(new DataBuffer(curve.getECParameter().getGenerator().getAffineY().toByteArray()).getHexSplit(":", "\t\t\t\t\t", 48));

			out.append("\t\t\t\tCofactor: ");
			out.append(curve.getECParameter().getCofactor());
			out.append(" (0x");
			out.append(Integer.toHexString(curve.getECParameter().getCofactor()));
			out.append(")\n");
		}

		if (domainParams != null && domainParams.getSeed() != null)
		{
			out.append("\t\t\t\tSeed:\n");
			out.append(new DataBuffer(domainParams.getSeed()).getHexSplit(":", "\t\t\t\t\t", 48));
			out.append("\n");
		}

		return out.toString();
	}

	private String getRSAPublicKeyInfos() throws CVInvalidKeySourceException, CVMissingKeyException, CVKeyTypeNotSupportedException
	{
		StringBuilder out = new StringBuilder(2000);

		RSAPublicKeySpec rsaPublicKeySpec = m_publicKey.getRSAKey();
		out.append("\t\t\tRSA Public Key: (");
		out.append(m_publicKey.getKeyLength());
		out.append(" bit)\n");

		out.append("\t\t\t\tModulus: (");
		out.append(rsaPublicKeySpec.getModulus().bitLength());
		out.append(" bit)\n");

		out.append(new DataBuffer(rsaPublicKeySpec.getModulus().toByteArray()).getHexSplit(":", "\t\t\t\t\t", 48));

		out.append("\t\t\t\tExponent: ");
		out.append(rsaPublicKeySpec.getPublicExponent().toString());
		out.append(" (0x");
		out.append(rsaPublicKeySpec.getPublicExponent().toString(16));
		out.append(")\n");

		return out.toString();
	}
}