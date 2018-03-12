package com.secunet.ipsmall.tobuilder;

import org.bouncycastle.crypto.tls.SignatureAndHashAlgorithm;

import com.secunet.ipsmall.tobuilder.ics.TLSSupportedCurveType;
import com.secunet.ipsmall.tobuilder.ics.TLSSupportedSignatureAlgorithmType;
import com.secunet.ipsmall.tobuilder.ics.TLSVersionType;
import com.secunet.ipsmall.tobuilder.ics.TLSchannelType;
import com.secunet.testbedutils.utilities.BouncyCastleTlsHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * Contains crypto parameters from ICS.
 */
public class CryptoParameters {
    private String tlsVersion = "";

    private List<String> supportedCurves = new ArrayList<String>();

    private List<Integer> supportedRSAKeyLength = new ArrayList<Integer>();
    private List<Integer> supportedDHEKeyLength = new ArrayList<Integer>();
    private List<Integer> supportedDSAKeyLength = new ArrayList<Integer>();

    private List<String> supportedSignatureAlgorithms  = new ArrayList<String>();

    private static final int[] allowedKeyLength = new int[] {
            1024,
            1536,
            2048,
            3072,
            4096,
    };

    /**
     * Creates crypto params from tlsElement.
     * @param tlsElement The tlsElement from ICS.
     */
    public CryptoParameters(TLSchannelType.TLSVersion tlsElement) {
        if (tlsElement != null) {
            // get TLS version
            TLSVersionType tlsVersion = tlsElement.getVersion();
            if (tlsVersion != null) {
                this.tlsVersion = tlsVersion.value();
            }

            // get curves
            List<TLSSupportedCurveType> supportedCurves = tlsElement.getSupportedCurve();
            if (supportedCurves != null) {
                for (TLSSupportedCurveType curve : supportedCurves) {
                    this.supportedCurves.add(curve.value());
                }
            }

            // supported key length values for ...
            supportedRSAKeyLength = getPossibleKeyLength(tlsElement.getMinRSAKeyLength()); // ... RSA
            supportedDHEKeyLength = getPossibleKeyLength(tlsElement.getMinDHEKeyLength()); // ... DHE
            supportedDSAKeyLength = getPossibleKeyLength(tlsElement.getMinDSAKeyLength()); // ... DSA

            // get supported signature algorithms
            List<TLSSupportedSignatureAlgorithmType> supportedSignatureAlgorithms = tlsElement.getSupportedSignatureAlgorithm();
            if (supportedSignatureAlgorithms != null) {
                for (TLSSupportedSignatureAlgorithmType algorithm : supportedSignatureAlgorithms) {
                    this.supportedSignatureAlgorithms.add(algorithm.value());
                }
            }
        }
    }

    /**
     * Gets TLS version.
     * @return TLS version.
     */
    public String getTlsVersion() {
        return tlsVersion;
    }

    /**
     * Gets supported elliptic curves.
     * @return Supported elliptic curves.
     */
    public String[] getSupportedCurves() {
        return supportedCurves.toArray(new String[supportedCurves.size()]);
    }

    /**
     * Gets supported key length values for RSA.
     * @return Supported key length values for RSA.
     */
    public Integer[] getSupportedRSAKeyLengthValues() {
        return supportedRSAKeyLength.toArray(new Integer[supportedRSAKeyLength.size()]);
    }

    /**
     * Gets supported key length values for DHE.
     * @return Supported key length values for DHE.
     */
    public Integer[] getSupportedDHEKeyLengthValues() {
        return supportedDHEKeyLength.toArray(new Integer[supportedDHEKeyLength.size()]);
    }

    /**
     * Gets supported key length values for DSA.
     * @return Supported key length values for DSA.
     */
    public Integer[] getSupportedDSAKeyLengthValues() {
        return supportedDSAKeyLength.toArray(new Integer[supportedDSAKeyLength.size()]);
    }

    /**
     * Gets supported supported signature algorithms.
     * @return Supported supported signature algorithms.
     */
    public String[] getSupportedSignatureAlgorithms() {
        return supportedSignatureAlgorithms.toArray(new String[supportedSignatureAlgorithms.size()]);
    }

    /**
     * Gets supported supported signature algorithms if fits to given signature algorithm.
     * @param signatureAlgorithm TLS signature algorithm.
     * @return Supported supported signature algorithms.
     */
    public String[] getSupportedSignatureAlgorithms(short signatureAlgorithm) {
        List<String> supportedSignatureAlgorithms  = new ArrayList<String>();
        for (String algorithmName : this.supportedSignatureAlgorithms) {
            SignatureAndHashAlgorithm algorithm = BouncyCastleTlsHelper.convertSignatureAndHashAlgorithmStringToClass(algorithmName);
            if (algorithm.getSignature() == signatureAlgorithm) {
                supportedSignatureAlgorithms.add(algorithmName);
            }
        }

        return supportedSignatureAlgorithms.toArray(new String[supportedSignatureAlgorithms.size()]);
    }

    /**
     * Gets a list of possible key length values.
     * @param minKeyLength Minimum key length.
     * @return List of possible key length values.
     */
    private List<Integer> getPossibleKeyLength(Long minKeyLength) {
        List<Integer> possibleKeyLength = new ArrayList<Integer>();
        if (minKeyLength != null) {
            for (int keyLength : allowedKeyLength) {
                if (keyLength >= minKeyLength) {
                    possibleKeyLength.add(keyLength);
                }
            }
        }
        return possibleKeyLength;
    }
}
