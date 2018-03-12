package com.secunet.ipsmall.util;

import org.bouncycastle.crypto.tls.ProtocolVersion;

import com.secunet.ipsmall.test.ITestData.PROTOCOLS;

public class BouncyCastleTlsUtils {
    
    /**
     * Converts a configuration string for TLS versions to the corresponding BouncyCastle ProtocolVersion object.
     * If conversion fails, throws a RuntimeException.
     * @param protocol String as defined by enumeration {@link PROTOCOLS}
     * @return the corresponding BouncyCastle ProtocolVersion object
     */
    public static ProtocolVersion convertProtocolVersionFromEnumToObject(String protocol) {
        // SSLv2 not supported by BouncyCastle
        if (PROTOCOLS.sslv3.name().equalsIgnoreCase(protocol)) {
            return ProtocolVersion.SSLv3;
        } else if (PROTOCOLS.tls10.name().equalsIgnoreCase(protocol)) {
            return ProtocolVersion.TLSv10;
        } else if (PROTOCOLS.tls11.name().equalsIgnoreCase(protocol)) {
            return ProtocolVersion.TLSv11;
        } else if (PROTOCOLS.tls12.name().equalsIgnoreCase(protocol)) {
            return ProtocolVersion.TLSv12;
        }
        
        throw new RuntimeException("Could not convert '" + protocol + "' to a valid TLS ProtocolVerison for BouncyCastle.");
    }

}
