package com.secunet.ipsmall.eac;

import java.io.IOException;
import java.security.spec.AlgorithmParameterSpec;

import com.secunet.ipsmall.eac.DHDomainParameter.Type;



public class CardAccess {
	
	private SecurityInfos securityInfos = null;
	
	public void fromAsn1( byte[] data, int defaultCAKeyID ) throws IOException, EIDException 
	{
	
		SecurityInfos si = new SecurityInfos();
		si.fromAsn1( data, defaultCAKeyID );
		securityInfos = si;
	}

	public SecurityInfos getSecurityInfos() 
	{
		return securityInfos;
	}
	
	public AlgorithmParameterSpec getCADomainParameters()
	{
		return securityInfos.getDefaultCADomainParameter().getAlgorithmParameterSpec();
	}
	
	public String getCAKeyAgreementAlgorithm()
	{
		if(securityInfos.getDefaultCADomainParameter().getType() == Type.ECDH)
		{
			return "ECDH";
		}
		
		return "DH";
	}
	
	public String getCAKeylgorithm()
	{
		if(securityInfos.getDefaultCADomainParameter().getType() == Type.ECDH)
		{
			return "EC";
		}
		
		return "DH";
	}
	
	
	
}
