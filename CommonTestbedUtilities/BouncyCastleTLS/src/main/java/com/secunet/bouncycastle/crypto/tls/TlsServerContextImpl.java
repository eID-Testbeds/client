package com.secunet.bouncycastle.crypto.tls;

import java.security.SecureRandom;

import com.secunet.bouncycastle.crypto.tls.AbstractTlsContext;
import com.secunet.bouncycastle.crypto.tls.SecurityParameters;
import com.secunet.bouncycastle.crypto.tls.TlsServerContext;

class TlsServerContextImpl
    extends AbstractTlsContext
    implements TlsServerContext
{
    TlsServerContextImpl(SecureRandom secureRandom, SecurityParameters securityParameters)
    {
        super(secureRandom, securityParameters);
    }

    public boolean isServer()
    {
        return true;
    }
}
