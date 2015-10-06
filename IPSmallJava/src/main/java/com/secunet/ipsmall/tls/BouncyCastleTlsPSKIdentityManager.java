package com.secunet.ipsmall.tls;

import java.util.HashMap;

import com.secunet.bouncycastle.crypto.tls.TlsPSKIdentityManager;
import org.bouncycastle.util.Strings;

public class BouncyCastleTlsPSKIdentityManager implements TlsPSKIdentityManager {
    
    private HashMap<String,byte[]> pskCredentials = new HashMap<String,byte[]>();
    
    public byte[] addPSKCredential(String ident, byte[] psk) {
        return pskCredentials.put(ident, psk);
    }
    
    public boolean removePSKCredential(String ident, byte[] psk) {
        return pskCredentials.remove(ident, psk);
    }
    
    public void clearPSKCredentials() {
        pskCredentials.clear();
    }
    
    public byte[] getHint() {
        return Strings.toUTF8ByteArray("hint");
    }
    
    public byte[] getPSK(byte[] identity) {
        if (identity != null) {
            String name = Strings.fromUTF8ByteArray(identity);
            if (pskCredentials.containsKey(name)) {
                return pskCredentials.get(name);
            }
        }
        return null;
    }
    
}