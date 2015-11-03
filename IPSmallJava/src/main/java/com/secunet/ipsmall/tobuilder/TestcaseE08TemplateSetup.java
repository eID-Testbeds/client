package com.secunet.ipsmall.tobuilder;

import com.secunet.bouncycastle.crypto.tls.KeyExchangeAlgorithm;
import com.secunet.ipsmall.GlobalSettings;
import com.secunet.ipsmall.log.Logger;
import com.secunet.ipsmall.tls.BouncyCastleTlsHelper;
import com.secunet.ipsmall.tobuilder.ics.TLSCipherSuiteType;
import com.secunet.ipsmall.tobuilder.ics.TLSVersionType;
import com.secunet.ipsmall.tobuilder.ics.TLSchannelType;
import com.secunet.ipsmall.tobuilder.ics.TR031242ICS;
import com.secunet.ipsmall.util.FileUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

/**
 * Generates testcases from template for E_08.
 */
public class TestcaseE08TemplateSetup implements ITestObjectSetup {
    private static final String TESTCASE_NAME = "EID_CLIENT_E_08_T";

    private static final String PREFIX_ESERVICE = "eservice.";
    private static final String DEST_CONFIG_CIPHERSUITE = "tls.ciphersuites";
    private static final String DEST_CONFIG_CERTIFICATE = "certificate";
    private static final String DEST_CONFIG_PRIVATEKEY = "privatekey";
    private static final String DEST_CONFIG_DHPARAM = "tls.dhParameters";

    private static final String DEFAULT_NAME = "default";
    private static final String DEFAULT_TLSVERSION = "tlsversion." + DEFAULT_NAME;

    private final TestObjectSettings settings;
    private boolean isSetUp = false;

    /**
     * Creates setup to generate testcases from template for E_08.
     *
     * @param settings TestObject settings.
     */
    public TestcaseE08TemplateSetup(TestObjectSettings settings) {
        this.settings = settings;
    }

    @Override
    public void runSetup() throws Exception {
        Logger.TestObjectBuilder.logState("Generating testcases from template for E_08 ...");

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
                List<TLSchannelType.TLSVersion> tlsElements = tlsEService.getTLSVersion();
                if (tlsElements != null) {
                    for (TLSchannelType.TLSVersion tlsElement : tlsElements) {
                        TLSVersionType version = tlsElement.getVersion();
                        Properties properties = settings.getTestObjectProperies();
                        if (version != null && properties != null && version.value().equals(properties.getProperty(PREFIX_ESERVICE + DEFAULT_TLSVERSION))) {
                            TemplateTestcaseConfiguration[] testcases = getTestcasesFromTLSElement(tlsElement);
                            allTestcases.addAll(Arrays.asList(testcases));
                        }
                    }
                }
            }
        }

        // create testcases from configuration
        for (int i = 0; i < allTestcases.size(); i++) {
            allTestcases.get(i).create(TESTCASE_NAME + String.format("%03d", i + 1), settings);
        }

        Logger.TestObjectBuilder.logState("Testcases from template for E_08 generated.");

        isSetUp = true;
    }

    @Override
    public boolean IsSetUp() {
        return isSetUp;
    }

    /**
     * Gets a list of testcase configurations resulting of given tlsElement.
     * @param tlsElement tlsElement from ICS.
     * @return List of testcase configurations.
     */
    private TemplateTestcaseConfiguration[] getTestcasesFromTLSElement(TLSchannelType.TLSVersion tlsElement) {
        List<TemplateTestcaseConfiguration> resultTestcases = new ArrayList<>();

        // get testcases only if TLS is enabled
        if (tlsElement != null && tlsElement.isEnabled()) {
            // run over all chiphersuites in tlsElement
            List<TLSCipherSuiteType> cipherSuites = tlsElement.getCipherSuite();
            if (cipherSuites != null) {
                for (TLSCipherSuiteType cipherSuite : cipherSuites) {
                    if (cipherSuite != null) {
                        String cipherSuiteName = cipherSuite.value();
                        if (cipherSuiteName != null && !cipherSuiteName.isEmpty()) {
                            // get key exchange algorithm from chipher suite
                            int keyExchangeAlgorithm = BouncyCastleTlsHelper.getKeyExchangeAlgorithmFromCipherSuite(cipherSuiteName);
                            CertificateKeyAlgorithm keyAlgorithm = CertificateKeyAlgorithm.getCertificateKeyAlgorithmFromKeyExchangeAlgorithm(keyExchangeAlgorithm);
                            switch (keyExchangeAlgorithm) {
                                case KeyExchangeAlgorithm.DHE_RSA:
                                case KeyExchangeAlgorithm.DHE_DSS:
                                case KeyExchangeAlgorithm.ECDHE_RSA:
                                case KeyExchangeAlgorithm.ECDHE_ECDSA: {
                                    CertificateConfigParameters certConfig = CertificateConfigParameters.getCertificateConfigParameters(keyAlgorithm, PREFIX_ESERVICE, settings);
                                    TemplateTestcaseConfiguration tcConfig = new TemplateTestcaseConfiguration(TESTCASE_NAME);
                                    tcConfig.addValueToConfig(PREFIX_ESERVICE + DEST_CONFIG_CIPHERSUITE, cipherSuiteName);
                                    tcConfig.addValueToConfig(PREFIX_ESERVICE + DEST_CONFIG_CERTIFICATE, certConfig.getCertificateName());
                                    tcConfig.addValueToConfig(PREFIX_ESERVICE + DEST_CONFIG_PRIVATEKEY, certConfig.getPrivateKeyName());
                                    resultTestcases.add(tcConfig);
                                    } break;

                                default:
                                    break;
                            }
                        }
                    }
                }
            }
        }

        return resultTestcases.toArray(new TemplateTestcaseConfiguration[resultTestcases.size()]);
    }
}
