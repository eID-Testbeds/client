package com.secunet.bouncycastle.crypto.tls;

import java.io.IOException;

import com.secunet.bouncycastle.crypto.tls.TlsCipher;
import com.secunet.bouncycastle.crypto.tls.TlsContext;

public interface TlsCipherFactory
{
    /**
     * See enumeration classes EncryptionAlgorithm, MACAlgorithm for appropriate argument values
     */
    TlsCipher createCipher(TlsContext context, int encryptionAlgorithm, int macAlgorithm)
        throws IOException;
}
