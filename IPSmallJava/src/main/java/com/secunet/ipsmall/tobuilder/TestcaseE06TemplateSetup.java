package com.secunet.ipsmall.tobuilder;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

import com.secunet.ipsmall.GlobalSettings;
import com.secunet.ipsmall.log.Logger;
import com.secunet.ipsmall.tls.BouncyCastleTlsHelper;
import com.secunet.ipsmall.tobuilder.ics.TLSCipherSuiteType;
import com.secunet.ipsmall.tobuilder.ics.TLSchannelType;
import com.secunet.ipsmall.tobuilder.ics.TLSchannelType.TLSVersion;
import com.secunet.ipsmall.tobuilder.ics.TR031242ICS;
import com.secunet.ipsmall.util.FileUtils;

/**
 * Generates testcases from template for E_06.
 */
public class TestcaseE06TemplateSetup implements ITestObjectSetup {

    private static final String TESTCASE_NAME = "EID_CLIENT_E_06_T";

    private static final String SUFFIX_CIPHERSUITE = ".ciphersuite";

    private static final String CERTIFICATE_ALGORITM_RSA = "rsa";
    private static final String CERTIFICATE_ALGORITM_DSA = "dsa";
    private static final String CERTIFICATE_ALGORITM_ECDSA = "ecdsa";

    private static final String DEFAULT_NAME = "default";

    private static final String DEFAULT_TLSVERSION = "tlsversion." + DEFAULT_NAME;
    private static final String DEFAULT_ECDSA_CIPHERSUITE = CERTIFICATE_ALGORITM_ECDSA + "." + DEFAULT_NAME + SUFFIX_CIPHERSUITE;
    private static final String DEFAULT_DSA_CIPHERSUITE = CERTIFICATE_ALGORITM_DSA + "." + DEFAULT_NAME + SUFFIX_CIPHERSUITE;
    private static final String DEFAULT_RSA_CIPHERSUITE = CERTIFICATE_ALGORITM_RSA + "." + DEFAULT_NAME + SUFFIX_CIPHERSUITE;

    private static final String DEST_CONFIG_CERTIFICATE = "certificate";
    private static final String DEST_CONFIG_PRIVATEKEY = "privatekey";
    private static final String DEST_CONFIG_CIPHERSUITE = "tls.ciphersuites";
    private static final String DEST_CONFIG_TLSVERSION = "tls.version";
    private static final String DEST_CONFIG_ECCURVE = "tls.eccurve";
    private static final String DEST_CONFIG_DHPARAM = "tls.dhParameters";
    private static final String DEST_CONFIG_SIGNATUREALGORITHM = "tls.signaturealgorithm";

    private final TestObjectSettings settings;
    private boolean isSetUp = false;

    /**
     * Creates setup to generate testcases from template for E_06.
     *
     * @param settings TestObject settings.
     */
    public TestcaseE06TemplateSetup(TestObjectSettings settings) {
        this.settings = settings;
    }

    @Override
    public void runSetup() throws Exception {
        Logger.TestObjectBuilder.logState("Generating testcases from template for E_06 ...");

        // delete old template testcase
        File testcaseTemplate = new File(new File(settings.getTestObjectDir(), GlobalSettings.getTOTestsDir()),
                settings.getModule(TESTCASE_NAME) + File.separator + TESTCASE_NAME);
        if (testcaseTemplate.exists()) {
            FileUtils.deleteDir(testcaseTemplate);
        }

        List<TemplateTestcaseConfiguration> allTestcases = new ArrayList<>();

        // generate testcase configurations from ics
        TR031242ICS ics = settings.getICS();
        if (ics != null && ics.getSupportedCryptography() != null) {
            // TLS for eService
            TLSchannelType tlsEService = ics.getSupportedCryptography().getTLSchannel12();
            if (tlsEService != null) {
                List<TLSVersion> tlsElements = tlsEService.getTLSVersion();
                if (tlsElements != null) {
                    for (TLSVersion tlsElement : tlsElements) {
                        TemplateTestcaseConfiguration[] testcases = getTestcasesFromTLSElement(tlsElement, RelatedServer.eService);
                        allTestcases.addAll(Arrays.asList(testcases));
                    }
                }
            }

            // TLS for eIDServer
            TLSchannelType tlsEIDServer = ics.getSupportedCryptography().getTLSchannel2();
            if (tlsEIDServer != null) {
                List<TLSVersion> tlsElements = tlsEIDServer.getTLSVersion();
                if (tlsElements != null) {
                    for (TLSVersion tlsElement : tlsElements) {
                        TemplateTestcaseConfiguration[] testcases = getTestcasesFromTLSElement(tlsElement, RelatedServer.eIDServer);
                        allTestcases.addAll(Arrays.asList(testcases));
                    }
                }
            }
        }

        // create testcases from configuration
        for (int i = 0; i < allTestcases.size(); i++) {
            allTestcases.get(i).create(TESTCASE_NAME + String.format("%03d", i + 1), settings);
        }

        Logger.TestObjectBuilder.logState("Testcases from template for E_06 generated.");
        isSetUp = true;
    }

