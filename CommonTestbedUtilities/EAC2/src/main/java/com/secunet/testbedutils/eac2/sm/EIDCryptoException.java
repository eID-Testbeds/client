package com.secunet.testbedutils.eac2.sm;

import com.secunet.testbedutils.eac2.EIDException;


public class EIDCryptoException extends EIDException {

	/**
	 * random uid
	 */
	private static final long serialVersionUID = -6925496608861317673L;

	public EIDCryptoException() {
		super();
	}

	public EIDCryptoException(String message, Throwable cause) {
		super(message, cause);
	}

	public EIDCryptoException(String message) {
		super(message);
	}

	public EIDCryptoException(Throwable cause) {
		super(cause);
	}

}
