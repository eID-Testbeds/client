package com.secunet.testbedutils.cvc.cvcertificate.exception;


/**
 * This exception will be thrown if an key or the domain parameter are unavailable  
 * @author meier.marcus
 *
 */
public class CVMissingKeyException extends CVBaseException {
	static final long serialVersionUID = 1;
	/**
	 * @brief constructor
	 *
	 */
	public CVMissingKeyException()
	{
		super("res:com.secunet.cvca.exception.CVMissingKeyException");
	}
}
