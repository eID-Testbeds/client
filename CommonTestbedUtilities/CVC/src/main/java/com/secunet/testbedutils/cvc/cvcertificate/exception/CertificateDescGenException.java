package com.secunet.testbedutils.cvc.cvcertificate.exception;


/**
 * 
 * @author meier.marcus
 *
 */
public class CertificateDescGenException extends CVBaseException
{
	/**
	 *  
	 */
	private static final long serialVersionUID = 1L;
	
	/**
	 * default constructor
	 */
	public CertificateDescGenException()
	{
		super("res:com.secunet.crypto.TR03111.exception.CertificateDescGenException");
	}

	public CertificateDescGenException(Throwable cause)
	{
		super("res:com.secunet.crypto.TR03111.exception.CertificateDescGenException",cause);
	}
}
