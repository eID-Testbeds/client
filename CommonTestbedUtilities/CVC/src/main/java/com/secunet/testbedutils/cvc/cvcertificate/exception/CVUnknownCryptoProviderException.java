package com.secunet.testbedutils.cvc.cvcertificate.exception;


/**
 * Something is wrong with certificate date
 * @author neunkirchen.bernd
 *
 */
public class CVUnknownCryptoProviderException extends CVBaseException {
	static final long serialVersionUID = 1;
	/**
	 * @brief constructor
	 *
	 */
	public CVUnknownCryptoProviderException()
	{
		super("res:com.secunet.cvca.exception.CVUnknownCryptoProviderException");
	}
}
