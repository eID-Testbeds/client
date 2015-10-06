package com.secunet.testbedutils.cvc.cvcertificate;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.util.Arrays;
import java.util.Collection;

import org.bouncycastle.util.encoders.Base64;
import org.bouncycastle.util.encoders.Hex;

/**
 * @brief simple DataBuffer like the DataBuffer in our secstd
 *
 *
 * @author meier.marcus
 *
 */
public class DataBuffer implements Serializable
{
	/**
	 *
	 */
	private static final long serialVersionUID = -7509151019663084135L;


	private byte[] m_data = new byte[0];

	/**
	 *
	 * @brief creates an empty DataBuffer
	 *
	 */
	public DataBuffer()
	{

	}
	/**
	 *
	 * @brief Creates a DataBuffer from any Byte collection
	 *
	 * @param data
	 */
	public DataBuffer(Collection<Byte> data)
	{
		m_data = new byte[data.size()];
		int i = 0;
		for(Byte cur : data)
		{
			m_data[i] = cur.byteValue();
			i++;
		}
	}
	/**
	 *
	 * @brief creates a DataBuffer from another DataBuffer
	 *
	 * @param buffer
	 */
	public DataBuffer(DataBuffer buffer)
	{
		m_data = Arrays.copyOf(buffer.m_data, buffer.m_data.length);
	}
	/**
	 *
	 * @brief creates an DataBuffer from a byte array
	 *
	 * @param data
	 */
	public DataBuffer(byte[] data)
	{
		m_data = Arrays.copyOf(data, data.length);
	}
	/**
	 *
	 * @param string
	 */
	public DataBuffer(String string)
	{
		m_data = string.getBytes();
	}
	/**
	 *
	 * @brief appends the given byte array to the end of this data buffer
	 *
	 * @param data
	 * @param size
	 */
	public void append(byte[] data,int size)
	{
		byte[] tmp = new byte[m_data.length + size];

		System.arraycopy(m_data, 0, tmp, 0, m_data.length);
		System.arraycopy(data, 0, tmp, m_data.length, size);

		m_data = tmp;
	}
	/**
	 *
	 * @brief appends the given byte array to the end of this data buffer
	 *
	 * @param data
	 */
	public void append(byte[] data)
	{
		append(data,data.length);
	}
	/**
	 *
	 * @brief appends the given byte to the end of this data buffer
	 *
	 * @param data
	 */
	public void append(byte data)
	{
		byte[] tmp = {data};
		append(tmp);
	}
	/**
	 *
	 * @brief appends the data of the given data buffer to the end of this data buffer
	 *
	 * @param data
	 */
	public void append(DataBuffer data)
	{
		append(data.m_data);
	}
	/**
	 *
	 * @brief appends the data of the given data buffer to the end of this data buffer
	 *
	 * @param data
	 */
	public void addAll(DataBuffer data)
	{
		append(data.m_data);
	}

	/**
	 *
	 * @brief removes all content of this data buffer and assigns the data of the byte array to this data buffer
	 *
	 * @param data
	 */
	public void assign(byte[] data)
	{
		assign(data,data.length);
	}

	/**
	 *
	 * @brief removes all content of this data buffer and assigns the data of the byte array to this data buffer
	 *
	 * @param data Data to be assigned
	 * @param size determins how much of data has to be assigned
	 */
	public void assign(byte[] data, int size)
	{
		m_data = new byte[size];
		System.arraycopy(data, 0, m_data, 0, size);
	}
	/**
	 *
	 * @brief removes all content of this data buffer and assigns the data of the given data buffer to this data buffer
	 *
	 * @param data
	 */
	public void assign(DataBuffer data)
	{
		assign(data.m_data);
	}

