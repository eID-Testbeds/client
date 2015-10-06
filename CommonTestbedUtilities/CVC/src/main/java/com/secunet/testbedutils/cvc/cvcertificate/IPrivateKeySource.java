package com.secunet.testbedutils.cvc.cvcertificate;

import com.secunet.testbedutils.cvc.cvcertificate.exception.CVKeyTypeNotSupportedException;


/**
 * 
 * @brief This interface class describes some functions that are used by the CVClasses for signature creation 
 * 
 *   
 * This interface class will be used by the class
 * SignKeyHolder. This class will prepare some functions for key loading and signature
 * generation. 
 * 
 * @author meier.marcus
 * @version 1.0
 * @created 27-Aug-2009 14:19:20
 */
public abstract class IPrivateKeySource {

	/**
	 * @brief This function returns the key type  
	 * 
	 * One class can provide a key only for a specific key type. 
	 * 
	 * @return Returns the key type that the object stores
	 * @throws CVKeyTypeNotSupportedException 
	 * 
	 */
	public abstract KeyType getKeyType() throws CVKeyTypeNotSupportedException;
	/**
	 * @brief This function generate a signature for the given content and hash algorithm  
	 * 
	 * This function generate a signature of the given parameter rContent.
	 * The HASH algorithm will be defined with the parameter signGenOp. The function
	 * should 
	 * 
	 * 
	 * @param rContent Reference to a DataBuffer with content that should be signed by this function
	 * @param signGenOp Parameter with the Identifier for the signature generation operation
	 * @param rSignature Reference to a DataBuffer object where the generated signature shall be stored
	 * @throws CVKeyTypeNotSupportedException 

	 */
	public abstract void signContent(final DataBuffer rContent, TAAlgorithm signGenOp, DataBuffer rSignature) throws CVKeyTypeNotSupportedException;
}
