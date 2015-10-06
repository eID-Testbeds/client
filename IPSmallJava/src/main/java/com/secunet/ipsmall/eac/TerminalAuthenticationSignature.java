package com.secunet.ipsmall.eac;

import java.io.IOException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.Signature;
import java.security.spec.PKCS8EncodedKeySpec;

import com.secunet.ipsmall.eac.cv.CardVerifiableCertificate;
import com.secunet.ipsmall.eac.cv.EIDCertificateException;
import com.secunet.ipsmall.log.Logger;
import com.secunet.ipsmall.util.Base64Util;
import com.secunet.ipsmall.util.CommonUtil;

public class TerminalAuthenticationSignature 
{
	CardVerifiableCertificate m_cvcert;
	PrivateKey m_key;
	
	byte[] m_idPicc;
	byte[] m_compEmPubKey;
	byte[] m_auxData;
	
	
	public TerminalAuthenticationSignature(byte[] terminalCert, byte[] privatekey) throws Exception
	{
		m_cvcert = new CardVerifiableCertificate(terminalCert);
		
		m_cvcert.getAlgorithmOID();
		
		
		PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(privatekey);
		KeyFactory kf = KeyFactory.getInstance(getKeyType());
		m_key = kf.generatePrivate(keySpec);
	}
	
	public void init(byte[] idPicc, byte[] pubKey, byte[] auxData) throws IOException
	{
		m_idPicc = idPicc;
		m_compEmPubKey = pubKey;
		m_auxData = auxData;
	}

	public String getKeyType() throws EIDCertificateException
	{
		if( EAC2ObjectIdentifiers.id_TA_ECDSA_SHA_1.equals( m_cvcert.getAlgorithmOID() ) ||
				EAC2ObjectIdentifiers.id_TA_ECDSA_SHA_224.equals( m_cvcert.getAlgorithmOID() ) ||
				EAC2ObjectIdentifiers.id_TA_ECDSA_SHA_256.equals( m_cvcert.getAlgorithmOID() ))
		{
			return "EC";
		}
		else
		{
			return "RSA";
		}
	}
	
	public String getSignatureAlgo() throws Exception
	{
		if( EAC2ObjectIdentifiers.id_TA_ECDSA_SHA_1.equals( m_cvcert.getAlgorithmOID() ) ) {
			return "SHA1WITHCVC-ECDSA";
		}
		if( EAC2ObjectIdentifiers.id_TA_ECDSA_SHA_224.equals( m_cvcert.getAlgorithmOID() ) ) {
			return "SHA224WITHCVC-ECDSA";
		}
		if( EAC2ObjectIdentifiers.id_TA_ECDSA_SHA_256.equals( m_cvcert.getAlgorithmOID() ) ) {
			return "SHA256WITHCVC-ECDSA";
		}
		
		if( EAC2ObjectIdentifiers.id_TA_RSA_v1_5_SHA_1.equals(  m_cvcert.getAlgorithmOID() ) ) {
			return "RSAwithSHA1";
		}
		if( EAC2ObjectIdentifiers.id_TA_RSA_v1_5_SHA_256.equals(  m_cvcert.getAlgorithmOID() ) ) {
			return "RSAwithSHA256";
		}
		
		throw new Exception("Uknown OID "+ m_cvcert.getAlgorithmOID());
	}
	
	public byte[] sign(byte[] challenge) throws NoSuchAlgorithmException, NoSuchProviderException, Exception 
	{
		
		byte[] toBeSigned = CommonUtil.concatArrays(m_idPicc,challenge,m_compEmPubKey,m_auxData);

		Logger.EAC.logState("toBeSigned: " + Base64Util.encodeHEX(toBeSigned));
		
		Signature signer = Signature.getInstance(getSignatureAlgo(), "BC");
		signer.initSign(m_key);
		signer.update(toBeSigned);
		
		byte[] signature = signer.sign();
		
		Logger.EAC.logState("signature: " + Base64Util.encodeHEX(signature));
		
		return signature;
	}
}
