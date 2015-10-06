package com.secunet.testbedutils.cvc.cvcertificate.exception;



/**
 * 
 * @author meier.marcus
 *
 */
public class CVInvalidKeySourceException extends CVBaseException {
	static final long serialVersionUID = 1;
	/**
	 * @brief constructor
	 *
	 */
	public CVInvalidKeySourceException()
	{
		super("res:com.secunet.cvca.exception.CVInvalidKeySourceException");
	}
}
