package com.secunet.testbedutils.cvc.cvcertificate;


import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.spec.RSAPublicKeySpec;

import org.bouncycastle.jce.interfaces.ECPublicKey;
import org.bouncycastle.jce.spec.ECParameterSpec;
import org.bouncycastle.math.ec.ECPoint;

import com.secunet.testbedutils.cvc.cvcertificate.exception.CVKeyTypeNotSupportedException;
/**
 * 
 * @author meier.marcus
 */
public class PublicKeySource extends IPublicKeySource {
	
	
	protected PublicKey m_key = null;
	
	/**
	 * 
	 * @brief constructor
	 *
	 * @param key
	 * @param provider This parameter consigns the provider name of the key
	 */
	public PublicKeySource(PublicKey key)
	{
		
	
		m_key = key;
	}
	
	@Override
	public KeyType getKeyType() throws CVKeyTypeNotSupportedException {
	
		String strAlgo = m_key.getAlgorithm();

		if("RSA".equals(strAlgo))
		{
			return KeyType.KEY_RSA;
		}
		else if("EC".equals(strAlgo) || "ECC".equals(strAlgo) || "ECDSA".equals(strAlgo))
		{
			return KeyType.KEY_ECDSA;
		}
		
		throw new CVKeyTypeNotSupportedException();
		
	}
	
	@Override
	public ECParameterSpec getECDSADomain() throws CVKeyTypeNotSupportedException {
		
		if(getKeyType() == KeyType.KEY_RSA) throw new CVKeyTypeNotSupportedException();
		try {
			ECPublicKey bckey = (ECPublicKey)m_key;			
			return bckey.getParameters();
		}
		catch(Exception e)
		{
			throw new CVKeyTypeNotSupportedException(e);
		}
	}

	@Override
	public ECPubPoint getECDSAPublicPoint() throws CVKeyTypeNotSupportedException {
		
		if(getKeyType() == KeyType.KEY_RSA) throw new CVKeyTypeNotSupportedException();
		try {
			ECPublicKey bckey = (ECPublicKey)m_key;		
			ECPoint ecpQ = bckey.getQ().normalize();
			ECPubPoint point = new ECPubPoint( ecpQ.getXCoord().toBigInteger(), ecpQ.getYCoord().toBigInteger() );
			return point;
		}
		catch(Exception e)
		{
			throw new CVKeyTypeNotSupportedException(e);
		}
		
	}

	@Override
	public RSAPublicKeySpec getRSAPublicKey() throws CVKeyTypeNotSupportedException {
		
		if(getKeyType() == KeyType.KEY_ECDSA) throw new CVKeyTypeNotSupportedException();
		try {
			PublicKey pub = m_key;

			KeyFactory kf = KeyFactory.getInstance("RSA","BC");
			RSAPublicKeySpec pkey = kf.getKeySpec(pub,RSAPublicKeySpec.class);
			
			return pkey;
		}
		catch(Exception e)
		{
			throw new CVKeyTypeNotSupportedException(e);
		}
		
	}

}
