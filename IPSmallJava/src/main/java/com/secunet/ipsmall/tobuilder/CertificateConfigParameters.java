package com.secunet.ipsmall.tobuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * Contains certificate parameters from configuration.
 */
public class CertificateConfigParameters {

    private static final String DEFAULT_NAME = "default";
    private static final String SUFFIX_CERTIFICATE = ".certificate";
    private static final String SUFFIX_PRIVATEKEY = ".privatekey";
    private static final String SUFFIX_CURVE = ".eccurve";
    private static final String SUFFIX_DHPARAM = ".dhParameters";
    private static final String SUFFIX_KEYLENGTH = ".keylength";

    private String certificateName = null;
    private String privateKeyName = null;

    private Integer keyLength = null;
    private String curveName = null;
    private String dhParamName = null;

    /**
     * Creates certificate parameters from configuration.
     * @param configIndex Configuration index for certificate. If <i>null</i> default will be taken.
     * @param keyAlgorithm Certificate key algorithm.
     * @param serverPrefix Configuration prefix for related server.
     * @param settings Test object generation settings.
     */
    private CertificateConfigParameters(String configIndex, CertificateKeyAlgorithm keyAlgorithm, String serverPrefix, TestObjectSettings settings) {
        String configCertKey = serverPrefix + keyAlgorithm.getConfigKey() + "." + DEFAULT_NAME;
        if (configIndex != null)
            configCertKey = serverPrefix + keyAlgorithm.getConfigKey() + "." + configIndex;

        if (settings != null) {
            Properties properties = settings.getTestObjectProperies();
            if (properties != null) {
                certificateName = properties.getProperty(configCertKey + SUFFIX_CERTIFICATE);
                privateKeyName = properties.getProperty(configCertKey + SUFFIX_PRIVATEKEY);
                String keyLength = properties.getProperty(configCertKey + SUFFIX_KEYLENGTH);
                if (keyLength != null) {
                    this.keyLength = Integer.parseInt(keyLength);
                }
                curveName = properties.getProperty(configCertKey + SUFFIX_CURVE);
                dhParamName = properties.getProperty(configCertKey + SUFFIX_DHPARAM);
            }
        }
    }

    /**
     * Gets default certificate parameters from configuration.
     * @param keyAlgorithm Certificate key algorithm.
     * @param serverPrefix Configuration prefix for related server.
     * @param settings Test object generation settings.
     * @return Certificate parameters from configuration. Null, if no default certificate and private key was found.
     */
    public static CertificateConfigParameters getCertificateConfigParameters(CertificateKeyAlgorithm keyAlgorithm, String serverPrefix, TestObjectSettings settings) {
        return getCertificateConfigParameters(null, keyAlgorithm, serverPrefix, settings);
    }

    /**
     * Gets certificate parameters from configuration.
     * @param configIndex Configuration index for certificate. If <i>null</i> default will be taken.
     * @param keyAlgorithm Certificate key algorithm.
     * @param serverPrefix Configuration prefix for related server.
     * @param settings Test object generation settings.
     * @return Certificate parameters from configuration. Null, if no default certificate and private key was found.
     */
    public static CertificateConfigParameters getCertificateConfigParameters(String configIndex, CertificateKeyAlgorithm keyAlgorithm, String serverPrefix, TestObjectSettings settings) {
        CertificateConfigParameters params = new CertificateConfigParameters(configIndex, keyAlgorithm, serverPrefix, settings);
        if (params == null || params.getCertificateName() == null || params.getCertificateName().isEmpty() ||
                params.getPrivateKeyName() == null || params.getPrivateKeyName().isEmpty()) {
            params = null;
        }

        return params;
    }

    /**
     * Gets file name of certificate.
     * @return File name of certificate.
     */
    public String getCertificateName() {
        return certificateName;
    }

    /**
     * Gets file name of certificates private key.
     * @return File name of certificates private key.
     */
    public String getPrivateKeyName() {
        return privateKeyName;
    }

    /**
     * Gets length of certificates private key.
     * @return Length of certificates private key.
     */
    public Integer getKeyLength() {
        return keyLength;
    }

    /**
     * Gets curve name of certificate.
     * @return Curve name of certificate.
     */
    public String getCurveName() {
        return curveName;
    }
    
    /**
     * Gets coresponding DH parameters name of certificate.
     * @return Coresponding DH parameters name of certificate.
     */
    public String getDHParamName() {
        return dhParamName;
    }

    /**
     * Gets a list of certificate configurations for given configuration key.
     * @param keyAlgorithm Certificate key algorithm.
     * @param serverPrefix Configuration prefix for related server.
     * @param settings Test object generation settings.
     * @return
     */
    public static CertificateConfigParameters[] getNumberedCertificateConfigurations(CertificateKeyAlgorithm keyAlgorithm, String serverPrefix, TestObjectSettings settings, CryptoParameters params) {
        List<CertificateConfigParameters> configurations = new ArrayList<CertificateConfigParameters>();

        if (settings != null) {
            Properties properties = settings.getTestObjectProperies();
            if (properties != null) {
                int certNumber = 0;
                boolean search = true;
                while (search) {
                    String certName = properties.getProperty(serverPrefix + keyAlgorithm.getConfigKey() + "." + certNumber + SUFFIX_CERTIFICATE);
                    String certKey = properties.getProperty(serverPrefix + keyAlgorithm.getConfigKey() + "." + certNumber + SUFFIX_PRIVATEKEY);
                    if (certName != null && !certName.isEmpty() && certKey != null && !certKey.isEmpty()) {
                        CertificateConfigParameters configParams = new CertificateConfigParameters((new Integer(certNumber)).toString(), keyAlgorithm, serverPrefix, settings);
                        boolean valid = true;

                        // check if certificate config parameters are valid for ICS parameters
                        if (params != null) {
                            switch (keyAlgorithm) {
                                case RSA:
                                    valid = false;
                                    for (Integer supportedLength : params.getSupportedRSAKeyLengthValues()) {
                                        if (supportedLength != null && configParams.getKeyLength() != null &&
                                                supportedLength.compareTo(configParams.getKeyLength()) == 0) {
                                            valid = true;
                                            break;
                                        }
                                    }
                                    break;

                                case DSA:
                                    valid = false;
                                    for (Integer supportedLength : params.getSupportedDSAKeyLengthValues()) {
                                        if (supportedLength != null && configParams.getKeyLength() != null &&
                                                supportedLength.compareTo(configParams.getKeyLength()) == 0) {
                                            valid = true;
                                            break;
                                        }
                                    }
                                    break;

                                case ECDSA:
                                    valid = false;
                                    for (String supportedCurve : params.getSupportedCurves()) {
                                        if (supportedCurve != null && configParams.getCurveName() != null &&
                                                supportedCurve.equals(configParams.getCurveName())) {
                                            valid = true;
                                            break;
                                        }
                                    }
                                    break;

                                default:
                                    break;
                            }
                        }

                        if (valid) {
                            configurations.add(configParams);
                        }
                        certNumber++;
                    } else {
                        search = false;
                        break;
                    }
                }
            }
        }

        return configurations.toArray(new CertificateConfigParameters[configurations.size()]);
    }
}
