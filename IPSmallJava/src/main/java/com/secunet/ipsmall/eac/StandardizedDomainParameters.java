package com.secunet.ipsmall.eac;

import java.math.BigInteger;
import java.security.spec.AlgorithmParameterSpec;

import org.bouncycastle.asn1.teletrust.TeleTrusTNamedCurves;
import org.bouncycastle.asn1.x9.X9ECParameters;
import org.bouncycastle.jce.spec.ECParameterSpec;

public class StandardizedDomainParameters 
{

	// TODO add all Domain parameters
	
	
	public static AlgorithmParameterSpec getParameters(BigInteger positiveValue) 
	{
		
		switch (positiveValue.intValue()) 
		{
		case 13:
			return convert(TeleTrusTNamedCurves.getByName("brainpoolp256r1"));
		case 14:
			return convert(TeleTrusTNamedCurves.getByName("brainpoolp320r1"));
		default:
			break;
		}
		return null;
	}
	
	private static AlgorithmParameterSpec convert(X9ECParameters params)
	{
		return new ECParameterSpec( params.getCurve(), params.getG(), params.getN(), params.getH(), params.getSeed() );
	}
	
}