	/**
	 *
	 * @brief Inserts the data of the given data buffer to the given position of this data buffer
	 *
	 * @param index consigns a position (index < size())
	 * @param data consigns the data
	 */
	public void insert(int index, byte[] data)
	{
		byte[] tmp = new byte[m_data.length + data.length];

		System.arraycopy(m_data, 0, tmp, 0, index );
		System.arraycopy(data, 0, tmp, index, data.length );
		System.arraycopy(m_data, index, tmp, index+data.length, m_data.length - index);
		m_data = tmp;
	}


	/**
	 *
	 * @brief Inserts the data of the given data buffer to the given position of this data buffer
	 *
	 * @param index consigns a position (index < size())
	 * @param data consigns the data
	 */
	public void insert(int index, DataBuffer data)
	{
		insert(index, data.m_data);
	}
	/**
	 *
	 * @brief Inserts the given byte to the given position of this data buffer
	 *
	 * @param index consigns a position (index < size())
	 * @param data consigns the data
	 */
	public void insert(int index, byte data)
	{
		byte[] tmp = {data};
		insert(index, tmp);
	}
	/**
	 *
	 * @brief This method returns a sub sequence of bytes which will start at the given start position
	 *
	 * @param start position where the byte sequence will start  (index < size())
	 * @param length Length of the byte sequence (index+length <= size())
	 * @return returns a DataBuffer with the sub byte sequence
	 */
	public DataBuffer substr(int start,int length)
	{
		return new DataBuffer(Arrays.copyOfRange(m_data, start, start+length));
	}
	/**
	 *
	 * @brief This method returns a sub sequence of bytes which will start at the given start position till the end of this buffer
	 *
	 * @param start position where the byte sequence will start  (index < size())
	 *
	 * @return returns a DataBuffer with the sub byte sequence
	 */
	public DataBuffer substr(int start)
	{
		return substr(start,m_data.length-start);
	}
	/**
	 *
	 * @brief This method deletes the given range of bytes
	 *
	 * @param start
	 * @param length
	 */
	public void erase(int start,int length)
	{
		removeRange(start,length);
	}

	public void clear()
	{
		m_data = new byte[0];
	}

	public byte get(int index)
	{
		return m_data[index];
	}
	/**
	 *
	 * @brief This method converts this DataBuffer into a byte array
	 *
	 * @return returns a byte array with the content of this data buffer
	 */
	public byte[] toByteArray()
	{
		return Arrays.copyOf(m_data, m_data.length);
	}
	/**
	 *
	 * @brief This method deletes the given range of bytes
	 *
	 * @param start
	 * @param length
	 */
	public void removeRange(int start, int length)
	{
		byte[] tmp = new byte[m_data.length - length];

		System.arraycopy(m_data, 0, tmp, 0, start );

		System.arraycopy(m_data, start+length, tmp, start, m_data.length -(start+length)  );

		m_data = tmp;

	}
	/**
	 *
	 * @brief This method removes all bytes from the given start position till the end of this data buffer
	 *
	 * @param start
	 */
	public void removeRange(int start)
	{
		removeRange(start,m_data.length-start);
	}

	/**
	 *
	 * @brief This method decodes a base64 String and returns a data buffer with the decoded data
	 *
	 * @param base64Str consigns the base64 string
	 * @return returns a new instance of a data buffer
	 */
	static public DataBuffer decodeB64(String base64Str)
	{
		if(base64Str == null) return null;
		if(base64Str.length() == 0) return new DataBuffer();

		return new DataBuffer(Base64.decode(base64Str));
	}
	/**
	 *
	 * @brief This method decodes a base64 String and returns a data buffer with the decoded data
	 *
	 * @param base64Str consigns the base64 string
	 * @return returns a new instance of a data buffer
	 */


	/**
	 *
	 * @brief This method encodes the given data buffer into an base64 string
	 *
	 * @param buffer consigns a data buffer with data
	 * @return returns an base64 string
	 */
	static public String encodeB64(DataBuffer buffer)
	{
		return new String(Base64.encode(buffer.m_data));
	}

	

	/**
	 *
	 * @brief This method encodes the given data buffer into an base64 string
	 *
	 * @return returns an base64 string of the data buffer
	 */
	public String getB64encoded()
	{
		return new String(Base64.encode(m_data));
	}

