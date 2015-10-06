package com.secunet.testbedutils.eac2.sm;

import java.io.IOException;
import java.math.BigInteger;
import java.security.InvalidKeyException;
import java.security.PrivateKey;
import java.security.PublicKey;

import javax.crypto.interfaces.DHPublicKey;

import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.x9.X9IntegerConverter;
import org.bouncycastle.crypto.params.DHPrivateKeyParameters;
import org.bouncycastle.crypto.params.DHPublicKeyParameters;
import org.bouncycastle.crypto.params.ECPrivateKeyParameters;
import org.bouncycastle.crypto.params.ECPublicKeyParameters;
import org.bouncycastle.jcajce.provider.asymmetric.util.ECUtil;
import org.bouncycastle.jce.interfaces.ECPublicKey;
import org.bouncycastle.jce.provider.DHUtil;
import org.bouncycastle.math.ec.ECPoint;

import com.secunet.testbedutils.eac2.DHDomainParameter.Type;

public class FullElementKeyAgreement {

	private Type type;
	DHPrivateKeyParameters dhPrivKey;
	DHPublicKeyParameters dhPubKey;
	ECPrivateKeyParameters ecPrivKey;
	ECPublicKeyParameters ecPubKey;
	
	public FullElementKeyAgreement( Type type ) {
		this.type = type;
	}

	public void init( PrivateKey privateKey ) throws InvalidKeyException {
		if( type == Type.DH ) {
			dhPrivKey = (DHPrivateKeyParameters) DHUtil.generatePrivateKeyParameter( privateKey );
		}
		else if( type == Type.ECDH ) {
	        ecPrivKey = (ECPrivateKeyParameters) ECUtil.generatePrivateKeyParameter( privateKey );
		}
	}
	
	public  void doPhase( PublicKey publicKey, boolean lastPhase ) throws InvalidKeyException {
		doPhase( publicKey );
	}
	public  void doPhase( PublicKey publicKey ) throws InvalidKeyException {
		if( type == Type.DH ) {
	        if( dhPrivKey == null )
	        {
	            throw new IllegalStateException( "Diffie-Hellman not initialised." );
	        }

	        if( !(publicKey instanceof DHPublicKey) )
	        {
	            throw new InvalidKeyException( "Key Agreement doPhase requires DHPublicKey" );
	        }

	        dhPubKey = (DHPublicKeyParameters) DHUtil.generatePublicKeyParameter( publicKey );
		}
		else if( type == Type.ECDH ) {
	        if (ecPrivKey == null)
	        {
	            throw new IllegalStateException("EC Diffie-Hellman not initialised.");
	        }

	        if (!(publicKey instanceof ECPublicKey))
	        {
	            throw new InvalidKeyException("EC Key Agreement doPhase requires ECPublicKey");
	        }

	        ecPubKey = (ECPublicKeyParameters) ECUtil.generatePublicKeyParameter( publicKey );
		}
	}

	public byte[] generateSecret() throws IOException 
	{
		if( type == Type.DH ) {
			BigInteger P = dhPubKey.getY().modPow( dhPrivKey.getX(), dhPrivKey.getParameters().getP() );
			ASN1Integer diP = new ASN1Integer( P );
			return diP.getEncoded();
		}
		else if( type == Type.ECDH ) {
			
			
			
			BigInteger h = ecPubKey.getParameters().getH();
			BigInteger n = ecPubKey.getParameters().getN();
			
			BigInteger d = ecPrivKey.getD();
			
			
			BigInteger l = h.modInverse(n);
			ECPoint Q = ecPubKey.getQ().multiply( h );
			
			ECPoint calculated = Q.multiply(((d.multiply(l)).mod(n)));
			
			X9IntegerConverter converter = new X9IntegerConverter();
			return converter.integerToBytes(calculated.normalize().getXCoord().toBigInteger(), converter.getByteLength(ecPubKey.getParameters().getG().normalize().getXCoord()));	
		}
		return null;
	}
}
