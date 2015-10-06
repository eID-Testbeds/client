package com.secunet.ipsmall.eac.sm;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;

import javax.crypto.Mac;
import javax.crypto.SecretKey;

import org.bouncycastle.crypto.engines.AESEngine;
import org.bouncycastle.crypto.macs.CMac;
import org.bouncycastle.crypto.params.KeyParameter;

import com.secunet.ipsmall.eac.DHDomainParameter;
import com.secunet.ipsmall.eac.cv.UtilPublicKey;
import com.secunet.ipsmall.log.Logger;
import com.secunet.ipsmall.util.Base64Util;

public class AuthenticationToken {
    // private static Logger log = Logger.getLogger(AuthenticationToken.class);
    
    public static byte[] calculate(SecretKey macKey, PublicKey publicKey, DHDomainParameter domainParameter)
            throws EIDCryptoException {
        try {
            // log.debug( "calculating authentication token" );
            byte[] macContent = UtilPublicKey.toDataObjectBytes(publicKey, domainParameter, 2);
            Logger.EAC.logState("Authentication Token Plain: " + Base64Util.encodeHEX(macContent));
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
    
}
