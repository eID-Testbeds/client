package com.secunet.ipsmall.tobuilder;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.secunet.ipsmall.GlobalSettings;
import com.secunet.ipsmall.log.IModuleLogger;
import com.secunet.ipsmall.log.Logger;
import com.secunet.ipsmall.tobuilder.ics.TR03110Type;
import com.secunet.ipsmall.tobuilder.ics.TR031242ICS;
import com.secunet.ipsmall.tobuilder.ics.TR031242ICS.SupportedCryptography;
import com.secunet.ipsmall.util.FileUtils;

/**
 * Setup to deactivate testcases with undefined EAC algorithms in module C1.
 */
public class ModuleC1AlgorithmSetup implements ITestObjectSetup {
	
	private static final String MODULE_NAME = "Module_C1";
	
	private static final String TCCONFIG_LOAD = "ecard.testcase.load";
	private static final String TCCONFIG_EAC_ALGORITMS_ATTRIBUTE_NAME = "ecard.eac.algorithms";

	private TestObjectSettings settings;
	private boolean isSetUp = false;
	
	/**
	 * Creates setup to deactivate testcases with undefined EAC algorithms in module C1.
	 * 
	 * @param settings TestObject settings.
	 */
	public ModuleC1AlgorithmSetup(TestObjectSettings settings) {		
		this.settings = settings;
	}
	
	@Override
	public void runSetup() throws Exception {
        Logger.TestObjectBuilder.logState("Deactivate testcases with undefined EAC algorithms in module C1 ...");
			
		// get algorithms from ICS
		String[] eacAlgorithms = getSupportedEACAlgorithmsFromICS();
		
		// iterate over all module C1 testcases
		File totests = new File(settings.getTestObjectDir(), GlobalSettings.getTOTestsDir());
        if (totests.exists()) {
            File[] modules = totests.listFiles();
            for (File module : modules) {
                if (module.isDirectory() && module.getName().equals(MODULE_NAME)) {
		            File[] testcases = module.listFiles();
		            for (File testcase : testcases) {
		            	if (!checkAlgorithms(testcase, eacAlgorithms)) {
		            		settings.updateTestcase(testcase.getName(), settings.getModule(testcase.getName()), TCCONFIG_LOAD, "false");
		            	}
		            }
                }
            }
        }

        Logger.TestObjectBuilder.logState("Testcases with undefined EAC algorithms in module C1 deactivated.");
		
		isSetUp = true;
	}
	
	/**
	 * Checks if algorithms are supported in given testcase.
	 * @param testcase The testcase.
	 * @param supportedAlgorithms List of supported algorithms.
	 * @return True, if algorithms in testcase are supported.
	 */
	private boolean checkAlgorithms(File testcase, String[] supportedAlgorithms) {
		boolean result = true;
		
		// get algorithms from testcase
    	File source = new File(testcase, GlobalSettings.getTestcasePropertiesFileName());
        if (source.exists()) {
        	String algorithms;
        	try {
        		algorithms = FileUtils.readAttributeValue(source, TCCONFIG_EAC_ALGORITMS_ATTRIBUTE_NAME);
        	} catch (IOException e) {
        		algorithms = null;
                Logger.TestObjectBuilder.logState("Error while reading: " + source.getAbsolutePath() + ": " + e.getMessage(), IModuleLogger.LogLevel.Error);
        	}
        	
        	// check all given algorithms
        	if (algorithms != null && !algorithms.isEmpty()) {
        		String[] algorithmList = algorithms.split(";");
        		for (String algoritm : algorithmList) {
        			boolean algoritmSupported = false;
        			for (String supportedAlgorithm : supportedAlgorithms) {
        				if (supportedAlgorithm.equals(algoritm)) {
        					algoritmSupported = true;
        					break;
        				}
        			}
        			result &= algoritmSupported;
        			if (!result) {
        				break;
        			}
        		}
        	}
        }
		
		return result;
	}
	
	/**
	 * Gets supported EAC (PACE, TA & CA) algorithms from ICS.
	 * @return Supported EAC (PACE, TA & CA) algorithms from ICS.
	 */
	private String[] getSupportedEACAlgorithmsFromICS() {
		List<String> eacAlgorithms = new ArrayList<String>();
		if (settings != null) {
			TR031242ICS ics = settings.getICS();
			if (ics != null) {
				SupportedCryptography crypto = ics.getSupportedCryptography();
				if (crypto != null) {
					TR03110Type tr3110 = crypto.getTR03110();
					if (tr3110 != null) {
						// get PACE algorithms
						List<String> paceAlgorithms = tr3110.getPACE();
						if (paceAlgorithms != null) {
							for (String algorithm : paceAlgorithms) {
								if (algorithm != null && !algorithm.isEmpty()) {
									eacAlgorithms.add(algorithm);
								}
							}
						}
						
						// get TA algorithms
						List<String> taAlgorithms = tr3110.getTA();
						if (paceAlgorithms != null) {
							for (String algorithm : taAlgorithms) {
								if (algorithm != null && !algorithm.isEmpty()) {
									eacAlgorithms.add(algorithm);
								}
							}
						}
						
						// get CA algorithms
						List<String> caAlgorithms = tr3110.getCA();
						if (paceAlgorithms != null) {
							for (String algorithm : caAlgorithms) {
								if (algorithm != null && !algorithm.isEmpty()) {
									eacAlgorithms.add(algorithm);
								}
							}
						}
					}
				}
			}
		}
		
		return eacAlgorithms.toArray(new String[eacAlgorithms.size()]);
	}

	@Override
	public boolean IsSetUp() {
		return isSetUp;
	}

}
