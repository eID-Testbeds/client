package com.secunet.bouncycastle.crypto.tls;

import com.secunet.bouncycastle.crypto.tls.Certificate;

public interface TlsCredentials
{
    Certificate getCertificate();
}
