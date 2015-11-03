package com.secunet.ipsmall.tobuilder;

import java.io.File;
import java.io.IOException;
import java.util.Properties;

import com.secunet.ipsmall.GlobalSettings;
import com.secunet.ipsmall.log.IModuleLogger;
import com.secunet.ipsmall.log.Logger;
import com.secunet.ipsmall.tobuilder.ics.CCHProfileType;
import com.secunet.ipsmall.tobuilder.ics.TR031242ICS;
import com.secunet.ipsmall.tobuilder.ics.TR031242ICS.Profiles;
import com.secunet.ipsmall.util.FileUtils;

/**
 * Setup to set number of sent link certificates in module C3.
 */
public class ModuleC3LinkCertificateSetup implements ITestObjectSetup {

	private static final String CVCERT = ".cvcert";

	private static final String MODULE_NAME = "Module_C3";
	
	private static final String TR_INDEX_S_ATTRIBUTE_NAME = "cch.trindex.s";
	
	private static final String LINKCERT_ATTRIBUTE_PREFIX = "ecard.testcase.linkcertmode.";
	private static final String INPUTTYPE_EAC1 = LINKCERT_ATTRIBUTE_PREFIX + "eac1";
	private static final String INPUTTYPE_EAC2 = LINKCERT_ATTRIBUTE_PREFIX + "eac2";
	
	private static final String LINKCERT_MODE_ALL = "all";
	private static final String LINKCERT_MODE_PREVERIFICATION = "preverification";
	private static final String LINKCERT_MODE_NONE = "none";

    private static final String TRUSTPOINT_INDEX_R = "r";
    private static final String TRUSTPOINT_INDEX_S = "s";
    private static final String TRUSTPOINT_INDEX_SM1 = "s-1";
	
	private static final String TEMPLATE_FILE_EAC1 = "Step_EAC1_Link_";
	private static final String TEMPLATE_FILE_EAC2 = "Step_EAC2_Link_";
	private static final String TEMPLATE_FILE_SUFFIX = ".xml";
	
	private static final String TCCONFIG_TEMPLATEKEY_EAC1 = "ecard.DIDAuthenticate1";
	private static final String TCCONFIG_TEMPLATEKEY_EAC2 = "ecard.DIDAuthenticate2";
	
	private static final String TCCONFIG_CV_CVCA_PREFIX = "eidservice.cv.cvca.";
	
	private static final String TCCONFIG_CV_DVCA = "eidservice.cv.dvca";
	private static final String TCCONFIG_CV_TERM = "eidservice.cv.terminal";
	private static final String TCCONFIG_CV_TERM_KEY = "eidservice.cv.terminal.key";
	private static final String TCCONFIG_CV_TERM_SECTOR = "eidservice.cv.terminal.sector";
	private static final String TCCONFIG_CV_TERM_DESC = "eidservice.cert.description";

    private static final String TCCONFIG_CARDSIM_TRUSTPOINT1 = "cardsimulation.trustpoint1";
    private static final String TCCONFIG_CARDSIM_TRUSTPOINT2 = "cardsimulation.trustpoint2";
    private static final String TCCONFIG_CARDSIM_TRUSTPOINT_INDEX_SUFFIX = ".index";

    private static final String CERT_CV_CVCA_PREFIX = "CERT_CV_CVCA_4_";
	private static final String CERT_CV_LINK_PREFIX = "CERT_CV_LINK_4_";
	private static final String CERT_CV_DV_PREFIX = "CERT_CV_DV_4_";
	private static final String CERT_CV_TERM_PREFIX = "CERT_CV_TERM_4_";
	
	
	/**
	 * Represents a link certificates mode.
	 *
	 */
	public enum Mode {
		All(LINKCERT_MODE_ALL),
		Preferification(LINKCERT_MODE_PREVERIFICATION),
		None(LINKCERT_MODE_NONE);
		
		String name;
		
		/**
		 * Creates a link certificates mode.
		 * @param name Name of mode.
		 */
		Mode(String name) {
			this.name = name;
		}
		
		/**
		 * Gets name of mode.
		 * @return Name of mode.
		 */
		public String getName() {
			return name;
		}
		
		/**
		 * Gets link certificates mode by name.
		 * @param name Name of mode.
		 * @return Link certificates mode by name.
		 */
		public static Mode getMode(String name) {
			for (Mode mode : Mode.values()) {
				if (mode.getName().equals(name)) {
					return mode;
				}
			}
			
			return None;
		}
	}
	
	private TestObjectSettings settings;
	private boolean isSetUp = false;
	
