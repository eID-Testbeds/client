package com.secunet.testbedutils.cvc.cvcertificate.exception;


/**
 * Something is wrong with certificate date
 * @author meier.marcus
 *
 */
public class CVInvalidDateException extends CVBaseException {
	static final long serialVersionUID = 1;
	/**
	 * @brief constructor
	 *
	 */
	public CVInvalidDateException()
	{
		super("res:com.secunet.cvca.exception.CVInvalidDateException");
	}
}
