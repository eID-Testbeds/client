package com.secunet.testbedutils.cvc.cvcertificate;
/**
 * identifier for the key type
 * @author meier.marcus
 * @version 1.0
 * @created 27-Aug-2009 14:19:24
 */
public enum KeyType {
	/**
	 * RSA key identifier
	 */
	KEY_RSA,
	/**
	 * ECC key identifier
	 */
	KEY_ECDSA,
	/**
	 * unknown key type
	 */
	KEY_UNDEFINED
}