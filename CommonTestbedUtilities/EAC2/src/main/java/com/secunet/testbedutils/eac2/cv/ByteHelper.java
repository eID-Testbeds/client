package com.secunet.testbedutils.eac2.cv;

import java.math.BigInteger;

public class ByteHelper {

	public static String toHexString( byte b ) {
		String s = Integer.toHexString( b & 0xFF ).toUpperCase();
		return ( s.length() == 2 ) ? ( s ) : ( "0" + s );
	}
	
	public static String toHexString( byte[] b ) {
		return toHexString( b, " " );
	}
	
	public static String toHexString( byte[] b, String glue ) {
		String s = "";
		if( null != b && null != glue ) {
			for( int i = 0 ; i < b.length ; i++ ) {
				s += ( i==0 ? "" : glue ) + toHexString( b[i] );
			}
		}
		return s;
	}

	public static int toInt( byte[] b ) {
		if( b.length > 4 ) throw new NumberFormatException("int has only 4 bytes");
		int i = 0;
		for( byte x : b ) {
			i <<= 8;
			i |= (x & 0xFF);
		}
		return i;
	}
	
	public static long toLongInt( byte[] b ) {
		if( b.length > 8 ) throw new NumberFormatException("long has only 8 bytes");
		long i = 0;
		for( byte x : b ) {
			i <<= 8;
			i |= (x & 0xFF);
		}
		return i;
	}
	
	public static byte[] toPaddedBytes( int i ) {
		byte[] b = new byte[4];
		b[0] = (byte)(i >>> 24);
		b[1] = (byte)(i >>> 16);
		b[2] = (byte)(i >>>  8);
		b[3] = (byte)(i       );
		return b;
	}

	public static byte[] toBytes( int i ) {
		int step = 8;
		for( ; (i >>> step) > 0 && step < 32 ; step += 8);
		byte[] b = new byte[ step/8 ];
		for( int x=0, move=step-8 ; x < (step/8) ; x++, move-=8 ) {
			b[x] = (byte)(i >>> move);
		}
		return b;
	}

	public static byte[] toPaddedBytes( long l ) {
		byte[] b = new byte[8];
		b[0] = (byte)(l >>> 56);
		b[1] = (byte)(l >>> 48);
		b[2] = (byte)(l >>> 40);
		b[3] = (byte)(l >>> 32);
		b[4] = (byte)(l >>> 24);
		b[5] = (byte)(l >>> 16);
		b[6] = (byte)(l >>>  8);
		b[7] = (byte)(l       );
		return b;
	}

	public static byte[] toBytes( long l ) {
		int step = 8;
		for( ; (l >>> step) > 0 && step < 64 ; step += 8);
		byte[] b = new byte[ step/8 ];
		for( int x=0, move=step-8 ; x < (step/8) ; x++, move-=8 ) {
			b[x] = (byte)(l >>> move);
		}
		return b;
	}

	public static byte[] toPaddedBytes( BigInteger bi, int length ) {
		byte[] biBytes = bi.toByteArray();
		int diff = length - biBytes.length;
		if( diff < 0 ) throw new NumberFormatException("BigInteger value is bigger than requested " + length + " bytes");
		byte[] out = new byte[ length ];
		System.arraycopy( biBytes, 0, out, diff, biBytes.length );
		return out;
	}

	public static byte[] toPaddedBytes( long l, int length ) {
		byte[] lBytes = toBytes( l );
		int diff = length - lBytes.length;
		if( diff < 0 ) throw new NumberFormatException("long value is bigger than requested " + length + " bytes");
		byte[] out = new byte[ length ];
		System.arraycopy( lBytes, 0, out, diff, lBytes.length );
		return out;
	}
}
