package com.secunet.testbedutils.cvc.cvcertificate;

import java.util.Iterator;

import com.secunet.testbedutils.cvc.cvcertificate.exception.CVBufferNotEmptyException;
import com.secunet.testbedutils.cvc.cvcertificate.exception.CVDecodeErrorException;
import com.secunet.testbedutils.cvc.cvcertificate.exception.CVInvalidOidException;
import com.secunet.testbedutils.cvc.cvcertificate.exception.CVTagNotFoundException;

/**
 * @brief This class generates and parse the CV certificate extensions
 * 
 * @author meier.marcus
 * @version 1.0
 * @created 27-Aug-2009 14:18:54
 */
public class CVExtension {

	/**
	 * < extension list
	 */
	protected CVExtensionDataList m_ExtensionsList;

	/**
	 * @brief constructor
	 *      
	 * 
	 * 
	 */
	public CVExtension(){

	}

	/**
	 * @brief This method generates the raw certificate extension depended on the
	 * class information
	 * @return returns the raw extensions
	 * @throws CVInvalidOidException 
	 */
	public DataBuffer genRawExtensions() throws CVInvalidOidException{
		DataBuffer out = new DataBuffer();
		      
		if(m_ExtensionsList == null) return out;
		Iterator<CVExtensionData> it = m_ExtensionsList.iterator();
		while(it.hasNext())
		{
			DataBuffer temp = genExtension(it.next());
		
			TLV.append(out, CVCertificate.s_CvDataTemplateTag, temp);
		}
		
		return out;
	}

	/**
	 * @brief This method parse the raw extension from a certificate
	 * @param in This parameter consigns the extension raw data
	 * @throws CVTagNotFoundException 
	 * @throws CVInvalidOidException 
	 * @throws CVBufferNotEmptyException 
	 * @throws CVDecodeErrorException 
	 * 
	 * 
	 */
	public void parseRawExtensions(final DataBuffer in) throws CVTagNotFoundException, CVBufferNotEmptyException, CVInvalidOidException, CVDecodeErrorException{
		DataBuffer rawData = new DataBuffer(in);
		if(m_ExtensionsList == null)
		{
			m_ExtensionsList = new CVExtensionDataList();
		}
		m_ExtensionsList.clear();
		
		while(!rawData.isEmpty())
		{
			TLV extrDataTemp = TLV.extract(rawData);
			
			if (extrDataTemp.getTag() != CVCertificate.s_CvDataTemplateTag)
			{
//				m_rLog << "0x73 Certificate discretionary data tag not found" << std::endl;
				throw new CVTagNotFoundException("0x73");
			}
			parseExtension(extrDataTemp.getValue());
		}
	}

	/**
	 * @brief This method sets new extension information
	 *@param rExtList This parameter consigns a reference to a extension data
	 * list
	 * 
	 * @param rExtList
	 */
	public void setExtensions(final CVExtensionDataList rExtList){
		m_ExtensionsList = rExtList;
	}

	/**
	 * @brief This method returns the currently known extensions of this class
	 *@return CVExtensionDataList returns a list with CVExtensionData
	 */
	public CVExtensionDataList getExtensions(){
		return m_ExtensionsList;
	}

	/**
	 * @brief This method parse one extension
	 *@param rRawData reference to the raw data
	 * 
	 * @param rRawData
	 * @throws CVBufferNotEmptyException 
	 * @throws CVTagNotFoundException 
	 * @throws CVInvalidOidException 
	 * @throws CVDecodeErrorException 
	 */
	protected void parseExtension(final DataBuffer rRawData) throws CVBufferNotEmptyException, CVTagNotFoundException, CVInvalidOidException, CVDecodeErrorException{
		DataBuffer rawData = new DataBuffer(rRawData);
		CVExtensionData extData = new CVExtensionData();
		
		
		TLV extrOID = TLV.extract(rawData);
		if (extrOID.getTag() != CVCertificate.s_CvOIDTag)
		{
			//m_rLog << "0x06 OID tag not found" << std::endl;
			throw new CVTagNotFoundException("0x06");
		}
		
		if (extrOID.getValue().equals(Oids.concat(Oids.OID_BSI_DE,Oids.OID_BASE_EXTENSION,Oids.OID_EXT_DESCRIPTION)))
		{
			extData.setType(CVExtensionType.extDescription);
		}
		else if (extrOID.getValue().equals(Oids.concat(Oids.OID_BSI_DE,Oids.OID_BASE_EXTENSION,Oids.OID_EXT_SECTOR)))
		{
			extData.setType(CVExtensionType.extSector);
		}
		else
		{
//			m_rLog << "unknown OID found" << std::endl;
			throw new CVInvalidOidException();
		}
		
		TLV extrHash1 = TLV.extract(rawData);
		if (extrHash1.getTag() != CVCertificate.s_CvExtensionHash1Tag)
		{
//			m_rLog << "0x80 extension hash 1 tag not found" << std::endl;
			throw new CVTagNotFoundException("0x80");
		}
		extData.setHash1( extrHash1.getValue() );
		
		if (extData.getType() == CVExtensionType.extSector && rawData.size() > 0)
		{
			TLV extrHash2 = TLV.extract(rawData);
			if (extrHash2.getTag() != CVCertificate.s_CvExtensionHash2Tag)
			{
//				m_rLog << "0x81 extension hash 2 tag not found" << std::endl;
				throw new CVTagNotFoundException("0x81");
			}
			extData.setHash2(extrHash2.getValue());
		}
		//data should be empty, otherwise the parse of the cert has failed
		if(rawData.size() > 0)
		{
			// Error message 

			throw new CVBufferNotEmptyException();
		}
		
		m_ExtensionsList.add(extData);
	}

	/**
	 * @brief This method generates one extension raw data
	 *@param data consigns a reference to an extension data
	 *@return DataBuffer returns the raw data for the certificate
	 * 
	 * @param data
	 * @throws CVInvalidOidException 
	 */
	protected DataBuffer genExtension(final CVExtensionData data) throws CVInvalidOidException{
		DataBuffer out = new DataBuffer();
		

		switch(data.getType())
		{
		case extDescription:
			TLV.append(out, CVCertificate.s_CvOIDTag, 
					Oids.concat(Oids.OID_BSI_DE,Oids.OID_BASE_EXTENSION,Oids.OID_EXT_DESCRIPTION));
			break;
		
		case extSector:
			TLV.append(out, CVCertificate.s_CvOIDTag, 
					Oids.concat(Oids.OID_BSI_DE,Oids.OID_BASE_EXTENSION,Oids.OID_EXT_SECTOR));
			break;
		default:
//			m_rLog << "bad OID selected" << std::endl;

			throw new CVInvalidOidException();
		}
		
		TLV.append(out, CVCertificate.s_CvExtensionHash1Tag, data.getHash1());
		
		if (data.getType() == CVExtensionType.extSector && data.getHash2() != null)
		{
			TLV.append(out, CVCertificate.s_CvExtensionHash2Tag, data.getHash2());
		}
		
		return out;
	}

}