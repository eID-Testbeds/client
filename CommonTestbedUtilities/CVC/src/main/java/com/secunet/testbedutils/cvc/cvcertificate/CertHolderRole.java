package com.secunet.testbedutils.cvc.cvcertificate;

/**
 * This enum describes the possible certificate holder roles
 * @author meier.marcus
 * @version 1.0
 * @created 27-Aug-2009 14:19:00
 */
public enum CertHolderRole {
	/**
	 * Terminal or Inspection system role
	 */
	Terminal,
	/**
	 * role of a foreign DV certificate 
	 */
	DVforeign,
	/**
	 * role of a national DV certificate
	 */
	DVdomestic,
	/**
	 * CA or link CA certificate role
	 */
	CVCA
}