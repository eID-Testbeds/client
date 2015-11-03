package com.secunet.testbedutils.cvc.cvcertificate.exception;


/**
 * Something is wrong with certificate date
 * @author neunkirchen.bernd
 *
 */
public class CVUnknownAlgorithmException extends CVBaseException {
	static final long serialVersionUID = 1;
	/**
	 * @brief constructor
	 *
	 */
	public CVUnknownAlgorithmException()
	{
		super("res:com.secunet.cvca.exception.CVUnknownAlgorithmException");
	}
}