    @Override
    public boolean IsSetUp() {
        return isSetUp;
    }

    /**
     * Gets a list of testcase configurations resulting of given tlsElement.
     *
     * @param tlsElement tlsElement from ICS.
     * @param server Related server.
     * @return List of testcase configurations.
     */
    private TemplateTestcaseConfiguration[] getTestcasesFromTLSElement(TLSchannelType.TLSVersion tlsElement, RelatedServer server) {
        List<TemplateTestcaseConfiguration> resultTestcases = new ArrayList<>();

        // get testcases only if TLS is enabled
        if (tlsElement != null && tlsElement.isEnabled()) {
            // get crypto parameters from tlsElement
            CryptoParameters params = new CryptoParameters(tlsElement);

            // run over all chiphersuites in tlsElement
            List<TLSCipherSuiteType> cipherSuites = tlsElement.getCipherSuite();
            if (cipherSuites != null) {
                for (TLSCipherSuiteType cipherSuite : cipherSuites) {
                    if (cipherSuite != null) {
                        String cipherSuiteName = cipherSuite.value();
                        if (cipherSuiteName != null && !cipherSuiteName.isEmpty()) {
                            TemplateTestcaseConfiguration[] testcases = getTestcasesForCipherSuite(cipherSuiteName, params, server);
                            resultTestcases.addAll(Arrays.asList(testcases));
                        }
                    }
                }
            }
        }

        return resultTestcases.toArray(new TemplateTestcaseConfiguration[resultTestcases.size()]);
    }

