package com.secunet.testbedutils.cvc.cvcertificate;

import com.secunet.testbedutils.cvc.cvcertificate.exception.CVBufferNotEmptyException;
import com.secunet.testbedutils.cvc.cvcertificate.exception.CVDecodeErrorException;
import com.secunet.testbedutils.cvc.cvcertificate.exception.CVInvalidOidException;
import com.secunet.testbedutils.cvc.cvcertificate.exception.CVTagNotFoundException;

/**
 * @class CCVHolderAuth
 * @brief This class store the role and the read access rights for a cvcertificate
 * 
 * This class is used to store the information
 * about the role and the access rights for the certificate owner.
 * 
 * @author meier.marcus
 * @version 1.0
 * @created 27-Aug-2009 14:18:56
 */
public class CVHolderAuth {

	/**
	 * < This member stores the authorization for an certificate
	 */
	protected CVAuthorization m_Auth = null;

	/**
	 * @brief constructor
	 *
	 */
	public CVHolderAuth(){

	
	}

	/**
	 * This function generate a DataBuffer with the certificate holder authorization
	 * for the certificate
	 * 
	 * @return returns the certificate holder authorization 
	 */
	public DataBuffer genHolderAuth(){
		DataBuffer output = new DataBuffer();
		  
		// examines the terminal type
		switch(m_Auth.getTermType())
		{
		case InspectionSystem:
			if(m_Auth.instanceOid() != null) {
				TLV.append(output, CVCertificate.s_CvOIDTag, m_Auth.instanceOid());
			} else {
				TLV.append(output, CVCertificate.s_CvOIDTag, CVAuthorizationIS.Oid());
			}
		  	break;
		case AuthenticationTerminal:
			if(m_Auth.instanceOid() != null) {
				TLV.append(output, CVCertificate.s_CvOIDTag, m_Auth.instanceOid());
			} else {
				TLV.append(output, CVCertificate.s_CvOIDTag, CVAuthorizationAT.Oid());
			}
		  	break;
		case SignatureTerminal:
			if(m_Auth.instanceOid() != null) {
				TLV.append(output, CVCertificate.s_CvOIDTag, m_Auth.instanceOid());
			} else {
				TLV.append(output, CVCertificate.s_CvOIDTag, CVAuthorizationST.Oid());
			}
		  	break;
		}
		
		output.append(m_Auth.genAuth());
		
		return output;
	}

	/**
	 * This function decode the holder authorization from a cv certificate
	 * 
	 * @param buffer
	 * @throws CVTagNotFoundException 
	 * @throws CVInvalidOidException 
	 * @throws CVBufferNotEmptyException 
	 * @throws CVDecodeErrorException 
	 */
	public void parseRawHolderAuth(DataBuffer buffer) throws CVTagNotFoundException, CVInvalidOidException, CVBufferNotEmptyException, CVDecodeErrorException{
		DataBuffer data = new DataBuffer(buffer);

		TLV extrOID = TLV.extract(data);
  
		if(extrOID.getTag() == CVCertificate.s_CvOIDTag)
		{
		
			// examines the terminal type
			if(extrOID.getValue().equals(CVAuthorizationIS.Oid()))
			{
				//m_rLog << "The Certificate holder authorization OID seems to be valid, found an authorization for a inspection system" << std::endl;
				//set the type
				m_Auth = new CVAuthorizationIS();
			} 
			else if(extrOID.getValue().equals(CVAuthorizationAT.Oid())) 
			{
				//m_rLog << "The Certificate holder authorization OID seems to be valid, found an authorization for a authentication terminal" << std::endl;
				//set the type
				m_Auth = new CVAuthorizationAT();
			}
			else if(extrOID.getValue().equals(CVAuthorizationST.Oid())) 
			{
				//m_rLog << "The Certificate holder authorization OID seems to be valid, found an authorization for a signature terminal" << std::endl;
				//set the type
				m_Auth = new CVAuthorizationST();
			}
			else
			{
				//m_rLog << "Certificate holder authorization OID invalid or unknown" << std::endl;
				throw new CVInvalidOidException();
			}
			m_Auth.parseAuth(data);
  
		} 
		else 
		{
			//m_rLog << "0x06 OID tag not found" << std::endl;
			throw new CVTagNotFoundException("0x06");
		}
	}

	
	/**
	 * This function returns the authorization for a inspection system as union
	 * 
	 * @return returns the authorization object
	 */
	public CVAuthorization getAuth(){
		return m_Auth;
	}

	/**
	 * This function sets the authorization for an certificate
	 * 
	 * @param auth consigns the auth object
	 */
	public void setAuth(CVAuthorization auth){
		m_Auth = auth;
	}



}
