package com.secunet.bouncycastle.crypto.tls;

import java.io.OutputStream;

import com.secunet.bouncycastle.crypto.tls.TlsCompression;

public class TlsNullCompression
    implements TlsCompression
{
    public OutputStream compress(OutputStream output)
    {
        return output;
    }

    public OutputStream decompress(OutputStream output)
    {
        return output;
    }
}