	/**
	 * Creates setup to set number of sent link certificates in module C3.
	 * 
	 * @param settings TestObject settings.
	 */
	public ModuleC3LinkCertificateSetup(TestObjectSettings settings) {		
		this.settings = settings;
	}

	@Override
	public void runSetup() throws Exception {

        Logger.TestObjectBuilder.logState("Set number of link certificate for module C3 ...");
		
		// get s and b
		Long s = null;
		Long b = null;
		if (this.settings != null) {
			// get s from to.properties
			Properties properties = this.settings.getTestObjectProperies();
			if (properties != null) {
				try {
					s = Long.parseLong(properties.getProperty(TR_INDEX_S_ATTRIBUTE_NAME));
				} catch (Exception ignore) { }
			}
			
			// get b from ICS
			TR031242ICS ics = this.settings.getICS();
			if (ics != null) {
				Profiles profiles = ics.getProfiles();
				if (profiles != null) {
					CCHProfileType cch = profiles.getCCH();
					if (cch != null) {
						b = cch.getTrIndexB();
					}
				}
			}
		}
		
		if (s == null) {
            Logger.TestObjectBuilder.logState("TR index 's' not found at properties.", IModuleLogger.LogLevel.Error);
		}
		
		if (b == null) {
            Logger.TestObjectBuilder.logState("TR index 'b' not found at properties.", IModuleLogger.LogLevel.Error);
		}
		
		if (s != null && b != null) {
			// iterate over all testcases of module C3
			File totests = new File(settings.getTestObjectDir(), GlobalSettings.getTOTestsDir());
	        if (totests.exists()) {
	            File[] modules = totests.listFiles();
	            for (File module : modules) {
	                if (module.isDirectory() && module.getName().equals(MODULE_NAME)) {
			            File[] testcases = module.listFiles();
			            for (File testcase : testcases) {
							Logger.TestObjectBuilder.logState("Updating testcase " + testcase.getName() + " ...");

                            // set trustpoints
                            setTrustpoint(testcase, (int)(long)s, TCCONFIG_CARDSIM_TRUSTPOINT1);
                            setTrustpoint(testcase, (int)(long)s, TCCONFIG_CARDSIM_TRUSTPOINT2);
			            	
			            	// set linkcertificates
			            	setLinkcertificates(testcase, (int)(long)s, (int)(long)b, INPUTTYPE_EAC1);
			            	setLinkcertificates(testcase, (int)(long)s, (int)(long)b, INPUTTYPE_EAC2);
			            	
			            	String testcaseName = testcase.getName();
			            	
			            	int latestCert = (int)(long)s;
			            	int numberOfLinkCerts = latestCert - 1;
			            	
			            	// set link certificates
							for (int i = numberOfLinkCerts; i > 0; i--) {
								settings.updateTestcase(testcaseName, settings.getModule(testcaseName), TCCONFIG_CV_CVCA_PREFIX + i, CERT_CV_LINK_PREFIX + i + CVCERT);
							}
			            	
			            	// set dv and terminal certificate corresponding to latest link certificate
							settings.updateTestcase(testcaseName, settings.getModule(testcaseName), TCCONFIG_CV_DVCA, CERT_CV_DV_PREFIX + latestCert + CVCERT);
							settings.updateTestcase(testcaseName, settings.getModule(testcaseName), TCCONFIG_CV_TERM, CERT_CV_TERM_PREFIX + latestCert + CVCERT);
							settings.updateTestcase(testcaseName, settings.getModule(testcaseName), TCCONFIG_CV_TERM_KEY, CERT_CV_TERM_PREFIX + latestCert + "_KEY.pkcs8");
							settings.updateTestcase(testcaseName, settings.getModule(testcaseName), TCCONFIG_CV_TERM_SECTOR, CERT_CV_TERM_PREFIX + latestCert + "_SectorKey.bin");
							settings.updateTestcase(testcaseName, settings.getModule(testcaseName), TCCONFIG_CV_TERM_DESC, CERT_CV_TERM_PREFIX + latestCert + "_DESC.bin");

                            Logger.TestObjectBuilder.logState("Testcase " + testcase.getName() + " updated.");
			            }
			        }
	            }
	        }
		}

        Logger.TestObjectBuilder.logState("Number of link certificate for module C3 set.");

		isSetUp = true;
	}

