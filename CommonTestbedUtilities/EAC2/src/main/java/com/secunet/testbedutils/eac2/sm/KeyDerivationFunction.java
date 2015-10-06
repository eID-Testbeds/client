package com.secunet.testbedutils.eac2.sm;

import java.io.ByteArrayOutputStream;
import java.security.MessageDigest;
import java.util.Arrays;

import org.bouncycastle.asn1.ASN1ObjectIdentifier;

import com.secunet.testbedutils.eac2.EAC2ObjectIdentifiers;
import com.secunet.testbedutils.eac2.cv.ByteHelper;

public class KeyDerivationFunction {

	public enum HashAlgorithmAndKeyLength {
		UNKNOWN,
		SHA1_112,
		SHA1_128,
		SHA256_192,
		SHA256_256
	}

	private HashAlgorithmAndKeyLength hashAlgorithmAndKeyLength;
	private byte[] sharedSecret;
	
	public void init( ASN1ObjectIdentifier oid , byte[] sharedSecret ) {
		this.hashAlgorithmAndKeyLength = getHashAlgorithmAndKeyLength(oid);
		this.sharedSecret = sharedSecret;
	}
	
	public byte[] perform( int counter ) throws EIDCryptoException {
		return perform( counter, null );
	}
	public byte[] perform( int counter, byte[] nonce ) throws EIDCryptoException {
		byte[] derived = null;
		try {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			baos.write( sharedSecret );
			if( nonce != null && nonce.length > 0 ) baos.write( nonce );
			
			
			baos.write( ByteHelper.toPaddedBytes( counter ) );
			byte[] data = baos.toByteArray();
			switch( hashAlgorithmAndKeyLength ) {
				case SHA1_112: {
					MessageDigest sha1 = MessageDigest.getInstance("SHA-1");
					byte[] digest = sha1.digest(data);
					derived = Arrays.copyOfRange( digest, 0, 16 );
					break;
				}
				case SHA1_128: {
					MessageDigest sha1 = MessageDigest.getInstance("SHA-1");
					byte[] digest = sha1.digest(data);
					derived = Arrays.copyOfRange( digest, 0, 16 );
					break;
				}
				case SHA256_192: {
					MessageDigest sha256 = MessageDigest.getInstance("SHA-256");
					byte[] digest = sha256.digest(data);
					derived = Arrays.copyOfRange( digest, 0, 24 );
					break;
				}
				case SHA256_256: {
					MessageDigest sha256 = MessageDigest.getInstance("SHA-256");
					byte[] digest = sha256.digest(data);
					derived = digest;
					break;
				}
				default:
					return null;
			}
		}
		catch( Exception e) {
			throw new EIDCryptoException(e);
		}
		return derived;
	}

	
	private HashAlgorithmAndKeyLength getHashAlgorithmAndKeyLength( ASN1ObjectIdentifier oid ) {
		if( EAC2ObjectIdentifiers.id_CA_DH_3DES_CBC_CBC.equals( oid ) ||
			EAC2ObjectIdentifiers.id_CA_ECDH_3DES_CBC_CBC.equals( oid ) ) {
			return HashAlgorithmAndKeyLength.SHA1_112;
		}
		if( EAC2ObjectIdentifiers.id_CA_DH_AES_CBC_CMAC_128.equals( oid ) ||
			EAC2ObjectIdentifiers.id_CA_ECDH_AES_CBC_CMAC_128.equals( oid ) ) {
			return HashAlgorithmAndKeyLength.SHA1_128;
		}
		if( EAC2ObjectIdentifiers.id_CA_DH_AES_CBC_CMAC_192.equals( oid ) ||
			EAC2ObjectIdentifiers.id_CA_ECDH_AES_CBC_CMAC_192.equals( oid ) ) {
			return HashAlgorithmAndKeyLength.SHA256_192;
		}
		if( EAC2ObjectIdentifiers.id_CA_DH_AES_CBC_CMAC_256.equals( oid ) ||
			EAC2ObjectIdentifiers.id_CA_ECDH_AES_CBC_CMAC_256.equals( oid ) ) {
			return HashAlgorithmAndKeyLength.SHA256_256;
		}
		return HashAlgorithmAndKeyLength.UNKNOWN;
		
	}
}
