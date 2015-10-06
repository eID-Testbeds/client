package com.secunet.testbedutils.cvc.cvcertificate;



/**
 * This class represents the authorization for the authentication terminal certificate
 *
 * @author meier.marcus
 *
 */
public class CVAuthorizationAT extends CVAuthorization {
	
	// the oid for this certificate type
	private static final DataBuffer oid;

	/**
	 * Authorization
	 * write access to DG 17
	 */
	public static final int auth_Write_DG17 = 37;
	/**
	 * Authorization
	 * write access to DG 18
	 */
	public static final int auth_Write_DG18 = 36;
	/**
	 * Authorization
	 * write access to DG 19
	 */
	public static final int auth_Write_DG19 = 35;
	/**
	 * Authorization
	 * write access to DG 20
	 */
	public static final int auth_Write_DG20 = 34;
	/**
	 * Authorization
	 * write access to DG 21
	 */
	public static final int auth_Write_DG21 = 33;
	/**
	 * Authorization
	 * read access to DG 1
	 */
	public static final int auth_Read_DG1 = 8;
	/**
	 * Authorization
	 * read access to DG 2
	 */
	public static final int auth_Read_DG2 = 9;
	/**
	 * Authorization
	 * read access to DG 3
	 */
	public static final int auth_Read_DG3 = 10;
	/**
	 * Authorization
	 * read access to DG 4
	 */
	public static final int auth_Read_DG4 = 11;
	/**
	 * Authorization
	 * read access to DG 5
	 */
	public static final int auth_Read_DG5 = 12;
	/**
	 * Authorization
	 * read access to DG 6
	 */
	public static final int auth_Read_DG6 = 13;
	/**
	 * Authorization
	 * read access to DG 7
	 */
	public static final int auth_Read_DG7 = 14;
	/**
	 * Authorization
	 * read access to DG 8
	 */
	public static final int auth_Read_DG8 = 15;
	/**
	 * Authorization
	 * read access to DG 9
	 */
	public static final int auth_Read_DG9 = 16;
	/**
	 * Authorization
	 * read access to DG 10
	 */
	public static final int auth_Read_DG10 = 17;
	/**
	 * Authorization
	 * read access to DG 11
	 */
	public static final int auth_Read_DG11 = 18;
	/**
	 * Authorization
	 * read access to DG 12
	 */
	public static final int auth_Read_DG12 = 19;
	/**
	 * Authorization
	 * read access to DG 13
	 */
	public static final int auth_Read_DG13 = 20;
	/**
	 * Authorization
	 * read access to DG 14
	 */
	public static final int auth_Read_DG14 = 21;
	/**
	 * Authorization
	 * read access to DG 15
	 */
	public static final int auth_Read_DG15 = 22;
	/**
	 * Authorization
	 * read access to DG 16
	 */
	public static final int auth_Read_DG16 = 23;
	/**
	 * Authorization
	 * read access to DG 17
	 */
	public static final int auth_Read_DG17 = 24;
	/**
	 * Authorization
	 * read access to DG 18
	 */
	public static final int auth_Read_DG18 = 25;
	/**
	 * Authorization
	 * read access to DG 19
	 */
	public static final int auth_Read_DG19 = 26;
	/**
	 * Authorization
	 * read access to DG 20
	 */
	public static final int auth_Read_DG20 = 27;
	/**
	 * Authorization
	 * read access to DG 21
	 */
	public static final int auth_Read_DG21 = 28;
	/**
	 * Authorization
	 * Install qualified certificate for the eSign application
	 */
	public static final int auth_InstallQulifiedCertificate = 7;
	/**
	 * Authorization
	 * Install certificate for the eSign application
	 */
	public static final int auth_InstallCertificate = 6;
	/**
	 * Authorization
	 * The certificate owner can use the pin management functions
	 */
	public static final int auth_PINManagement = 5;
	/**
	 * Authorization
	 * The certificate owner can use the CAN to establish a secure messaging context
	 */
	public static final int auth_CANAllowed = 4;
	/**
	 * Authorization
	 * The certificate owner can use the privileged terminal functions
	 */
	public static final int auth_PrivilegedTerminal = 3;
	/**
	 * Authorization
	 * The certificate owner can use the restricted identification functions
	 */
	public static final int auth_RestrictedIdentification = 2;
	/**
	 * Authorization
	 *The certificate owner can use the community ID verification function
	 */
	public static final int auth_CommunityIDVerification = 1;
	/**
	 * Authorization
	 * The owner of the certificate can use the age verification function
	 */
	public static final int auth_AgeVerification = 0;
	
	static {
		oid = new DataBuffer();
        oid.append(Oids.OID_BSI_DE);
        oid.append(Oids.OID_TERM_ROLE);
        oid.append(Oids.OID_AT_TERMINAL);
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
		return TermType.AuthenticationTerminal;
	}

	@Override
	public int size() {
		return 5;
	}

	public static String getText(int cvAuthorizationAT)
	{
		switch (cvAuthorizationAT)
		{
		case auth_AgeVerification:
			return "AgeVerification";
		case auth_CommunityIDVerification:
			return "CommunityIDVerification";
		case auth_RestrictedIdentification:
			return "RestrictedIdentification";
		case auth_PrivilegedTerminal:
			return "PrivilegedTerminal";
		case auth_CANAllowed:
			return "CANAllowed";
		case auth_PINManagement:
			return "PINManagement";
		case auth_InstallCertificate:
			return "InstallCertificate";
		case auth_InstallQulifiedCertificate:
			return "InstallQualifiedCertificate";
		case auth_Read_DG1:
			return "DG1";
		case auth_Read_DG2:
			return "DG2";
		case auth_Read_DG3:
			return "DG3";
		case auth_Read_DG4:
			return "DG4";
		case auth_Read_DG5:
			return "DG5";
		case auth_Read_DG6:
			return "DG6";
		case auth_Read_DG7:
			return "DG7";
		case auth_Read_DG8:
			return "DG8";
		case auth_Read_DG9:
			return "DG9";
		case auth_Read_DG10:
			return "DG10";
		case auth_Read_DG11:
			return "DG11";
		case auth_Read_DG12:
			return "DG12";
		case auth_Read_DG13:
			return "DG13";
		case auth_Read_DG14:
			return "DG14";
		case auth_Read_DG15:
			return "DG15";
		case auth_Read_DG16:
			return "DG16";
		case auth_Read_DG17:
		case auth_Write_DG17:
			return "DG17";
		case auth_Read_DG18:
		case auth_Write_DG18:
			return "DG18";
		case auth_Read_DG19:
		case auth_Write_DG19:
			return "DG19";
		case auth_Read_DG20:
		case auth_Write_DG20:
			return "DG20";
		case auth_Read_DG21:
		case auth_Write_DG21:
			return "DG21";
		}
		return null;
	}
}