    /**
     * Gets a list of testcase configurations resulting of given cipherSuite.
     *
     * @param cipherSuite The cipher suite.
     * @param params Crypto parameters.
     * @param server Related server.
     * @return List of testcase configurations.
     */
    private TemplateTestcaseConfiguration[] getTestcasesForCipherSuite(String cipherSuite, CryptoParameters params, RelatedServer server) {
        List<TemplateTestcaseConfiguration> resultTestcases = new ArrayList<>();

        // get key exchange algorithm from chipher suite
        int keyExchangeAlgorithm = BouncyCastleTlsHelper.getKeyExchangeAlgorithmFromCipherSuite(cipherSuite);
        CertificateKeyAlgorithm keyAlgorithm = CertificateKeyAlgorithm.getCertificateKeyAlgorithmFromKeyExchangeAlgorithm(keyExchangeAlgorithm);

        // check if ciphersuite and tlsversion is configured as default
        if (isDefault(cipherSuite, params.getTlsVersion(), server)) { // iterate over several crypto parameters
            // iterate over possible key params (certificates)
            CertificateConfigParameters[] certConfigs = CertificateConfigParameters.getNumberedCertificateConfigurations(keyAlgorithm, server.getConfigPrefix(), settings, params);
            for (CertificateConfigParameters certConfig : certConfigs) {
                if (certConfig != null) {
                    TemplateTestcaseConfiguration tcConfig = new TemplateTestcaseConfiguration(TESTCASE_NAME);
                    tcConfig.addValueToConfig(server.getConfigPrefix() + DEST_CONFIG_CIPHERSUITE, cipherSuite);
                    tcConfig.addValueToConfig(server.getConfigPrefix() + DEST_CONFIG_TLSVERSION, params.getTlsVersion());
                    tcConfig.addValueToConfig(server.getConfigPrefix() + DEST_CONFIG_CERTIFICATE, certConfig.getCertificateName());
                    tcConfig.addValueToConfig(server.getConfigPrefix() + DEST_CONFIG_PRIVATEKEY, certConfig.getPrivateKeyName());
                    switch (keyAlgorithm) {
                        case ECDSA: // on ECDSA key use for key agreement corresponding curve.
                            tcConfig.addValueToConfig(server.getConfigPrefix() + DEST_CONFIG_ECCURVE, certConfig.getCurveName());
                            break;
                        case DSA: // on DSA key use for key agreement corresponding group.
                            tcConfig.addValueToConfig(server.getConfigPrefix() + DEST_CONFIG_DHPARAM, certConfig.getDHParamName());
                            break;
                        default:
                            break;
                    }
                    resultTestcases.add(tcConfig);
                }
            }

            // iterate over supported signature algorithm (using default certificate)
            CertificateConfigParameters certConfig = CertificateConfigParameters.getCertificateConfigParameters(keyAlgorithm, server.getConfigPrefix(), settings); // default certificate for this algorithm
            if (certConfig != null) {
                String[] supportedSignatureAlgorithms = params.getSupportedSignatureAlgorithms(keyAlgorithm.getSignatureAlgorithm()); // get only for this algorithm usable supported signature algorithms
                for (String supportedSignatureAlgorithm : supportedSignatureAlgorithms) {
                    TemplateTestcaseConfiguration tcConfig = new TemplateTestcaseConfiguration(TESTCASE_NAME);
                    tcConfig.addValueToConfig(server.getConfigPrefix() + DEST_CONFIG_CIPHERSUITE, cipherSuite);
                    tcConfig.addValueToConfig(server.getConfigPrefix() + DEST_CONFIG_TLSVERSION, params.getTlsVersion());
                    tcConfig.addValueToConfig(server.getConfigPrefix() + DEST_CONFIG_CERTIFICATE, certConfig.getCertificateName());
                    tcConfig.addValueToConfig(server.getConfigPrefix() + DEST_CONFIG_PRIVATEKEY, certConfig.getPrivateKeyName());
                    tcConfig.addValueToConfig(server.getConfigPrefix() + DEST_CONFIG_SIGNATUREALGORITHM, supportedSignatureAlgorithm);
                    resultTestcases.add(tcConfig);
                }
            }

        } else { // use default crypto parameters
            CertificateConfigParameters certConfig = CertificateConfigParameters.getCertificateConfigParameters(keyAlgorithm, server.getConfigPrefix(), settings);
            if (certConfig != null) {
                TemplateTestcaseConfiguration tcConfig = new TemplateTestcaseConfiguration(TESTCASE_NAME);
                tcConfig.addValueToConfig(server.getConfigPrefix() + DEST_CONFIG_CIPHERSUITE, cipherSuite);
                tcConfig.addValueToConfig(server.getConfigPrefix() + DEST_CONFIG_TLSVERSION, params.getTlsVersion());
                tcConfig.addValueToConfig(server.getConfigPrefix() + DEST_CONFIG_CERTIFICATE, certConfig.getCertificateName());
                tcConfig.addValueToConfig(server.getConfigPrefix() + DEST_CONFIG_PRIVATEKEY, certConfig.getPrivateKeyName());
                resultTestcases.add(tcConfig);
            }
        }

        return resultTestcases.toArray(new TemplateTestcaseConfiguration[resultTestcases.size()]);
    }

    /**
     * Gets if given cipher suite and TLS version are default configuration.
     *
     * @param cipherSuite The cipher suite.
     * @param tlsVersion TLS Version.
     * @param server Related server.
     * @return True, if given cipher suite and TLS version are default
     * configuration.
     */
    private boolean isDefault(String cipherSuite, String tlsVersion, RelatedServer server) {
        boolean tlsVersionEqual = false;
        boolean cipherSuiteEqual = false;

        if (settings != null) {
            Properties properties = settings.getTestObjectProperies();
            if (properties != null) {
                tlsVersionEqual = tlsVersion.equals(properties.getProperty(server.getConfigPrefix() + DEFAULT_TLSVERSION));

                cipherSuiteEqual = cipherSuite.equals(properties.getProperty(server.getConfigPrefix() + DEFAULT_ECDSA_CIPHERSUITE))
                        || cipherSuite.equals(properties.getProperty(server.getConfigPrefix() + DEFAULT_DSA_CIPHERSUITE))
                        || cipherSuite.equals(properties.getProperty(server.getConfigPrefix() + DEFAULT_RSA_CIPHERSUITE));
            }
        }

        return tlsVersionEqual & cipherSuiteEqual;
    }
}
