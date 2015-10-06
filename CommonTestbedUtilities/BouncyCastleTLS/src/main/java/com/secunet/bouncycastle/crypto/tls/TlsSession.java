package com.secunet.bouncycastle.crypto.tls;

import com.secunet.bouncycastle.crypto.tls.SessionParameters;

public interface TlsSession
{
    SessionParameters exportSessionParameters();

    byte[] getSessionID();

    void invalidate();

    boolean isResumable();
}
