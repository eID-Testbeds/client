package com.secunet.testbedutils.cvc.cvcertificate;

import com.secunet.testbedutils.cvc.cvcertificate.exception.CVInvalidKeySourceException;
import com.secunet.testbedutils.cvc.cvcertificate.exception.CVKeyTypeNotSupportedException;
import com.secunet.testbedutils.cvc.cvcertificate.exception.CVSignOpKeyMismatchException;

/**
 * @class  CCVSignKeyHolder
 * @brief  This class load, store the private key and generate the signature for a cv certificate
 * 
 * @author meier.marcus
 * @version 1.0
 * @created 27-Aug-2009 14:18:59
 */
public class CVSignKeyHolder {

	/**
	 * < This member holds the algorithm type for the signature generation
	 */
	protected TAAlgorithm m_algorithmType;
	/**
	 * < This member stores a pointer to an KeySource object
	 */
	protected IPrivateKeySource m_KeySource = null;
	
	/**
	 * @brief constructor 
	 * 
	 */
	public CVSignKeyHolder(){
		m_algorithmType = TAAlgorithm.UNDEFINED;
	}

	/**
	 * This function sets the hash algorithm type
	 * 
	 * @param type
	 */
	public void setAlgorithm(TAAlgorithm type){
		m_algorithmType = type;
	}

	/**
	 * This function returns the hash algorithm type
	 * 
	 * @return returns the currently used signature algorithm 
	 */
	public TAAlgorithm getAlgorithm(){
		return m_algorithmType;
	}

	/**
	 * @brief this method returns the used key type depending on the used signature algorithm
	 * 
	 * @return returns the key type 
	 */
	public KeyType getKeyType(){
		if (m_algorithmType == TAAlgorithm.RSA_v1_5_SHA_1 ||
		      	m_algorithmType == TAAlgorithm.RSA_v1_5_SHA_256 ||
		      	m_algorithmType == TAAlgorithm.RSA_v1_5_NONE ||
		      	m_algorithmType == TAAlgorithm.RSA_PSS_SHA_1 ||
		      	m_algorithmType == TAAlgorithm.RSA_PSS_SHA_256 ||
		      	m_algorithmType == TAAlgorithm.RSA_PSS_NONE)
		      {
		      	return KeyType.KEY_RSA;
		      }
		      else if (m_algorithmType == TAAlgorithm.ECDSA_SHA_1 ||
		      	m_algorithmType == TAAlgorithm.ECDSA_SHA_224 ||
		      	m_algorithmType == TAAlgorithm.ECDSA_SHA_256 ||
		      	m_algorithmType == TAAlgorithm.ECDSA_NONE)
		      {
		      	return KeyType.KEY_ECDSA;
		      }
		      
		      return KeyType.KEY_UNDEFINED;
	}


	/**
	 * This function set the key source object for this class
	 * 
	 * @param pSource
	 */
	public void setKeySource(IPrivateKeySource pSource){
		m_KeySource = pSource;
	}

	/**
	 * This function returns the key source object of this class
	 * 
	 * @return returns the key source object of this class 
	 */
	public IPrivateKeySource getKeySource(){
		return m_KeySource;
	}
	
	/**
	 * This function generate a signature
	 * 
	 * @param content consigns the plain data
	 * @return returns the signature as data buffer 
	 * @throws CVInvalidKeySourceException 
	 * @throws CVSignOpKeyMismatchException 
	 * @throws CVKeyTypeNotSupportedException 
	 */
	public DataBuffer signContent(DataBuffer content) throws CVInvalidKeySourceException, CVSignOpKeyMismatchException, CVKeyTypeNotSupportedException{
		DataBuffer out = new DataBuffer();
	  
	    if(m_KeySource == null)
	    {
	    	throw new CVInvalidKeySourceException();
	    }
	      
	      
	    if(m_algorithmType == TAAlgorithm.RSA_PSS_SHA_1 ||
	    	m_algorithmType == TAAlgorithm.RSA_PSS_SHA_256 ||
		    m_algorithmType == TAAlgorithm.RSA_PSS_NONE ||
	      	m_algorithmType == TAAlgorithm.RSA_v1_5_SHA_1 ||
	      	m_algorithmType == TAAlgorithm.RSA_v1_5_SHA_256 ||
	      	m_algorithmType == TAAlgorithm.RSA_v1_5_NONE)
	    {
	      	if(m_KeySource.getKeyType() != KeyType.KEY_RSA)
	      	{

	      		throw new CVSignOpKeyMismatchException();
	      	}
	      
	    } 
	    else if(m_algorithmType == TAAlgorithm.ECDSA_SHA_1 ||
	     	m_algorithmType == TAAlgorithm.ECDSA_SHA_224 ||
	      	m_algorithmType == TAAlgorithm.ECDSA_SHA_256 || 
	     	m_algorithmType == TAAlgorithm.ECDSA_NONE)
	    {
	     	if(m_KeySource.getKeyType() != KeyType.KEY_ECDSA)
	      	{
	     	
	     		throw new CVSignOpKeyMismatchException();
	      	}
	    }
  
	    // all seems ok now sign the content
	    m_KeySource.signContent(content,m_algorithmType,out);
	      
	    return out;
	}

}