	/**
	 * @param width
	 * @return
	 */
	public String getB64encoded(int width)
	{
		String text = getB64encoded();
		StringBuffer builder = new StringBuffer(text.length() * 3);
		int index = 0;
	    String prefix = "";
	    while (index < text.length())
	    {
	        // Don't put the insert in the very first iteration.
	        // This is easier than appending it *after* each substring
	        builder.append(prefix);
	        prefix = "\n";
	        builder.append(text.substring(index, Math.min(index + width, text.length())));
	        index += width;
	    }

	    return builder.toString();
	}

	/**
	 *
	 * @brief This method writes the data buffer into a file
	 *
	 * @param filename consigns the filename
	 * @throws IOException
	 */
	public void writeToFile(String filename) throws IOException
	{
		FileOutputStream fileoutputstream = new FileOutputStream(filename);

        for (int i = 0; i < m_data.length; i++) {
            fileoutputstream.write(m_data[i]);
        }

        fileoutputstream.close();
	}
	/**
	 *
	 * @param filename
	 * @return returns a DataBuffer with the file content
	 * @throws IOException
	 */
	static public DataBuffer readFromFile(String filename) throws IOException
	{
		FileInputStream stream = null;
		try
		{
			stream = new FileInputStream(filename);
			return fromInputStream(stream);
		}
		finally
		{
			if (stream != null)
				stream.close();
		}
	}

	/**
	 *
	 * @brief This method compares to DataBuffer like the corresponding String method
	 *
	 * @param other
	 * @return 0 if the two DataBuffers have equal content; a value less than 0 if this DataBuffer is lexicographically less than the DataBuffer argument; and a value greater than 0 if this DataBuffer is lexicographically greater than the DataBuffer argument.
	 */
	public int compareTo(DataBuffer other)
	{
		return equals(other) ? 0 : 1;
	}

	public String asHex()
	{
		return asHex(" ");
	}

	/**
	 *
	 * @brief This method gives an hex-encoded String representation of the DataBuffer
	 *
	 * @return The hex-encoded value
	 */
	public String asHex(String seperator)
	{
		StringBuffer buffer = new StringBuffer((this.size()*3));
		String prefix = "";
		for( int i = 0; i< this.size(); i++ ) {
			buffer.append(prefix);
			prefix = seperator;
			String temp = Integer.toHexString(unsigned(m_data[i])).toUpperCase();
			if(temp.length() == 1) {
				buffer.append("0");
			}
			buffer.append(temp);
		}
		return buffer.toString();
	}

	/**
	 *
	 * @brief This method sets the value of the DataBuffer according to the given hex-encoded String
	 *
	 * @param strHexString the hex-encoded value (10 01 10 ..)
	 */
	public void fromHex(String strHexString)
	{
		assign(Hex.decode(strHexString));
	}


	/**
	 *
	 * @brief This method gives an hex-encoded String representation of the DataBuffer,
	 * so that it can be used as xsd:hexBinary value
	 * @return The hex-encoded value
	 */
	public String asHexBinary()
	{
		StringBuffer buffer = new StringBuffer((this.size()*3));
		for( int i = 0; i< this.size(); i++ ) {
			String temp = Integer.toHexString(unsigned(m_data[i])).toUpperCase();
			if(temp.length() == 1) {
				buffer.append("0");
			}
			buffer.append(temp);
		}
		return buffer.toString();
	}


	/**
	 *
	 * @brief This method sets the value of the DataBuffer according to the given
	 * hex-encoded String (e.g. xsd:hexBinary)
	 *
	 * @param strHexString the hex-encoded value (0047dedbef..)
	 */
	public void fromHexBinary(String strHexString)
	{
		assign(Hex.decode(strHexString));
	}

