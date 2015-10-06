package com.secunet.testbedutils.cvc.cvcertificate;


/**
 * CVCA signature algorithm
 * @author meier.marcus
 * @version 1.0
 * @created 27-Aug-2009 14:19:55
 */
public enum TAAlgorithm {
	/**
	 * Undefined algorithm 
	 */
	UNDEFINED,
	/**
	 * RSA v1.5 with SHA 1 
	 */
	RSA_v1_5_SHA_1,
	/**
	 * RSA v1.5 with SHA 256 
	 */
	RSA_v1_5_SHA_256,
	/**
	 * RSA v1.5 without hashing
	 */
	RSA_v1_5_NONE,
	/**
	 * RSA PSS with SHA 1 
	 */
	RSA_PSS_SHA_1,
	/**
	 * RSA PSS with SHA 256 
	 */
	RSA_PSS_SHA_256,
	/**
	 * RSA PSS without hashing 
	 */
	RSA_PSS_NONE,
	/**
	 * ECC with SHA1 
	 */
	ECDSA_SHA_1,
	/**
	 * ECC with SHA224 
	 */
	ECDSA_SHA_224,
	/**
	 * ECC with SHA256 
	 */
	ECDSA_SHA_256,
	/**
	 * ECDSA without hashing
	 */
	ECDSA_NONE;
	
	

}