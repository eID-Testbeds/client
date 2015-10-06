package com.secunet.bouncycastle.crypto.tls;

import java.io.IOException;

import com.secunet.bouncycastle.crypto.tls.AlertDescription;
import com.secunet.bouncycastle.crypto.tls.TlsCipher;
import com.secunet.bouncycastle.crypto.tls.TlsCipherFactory;
import com.secunet.bouncycastle.crypto.tls.TlsContext;
import com.secunet.bouncycastle.crypto.tls.TlsFatalAlert;

public class AbstractTlsCipherFactory
    implements TlsCipherFactory
{
    public TlsCipher createCipher(TlsContext context, int encryptionAlgorithm, int macAlgorithm)
        throws IOException
    {
        throw new TlsFatalAlert(AlertDescription.internal_error);
    }
}
