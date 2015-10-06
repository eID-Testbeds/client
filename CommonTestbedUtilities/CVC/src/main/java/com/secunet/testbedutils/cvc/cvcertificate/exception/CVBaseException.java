package com.secunet.testbedutils.cvc.cvcertificate.exception;


/**
 * This is the base exception for all CVCertificate exception
 * This exception has no function, and should never used only for a catch block
 * @author meier.marcus
 *
 */
public class CVBaseException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * constructor without message parameter 
	 * @see java.lang.Exception
	 */
	public CVBaseException() {
		
	}

	/**
	 * @see java.lang.Exception
	 * @param message consigns a message 
	 */
	public CVBaseException(String message) {
		super(message);
	}

	/**
	 * @see java.lang.Exception
	 * @param cause
	 */
	public CVBaseException(Throwable cause) {
		super(cause);
		
	}

	/**
	 * @see java.lang.Exception
	 * @param message
	 * @param cause
	 */
	public CVBaseException(String message, Throwable cause) {
		super(message, cause);
		
	}

}
