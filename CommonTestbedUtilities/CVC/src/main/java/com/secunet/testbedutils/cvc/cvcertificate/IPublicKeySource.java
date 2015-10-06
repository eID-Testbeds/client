package com.secunet.testbedutils.cvc.cvcertificate;

import java.security.spec.RSAPublicKeySpec;

import org.bouncycastle.jce.spec.ECParameterSpec;

import com.secunet.testbedutils.cvc.cvcertificate.exception.CVKeyTypeNotSupportedException;

/**
 * @class IKeySource
 * @brief This interface class describes some functions that are used by the CVClasses to  getting the public key.
 * 
 *   
 * The CVPubKeyHolder will use this class to get the key information
 * for the public key data object.
 * 
 * @author meier.marcus
 * @version 1.0
 * @created 27-Aug-2009 14:19:20
 */
public abstract class IPublicKeySource {

	/**
	 * @brief This function returns the key type  
	 * 
	 * One class can provide a key
	 * only for a specific key type. It should not change the key type during lifetime.
	 * Functions that are not supported by the implementation class, must thrown a
	 * KeyTypeNotSupportedException
	 * 
	 * @return Returns the key type that the object stores
	 * @throws CVKeyTypeNotSupportedException
	 * 
	 */
	public abstract KeyType getKeyType() throws CVKeyTypeNotSupportedException;

	/**
	 * @brief This function returns the RSA key components  
	 * This function returns
	 * the public RSA key parameter. If this function is not supported by the
	 * implementation it must thrown a KeyTypeNotSupportedException .
	 * @return Reference to a RSAKey object, where the key shall be stored
	 * @throws CVKeyTypeNotSupportedException
	 * 
	 */
	public abstract RSAPublicKeySpec getRSAPublicKey() throws CVKeyTypeNotSupportedException;

	/**
	 * @brief This function returns the ECDSA public key components  This
	 * function returns the public point of an EC key. If this function is not
	 * supported by the implementation it must thrown a KeyTypeNotSupportedException
	 * .
	 * @return Reference to a ECDSAPubPoint object, where the public point of an ecdsa key  shall be stored
	 * @throws CVKeyTypeNotSupportedException
	 */
	public abstract ECPubPoint getECDSAPublicPoint() throws CVKeyTypeNotSupportedException;

	/**
	 * @brief This function returns the ECDSA domain parameter key components
	 * 
	 * This function returns the domain parameter for the known EC key. If an EC isn't
	 * supported it must thrown a KeyTypeNotSupportedException.
	 * 
	 * @return Reference to a ECDSADomain object, where the domain parameter of an ECDSA key shall be stored
	 * @throws CVKeyTypeNotSupportedException
	 */
	public abstract ECParameterSpec getECDSADomain() throws CVKeyTypeNotSupportedException;

}