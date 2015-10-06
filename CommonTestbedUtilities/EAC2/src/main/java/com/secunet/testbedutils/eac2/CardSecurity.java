package com.secunet.testbedutils.eac2;

import java.io.IOException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PublicKey;
import java.security.spec.AlgorithmParameterSpec;
import java.security.spec.InvalidKeySpecException;

import javax.crypto.spec.DHParameterSpec;
import javax.crypto.spec.DHPublicKeySpec;

import org.bouncycastle.asn1.ASN1InputStream;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.cms.CMSObjectIdentifiers;
import org.bouncycastle.asn1.cms.ContentInfo;
import org.bouncycastle.asn1.cms.SignedData;
import org.bouncycastle.asn1.x9.X9ECPoint;
import org.bouncycastle.jce.provider.JCEECPublicKey;
import org.bouncycastle.jce.spec.ECParameterSpec;
import org.bouncycastle.jce.spec.ECPublicKeySpec;
import org.bouncycastle.math.ec.ECPoint;

import com.secunet.testbedutils.eac2.DHDomainParameter.Type;

public class CardSecurity {
	
	private SecurityInfos securityInfos = null;
	
	public void fromAsn1( byte[] data, int defaultCAKeyID ) throws IOException, EIDException 
	{
	
		try {
			ASN1InputStream ais = new ASN1InputStream( data );
			try {
				
				ASN1Sequence seq = (ASN1Sequence) ais.readObject();
		        ContentInfo ci = ContentInfo.getInstance( seq );
		        
		        if( ! ci.getContentType().equals( CMSObjectIdentifiers.signedData ) ) 
		        	throw new EIDException( "wrong content type in CardSecurity" );
		        
		        SignedData sd = SignedData.getInstance( (ASN1Sequence) ci.getContent() );
		        ContentInfo eci = sd.getEncapContentInfo();
		        
		        if( ! eci.getContentType().equals( EAC2ObjectIdentifiers.id_SecurityObject ) ) 
		        	throw new EIDException( "CardSecurity does not encapsulate SecurityInfos" );
		        
		        
				SecurityInfos si = new SecurityInfos();
				si.fromAsn1( ((ASN1OctetString) eci.getContent()).getOctets(), defaultCAKeyID );
				securityInfos = si;
			}
			finally
			{
				ais.close();
			}
			
		
		}
		catch(Exception e)
		{
			throw new EIDException(e);
		}
	}

	public SecurityInfos getSecurityInfos() 
	{
		return securityInfos;
	}
	
	public DHDomainParameter.Type getCAAlgorithmType()
	{
		return securityInfos.getDefaultCADomainParameter().getType();
	}
	
	
	public PublicKey getPublicCAKey() throws InvalidKeySpecException, IOException, NoSuchAlgorithmException, NoSuchProviderException
	{
		ChipAuthenticationPublicKeyInfo info = securityInfos.getDefaultChipAuthenticationPublicKeyInfo();
		AlgorithmParameterSpec algorithmParameterSpec = securityInfos.getDefaultCADomainParameter().getAlgorithmParameterSpec();
		Type type = securityInfos.getDefaultCADomainParameter().getType();

		PublicKey pubKey = null;
		if(type == Type.ECDH)
		{
			ECParameterSpec eps = (ECParameterSpec) algorithmParameterSpec;
			DEROctetString dos = new DEROctetString( info.getSubjectPublicKeyInfo().getPublicKeyData().getBytes() );

			ECPoint point = new X9ECPoint( eps.getCurve(), dos ).getPoint();
			ECPublicKeySpec eks = new ECPublicKeySpec(point, eps );
			
			pubKey = new JCEECPublicKey( type.toString(), eks );
			
			
		}
		else
		{
		
			DHParameterSpec dps = (DHParameterSpec) algorithmParameterSpec;
			ASN1Integer dos = new ASN1Integer( info.getSubjectPublicKeyInfo().getPublicKeyData().getBytes() );
			DHPublicKeySpec keySpec = new DHPublicKeySpec( dos.getPositiveValue(), dps.getP(), dps.getG() );
			KeyFactory kf = KeyFactory.getInstance( type.toString() );
			return kf.generatePublic( keySpec );
		}
		
		return pubKey;
	}
	
}
