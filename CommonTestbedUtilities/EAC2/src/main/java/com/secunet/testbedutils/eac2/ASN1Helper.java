package com.secunet.testbedutils.eac2;

import java.math.BigInteger;

import org.bouncycastle.asn1.ASN1Integer;

public class ASN1Helper {
	public static final BigInteger BIG_INT_MAX_VALUE = BigInteger.valueOf(Integer.MAX_VALUE);

	public static int getCheckedInt( ASN1Integer integer ) {
		BigInteger bi = integer.getPositiveValue();
		if( BIG_INT_MAX_VALUE.compareTo( bi ) < 0 ) throw new NumberFormatException( "This DERInteger is too big for int." );
		return bi.intValue();
	}

}
