package com.secunet.testbedutils.cvc.cvcertificate;


/**
 * 
 * @brief defines the needed OIDs for the certificate extensions
 * @author meier.marcus
 * @version 1.0
 * @created 27-Aug-2009 14:19:13
 */
public enum CVExtensionType {
	/**
	 * define for the description extension   
	 */
	extDescription,
	/**
	 * plain format extension description 
	 */
	extPlainFormat,
	/**
	 * HTML format extension description 
	 */
	extHTMLFormat,
	/**
	 * PDF format extension description
	 */
	extPDFFormat,
	/**
	 * terminal sector pub key extension
	 */
	extSector
}