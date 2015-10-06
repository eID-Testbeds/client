package com.secunet.testbedutils.cvc.cvcertificate;

import java.io.IOException;
import java.util.ArrayList;
import java.util.EnumSet;

import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1InputStream;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DERIA5String;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.DERPrintableString;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.DERSet;
import org.bouncycastle.asn1.DERTaggedObject;
import org.bouncycastle.asn1.DERUTF8String;

import com.secunet.testbedutils.cvc.cvcertificate.exception.CertificateDescGenException;
import com.secunet.testbedutils.cvc.cvcertificate.exception.CertificateDescParseException;
/**
 * This class can generate and parse the certificate description structure
 * @author meier.marcus
 *
 */
public class CertificateDescription
{
	/**
	 * defines the possible certificate description types
	 * @author meier.marcus
	 *
	 */
	public enum DescriptionType {
		plainFormat(Oids.concat(Oids.OID_BSI_DE, Oids.OID_BASE_EXTENSION, Oids.OID_EXT_DESCRIPTION,Oids.OID_EXT_DESC_PLAIN)),
		htmlFormat(Oids.concat(Oids.OID_BSI_DE, Oids.OID_BASE_EXTENSION, Oids.OID_EXT_DESCRIPTION,Oids.OID_EXT_DESC_HTML)),
		pdfFormat(Oids.concat(Oids.OID_BSI_DE, Oids.OID_BASE_EXTENSION, Oids.OID_EXT_DESCRIPTION,Oids.OID_EXT_DESC_PDF));		
	
		private String m_strOid;
		/**
		 * constructor 
		 * @param oid consigns the raw OID
		 */
		private DescriptionType(DataBuffer oid)
		{
			m_strOid = Oids.getStringOid(oid);
		}
		/**
		 *  
		 * @param oid
		 * @return returns the enum for the given OID
		 */
		public static DescriptionType getType(ASN1ObjectIdentifier oid)
		{
			return getType(oid.getId());
		}
		/**
		 *  
		 * @param oid
		 * @return returns the enum for the given OID
		 */
		public static DescriptionType getType(String oid)
		{
			for(DescriptionType cur : EnumSet.allOf(DescriptionType.class))
			{
				if(cur.m_strOid.equals(oid)) return cur;
			}
			return null;
		}
		/**
		 * 
		 * @return the OID of the enum
		 */
		public String getOid()
		{
			return m_strOid;
		}
	}
	
	
	protected DescriptionType m_type = null;
		
	protected String m_issuerName = null;
	protected String m_issuerURL = null;
	protected String m_subjectName = null;
	protected String m_subjectURL = null;
	
	protected DataBuffer m_termsOfUsageRawData = null;
	protected String m_termsOfUsageText = null;
	
	protected String m_redirectURL = null;
	protected ArrayList<DataBuffer> m_commCertificates = null;
	/**
	 * constructor
	 */
	public CertificateDescription()
	{
		
	}
	/**
	 * parsing constructor
	 * @param buffer consigns a certificate description as raw buffer
	 * @throws CertificateDescParseException
	 */
	public CertificateDescription(DataBuffer buffer) throws CertificateDescParseException
	{
		parse(buffer);
	}
	/**
	 * parse the given raw buffer
	 * @param buffer
	 * @throws CertificateDescParseException
	 */
	public void parse(DataBuffer buffer) throws CertificateDescParseException
	{
		ASN1InputStream dIn = null;
		try {
			dIn = new ASN1InputStream(buffer.toByteArray());
			ASN1Primitive obj = dIn.readObject();
        
	        if(!(obj instanceof ASN1Sequence))
	        {
	        	throw new CertificateDescParseException();
	        }
	        
	        ASN1Sequence seq = (ASN1Sequence)obj;
	        
	        if(!(seq.getObjectAt(0) instanceof ASN1ObjectIdentifier))
	        {
	        	throw new CertificateDescParseException();
	        }
	        
	        ASN1ObjectIdentifier oid = (ASN1ObjectIdentifier)seq.getObjectAt(0);
	        m_type = DescriptionType.getType(oid);
	        
	        if(m_type == null)
	        {
	        	throw new CertificateDescParseException();
	        }
	        
	        for(int i = 1; i < seq.size();i++)
	        {
	        	ASN1Encodable taggedObj = seq.getObjectAt(i);
	        	if(!(taggedObj instanceof DERTaggedObject))
	 	        {
	 	        	throw new CertificateDescParseException();
	 	        }
	        	
	        	parseTaggedObj((DERTaggedObject)taggedObj);
	        }
		}
		catch(IOException e)
		{
			throw new CertificateDescParseException(e);
		}
		finally
		{
			if (dIn != null)
			{
				try
				{
					dIn.close();
				}
				catch (IOException e)
				{
				}
			}
		}
	}
	
