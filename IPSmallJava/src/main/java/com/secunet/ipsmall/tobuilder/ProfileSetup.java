package com.secunet.ipsmall.tobuilder;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import com.secunet.ipsmall.log.IModuleLogger;
import com.secunet.ipsmall.log.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.secunet.ipsmall.tobuilder.ics.ProfileType;
import com.secunet.ipsmall.tobuilder.ics.TR031242ICS;
import com.secunet.testbedutils.utilities.JaxBUtil;

/**
 * Deactivates profiles.
 */
public class ProfileSetup implements ITestObjectSetup {
	
	private static final String TCCONFIG_LOAD = "ecard.testcase.load";
	
	private TestObjectSettings settings;
	private boolean isSetUp = false;
	
	/**
	 * Creates setup to setup profiles.
	 * 
	 * @param settings TestObject settings.
	 */
	public ProfileSetup(TestObjectSettings settings) {		
		this.settings = settings;
	}
	
	@Override
	public void runSetup() throws Exception {
        Logger.TestObjectBuilder.logState("Deactivating testcases for disabled profiles ...");
		
		TR031242ICS ics = settings.getICS();
		
		if (ics != null && ics.getProfiles() != null) {
			List<ProfileTestcases> profiles = new ArrayList<ProfileTestcases>();
			
			// get profiles and if enabled
			String[] profileElements = JaxBUtil.getAllowedChildElementNames(TR031242ICS.Profiles.class);
			for (String profileElement : profileElements) {
				try {
					Method getter = JaxBUtil.getGetterMethod(profileElement, TR031242ICS.Profiles.class);
					if (ProfileType.class.isAssignableFrom(getter.getReturnType())) {
						ProfileType profileType = (ProfileType)getter.invoke(ics.getProfiles(), new Object[]{});
						
						// add testcases to profile
	                    ProfileTestcases profileTestcases = new ProfileTestcases(profileElement, profileType.isEnabled(), settings.getTRTestcaseXMLDir());
	                    profiles.add(profileTestcases);
					}
				} catch (Exception e) {
                    Logger.TestObjectBuilder.logState("Unable to load element <" + profileElement + "> from ICS: " + e.getMessage(), IModuleLogger.LogLevel.Error);
				}
			}

            // deactivates profiles
            for (ProfileTestcases profile : profiles) {
            	deactivateProfile(profile);
            }
                
        }

        Logger.TestObjectBuilder.logState("Testcases for disabled profiles deactivated.");
		
		isSetUp = true;
	}
	
	@Override
	public boolean IsSetUp() {
		return isSetUp;
	}
	
	/**
	 * Deactivate all testcases in given profile.
	 * 
	 * @param profile Profile containing its testcases.
	 */
	private void deactivateProfile(ProfileTestcases profile) {
		if (profile != null && !profile.isEnabled()) {
        	if (profile.getTestcaseNames() != null) {
                Logger.TestObjectBuilder.logState("Deactivating profile " + profile.getName() + " ...");
        		
	            for (String testcaseName : profile.getTestcaseNames()) {
	                settings.updateTestcase(testcaseName, settings.getModule(testcaseName), TCCONFIG_LOAD, "false");
	            }

                Logger.TestObjectBuilder.logState("Profile " + profile.getName() + " deaktivated.");
        	}
        }
	}
	
	/**
	 * Contains all testcases for specific profile.
	 */
	private class ProfileTestcases {
		private String name;
		private boolean enabled;
		
		private List<String> testcaseNames = new ArrayList<String>();
		
		/**
		 * Creates profile.
		 * @param name Name of the profile.
		 * @param enabled Indicates if profile is activated.
		 * @param trXMLDir TR03124-2 XML files.
		 */
		public ProfileTestcases(String name, boolean enabled, File trXMLDir) {
			this.name = name;
			this.enabled = enabled;
			
			populateTastcaseNames(trXMLDir);
		}

		/**
		 * Gets name of the profile. 
		 * @return Name of the profile.
		 */
		public String getName() {
			return name;
		}

		/**
		 * Get if profile is activated.
		 * @return True if profile is activated.
		 */
		public boolean isEnabled() {
			return enabled;
		}
		
		/**
		 * Gets all testcase names covert by profile.
		 * @return List of testcase names covert by profile.
		 */
		public String[] getTestcaseNames() {			
			return testcaseNames.toArray(new String[testcaseNames.size()]);
		}
		
		/**
		 * Gets all testcases for profile from TR03124-2 XML files.
		 * @param trXMLDir TR03124-2 XML files.
		 */
		private void populateTastcaseNames(File trXMLDir) {
			if (trXMLDir.exists() && trXMLDir.isDirectory()) {
				File[] files = trXMLDir.listFiles();
		        for (File file : files)
		            if (file.isDirectory())
		            	populateTastcaseNames(file);
		            else
		            	if (file.getName().endsWith(".xml")) {
		            		String testcaseName = "";
		            		
		            		try {
								Document testcase = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(file);
								testcase.getDocumentElement().normalize();
								testcaseName = testcase.getDocumentElement().getAttribute("id");
								
								NodeList profileNodes = testcase.getDocumentElement().getElementsByTagName("Profile");
								for (int i = 0; i < profileNodes.getLength(); i++) {
									String[] profileEntries = profileNodes.item(i).getTextContent().split(";");
									for (String profile : profileEntries)
										if (profile.equals(this.name)) {
											testcaseNames.add(testcaseName);
										}
								}
							} catch (SAXException | IOException | ParserConfigurationException e) {
                                Logger.TestObjectBuilder.logState("Unable to parse testcase \"" + file.getName() + "\": " + e.getMessage(), IModuleLogger.LogLevel.Error);
							}
		            	}
			}
		}
	}
}
