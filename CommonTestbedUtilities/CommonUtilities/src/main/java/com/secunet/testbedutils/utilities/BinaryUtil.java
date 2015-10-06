package com.secunet.testbedutils.utilities;

public class BinaryUtil {
	
	/**
	 * Create a binary inclusive OR of all given arrays 
	 * @param arrays
	 * @return
	 */
	public static byte[] inclusiveOrArrays(byte[] ... arrays) throws IllegalArgumentException {
		if(arrays.length == 0) {
			throw new IllegalArgumentException("At least one array is required in order to use this method");
		}
		byte[] result = arrays[0];
		for(int arraynum = 1; arraynum < arrays.length; arraynum++) {
			if(arrays[arraynum].length != result.length) {
				throw new IllegalArgumentException("All passed arrays have to be of equal size");
			}
			for(int i = 0; i < result.length; i++) {
				result[i] |= arrays[arraynum][i];
			}
		}
		return result;
	}

}