	static public DataBuffer fromInputStream(InputStream is) throws IOException
	{
		DataBuffer buffer = null;
		ByteArrayOutputStream out = null;
		BufferedInputStream in = null;

		try
		{
			byte buf[] = new byte[1024*32];
			in = new BufferedInputStream(is);
			out = new ByteArrayOutputStream();

			int ret = 0;
			while ((ret = in.read(buf)) != -1)
			{
				out.write(buf, 0, ret);
			}
			
			buffer = new DataBuffer(out.toByteArray());
		}
		finally
		{
			
			if (out != null)
				out.close();
		}

		return buffer;
	}

	/**
	 * @brief Generates a string representation of the hex data split into multiple lines.
	 * Each line has a defined number of characters and is starting with an indentation string.
	 *
	 * @param hexSeperator string to split the single hex values
	 * @param indentation string inserted before every row
	 * @param charsPerRow number of characters per row
	 * @return the hex data string split into multiple lines
	 */
	public String getHexSplit(String hexSeperator, String indentation, int charsPerRow)
	{
		String hexStr = asHex(hexSeperator);
		if (hexStr.startsWith("00") && hexStr.length() > 3)
			hexStr = hexStr.substring(3);
		if (hexStr.length() <= charsPerRow)
			return hexStr;

		int length = hexStr.length();
		int beginIndex = 0;
		int endIndex = charsPerRow;

		StringBuffer out = new StringBuffer(length * 2);
		while (beginIndex < length)
		{
			out.append(indentation);
			if (hexSeperator.equals(hexStr.substring(endIndex-1, endIndex)))
				out.append(hexStr.substring(beginIndex, endIndex -1)); // -1 to remove the last hexSeperator
			else
				out.append(hexStr.substring(beginIndex, endIndex));
			out.append("\n");

			beginIndex += charsPerRow;
			if (beginIndex > length)
				beginIndex = length;
			endIndex += charsPerRow;
			if (endIndex > length)
				endIndex = length;
		}
		return out.toString();
	}

	@Override
	public boolean equals(Object obj)
	{
		if(obj != null && obj instanceof DataBuffer)
		{
			return Arrays.equals(m_data, ((DataBuffer)obj).m_data);
		}

		return false;
	}

	/**
	 * @return
	 */
	public boolean isEmpty()
	{
		return m_data.length == 0;
	}

	@Override
	public synchronized int hashCode()
	{
		// Use the hashCode method from String
		return Arrays.hashCode(m_data);
	}

	public int size()
	{
		return m_data.length;
	}

	public int indexOf(byte b)
	{
		for(int i = 0; i < m_data.length; i++)
		{
			if(m_data[i] == b)
			{
				return i;
			}
		}
		return -1;
	}
	
	static public int unsigned(byte in)
	{
		
		return in & 0xFF;
	}
	
	/**
	 * Generates a hash value of given data.
	 * 
	 * @param data Given data.
	 * @param algorithm Use hash algorithm from TAAlgorithm
	 * @return Hash value.
	 * @throws NoSuchProviderException 
	 * @throws NoSuchAlgorithmException 
	 * @throws Exception
	 */
	public static DataBuffer generateHash(DataBuffer data, TAAlgorithm algorithm) throws NoSuchAlgorithmException, NoSuchProviderException {
		DataBuffer out = null;
		MessageDigest md = null;
		switch (algorithm) {
		case RSA_v1_5_SHA_1:
		case RSA_PSS_SHA_1:
		case ECDSA_SHA_1:
			md = MessageDigest.getInstance("SHA-1", "BC");
			break;
		case ECDSA_SHA_224:
			md = MessageDigest.getInstance("SHA-224", "BC");
			break;
		case RSA_v1_5_SHA_256:
		case RSA_PSS_SHA_256:
		case ECDSA_SHA_256:
			md = MessageDigest.getInstance("SHA-256", "BC");
			break;
		default:
			break;
		}
		
		if (md != null && data != null) {
			byte[] hash = md.digest(data.toByteArray());
			out = new DataBuffer(hash);
		}

		return out;
	}
}
