package com.secunet.testbedutils.cvc.cvcertificate.exception;


/**
 * some forbidden characters found
 * @author meier.marcus
 *
 */
public class CVIA5StringException extends CVBaseException {
	static final long serialVersionUID = 1;
	/**
	 * @brief constructor
	 *
	 */
	public CVIA5StringException()
	{
		super("res:com.secunet.cvca.exception.CVIA5StringException");
	}
}
