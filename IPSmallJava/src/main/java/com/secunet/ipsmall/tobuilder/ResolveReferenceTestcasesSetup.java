package com.secunet.ipsmall.tobuilder;

import java.io.File;
import java.io.IOException;

import com.secunet.ipsmall.GlobalSettings;
import com.secunet.ipsmall.log.IModuleLogger;
import com.secunet.ipsmall.log.Logger;
import com.secunet.ipsmall.util.FileUtils;

/**
 * Resolves reference testcases.
 */
public class ResolveReferenceTestcasesSetup implements ITestObjectSetup {
	
	private static final String MODULE_PREFIX = "Module_";
	
	private static final String REFERENCE_ATTRIBUTE_NAME = "ecard.testcase.reference";
	
	private TestObjectSettings settings;
	private boolean isSetUp = false;
	
	/**
	 * Creates setup to resolve reference testcases.
	 * 
	 * @param settings TestObject settings.
	 */
	public ResolveReferenceTestcasesSetup(TestObjectSettings settings) {		
		this.settings = settings;
	}

	@Override
	public void runSetup() throws Exception {
        Logger.TestObjectBuilder.logState("Resolving reference testcases ...");
		
    	File templates = new File(settings.getTestbedDir(), GlobalSettings.getTestcaseTemplatesDir());
        if (templates.exists()) {
            File[] modules = templates.listFiles();
            for (File module : modules) {
                if (module.isDirectory() && module.getName().startsWith(MODULE_PREFIX)) {
		            File[] testcases = module.listFiles();
		            for (File testcase : testcases) {
		            	resolveTestcase(testcase);
		            }
		        }
            }
        }

        Logger.TestObjectBuilder.logState("Reference testcases resolved.");
        
        isSetUp = true;
	}
	
	@Override
	public boolean IsSetUp() {
		return isSetUp;
	}
	
	/**
	 * Resolves reference in given testcase.
	 * 
	 * @param testcase The testcase to resolve.
	 */
	private void resolveTestcase(File testcase) {
		if (testcase != null && testcase.exists() && testcase.isDirectory()) {
			File source = new File(testcase, GlobalSettings.getTestcasePropertiesFileName());
	        
	        if (source.exists()) {
	        	String referencedTestcase = "";
	        	try {
	            	referencedTestcase = FileUtils.readAttributeValue(source, REFERENCE_ATTRIBUTE_NAME);
	        	} catch (IOException e) {
	        		referencedTestcase = "";
                    Logger.TestObjectBuilder.logState("Error while reading: " + source.getAbsolutePath() + ": " + e.getMessage(), IModuleLogger.LogLevel.Error);
	        	}
	            
	            if (!referencedTestcase.isEmpty()) {
	                // get source
	                File sourceRef = new File(new File(settings.getTestbedDir(), GlobalSettings.getTestcaseTemplatesDir()),
	                		settings.getModule(referencedTestcase) + File.separator + referencedTestcase);
	                
	                if (sourceRef.exists()) {
	                    // set destination
	                    File dest = new File(new File(settings.getTestObjectDir(), GlobalSettings.getTOTestsDir()),
	                    		settings.getModule(testcase.getName()) + File.separator + testcase.getName());
	                    
	                    try {
	                    	// delete old testcase
	                    	FileUtils.deleteDir(dest);
	                    	
	                        // copy referenced testcase
	                        FileUtils.copyDir(sourceRef, dest, true, false);
	                    	
	                    	// merge/overwrite referenced testcase with source testcase
	                        FileUtils.copyDir(testcase, dest, true, true);

                            Logger.TestObjectBuilder.logState("Referenced Testcase " + testcase.getName() + " created.");
	                    } catch (IOException e) {
                            Logger.TestObjectBuilder.logState("Unable to copy properties to " + dest.getAbsolutePath() + ": " + e.getMessage(), IModuleLogger.LogLevel.Error);
	                    }
	                }
	            }
	        }
		}
	}
}
