package com.secunet.testbedutils.cvc.cvcertificate;

import org.bouncycastle.asn1.DERBitString;

/**
 * Just a small helper class with some small static methods
 * @author meier.marcus
 *
 */
public class Util {

	/**
	 * This method removes leading zeros of an byte array
	 * @param in
	 * @return
	 */
	public static byte[] removeLeadingZeros(byte[] in)
	{
		if(in.length > 1)
		{
			DataBuffer buffer = new DataBuffer();
			int i = 0;
			while(in[i] == 0x00)
			{
				i++;
			}

			while(i < in.length)
			{
				buffer.append(in[i]);
				i++;
			}
			return buffer.toByteArray();
		}
		return in;
	}

	public static boolean[] bitStringToBoolean(DERBitString bitString)
    {
        if (bitString != null)
        {
            byte[]          bytes = bitString.getBytes();
            boolean[]       boolId = new boolean[bytes.length * 8 - bitString.getPadBits()];

            for (int i = 0; i != boolId.length; i++)
            {
                boolId[i] = (bytes[i / 8] & (0x80 >>> (i % 8))) != 0;
            }

            return boolId;
        }

        return null;
    }

	public static DERBitString booleanToBitString(boolean[] id)
    {
        byte[] bytes = new byte[(id.length + 7) / 8];

        for (int i = 0; i != id.length; i++)
        {
            bytes[i / 8] |= (id[i]) ? (1 << ((7 - (i % 8)))) : 0;
        }

        int pad = id.length % 8;

        if (pad == 0)
        {
            return new DERBitString(bytes);
        }
        else
        {
            return new DERBitString(bytes, 8 - pad);
        }
    }
}
