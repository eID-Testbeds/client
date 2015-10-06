package com.secunet.testbedutils.cvc.cvcertificate;

import com.secunet.testbedutils.cvc.cvcertificate.exception.CVAuthorityRefNotValidException;
import com.secunet.testbedutils.cvc.cvcertificate.exception.CVCertificateHolderReferenceInvalidCountryCode;
import com.secunet.testbedutils.cvc.cvcertificate.exception.CVCertificateHolderReferenceTooLong;

/**
 * @author hellrung.andreas
 * 
 * Class for encoding and decoding the Certificate Holder Reference
 *
 */
public class CVCertificateHolderReference {

	private String m_strCountryCode = null;
	private String m_strMnemonic = null;
	private String m_strLongSequenceNumber = null;
	private String m_strShortSequenceNumber = null;
	private String m_strForeignCountryCode = null;
	
	/**
	 * @param strCertHolderRef String representation of Certificate Holder Reference
	 * @throws CVCertificateHolderReferenceInvalidCountryCode 
	 * @throws CVCertificateHolderReferenceTooLong 
	 * @throws CVAuthorityRefNotValidException 
	 */
	public CVCertificateHolderReference(String strCertHolderRef) throws CVCertificateHolderReferenceInvalidCountryCode, CVCertificateHolderReferenceTooLong, CVAuthorityRefNotValidException
	{
		CVCertificateHolderReference.verify(strCertHolderRef);
		
		m_strCountryCode = strCertHolderRef.substring(0,2);
		if ( ! m_strCountryCode.matches("[A-Z]{2}")) {
			throw new CVCertificateHolderReferenceInvalidCountryCode();
		}
		m_strMnemonic = strCertHolderRef.substring(2,strCertHolderRef.length()-5);
		
		m_strLongSequenceNumber= strCertHolderRef.substring(strCertHolderRef.length()-5);
		
		
		if ( (m_strForeignCountryCode = findForeignCountryCode()) != null) { 
			m_strShortSequenceNumber = m_strLongSequenceNumber.substring(2);
			m_strLongSequenceNumber = null;
		}
	}
	
	/**
	 * @param strCountryCode The ISO 3166-1 Alpha-2 Country Code of the certificate holder's country
	 * @param strMnemonic A unique identifier with maximum nine characters
	 * @param strForeignCountryCode The ISO 3166-1 encoded Country Code of the certifying CA country
	 * @param strShortSequenceNumber The three characters sequence number
	 */
	public CVCertificateHolderReference(String strCountryCode, String strMnemonic,String strForeignCountryCode, String strShortSequenceNumber)
	{
		m_strCountryCode = strCountryCode;
		m_strMnemonic = strMnemonic;
		m_strShortSequenceNumber = strShortSequenceNumber;
		m_strForeignCountryCode = strForeignCountryCode;
	}
	
	/**
	 * @param strCountryCode The ISO 3166-1 Alpha-2 Country Code of the certificate holder's country
	 * @param strMnemonic A unique identifier with maximum nine characters
	 * @param strLongSequenceNumber The five characters sequence number
	 */
	public CVCertificateHolderReference(String strCountryCode, String strMnemonic, String strLongSequenceNumber)
	{
		m_strCountryCode = strCountryCode;
		m_strMnemonic = strMnemonic;
		m_strLongSequenceNumber = strLongSequenceNumber;
	}
	
	public String toString()
	{
		return m_strCountryCode +m_strMnemonic + ((m_strLongSequenceNumber== null)? (m_strForeignCountryCode +m_strShortSequenceNumber):m_strLongSequenceNumber);
	}

	/**
	 * @return The ISO 3166-1 Alpha-2 Country Code of the certificate holder's country
	 */
	public String getCountryCode() {
		return m_strCountryCode;
	}

	/**
	 * @return The unique identifier with maximum nine characters
	 */
	public String getMnemonic() {
		return m_strMnemonic;
	}

	/**
	 * @return The five characters sequence number, Null if the Certificate Holder Reference follows the MAY - encoding of the Sequence number
	 */
	public String getLongSequenceNumber() {
		return m_strLongSequenceNumber;
	}

	/**
	 * @return The three characters sequence number, Null if the five-characters sequence number is used.
	 */
	public String getShortSequenceNumber() {
		return m_strShortSequenceNumber;
	}

	public void setNewSequenceNumber(Integer seq)
	{
		if(hasLongSequenceNumber())
		{
			m_strLongSequenceNumber = getCVSerialLong(seq);
		}
		else
		{
			m_strShortSequenceNumber = getCVSerialShort(seq);;
		}
	}
	
	public Integer getSequenceNumber()
	{
		if(hasLongSequenceNumber())
		{
			return Integer.valueOf(m_strLongSequenceNumber);
		}
		else
		{
			return Integer.valueOf(m_strShortSequenceNumber);
		}
		 
	}
	
	public boolean hasLongSequenceNumber()
	{
		return m_strLongSequenceNumber != null;
	}
	
	/**
	 * @return The ISO 3166-1 encoded Country Code of the certifying CA country, Null if the five-characters sequence number is used.
	 */
	public String getForeignCountryCode() {
		return m_strForeignCountryCode;
	}
	
	private String findForeignCountryCode() {
		String retval = null;
		if (m_strLongSequenceNumber.substring(0,2).matches("[A-Z]{2}")) {
			retval = m_strLongSequenceNumber.substring(0,2);
		}
		return retval;
	}
	
	
	public static String getCVSerialShort(Integer serial)
	{
		String strSerial = serial.toString();
		if(strSerial.length() > 3)
		{
			return strSerial.substring(strSerial.length() - 3);
		}
		else
		{
			while(strSerial.length() < 3)
			{
				strSerial = "0"+strSerial;
			}
		}
		return strSerial;
	}
	
	public static String getCVSerialLong(Integer serial)
	{
		String strSerial = serial.toString();
		if(strSerial.length() > 5)
		{
			return strSerial.substring(strSerial.length() - 5);
		}
		else
		{
			while(strSerial.length() < 5)
			{
				strSerial = "0"+strSerial;
			}
		}
		return strSerial;
	}
	
	public static void verify(String strCertHolderRef) throws CVCertificateHolderReferenceTooLong, CVAuthorityRefNotValidException
	{
		if(strCertHolderRef.length() > 16) 
			throw new CVCertificateHolderReferenceTooLong();
		
		if(strCertHolderRef.length() < 7) 
			throw new CVAuthorityRefNotValidException();
		
		// Long sequence
		if(!strCertHolderRef.matches("[A-Z]{2}.{0,9}[0-9A-Z]{5}")/* &&
				// short sequence
				!strCertHolderRef.matches("[A-Z]{2}.{0,9}[A-Z]{2}[0-9]{3}")*/)
		{
			throw new CVAuthorityRefNotValidException();
		}
	}
	
}