	protected void parseTaggedObj(DERTaggedObject obj) throws CertificateDescParseException
	{
		switch (obj.getTagNo()) 
		{
		case 1:
			m_issuerName = getUTF8String(obj.getObject());
			break;
		case 2:
			m_issuerURL = getPrintableString(obj.getObject());		
			break;
		case 3:
			m_subjectName = getUTF8String(obj.getObject());
			break;
		case 4:
			m_subjectURL = getPrintableString(obj.getObject());
			break;
		case 5:
			parseTermsOfUsage(obj.getObject());
			break;
		case 6:
			m_redirectURL = getPrintableString(obj.getObject());
			break;
		case 7:
			parseCommCerts(obj.getObject());
			break;
		default:
			throw new CertificateDescParseException();
		}
	}
	
	protected String getUTF8String(ASN1Encodable obj) throws CertificateDescParseException
	{
		if(!(obj instanceof DERUTF8String))
        {
        	throw new CertificateDescParseException();
        }
		DERUTF8String string = (DERUTF8String)obj;
		
		return string.getString();
	}
	protected String getPrintableString(ASN1Encodable obj) throws CertificateDescParseException
	{
		if(!(obj instanceof DERPrintableString))
        {
        	throw new CertificateDescParseException();
        }
		
		DERPrintableString string = (DERPrintableString)obj;
		
		return string.getString();
	}
	protected void parseTermsOfUsage(ASN1Encodable obj) throws CertificateDescParseException
	{
		if(m_type == DescriptionType.plainFormat)
		{
			m_termsOfUsageText = getUTF8String(obj);
		}
		else if(m_type == DescriptionType.htmlFormat)
		{
			if(!(obj instanceof DERIA5String))
	        {
	        	throw new CertificateDescParseException();
	        }
			m_termsOfUsageText = ((DERIA5String)obj).getString();
		}
		else if(m_type == DescriptionType.pdfFormat)
		{
			if(!(obj instanceof DEROctetString))
	        {
	        	throw new CertificateDescParseException();
	        }
			
			m_termsOfUsageRawData = new DataBuffer(((DEROctetString)obj).getOctets());
		}
	}
	protected void parseCommCerts(ASN1Encodable obj) throws CertificateDescParseException
	{
		if(!(obj instanceof DERSet))
        {
        	throw new CertificateDescParseException();
        }
		
		DERSet set = (DERSet)obj;
		m_commCertificates = new ArrayList<DataBuffer>();
		for(int i = 0; i < set.size();i++)
		{
			if(!(set.getObjectAt(i) instanceof DEROctetString))
	        {
	        	throw new CertificateDescParseException();
	        }
			
			m_commCertificates.add(new DataBuffer(((DEROctetString)set.getObjectAt(i)).getOctets()));
		}
	}
	/**
	 * generates an new raw certificate description from the given informations
	 * @return returns an buffer with the generated certificate description
	 * @throws CertificateDescGenException
	 */
	public DataBuffer generate() throws CertificateDescGenException
	{	
		ASN1EncodableVector asn1CertDesc = new ASN1EncodableVector();
	
		// check the mandatory fields
		if(m_type == null || 
				m_issuerName == null || 
				m_subjectName == null || 
				(m_termsOfUsageRawData == null && m_termsOfUsageText == null))
		{
			throw new CertificateDescGenException();
		}
		// first the OID
		asn1CertDesc.add(new ASN1ObjectIdentifier(m_type.getOid()));
		
		// mandatory issuer name
		asn1CertDesc.add(new DERTaggedObject(1, new DERUTF8String(m_issuerName)));
		// issuer url
		if(m_issuerURL != null)
		{
			asn1CertDesc.add(new DERTaggedObject(2, new DERPrintableString(m_issuerURL)));
		}
		// mandatory subject name
		asn1CertDesc.add(new DERTaggedObject(3, new DERUTF8String(m_subjectName)));
		// subject url
		if(m_subjectURL != null)
		{
			asn1CertDesc.add(new DERTaggedObject(4, new DERPrintableString(m_subjectURL)));
		}
		// mandatory terms of usage 
		asn1CertDesc.add(new DERTaggedObject(5, getTermsOfUsageASN1()));
		// optional redirect URL
		if(m_redirectURL != null)
		{
			asn1CertDesc.add(new DERTaggedObject(6, new DERPrintableString(m_redirectURL)));
		}
		// optional comm certificate hashes
		if(m_commCertificates != null)
		{
			asn1CertDesc.add(new DERTaggedObject(7, getCommCertificatesASN1()));
		}
		
		try {
			DERSequence rawDesc = new DERSequence(asn1CertDesc);
		
			return new DataBuffer(rawDesc.getEncoded());
		}
		catch (IOException e) {
			throw new CertificateDescGenException(e);
		}
	}

