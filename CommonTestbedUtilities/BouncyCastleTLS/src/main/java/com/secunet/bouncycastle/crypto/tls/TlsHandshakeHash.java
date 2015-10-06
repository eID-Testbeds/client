package com.secunet.bouncycastle.crypto.tls;

import org.bouncycastle.crypto.Digest;
import com.secunet.bouncycastle.crypto.tls.TlsContext;
import com.secunet.bouncycastle.crypto.tls.TlsHandshakeHash;

public interface TlsHandshakeHash
    extends Digest
{
    void init(TlsContext context);

    TlsHandshakeHash notifyPRFDetermined();

    void trackHashAlgorithm(short hashAlgorithm);

    void sealHashAlgorithms();

    TlsHandshakeHash stopTracking();

    Digest forkPRFHash();

    byte[] getFinalHash(short hashAlgorithm);
}
