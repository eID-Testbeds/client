package com.secunet.bouncycastle.crypto.tls;

import java.io.IOException;

import com.secunet.bouncycastle.crypto.tls.TlsCredentials;

public interface TlsEncryptionCredentials extends TlsCredentials
{
    byte[] decryptPreMasterSecret(byte[] encryptedPreMasterSecret)
        throws IOException;
}
