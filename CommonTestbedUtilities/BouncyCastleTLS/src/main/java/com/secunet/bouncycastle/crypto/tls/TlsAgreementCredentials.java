package com.secunet.bouncycastle.crypto.tls;

import java.io.IOException;

import org.bouncycastle.crypto.params.AsymmetricKeyParameter;
import com.secunet.bouncycastle.crypto.tls.TlsCredentials;

public interface TlsAgreementCredentials
    extends TlsCredentials
{
    byte[] generateAgreement(AsymmetricKeyParameter peerPublicKey)
        throws IOException;
}
