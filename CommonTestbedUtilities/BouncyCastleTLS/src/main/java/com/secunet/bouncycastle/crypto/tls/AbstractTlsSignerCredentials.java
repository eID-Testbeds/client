package com.secunet.bouncycastle.crypto.tls;

import com.secunet.bouncycastle.crypto.tls.AbstractTlsCredentials;
import com.secunet.bouncycastle.crypto.tls.SignatureAndHashAlgorithm;
import com.secunet.bouncycastle.crypto.tls.TlsSignerCredentials;

public abstract class AbstractTlsSignerCredentials
    extends AbstractTlsCredentials
    implements TlsSignerCredentials
{
    public SignatureAndHashAlgorithm getSignatureAndHashAlgorithm()
    {
        throw new IllegalStateException("TlsSignerCredentials implementation does not support (D)TLS 1.2+");
    }
}
