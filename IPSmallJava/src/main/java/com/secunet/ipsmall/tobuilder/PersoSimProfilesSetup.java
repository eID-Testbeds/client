package com.secunet.ipsmall.tobuilder;

import com.secunet.ipsmall.GlobalSettings;
import com.secunet.ipsmall.cardsimulation.ICardPersonalization;
import com.secunet.ipsmall.cardsimulation.PersoSimPersonalization;
import com.secunet.ipsmall.log.IModuleLogger;
import com.secunet.ipsmall.log.Logger;
import com.secunet.ipsmall.util.FileUtils;
import com.secunet.testbedutils.cvc.cvcertificate.CVCertificate;
import com.secunet.testbedutils.cvc.cvcertificate.DataBuffer;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Setup to generate PersoSim profiles.
 */
public class PersoSimProfilesSetup implements ITestObjectSetup {

    private static final String MODULE_NAME = "Module_C3";

    private static final String CARDSIM_DIR = "CardSimulation" + File.separator + "PersoSim";
    private static final String TEMPLATE_TRUSTPOINT1 = "ProfileTr03124_template_1.xml";
    private static final String TEMPLATE_TRUSTPOINT2 = "ProfileTr03124_template_2.xml";

    private static final String PROFILE_SUFFIX = ".xml";

    private static final String DEFAULT_PROFILE = "Default";
    private static final String DEFAULT_CERTIFICATE_FILENAME = "CERT_CV_CVCA_1.cvcert";
    private static final String DEFAULT_PIN = "123456";

    private static final String TCCONFIG_CARDSIM_TRUSTPOINT1 = "cardsimulation.trustpoint1";
    private static final String TCCONFIG_CARDSIM_TRUSTPOINT2 = "cardsimulation.trustpoint2";

    private final DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

    private TestObjectSettings settings;
    private boolean isSetUp = false;

    private File template1;
    private File template2;
    private File commonDir;
    private File destinationDir;

    /**
     * Creates setup to generate PersoSim profiles.
     *
     * @param settings TestObject settings.
     */
    public PersoSimProfilesSetup(TestObjectSettings settings) {
        this.settings = settings;

        if (settings != null) {
            File configDir = new File(settings.getTestbedDir(), GlobalSettings.getConfigDir());
            template1 = new File(configDir, TEMPLATE_TRUSTPOINT1);
            template2 = new File(configDir, TEMPLATE_TRUSTPOINT2);

            commonDir = new File(settings.getTestObjectDir(), GlobalSettings.getTOTestsCommonDir());
            destinationDir = new File(settings.getTestObjectDir(), CARDSIM_DIR);
            if (!destinationDir.exists()) {
                destinationDir.mkdirs();
            }
        }
    }

    @Override
    public void runSetup() throws Exception {

        Logger.TestObjectBuilder.logState("Generates PersoSim profiles ...");

        if (settings != null) {

            // create default profile
            generateProfile(DEFAULT_PROFILE, DEFAULT_CERTIFICATE_FILENAME, null);

            // create profiles for C3
            File totests = new File(settings.getTestObjectDir(), GlobalSettings.getTOTestsDir());
            if (totests.exists()) {
                File[] modules = totests.listFiles();
                for (File module : modules) {
                    if (module.isDirectory() && module.getName().equals(MODULE_NAME)) {
                        File[] testcases = module.listFiles();
                        for (File testcase : testcases) {
                            File testcaseConfig = new File(testcase, GlobalSettings.getTestcasePropertiesFileName());
                            if (testcaseConfig != null && testcaseConfig.exists()) {
                                String trustpoint1 = FileUtils.readAttributeValue(testcaseConfig, TCCONFIG_CARDSIM_TRUSTPOINT1);
                                if (trustpoint1 != null && !trustpoint1.isEmpty()) {
                                    String trustpoint2 = FileUtils.readAttributeValue(testcaseConfig, TCCONFIG_CARDSIM_TRUSTPOINT2);
                                    generateProfile(testcase.getName(), trustpoint1, trustpoint2);
                                }
                            }
                        }
                    }
                }
            }

        }

        Logger.TestObjectBuilder.logState("PersoSim profiles generated.");

        isSetUp = true;
    }

    @Override
    public boolean IsSetUp() {
        return isSetUp;
    }

    /**
     * Generates profile.
     * @param profileName Name of profile.
     * @param trustpoint1 Certificate name of trustpoint1.
     * @param trustpoint2 Certificate name of trustpoint2.
     */
    private void generateProfile(String profileName, String trustpoint1, String trustpoint2) {
        try {
            List<CVCertificate> certs = new ArrayList<CVCertificate>();

            if (trustpoint1 != null && !trustpoint1.isEmpty()) { // get 1. trustpoint
                File certFile = new File(commonDir, trustpoint1);
                DataBuffer rawCert = DataBuffer.readFromFile(certFile.getAbsolutePath());
                certs.add(new CVCertificate(rawCert));
            } else {
                throw new Exception("trustprofil1 must not be null!");
            }

            File profile = new File(destinationDir, profileName + PROFILE_SUFFIX);
            ICardPersonalization persoSim;
            if (trustpoint2 != null && !trustpoint2.isEmpty()) { // get 2. trustpoint (if available) and select correct profile template.
                File certFile = new File(commonDir, trustpoint2);
                DataBuffer rawCert = DataBuffer.readFromFile(certFile.getAbsolutePath());
                certs.add(new CVCertificate(rawCert));

                persoSim = new PersoSimPersonalization(template2, profile);
            } else {
                persoSim = new PersoSimPersonalization(template1, profile);
            }

            // add trustpoints
            for (CVCertificate cert : certs) {
                persoSim.addTrustpoint(cert);
            }

            // add card date
            Date cardDate = dateFormat.parse(settings.getTestDate());
            persoSim.addCardDate(cardDate);

            // add PIN
            persoSim.addCardPIN(DEFAULT_PIN);

            // do personalization
            persoSim.personalizeCard();
            Logger.TestObjectBuilder.logState("PersoSim profile: " + profileName + " generated.");
        } catch (Exception e) {
            Logger.TestObjectBuilder.logState("Unable to create PersoSim profile: " + profileName + ": " + e.getMessage(), IModuleLogger.LogLevel.Error);
        }
    }
}
