package com.secunet.bouncycastle.crypto.tls;

import java.io.IOException;

import com.secunet.bouncycastle.crypto.tls.SignatureAndHashAlgorithm;
import com.secunet.bouncycastle.crypto.tls.TlsCredentials;

public interface TlsSignerCredentials
    extends TlsCredentials
{
    byte[] generateCertificateSignature(byte[] hash)
        throws IOException;

    SignatureAndHashAlgorithm getSignatureAndHashAlgorithm();
}