    /**
     * Sets trustpoint for given parameters.
     * @param testcase The testcase.
     * @param s s
     * @param trustpointKey Attribute key for trustpoint.
     */
    private void setTrustpoint(File testcase, int s, String trustpointKey) {
        if (testcase != null && testcase.exists() && testcase.isDirectory()) {
            String testcaseName = testcase.getName();
            if (trustpointKey != null && !trustpointKey.isEmpty()) {
                // read trustpint index from config
                File testcaseConfig = new File(testcase, GlobalSettings.getTestcasePropertiesFileName());
                String trustpointIndex = "";
                try {
                    trustpointIndex = FileUtils.readAttributeValue(testcaseConfig, trustpointKey + TCCONFIG_CARDSIM_TRUSTPOINT_INDEX_SUFFIX);
                } catch (IOException e) {
                    Logger.TestObjectBuilder.logState("Error while reading: " + testcaseConfig.getAbsolutePath() + ": " + e.getMessage(), IModuleLogger.LogLevel.Error);
                }

                // get get correct index
                int calculatedIndex = 0;
                switch (trustpointIndex) {
                    case TRUSTPOINT_INDEX_R:
                        calculatedIndex = 1;
                        break;
                    case TRUSTPOINT_INDEX_S:
                        calculatedIndex = s;
                        break;
                    case TRUSTPOINT_INDEX_SM1:
                        calculatedIndex = s - 1;
                        break;
                    default:
                        break;
                }

                // only set trustpoint if value seems valid (>0)
                if (calculatedIndex != 0) {
                    // set trustpoint
                    settings.updateTestcase(testcaseName, settings.getModule(testcaseName), trustpointKey, CERT_CV_CVCA_PREFIX + calculatedIndex + CVCERT);
                }
            }
        }
    }
	
	/**
	 * Sets link certificates for given parameters.
	 * @param testcase The testcase.
	 * @param s s
	 * @param b b
	 * @param modeKey Attribute key for mode.
	 */
	private void setLinkcertificates(File testcase, int s, int b, String modeKey) {
		if (testcase != null && testcase.exists() && testcase.isDirectory()) {
			String testcaseName = testcase.getName();
			if (modeKey != null && !modeKey.isEmpty()) {
				// read modes from config
				File testcaseConfig = new File(testcase, GlobalSettings.getTestcasePropertiesFileName());
				Mode mode = Mode.None;
				try {
					mode = Mode.getMode(FileUtils.readAttributeValue(testcaseConfig, modeKey));
				} catch (IOException e) {
                    Logger.TestObjectBuilder.logState("Error while reading: " + testcaseConfig.getAbsolutePath() + ": " + e.getMessage(), IModuleLogger.LogLevel.Warn);
				}
				
				int lastCert = 0;
				int latestCert = 0;
				switch (mode) {
				case None:
					break;
				case Preferification:
					switch (modeKey) {
					case INPUTTYPE_EAC1:
						lastCert = b;
						latestCert = s;
						break;
					case INPUTTYPE_EAC2:
						lastCert = 1;
						latestCert = b - 1;
						break;
					default:
						break;
					}
					break;
				case All:
					lastCert = 1;
					latestCert = s;
					break;
				}
				int numberOfLinkCerts = latestCert - lastCert;
				
				if (numberOfLinkCerts > 0) { // act only if more than zero link certificates are required. Otherwise default will be used.
					// use correct template
					String templateBaseFileName = null;
					String templateAttributeKey = null;
					switch (modeKey) {
					case INPUTTYPE_EAC1:
						templateBaseFileName = TEMPLATE_FILE_EAC1;
						templateAttributeKey = TCCONFIG_TEMPLATEKEY_EAC1;
						break;
					case INPUTTYPE_EAC2:
						templateBaseFileName = TEMPLATE_FILE_EAC2;
						templateAttributeKey = TCCONFIG_TEMPLATEKEY_EAC2;
						break;
					default:
						break;
					}
					
					if (templateBaseFileName != null && templateAttributeKey != null) {
						String templateFileName = templateBaseFileName + numberOfLinkCerts + TEMPLATE_FILE_SUFFIX;
						
						// copy template
						File templateSource = new File(new File(settings.getTestbedDir(), GlobalSettings.getTestcaseTemplatesCommonDir()),
								templateFileName);
			            
			            File templateDest = new File(new File(settings.getTestObjectDir(), GlobalSettings.getTOTestsDir()),
			            		settings.getModule(testcaseName) + File.separator + testcaseName + File.separator + templateFileName);
			            
			            try {
			                FileUtils.copyFile(templateSource, templateDest, true);
			            } catch (IOException e) {
                            Logger.TestObjectBuilder.logState("Unable to copy template " + templateSource.getName() + ": " + e.getMessage(), IModuleLogger.LogLevel.Error);
			            }
						
						// set template
						settings.updateTestcase(testcaseName, settings.getModule(testcaseName), templateAttributeKey, templateFileName);
					}
				}
			}
		}
	}

	@Override
	public boolean IsSetUp() {
		return isSetUp;
	}

}
