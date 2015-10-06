package com.secunet.testbedutils.cvc.cvcertificate.exception;



/**
 * secunet Security Networks AG
 *
 * 	Excpetion raises if the CHR is too long
 *
 * @author meyer.christopher 
 * @version 1.0
 * @created 12.05.2010
 */
public class CVCertificateHolderReferenceTooLong  extends CVBaseException {

	static final long serialVersionUID = 1;
	/**
	 * @brief constructor
	 *
	 */
	public CVCertificateHolderReferenceTooLong()
	{
		super("res:com.secunet.cvca.exception.CVCertificateHolderReferenceTooLong");
	}
		
}