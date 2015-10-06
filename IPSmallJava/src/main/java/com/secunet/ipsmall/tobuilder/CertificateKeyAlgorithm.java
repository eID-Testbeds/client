package com.secunet.ipsmall.tobuilder;

import com.secunet.bouncycastle.crypto.tls.KeyExchangeAlgorithm;
import com.secunet.bouncycastle.crypto.tls.SignatureAlgorithm;

/**
 * Represents a certificate key algorithm.
 */
public enum CertificateKeyAlgorithm {
    Unknown(null),
    RSA("rsa"),
    DSA("dsa"),
    ECDSA("ecdsa"),
    DH("dh"),
    ECDH("ecdh");

    private String configKey;

    /**
     * Creates representation of a certificate key algorithm.
     * @param configKey Configuration key.
     */
    CertificateKeyAlgorithm(String configKey) {
        this.configKey = configKey;
    }

    /**
     * Gets configuration key.
     * @return Configuration key.
     */
    public String getConfigKey() {
        return configKey;
    }

    /**
     * Gets corresponding TLS signature algorithm.
     * @return Corresponding TLS signature algorithm
     */
    public short getSignatureAlgorithm() {
        switch (this) {
            case RSA:
                return SignatureAlgorithm.rsa;
            case DSA:
                return SignatureAlgorithm.dsa;
            case ECDSA:
                return SignatureAlgorithm.ecdsa;

            default:
                return SignatureAlgorithm.anonymous;
        }
    }

    /**
     * Gets certificate key algorithm from key exchange algorithm.
     * @param keyExchangeAlgorithm Key exchange algorithm.
     * @return Certificate key algorithm.
     */
    public static CertificateKeyAlgorithm getCertificateKeyAlgorithmFromKeyExchangeAlgorithm(int keyExchangeAlgorithm) {
        switch (keyExchangeAlgorithm) {
            case KeyExchangeAlgorithm.RSA:
            case KeyExchangeAlgorithm.DHE_RSA:
            case KeyExchangeAlgorithm.ECDHE_RSA:
            case KeyExchangeAlgorithm.RSA_PSK:
                return CertificateKeyAlgorithm.RSA;

            case KeyExchangeAlgorithm.DH_DSS:
            case KeyExchangeAlgorithm.DH_RSA:
                return CertificateKeyAlgorithm.DH;

            case KeyExchangeAlgorithm.DHE_DSS:
                return CertificateKeyAlgorithm.DSA;

            case KeyExchangeAlgorithm.ECDHE_ECDSA:
                return CertificateKeyAlgorithm.ECDSA;

            case KeyExchangeAlgorithm.ECDH_RSA:
            case KeyExchangeAlgorithm.ECDH_ECDSA:
                return CertificateKeyAlgorithm.ECDH;

            default:
                return CertificateKeyAlgorithm.Unknown;
        }
    }
}
