package com.secunet.testbedutils.cvc.cvcertificate.exception;


/**
 * CertificateDescParseException
 * @author meier.marcus
 *
 */
public class CertificateDescParseException extends CVBaseException
{
	/**
	 *  
	 */
	private static final long serialVersionUID = 1L;
	
	/**
	 * default constructor
	 */
	public CertificateDescParseException()
	{
		super("res:com.secunet.crypto.TR03111.exception.CertificateDescParseException");
	}

	public CertificateDescParseException(Throwable cause)
	{
		super("res:com.secunet.crypto.TR03111.exception.CertificateDescParseException",cause);
	}
	
}
