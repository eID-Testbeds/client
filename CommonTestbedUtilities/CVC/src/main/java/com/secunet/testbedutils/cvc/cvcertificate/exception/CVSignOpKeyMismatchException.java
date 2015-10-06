package com.secunet.testbedutils.cvc.cvcertificate.exception;


/**
 * signature algorithm doesn't match to the given key source
 * @author meier.marcus
 *
 */
public class CVSignOpKeyMismatchException extends CVBaseException {
	static final long serialVersionUID = 1;
	/**
	 * @brief constructor
	 *
	 */
	public CVSignOpKeyMismatchException()
	{
		super("res:com.secunet.cvca.exception.CVSignOpKeyMismatchException");
	}
}
