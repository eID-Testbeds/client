package com.secunet.testbedutils.cvc.cvcertificate.exception;


/**
 * This exception will be thrown if a authority or a holder reference is to long  
 * @author meier.marcus
 *
 */
public class CVAuthorityRefNotValidException extends CVBaseException {
	static final long serialVersionUID = 1;
	/**
	 * 
	 * @brief constructor 
	 *
	 */
	public CVAuthorityRefNotValidException()
	{
		super("res:com.secunet.cvca.exception.CVAuthorityRefNotValidException");
	}
}
