package com.secunet.testbedutils.utilities;

import javax.xml.bind.DatatypeConverter;

public class Base64Util {

	/**
	 * Decode the given Base64 String to a byte array
	 * @param hex
	 * @return
	 */
	public static byte[] decode(String base64)
	{
		return DatatypeConverter.parseBase64Binary(base64);
		
	}
	
	/**
	 * Decode the given hex String to a byte array
	 * @param hex
	 * @return
	 */
	public static byte[] decodeHEX(String hex)
	{
		return DatatypeConverter.parseHexBinary(hex);
	}
	
	/**
	 * Encode the given byte value as hex String
	 * @param value
	 * @return
	 */
	public static String encodeHEX(byte[] value)
	{
		return DatatypeConverter.printHexBinary(value);
	}
	
	/**
	 * Encode the given byte value as Base64 String
	 * @param value
	 * @return
	 */
	public static String encode(byte[] value)
	{
		return DatatypeConverter.printBase64Binary(value);
	}
}
