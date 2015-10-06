package com.secunet.ipsmall.util;

import javax.xml.bind.DatatypeConverter;

public class Base64Util {

	
	public static byte[] decode(String base64)
	{
		return DatatypeConverter.parseBase64Binary(base64);
		
	}
	
	public static byte[] decodeHEX(String hex)
	{
		return DatatypeConverter.parseHexBinary(hex);
	}
	
	public static String encodeHEX(byte[] value)
	{
		return DatatypeConverter.printHexBinary(value);
	}
	
}
