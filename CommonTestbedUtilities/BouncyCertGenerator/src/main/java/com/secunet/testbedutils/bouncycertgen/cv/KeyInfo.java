package com.secunet.testbedutils.bouncycertgen.cv;

import java.security.KeyPair;
import java.security.spec.AlgorithmParameterSpec;

/**
 * @class KeyInfo
 * @brief This class contains key information.
 * @author neunkirchen.bernd
 *
 */
public class KeyInfo {
	private String m_name;
	private String m_filePrivateKey;
	private String m_filePublicKey;
	private String m_algorithm;
	private String m_type;
	
	private AlgorithmParameterSpec m_keyParamSpec;
	private KeyPair m_keyPair;

	/**
	 * Initializes new KeyInfo object.
	 * @param name Name of the key.
	 */
	public KeyInfo(String name) {
		this.m_name = name;
	}
	
	/**
	 * Gets key name.
	 * @return Key Name.
	 */
	public String getName() {
		return m_name;
	}

	/**
	 * Gets file path of the private key.
	 * @return File path of the private key.
	 */
	public String getFilePrivateKey() {
		return m_filePrivateKey;
	}

	/**
	 * Sets file path of the private key.
	 * @param filePrivateKey File path of the private key.
	 */
	public void setFilePrivateKey(String filePrivateKey) {
		this.m_filePrivateKey = filePrivateKey;
	}

	/**
	 * Gets file path of the public key.
	 * @return File path of the public key.
	 */
	public String getFilePublicKey() {
		return m_filePublicKey;
	}

	/**
	 * Sets file path of the public key.
	 * @param filePublicKey File path of the public key.
	 */
	public void setFilePublicKey(String filePublicKey) {
		this.m_filePublicKey = filePublicKey;
	}

	/**
	 * Gets key algorithm.
	 * @return Key algorithm.
	 */
	public String getAlgorithm() {
		return m_algorithm;
	}

	/**
	 * Sets key key algorithm.
	 * @param algorithm Key algorithm.
	 */
	public void setAlgorithm(String algorithm) {
		this.m_algorithm = algorithm;
	}
	
	/**
	 * Gets key type.
	 * @return Key type.
	 */
	public String getType() {
		return m_type;
	}

	/**
	 * Sets key type.
	 * @param type Key type.
	 */
	public void setType(String type) {
		this.m_type = type;
	}

	/**
	 * Sets algorithm parameter.
	 * @return Algorithm parameter.
	 */
	public AlgorithmParameterSpec getKeyParamSpec() {
		return m_keyParamSpec;
	}

	/**
	 * Gets algorithm parameter.
	 * @param keyParamSpec Algorithm parameter.
	 */
	public void setKeyParamSpec(AlgorithmParameterSpec keyParamSpec) {
		this.m_keyParamSpec = keyParamSpec;
	}

	/**
	 * Gets key pair.
	 * @return Key pair.
	 */
	public KeyPair getKeyPair() {
		return m_keyPair;
	}

	/**
	 * Sets key pair.
	 * @param keyPair Key pair.
	 */
	public void setKeyPair(KeyPair keyPair) {
		this.m_keyPair = keyPair;
	}
}
