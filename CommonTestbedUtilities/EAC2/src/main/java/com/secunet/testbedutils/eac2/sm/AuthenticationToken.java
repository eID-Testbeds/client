package com.secunet.testbedutils.eac2.sm;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.crypto.Mac;
import javax.crypto.SecretKey;

import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.crypto.engines.AESEngine;
import org.bouncycastle.crypto.macs.CMac;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.jce.spec.ECParameterSpec;

import com.secunet.testbedutils.eac2.DHDomainParameter;
import com.secunet.testbedutils.eac2.cv.UtilPublicKey;
import com.secunet.testbedutils.utilities.Base64Util;

public class AuthenticationToken {
	private static final Logger logger = Logger.getLogger(AuthenticationToken.class.getName());

	public static byte[] calculate(SecretKey macKey, PublicKey publicKey, DHDomainParameter domainParameter) throws EIDCryptoException {
		try {
			// log.debug( "calculating authentication token" );
			byte[] macContent = UtilPublicKey.toDataObjectBytes(publicKey, domainParameter, 2);
			logger.log(Level.FINE, "Authentication Token Plain: " + Base64Util.encodeHEX(macContent));
			switch (domainParameter.getSymmetricCipher()) {
			case AES: {
				CMac cmac = new CMac(new AESEngine(), 64);
				cmac.init(new KeyParameter(macKey.getEncoded()));
				cmac.update(macContent, 0, macContent.length);
				byte[] out = new byte[cmac.getMacSize()];
				cmac.doFinal(out, 0);
				return out;
			}
			case DESEDE: {
				Mac mac = Mac.getInstance("ISO9797ALG3WITHISO7816-4PADDING");
				mac.init(macKey);
				return mac.doFinal(macContent);
			}
			default: {
				// do nothing; null will be returned
			}
			}
		} catch (NoSuchAlgorithmException e) {
			throw new EIDCryptoException(e);
		} catch (InvalidKeyException e) {
			throw new EIDCryptoException(e);
		} catch (InvalidKeySpecException e) {
			throw new EIDCryptoException(e);
		} catch (IOException e) {
			throw new EIDCryptoException(e);
		}
		return null;
	}

	public static byte[] calculate(byte[] macKey, PublicKey publicKey, ECParameterSpec ecSpec, ASN1ObjectIdentifier identifier) throws EIDCryptoException {
		// log.debug( "calculating authentication token" );
		byte[] macContent = UtilPublicKey.toDataObjectBytes(publicKey, ecSpec, 2, identifier);
		logger.log(Level.FINE, "Authentication Token Plain: " + Base64Util.encodeHEX(macContent));
		CMac cmac = new CMac(new AESEngine(), 64);
		cmac.init(new KeyParameter(macKey));
		cmac.update(macContent, 0, macContent.length);
		byte[] out = new byte[cmac.getMacSize()];
		cmac.doFinal(out, 0);
		return out;
	}

}
