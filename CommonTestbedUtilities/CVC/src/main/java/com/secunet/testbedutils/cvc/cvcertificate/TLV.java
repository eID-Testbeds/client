package com.secunet.testbedutils.cvc.cvcertificate;

import com.secunet.testbedutils.cvc.cvcertificate.exception.CVDecodeErrorException;

/**
 * This class cuts and extract the ASN.1 TLV structures  
 * 
 * @author meier.marcus
 *
 */
public class TLV 
{
	protected int m_Tag = 0;
	protected int m_Length = 0;
	protected DataBuffer m_Value = null;
	protected DataBuffer m_TLV = null;
	
	protected TLV() {}
	
	/**
	 * @brief This method decodes the ASN.1 structure and returns the information as instance of this class
	 * 
	 * The extracted information will be removed from the given DataBuffer
	 * 
	 * @param buffer consigns a DataBuffer with  ASN.1 TLV structure 
	 * @return returns the information as instance of this class
	 * @throws CVDecodeErrorException 
	 */
	public static TLV extract(DataBuffer buffer) throws CVDecodeErrorException
	{
		int uOffset = 0;
		int uTag = 0;
		int uLength = 0;
		
		if(buffer.size() < 2)
		{
			throw new CVDecodeErrorException();
		}
		  
		  // 1 or 2 byte tag
		if ((buffer.get(uOffset) == 0x5f) || (buffer.get(uOffset) == 0x7f))
		{
		  	uTag = DataBuffer.unsigned(buffer.get(uOffset++));
		  	uTag = uTag << 8;
		  	uTag |= buffer.get(uOffset++);
		}
		else
		{
		  	uTag = DataBuffer.unsigned(buffer.get(uOffset++));
		}
		
		    // The next byte contains either the length (< 0x80) or the amount of length bytes
		int ucLengthByte = DataBuffer.unsigned(buffer.get(uOffset++));
		if (ucLengthByte < 0x80) 
		{
		    // Simple case: small length (< 0x80)
			uLength = ucLengthByte;
		} 
		else 
		{
			uLength = 0;
			ucLengthByte -= 0x80;
			  
			if (buffer.size() < ((long)ucLengthByte + uOffset))
			{
				throw new CVDecodeErrorException();
			}

		  
			for (char ucByte = 0; ucByte < ucLengthByte; ucByte++) {
				uLength *= 0x100;
				uLength += DataBuffer.unsigned(buffer.get(uOffset++));
			}
		}
		  
		      // Now check if the buffer contains enough byte for the value
		if (uLength + uOffset > buffer.size())
		{
			throw new CVDecodeErrorException();
		}
		  
		TLV extract = new TLV();  
		// Extract the value
		DataBuffer value = buffer.substr(uOffset, uLength);
		uOffset += uLength;
		  
		extract.m_TLV = buffer.substr(0,uOffset);
		// Remove the element from the buffer
		buffer.removeRange(0,uOffset);
		
		
		extract.m_Tag = (short)uTag;
		extract.m_Length = uLength;
		extract.m_Value = value;
		
		return extract;
	}
	
	/**
	 * @brief appends the given tag and value to the given destination data buffer
	 * 
	 * @param dest consigns the destination data buffer
	 * @param tag consigns the tag for the TLC structure 
	 * @param value consigns the value for the TLV structure
	 */
	public static void append(DataBuffer dest,int tag, DataBuffer value)
	{
		dest.append(convertTag(tag));
		dest.append(getEncodedLength(value.size()));
		dest.append(value);
	}
	
	/**
	 * @brief appends the given tag and value to the given destination data buffer
	 * 
	 * @param dest consigns the destination data buffer
	 * @param tag consigns the tag for the TLC structure 
	 * @param value consigns the value for the TLV structure
	 */
	public static void append(DataBuffer dest,int tag, String value)
	{
		dest.append(convertTag(tag));
		dest.append(getEncodedLength(value.getBytes().length));
		dest.append(value.getBytes());

	}
	/**
	 * @brief appends the given tag and value to the given destination data buffer
	 * 
	 * @param dest consigns the destination data buffer
	 * @param tag consigns the tag for the TLC structure 
	 * @param value consigns the value for the TLV structure
	 */
	public static void append(DataBuffer dest,int tag, byte[] value)
	{
		dest.append(convertTag(tag));
		dest.append(getEncodedLength(value.length));
		dest.append(value);

	}
	
	/**
	 * @brief this method returns the data Tag 
	 * 
	 * @return the tag as short
	 */
	public int getTag() {
		return m_Tag;
	}

	/**
	 * @brief This method returns the decoded length of the object  
	 * 
	 * @return returns the length as integer
	 */
	public int getLength() {
		return m_Length;
	}

	/**
	 * @brief This method returns the value of the ASN.1 structure
	 * 
	 * @return returns the value data 
	 */
	public DataBuffer getValue() {
		return m_Value;
	}
	
	/**
	 * This function returns the size encoded as ASN.1 length
	 * 
	 * @param size
	 * @return returns the size as data buffer
	 */
	public static DataBuffer getEncodedLength(int size){
		DataBuffer out = new DataBuffer();
		byte[] buffer = new byte[2];

   		buffer[1] = (byte) (size >> 8);
   		buffer[0] = (byte) size;
   		
		if(size > 255)
		{
		  	//255 -- 65535 Byte
		  	out.append((byte)0x82);
		  	out.append(buffer[1]);
		  	out.append(buffer[0]);
		} 
		else if (size >= 128)
		{
		  	//128 -- 255 Byte
			out.append((byte)0x81);
		  	out.append(buffer[0]);
		} 
		else
		{
			out.append(buffer[0]);
		}
		return out;
	}
	
	/**
	 * This function returns the static tag as data buffer
	 * 
	 * @param tag
	 * @return returns the tag as data buffer
	 */
	public static DataBuffer convertTag(int tag){	
		byte[] buffer = new byte[2];

   		buffer[1] = (byte) (tag >> 8);
   		buffer[0] = (byte) tag;

		DataBuffer buf = new DataBuffer();
  
		if(buffer[1] != 0)
			buf.append(buffer[1]);
		
		buf.append(buffer[0]);
  
		return buf;
	}
	
	/**
	 * @brief This method returns the encoded ASN.1 raw data structure
	 * 
	 * @return ASN.1 structure as Data buffer
	 */
	public DataBuffer getTLV()
	{

		return m_TLV;
	}
}