	private ASN1Encodable getCommCertificatesASN1()
	{
		ASN1EncodableVector certVec = new ASN1EncodableVector();
		
		for(int i = 0 ; i < m_commCertificates.size(); i++)
		{
			certVec.add(new DEROctetString(m_commCertificates.get(i).toByteArray()));
		}
		DERSet commCertSet = new DERSet(certVec);
		
		return commCertSet;
	}

	private ASN1Encodable getTermsOfUsageASN1() throws CertificateDescGenException
	{
		switch (m_type) {
		case plainFormat:
			if(m_termsOfUsageText == null) 
				throw new CertificateDescGenException();
			
			return new DERUTF8String(m_termsOfUsageText);
			

		case htmlFormat:
			if(m_termsOfUsageText == null) 
				throw new CertificateDescGenException();
			
			return new DERIA5String(m_termsOfUsageText);

					
		case pdfFormat:
			if(m_termsOfUsageRawData == null) 
				throw new CertificateDescGenException();
			
			return new DEROctetString(m_termsOfUsageRawData.toByteArray());
			
		default:
			throw new CertificateDescGenException();
		}
	}
	/**
	 * 
	 * @return the certificate description type 
	 */
	public DescriptionType getType()
	{
		return m_type;
	}
	/**
	 * sets the plain text terms of usage
	 * @param text 
	 */
	public void setPlainText(String text)
	{
		m_type = DescriptionType.plainFormat;
		m_termsOfUsageText = text;
	}
	
	public void setHTML(String text)
	{
		m_type = DescriptionType.htmlFormat;
		m_termsOfUsageText = text;
	}
	
	public void setPDF(DataBuffer buffer)
	{
		m_type = DescriptionType.pdfFormat;
		m_termsOfUsageRawData = buffer;
	}
	
	public String getPlainText()
	{
		if(m_type == null || m_type != DescriptionType.plainFormat) return null;
		return m_termsOfUsageText;
	}
	
	public String getHTML()
	{
		if(m_type == null || m_type != DescriptionType.htmlFormat) return null;
		return m_termsOfUsageText;
	}
	
	public DataBuffer getPDF()
	{
		if(m_type == null || m_type != DescriptionType.pdfFormat) return null;
		return m_termsOfUsageRawData;
	}
	
	public String getIssuerName()
	{
		return m_issuerName;
	}

	public void setIssuerName(String mIssuerName)
	{
		m_issuerName = mIssuerName;
	}

	public String getIssuerURL()
	{
		return m_issuerURL;
	}

	public void setIssuerURL(String mIssuerURL)
	{
		m_issuerURL = mIssuerURL;
	}

	public String getSubjectName()
	{
		return m_subjectName;
	}

	public void setSubjectName(String mSubjectName)
	{
		m_subjectName = mSubjectName;
	}

	public String getSubjectURL()
	{
		return m_subjectURL;
	}

	public void setSubjectURL(String mSubjectURL)
	{
		m_subjectURL = mSubjectURL;
	}

	public String getRedirectURL()
	{
		return m_redirectURL;
	}

	public void setRedirectURL(String mRedirectURL)
	{
		m_redirectURL = mRedirectURL;
	}
	
	public ArrayList<DataBuffer> getCommCertificates()
	{
		return m_commCertificates; 
	}
	
	public void addCommCertificates(DataBuffer hash)
	{
		if(m_commCertificates == null) m_commCertificates = new ArrayList<DataBuffer>();
		
		m_commCertificates.add(hash);
	}
	
	public void removeCommCertificateHashes(){
		m_commCertificates = null;
	}
	
	@Override
	public String toString()
	{
		StringBuilder buffer = new StringBuilder(2000);
		
		buffer.append("Certificate Description:\n");
		buffer.append("\tIssuer Name: ");
		buffer.append(getIssuerName());
		buffer.append("\n");
		
		if (getIssuerURL() != null)
		{
			buffer.append("\tIssuer URL: ");
			buffer.append(getIssuerURL());
			buffer.append("\n");
		}
		
		buffer.append("\tSubject Name: ");
		buffer.append(getSubjectName());
		buffer.append("\n");
		
		if (getSubjectURL() != null)
		{
			buffer.append("\tSubject URL: ");
			buffer.append(getSubjectURL());
			buffer.append("\n");
		}
		
		
		// Only add plain Text to display, HTML and PDF must be handled and displayed separately
		if (getPlainText() != null)
		{
			buffer.append("\tTerms of Usage:\n");
			buffer.append("\t\t");	
			buffer.append(getPlainText());
			buffer.append("\n");
		}

		if (getRedirectURL() != null)
		{
			buffer.append("\tRedirect URL: ");
			buffer.append(getRedirectURL());
			buffer.append("\n");
		}
		
		if (getCommCertificates() != null)
		{
			buffer.append("\tCommunication Certificate Hashes:\n");
			for (DataBuffer hash : getCommCertificates())
			{
				buffer.append("\t\t");
				buffer.append(hash);
				buffer.append("\n");
			}
		}
		
		return buffer.toString();
	}
}
