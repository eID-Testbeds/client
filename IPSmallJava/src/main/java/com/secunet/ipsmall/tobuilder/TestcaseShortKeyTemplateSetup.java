package com.secunet.ipsmall.tobuilder;

import com.secunet.bouncycastle.crypto.tls.KeyExchangeAlgorithm;
import com.secunet.ipsmall.GlobalSettings;
import com.secunet.ipsmall.log.IModuleLogger;
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
 * Generates testcases from template for to short keys/params.
 */
public class TestcaseShortKeyTemplateSetup implements ITestObjectSetup {

    private static final String DEST_CONFIG_CIPHERSUITE = "tls.ciphersuites";
    private static final String DEST_CONFIG_CERTIFICATE = "certificate";
    private static final String DEST_CONFIG_PRIVATEKEY = "privatekey";

    private static final String DEFAULT_NAME = "default";
    private static final String DEFAULT_TLSVERSION = "tlsversion." + DEFAULT_NAME;

    private static final String RSA = "rsa";
    private static final String DSA = "dsa";
    private static final String ECDSA = "ecdsa";
    
    private static final String TOSHORT_CERTIFICATE = ".toshort.certificate";
    private static final String TOSHORT_CERTIFICATE_KEY = ".toshort.privatekey";

    private final TestObjectSettings settings;
    private final String testcaseName;
    private final RelatedServer server;
    private boolean isSetUp = false;

    /**
     * Creates setup to generate testcases from template for to short
     * keys/params.
     *
     * @param testcaseName Name of testcase.
     * @param server Related TLS server.
     * @param settings TestObject settings.
     */
    public TestcaseShortKeyTemplateSetup(String testcaseName, RelatedServer server, TestObjectSettings settings) {
        this.settings = settings;
        this.testcaseName = testcaseName;
        this.server = server;
    }

    @Override
    public void runSetup() throws Exception {
        Logger.TestObjectBuilder.logState("Generating testcases from template for E_12 ...");

        // delete old template testcase
        File testcaseTemplate = new File(new File(settings.getTestObjectDir(), GlobalSettings.getTOTestsDir()),
                settings.getModule(testcaseName) + File.separator + testcaseName);
        if (testcaseTemplate.exists()) {
            FileUtils.deleteDir(testcaseTemplate);
        }

        List<TemplateTestcaseConfiguration> allTestcases = new ArrayList<>();

        // generate testcase configurations from ics
        TR031242ICS ics = settings.getICS();
        if (ics != null && ics.getSupportedCryptography() != null) {
            // TLS channel
            TLSchannelType tlsChannel = null;
            switch (server) {
                case eService:
                    tlsChannel = ics.getSupportedCryptography().getTLSchannel12();
                    break;
                case eIDServer:
                    tlsChannel = ics.getSupportedCryptography().getTLSchannel2();
                    break;
            }

            if (tlsChannel != null) {
                List<TLSchannelType.TLSVersion> tlsElements = tlsChannel.getTLSVersion();
                if (tlsElements != null) {
                    for (TLSchannelType.TLSVersion tlsElement : tlsElements) {
                        TLSVersionType version = tlsElement.getVersion();
                        Properties properties = settings.getTestObjectProperies();
                        if (version != null && properties != null && version.value().equals(properties.getProperty(server.getConfigPrefix() + DEFAULT_TLSVERSION))) {
                            TemplateTestcaseConfiguration[] testcases = getTestcasesFromTLSElement(tlsElement);
                            allTestcases.addAll(Arrays.asList(testcases));
                        }
                    }
                }
            }
        }

        // create testcases from configuration
        for (int i = 0; i < allTestcases.size(); i++) {
            allTestcases.get(i).create(testcaseName + String.format("%03d", i + 1), settings);
        }

        Logger.TestObjectBuilder.logState("Testcases from template for E_12 generated.");

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
                            resultTestcases.add(getTestcaseConfiguration(cipherSuiteName));
                        }
                    }
                }
            }
        }

        return resultTestcases.toArray(new TemplateTestcaseConfiguration[resultTestcases.size()]);
    }

    /**
     * Gets testcase configuration for given ciphersuite.
     * @param cipherSuiteName Name of cipher suite.
     * @return Testcase configuration.
     */
    private TemplateTestcaseConfiguration getTestcaseConfiguration(String cipherSuiteName) {
        TemplateTestcaseConfiguration tcConfig = null;
        
        // get key exchange algorithm from chipher suite
        int keyExchangeAlgorithm = BouncyCastleTlsHelper.getKeyExchangeAlgorithmFromCipherSuite(cipherSuiteName);
        String keyAlgorithm;
        switch (keyExchangeAlgorithm) {
            case KeyExchangeAlgorithm.DH_RSA:
            case KeyExchangeAlgorithm.DHE_RSA:
            case KeyExchangeAlgorithm.ECDHE_RSA:
            case KeyExchangeAlgorithm.RSA_PSK:
                keyAlgorithm = RSA;
                break;

            case KeyExchangeAlgorithm.DH_DSS:
            case KeyExchangeAlgorithm.DHE_DSS:
                keyAlgorithm = DSA;
                break;

            case KeyExchangeAlgorithm.ECDH_ECDSA:
            case KeyExchangeAlgorithm.ECDHE_ECDSA:
                keyAlgorithm = ECDSA;
                break;

            default:
                keyAlgorithm = null;
                break;
        }
        
        // build configuration
        if (keyAlgorithm != null) {
            tcConfig = new TemplateTestcaseConfiguration(testcaseName);
            try {
                tcConfig.addValueToConfig(server.getConfigPrefix() + DEST_CONFIG_CIPHERSUITE, cipherSuiteName);
                tcConfig.addValueToConfig(server.getConfigPrefix() + DEST_CONFIG_CERTIFICATE,
                        settings.getTestObjectProperies().getProperty(server.getConfigPrefix() + keyAlgorithm + TOSHORT_CERTIFICATE));
                tcConfig.addValueToConfig(server.getConfigPrefix() + DEST_CONFIG_PRIVATEKEY,
                        settings.getTestObjectProperies().getProperty(server.getConfigPrefix() + keyAlgorithm + TOSHORT_CERTIFICATE_KEY));
            } catch (Exception e) {
                Logger.TestObjectBuilder.logState("Unable to create testcase: " + e.getMessage(), IModuleLogger.LogLevel.Error);
                tcConfig = null;
            }
        }
        
        return tcConfig;
    }
}
