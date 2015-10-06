package com.secunet.ipsmall.tobuilder;

import com.secunet.ipsmall.GlobalSettings;
import com.secunet.ipsmall.log.Logger;
import com.secunet.ipsmall.tobuilder.ics.TLSVersionType;
import com.secunet.ipsmall.tobuilder.ics.TLSchannelType;
import com.secunet.ipsmall.tobuilder.ics.TR031242ICS;
import com.secunet.ipsmall.util.FileUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * Generates testcases from template for different TLS versions.
 */
public class TestcaseTLSVersionTemplateSetup implements ITestObjectSetup {
    private static final String PREFIX_EIDSERVER = "eidservice.";

    private static final String TLSVERSIONS = "tlsversion.invalid";
    private static final String DEST_CONFIG_TLSVERSION = "tls.version";

    private final TestObjectSettings settings;
    private final String testcaseName;
    private final RelatedServer server;
    private boolean isSetUp = false;

    /**
     * Creates setup to generate testcases from template for different TLS versions.
     *
     * @param testcaseName Name of testcase.
     * @param server Related TLS server.
     * @param settings TestObject settings.
     */
    public TestcaseTLSVersionTemplateSetup(String testcaseName, RelatedServer server, TestObjectSettings settings) {
        this.settings = settings;
        this.testcaseName = testcaseName;
        this.server = server;
    }

    @Override
    public void runSetup() throws Exception {
        Logger.TestObjectBuilder.logState("Generating testcases from template for " + testcaseName + " ...");

        // delete old template testcase
        File testcaseTemplate = new File(new File(settings.getTestObjectDir(), GlobalSettings.getTOTestsDir()),
                settings.getModule(testcaseName) + File.separator + testcaseName);
        if (testcaseTemplate.exists()) {
            FileUtils.deleteDir(testcaseTemplate);
        }

        List<String> invalidTLSVersions = new ArrayList<>();
        // load possible TLS versions
        Properties properties = settings.getTestObjectProperies();
        if (properties != null) {
            String[] configuredTLSVersions = properties.getProperty(server.getConfigPrefix() + TLSVERSIONS).split(",");
            for (String configuredTLSVersion : configuredTLSVersions) {
                invalidTLSVersions.add(configuredTLSVersion);
            }
        }

        // get TLS versions from ICS
        TR031242ICS ics = settings.getICS();
        if (ics != null && ics.getSupportedCryptography() != null) {
            switch (server) {
                case eService: {
                    // TLS for eService
                    TLSchannelType tlsEIDServer = ics.getSupportedCryptography().getTLSchannel12();
                    if (tlsEIDServer != null) {
                        List<TLSchannelType.TLSVersion> tlsElements = tlsEIDServer.getTLSVersion();
                        if (tlsElements != null) {
                            for (TLSchannelType.TLSVersion tlsElement : tlsElements) {
                                if (tlsElement != null && tlsElement.isEnabled()) {
                                    TLSVersionType tlsVersion = tlsElement.getVersion();
                                    if (tlsVersion != null) {
                                        // delete TLS version from list.
                                        invalidTLSVersions.remove(tlsVersion.value());
                                    }
                                }
                            }
                        }
                    }
                } break;

                case eIDServer: {
                    // TLS for eIDServer
                    TLSchannelType tlsEIDServer = ics.getSupportedCryptography().getTLSchannel2();
                    if (tlsEIDServer != null) {
                        List<TLSchannelType.TLSVersion> tlsElements = tlsEIDServer.getTLSVersion();
                        if (tlsElements != null) {
                            for (TLSchannelType.TLSVersion tlsElement : tlsElements) {
                                if (tlsElement != null && tlsElement.isEnabled()) {
                                    TLSVersionType tlsVersion = tlsElement.getVersion();
                                    if (tlsVersion != null) {
                                        // delete TLS version from list.
                                        invalidTLSVersions.remove(tlsVersion.value());
                                    }
                                }
                            }
                        }
                    }
                } break;
                default:
                    break;
            }
        }

        List<TemplateTestcaseConfiguration> allTestcases = new ArrayList<>();
        // generate testcase configurations for invalid TLS versions
        for (String invalidTLSVersion : invalidTLSVersions) {
            TemplateTestcaseConfiguration tcConfig = new TemplateTestcaseConfiguration(testcaseName);
            tcConfig.addValueToConfig(server.getConfigPrefix() + DEST_CONFIG_TLSVERSION, invalidTLSVersion);
            allTestcases.add(tcConfig);
        }

        // create testcases from configuration
        for (int i = 0; i < allTestcases.size(); i++) {
            allTestcases.get(i).create(testcaseName + String.format("%03d", i + 1), settings);
        }

        Logger.TestObjectBuilder.logState("Testcases from template for " + testcaseName + " generated.");

        isSetUp = true;
    }

    @Override
    public boolean IsSetUp() {
        return isSetUp;
    }
}
