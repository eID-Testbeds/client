package com.secunet.testbedutils.utilities;

public class HexUtil {
	
	final protected static char[] hexArray = "0123456789ABCDEF".toCharArray();

	/**
	 * Convert a hexadecimal string, i.e. <i>D34D33F</i>, to a byte array
	 * @param hexstring
	 * @return
	 */
	public static byte[] hexStringToByteArray(String hexstring) {
		hexstring = hexstring.toUpperCase();
		byte[] data = new byte[hexstring.length() / 2];
		for (int i = 0; i < hexstring.length(); i += 2) {
			data[i / 2] = (byte) ((Character.digit(hexstring.charAt(i), 16) << 4) + Character.digit(hexstring.charAt(i + 1), 16));
		}
		return data;
	}
	
	/**
	 * Convert byte array to hexadecimal string
	 * @param bytes
	 * @return
	 */
	public static String bytesToHex(byte[] bytes) {
	    char[] hexChars = new char[bytes.length * 2];
	    for ( int j = 0; j < bytes.length; j++ ) {
	        int v = bytes[j] & 0xFF;
	        hexChars[j * 2] = hexArray[v >>> 4];
	        hexChars[j * 2 + 1] = hexArray[v & 0x0F];
	    }
	    return new String(hexChars).toUpperCase();
	}

}
