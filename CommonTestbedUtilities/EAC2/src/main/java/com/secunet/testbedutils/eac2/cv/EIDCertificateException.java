package com.secunet.testbedutils.eac2.cv;

import com.secunet.testbedutils.eac2.EIDException;



public class EIDCertificateException extends EIDException {

	/**
	 * random uid
	 */
	private static final long serialVersionUID = 7341631953115897698L;

	public EIDCertificateException() {
		super();
	}

	public EIDCertificateException(String message, Throwable cause) {
		super(message, cause);
	}

	public EIDCertificateException(String message) {
		super(message);
	}

	public EIDCertificateException(Throwable cause) {
		super(cause);
	}

}
