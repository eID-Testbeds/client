package com.secunet.testbedutils.cvc.cvcertificate.exception;


/**
 * This exception will be thrown if the stored EC point length odd
 * @author meier.marcus
 *
 */
public class CVInvalidECPointLengthException extends CVBaseException {
	static final long serialVersionUID = 1;
	/**
	 * @brief constructor
	 *
	 */
	public CVInvalidECPointLengthException()
	{
		super("res:com.secunet.cvca.exception.CVInvalidECPointLengthException");
	}
}
