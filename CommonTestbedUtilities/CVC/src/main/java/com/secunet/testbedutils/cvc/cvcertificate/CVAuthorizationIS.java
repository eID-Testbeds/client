package com.secunet.testbedutils.cvc.cvcertificate;



/**
 * This class represents the authorization for an inspection system certificate
 *
 * @author meier.marcus
 *
 */
public class CVAuthorizationIS extends CVAuthorization {
	
	// the oid for this certificate type
	private static final DataBuffer oid;

	/**
	 * Authorization
	 * read access to the eID application
	 */
	public static final int auth_Read_eID = 5;
	/**
	 * Authorization
	 * read access to DG 4 ( Iris )
	 */
	public static final int auth_Read_DG4 = 1;
	/**
	 * Authorization
	 * read access to DG 3 ( Fingerprint )
	 */
	public static final int auth_Read_DG3 = 0;
	
	static {
		oid = new DataBuffer();
        oid.append(Oids.OID_BSI_DE);
        oid.append(Oids.OID_TERM_ROLE);
        oid.append(Oids.OID_IS_TERMINAL);
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

		return TermType.InspectionSystem;
	}

	@Override
	public int size() {
		return 1;
	}


	public static String getText(int cvAuthorizationIS)
	{
		switch (cvAuthorizationIS)
		{
		case auth_Read_eID:
			return "eID";
		case auth_Read_DG3:
			return "DG3";
		case auth_Read_DG4:
			return "DG4";
		}
		return null;
	}
}
