package com.secunet.bouncycastle.crypto.tls;

import com.secunet.bouncycastle.crypto.tls.CertificateRequest;
import com.secunet.bouncycastle.crypto.tls.TlsAuthentication;
import com.secunet.bouncycastle.crypto.tls.TlsCredentials;

public abstract class ServerOnlyTlsAuthentication
    implements TlsAuthentication
{
    public final TlsCredentials getClientCredentials(CertificateRequest certificateRequest)
    {
        return null;
    }
}
