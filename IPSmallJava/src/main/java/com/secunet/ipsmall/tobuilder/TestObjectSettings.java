package com.secunet.ipsmall.tobuilder;

import java.io.File;
import java.io.IOException;
import java.util.Properties;

import com.secunet.ipsmall.GlobalSettings;
import com.secunet.ipsmall.log.IModuleLogger;
import com.secunet.ipsmall.log.Logger;
import com.secunet.ipsmall.tobuilder.ics.TR031242ICS;
import com.secunet.ipsmall.util.FileUtils;

/**
 * Little toolbox for textcase related stuff.
 */
public class TestObjectSettings {
	
	private static final String TO_PROP_NAME = "testobject.name";
	private static final String TO_PROP_TESTDATE = "testobject.testdate";
	
	private File testbedDir;
	private File testObjectDir;
	private TR031242ICS ics;
	private Properties toGenProperties;

    private File cvcaDir;
    private File sslcaDir;
    private File templateDir;
	private File trTestcaseXMLDir;
	
	/**
	 * Initializes testcase toolbox.
	 * 
	 * @param testbedDir Testbed directory.
	 * @param ics ISC.
	 * @param toGenProperties Properties for testobject generation.
	 * @throws Exception 
	 */
	public TestObjectSettings(File testbedDir, TR031242ICS ics, Properties toGenProperties) throws Exception {
		this.testbedDir = testbedDir;
		this.ics = ics;
		this.toGenProperties = toGenProperties;
		
		String toName = toGenProperties.getProperty(TO_PROP_NAME);
        if (toName == null || toName.isEmpty()) {
        	throw new Exception("TestObject name not found.");
        }
        
        testObjectDir = new File(new File(testbedDir, GlobalSettings.getTOsDir()), toName);
	}
	
	/**
	 * Gets testbed root directory.
	 * @return Testbed root directory.
	 */
	public File getTestbedDir() {
		return testbedDir;
	}
	
	/**
	 * Gets test object directory.
	 * @return Test object directory.
	 */
	public File getTestObjectDir() {
		return testObjectDir;
	}
	
	/**
	 * Gets ICS.
	 * @return ICS.
	 */
	public TR031242ICS getICS() {
		return ics;
	}
	
	/**
	 * Gets properties for testobject generation.
	 * @return Properties for testobject generation.
	 */
	public Properties getTestObjectProperies() {
		return toGenProperties;
	}
	
	/**
	 * Gets CVCA directory.
	 * @return CVCA directory.
	 */
	public File getCVCADir() {
		return cvcaDir;
	}

    /**
     * Sets CVCA directory.
     * @param value CVCA directory.
     */
    public void setCVCADir(File value) {
        cvcaDir = value;
    }

    /**
     * Gets SSL CA directory.
     * @return SSL CA directory.
     */
    public File getSSLCADir() {
        return sslcaDir;
    }

    /**
     * Sets SSL CA directory.
     * @param value SSL CA directory.
     */
    public void setSSLCADir(File value) {
        sslcaDir = value;
    }

    /**
     * Gets template directory for test object specific settings.
     * @return Template directory for test object specific settings.
     */
    public File getTemplateDir() {
        return templateDir;
    }

    /**
     * Sets template directory for test object specific settings.
     * @param value Template directory for test object specific settings.
     */
    public void setTemplateDir(File value) {
        templateDir = value;
    }

    /**
     * Gets TR-03124-2 XML file directory.
     * @return TR-03124-2 XML file directory.
     */
    public File getTRTestcaseXMLDir() {
        return trTestcaseXMLDir;
    }

    /**
     * Sets TR-03124-2 XML file directory.
     * @param value TR-03124-2 XML file directory.
     */
    public void setTRTestcaseXMLDir(File value) {
        trTestcaseXMLDir = value;
    }
	
	/**
	 * Gets module name for given testcase.
	 * @param testcaseName name of testcase.
	 * @return Module name for testcase.
	 */
	public String getModule(String testcaseName) {
        File templates = new File(testbedDir, GlobalSettings.getTestcaseTemplatesDir());
        if (templates.exists()) {
            File[] modules = templates.listFiles();
            for (File module : modules) {
                if (module.isDirectory() && module.getName().startsWith("Module_")) {
                    File[] testcases = module.listFiles();
                    for (File testcase : testcases) {
                        if (testcase.isDirectory() && testcase.getName().equals(testcaseName)) {
                            return module.getName();
                        }
                    }
                }
            }
        } else {
            Logger.TestObjectBuilder.logState("Unable to find DEFAULT path: " + templates.getAbsolutePath(), IModuleLogger.LogLevel.Error);
        }
        
        return "";
    }
    
	/**
	 * Gets name of testobject.
	 * @return Name of testobject.
	 */
    public String getName() {
    	if (toGenProperties != null) {
    		return toGenProperties.getProperty(TO_PROP_NAME);
    	}
    	return "";
    }
    
    /**
     * Gets test date.
     * @return Test date.
     */
    public String getTestDate() {
    	if (toGenProperties != null) {
    		return toGenProperties.getProperty(TO_PROP_TESTDATE);
    	}
    	return "";
    }
	
	/**
	 * Updates specific value in testcase.
	 * 
	 * @param testcaseName Testcase name.
	 * @param moduleName Module name of the testcase to update.
	 * @param attribute Name of the attribute.
	 * @param value The new value.
	 */
	public void updateTestcase(String testcaseName, String moduleName, String attribute, String value) {
        File testCaseConfigFile = null;
        
        if (moduleName.isEmpty())
            return;
        
        testCaseConfigFile = new File(new File(testObjectDir, GlobalSettings.getTOTestsDir()), moduleName + System.getProperty("file.separator") + testcaseName + System.getProperty("file.separator")
                + "config.properties");
        
        try {
            FileUtils.updatePropertiesFile(testCaseConfigFile, attribute, value);
            Logger.TestObjectBuilder.logState("Testcase " + testcaseName + " updated: " + attribute + "=" + value);
        } catch (IOException e) {
            Logger.TestObjectBuilder.logState("Unable to update testcase configuration file: " + e.getMessage(), IModuleLogger.LogLevel.Error);
        }
    }
}
