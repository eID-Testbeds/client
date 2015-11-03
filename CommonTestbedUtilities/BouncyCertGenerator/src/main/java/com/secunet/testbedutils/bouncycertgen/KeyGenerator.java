package com.secunet.testbedutils.bouncycertgen;

import java.security.InvalidAlgorithmParameterException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SecureRandom;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.bouncycastle.jce.ECNamedCurveTable;
import org.bouncycastle.jce.spec.ECNamedCurveParameterSpec;

/**
 * @author Lukasz Kubik, secunet AG
 *
 */
public class KeyGenerator {
	private static final Logger logger = Logger.getLogger(KeyGenerator.class
			.getName());

	// No instances allowed ...
	private KeyGenerator() {
	}

	/**
	 * Create a RSA key pair with the given key length
	 * 
	 * @param keylength
	 *            The bit length to use
	 * @return {@link KeyPair} The generated key pair
	 */
	public static KeyPair generateRSAPair(int keylength) {
		KeyPairGenerator generator;
		try {
			generator = KeyPairGenerator.getInstance("RSA", "BC");
			return generateKeyPair(generator , keylength);
		} catch (NoSuchAlgorithmException e) {
			logger.log(
					Level.SEVERE,
					"No suitable algorithm was found:" + System.getProperty("line.separator")
							+ e.getMessage());
			e.printStackTrace();
		} catch (NoSuchProviderException e) {
			logger.log(
					Level.SEVERE,
					"No suitable provider was found:" + System.getProperty("line.separator")
							+ e.getMessage());
		}
		return null;
	}

	/**
	 * Create a DSA key pair with the given key length
	 * 
	 * @param keylength The bit length to use
	 * @return {@link KeyPair} The generated key pair
	 */
	public static KeyPair generateDSAPair(int keylength) {
		KeyPairGenerator generator;
		try {
			generator = KeyPairGenerator.getInstance("DSA", "BC");
			return generateKeyPair(generator , keylength);
		} catch (NoSuchAlgorithmException e) {
			logger.log(
					Level.SEVERE,
					"No suitable algorithm was found:" + System.getProperty("line.separator")
							+ e.getMessage());
			e.printStackTrace();
		} catch (NoSuchProviderException e) {
			logger.log(
					Level.SEVERE,
					"No suitable provider was found:" + System.getProperty("line.separator")
							+ e.getMessage());
		}
		return null;
	}

	/**
	 * Create a DSA key pair using the given curve name
	 * 
	 * @param {@link String} curveName The name of the curve to use. Valid options are:<br>
	 * <ul>
	 * <li>sect233k1</li>
	 * <li>sect233r1</li>
	 * <li>sect239k1</li>
	 * <li>sect283k1</li>
	 * <li>sect283r1</li>
	 * <li>sect409k1</li>
	 * <li>sect409r1</li>
	 * <li>sect571k1</li>
	 * <li>sect571r1</li>
	 * <li>secp224k1</li>
	 * <li>secp224r1</li>
	 * <li>secp256k1</li>
	 * <li>secp256r1</li>
	 * <li>secp384r1</li>
	 * <li>secp521r1</li>
	 * <li>brainpoolP256r1</li>
	 * <li>brainpoolP384r1</li>
	 * <li>brainpoolP512r1</li>
	 * </ul><br> 
	 * @return {@link KeyPair} The generated key pair
	 */
	public static KeyPair generateECPair(String curveName) {
		KeyPairGenerator generator;
		try {
			generator = KeyPairGenerator.getInstance("ECDSA", "BC");
			ECNamedCurveParameterSpec ecSpec = ECNamedCurveTable.getParameterSpec(curveName);
			generator.initialize(ecSpec, new SecureRandom());
			return generator.generateKeyPair();
		} catch (NoSuchAlgorithmException e) {
			logger.log(
					Level.SEVERE,
					"No suitable algorithm was found:" + System.getProperty("line.separator")
							+ e.getMessage());
			e.printStackTrace();
		} catch (NoSuchProviderException e) {
			logger.log(
					Level.SEVERE,
					"No suitable provider was found:" + System.getProperty("line.separator")
							+ e.getMessage());
		} catch (InvalidAlgorithmParameterException e) {
			logger.log(
					Level.SEVERE,
					"No suitable algorithm was found:" + System.getProperty("line.separator")
							+ e.getMessage());
		}
		return null;
	}

	// generate the key pair with the given length
	private static KeyPair generateKeyPair(
			KeyPairGenerator generator,
			int keylength) {
		generator.initialize(keylength, new SecureRandom());
		KeyPair keyPair = generator.generateKeyPair();
		return keyPair;
	}

}
