package com.secunet.testbedutils.cvc.cvcertificate;



/**
 * This class represents the authorization for the signature terminal certificate
 *
 * @author meier.marcus
 *
 */
public class CVAuthorizationST extends CVAuthorization {
	
	// the oid for this certificate type
	private static final DataBuffer oid;
	
	/**
	 * Authorization
	 * generate qualified signature
	 */
	public static final int auth_GenerateQualifiedSignature = 	1;
	/**
	 * Authorization
	 * generate simple signature
	 */
	public static final int auth_GenerateSignature = 			0;
	
	static {
		oid = new DataBuffer();
        oid.append(Oids.OID_BSI_DE);
        oid.append(Oids.OID_TERM_ROLE);
        oid.append(Oids.OID_ST_TERMINAL);
	}	

	/**
     * @brief This method returns the OId for this object type
     *
     * @return OID as data buffer
     */
    public static DataBuffer Oid()
    {
    	return oid;
    }

	@Override
	public TermType getTermType() {
		return TermType.SignatureTerminal;
	}

	@Override
	public int size() {
		return 1;
	}

	public static String getText(int cvAuthorizationST)
	{
		switch (cvAuthorizationST)
		{
		case auth_GenerateQualifiedSignature:
			return "GenerateQualifiedSignature";
		case auth_GenerateSignature:
			return "GenerateSignature";
		}
		return null;
	}
}
