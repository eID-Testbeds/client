package com.secunet.ipsmall.tobuilder;

import java.io.*;
import java.util.ArrayList;
import java.util.Properties;

import javax.swing.JOptionPane;

import com.secunet.ipsmall.GlobalSettings;
import com.secunet.ipsmall.log.Logger;
import com.secunet.ipsmall.tobuilder.ics.TR031242ICS;
import com.secunet.ipsmall.util.FileUtils;
import com.secunet.testbedutils.utilities.JaxBUtil;

/**
 * Builds a test object.
 */
public class BuildTestObject {
		
    private static final String TO_PROP_CLIENT_NAME = "client.name";
    private static final String TO_PROP_CLIENT_VENDOR = "client.vendor";
    private static final String TO_PROP_CLIENT_VERSION = "client.version";
    private static final String TO_PROP_CLIENT_PLATFORM = "client.platform";
	    
    private static final String CLIENT_NAME_ELEMENT = "name";
    private static final String CLIENT_VENDOR_ELEMENT = "vendor";
    private static final String CLIENT_VERSION_ELEMENT = "version";
    private static final String CLIENT_PLATFORM_ELEMENT = "platform";

    private static final String PROP_TO_DIR = "application.directory.testobject";

    private TestObjectSettings settings;
    
    File icsFile;
    private File toGenDir;
    boolean setDefault;

    /**
     * Prepares building of a new test object.
     * 
     * @param icsFilePath Path to an ICS configuration XML file.
     * @param templateDirName Name of template directory for test object specific settings (should be in same directory as ICS file).
     * @param testbedPath Path to root directory of testbed.
     * @param trTestcaseXMLPath Path to directory of TR-03124-2 testcase XML files.
     * @param setDefault If true, set generated TestObject as default in Testbed.
     * @throws Exception Exception if preparation of building of a test objects fails.
     */
    public BuildTestObject(String icsFilePath, String templateDirName, String testbedPath, String trTestcaseXMLPath, boolean setDefault) throws Exception {
        // prepare directories
    	icsFile = new File(icsFilePath);
        if (!icsFile.exists()) {
            throw new Exception("Unable to created TestObject: ICS file not found at: " + icsFile.getAbsolutePath());
        }

        toGenDir = icsFile.getParentFile();
        if (toGenDir == null) {
        	toGenDir = new File(System.getProperty("user.dir"));
        }
        
        File testbedDir;
        if (testbedPath.isEmpty()) {
        	testbedDir = new File(System.getProperty("user.dir"));
        } else {
        	testbedDir = new File(testbedPath);
        }
        if (!testbedDir.exists()) {
            throw new Exception("Unable to created TestObject: Testbed not found at: " + testbedDir.getAbsolutePath());
        }
        
        File trTestcaseXMLDir = new File(trTestcaseXMLPath);
        if (!trTestcaseXMLDir.exists())
            trTestcaseXMLDir = new File(testbedDir, GlobalSettings.getTR03124p2Dir());

        this.setDefault = setDefault;
        
        // import ICS
        InputStream xsd = Thread.currentThread().getContextClassLoader().getResourceAsStream(GlobalSettings.getICSSchemaFileName());
        TR031242ICS ics = JaxBUtil.unmarshal(icsFile, TR031242ICS.class, xsd);
        if (ics == null) {
        	throw new Exception("Unable to import \"" + icsFile.getAbsolutePath());
        }
        
        // import properties
        File toGenFile = new File(toGenDir, GlobalSettings.getTOGenPropertiesFileName());
        if (toGenFile == null || !toGenFile.exists()) {
        	throw new Exception("TestObject generation properties not found.");
        }
        Properties toGenProperties = new Properties();
        toGenProperties.load(new FileReader(toGenFile));
        
        // create settings
        settings = new TestObjectSettings(testbedDir, ics, toGenProperties);
        settings.setTRTestcaseXMLDir(trTestcaseXMLDir);
        settings.setTemplateDir(new File(toGenDir, templateDirName));
    }

    /**
     * Sets directory if given CVCA shall be used for certificate generation.
     * @param cvcaDir CVCA directory (should be in same directory as ICS file).
     */
    public void useGivenCVCA(File cvcaDir)
    {
        if (cvcaDir != null && cvcaDir.exists()) {
            settings.setCVCADir(cvcaDir);
        }
    }

    /**
     * Sets directory if given CVCA shall be used for certificate generation.
     * @param cvcaName Name of CVCA directory (should be in same directory as ICS file).
     */
    public void useGivenCVCA(String cvcaName)
    {
        useGivenCVCA(new File(toGenDir, cvcaName));
    }

    /**
     * Sets directory if given SSL CA shall be used for certificate generation.
     * @param sslcaDir SSL CA directory (should be in same directory as ICS file).
     */
    public void useGivenSSLCA(File sslcaDir)
    {
        if (sslcaDir != null && sslcaDir.exists()) {
            settings.setSSLCADir(sslcaDir);
        }
    }

    /**
     * Sets directory if given SSL CA shall be used for certificate generation.
     * @param sslcaName Name of SSL CA directory (should be in same directory as ICS file).
     */
    public void useGivenSSLCA(String sslcaName)
    {
        useGivenSSLCA(new File(toGenDir, sslcaName));
    }

    /**
     * Creates test object.
     *
     * @throws Exception
     */
    public void create() throws Exception {

        Logger.TestObjectBuilder.logState("Start generating TestObject at " + settings.getTestObjectDir().getAbsolutePath() + " ...");
    	
    	// set up base structures (must be done first and in this sequence)
    	setupTestObjectDir(); // 1.
    	setupTestcaseFiles(); // 2.
        
        // initialize test object setups
        ArrayList<ITestObjectSetup> toSetups = new ArrayList<>();
        toSetups.add(new DefaultTestcaseSetup(settings)); // update default testcase.
        toSetups.add(new CertificateSetup(settings)); // generate certificates.

        toSetups.add(new ModuleC3LinkCertificateSetup(settings)); // set link certificates for module C3.
        toSetups.add(new TestcaseTLSVersionTemplateSetup("EID_CLIENT_E_01_T", RelatedServer.eService, settings)); // generate testcases from template for E_01.
        toSetups.add(new TestcaseShortKeyTemplateSetup("EID_CLIENT_E_04_T", RelatedServer.eService, settings)); // generate testcases from template for E_04.
        toSetups.add(new TestcaseE06TemplateSetup(settings)); // generate testcases from template for E_06.
        toSetups.add(new TestcaseTLSVersionTemplateSetup("EID_CLIENT_E_07_T", RelatedServer.eIDServer, settings)); // generate testcases from template for E_07.
        toSetups.add(new TestcaseE08TemplateSetup(settings)); // generate testcases from template for E_08.
        toSetups.add(new TestcaseShortKeyTemplateSetup("EID_CLIENT_E_12_T_OPT_A", RelatedServer.eIDServer, settings)); // generate testcases from template for E_12.
        toSetups.add(new TestcaseShortKeyTemplateSetup("EID_CLIENT_E_12_T_OPT_B", RelatedServer.eIDServer, settings)); // generate testcases from template for E_12.
        
        toSetups.add(new ResolveReferenceTestcasesSetup(settings)); // resolve reference testcases.
        toSetups.add(new ProfileSetup(settings)); // deactivate profiles.
        toSetups.add(new ModuleC1AlgorithmSetup(settings)); // deactivated undefined EAC algorithms in module C1.

        toSetups.add(new PersoSimProfilesSetup(settings)); // creates persosim profiles.
        
        // run setups
        for(ITestObjectSetup setup : toSetups) {
        	setup.runSetup();
        }

        Logger.TestObjectBuilder.logState("TestObject generated.");
    }
    
    /**
     * Generates test object directory.
     *
     * @throws Exception
     */
    private void setupTestObjectDir() throws Exception {
//        String to_name = settings.getName();
//        String to_testDate = settings.getTestDate();
        
        String to_client_name = settings.getTestObjectProperies().getProperty(TO_PROP_CLIENT_NAME);
        String to_client_vendor = settings.getTestObjectProperies().getProperty(TO_PROP_CLIENT_VENDOR);
        String to_client_version = settings.getTestObjectProperies().getProperty(TO_PROP_CLIENT_VERSION);
        String to_client_platform = settings.getTestObjectProperies().getProperty(TO_PROP_CLIENT_PLATFORM);
        
        File testObjectDir = settings.getTestObjectDir();
        File testObjectTestsDir = new File(testObjectDir, GlobalSettings.getTOTestsDir());
        
        // check, if testObjectDir exists and delete after confirmation
        if (testObjectDir.exists()) {
            int result = JOptionPane.showConfirmDialog(null, "Test object folder already exists and will be overwritten.", "Warning", JOptionPane.OK_CANCEL_OPTION);
            if(JOptionPane.CANCEL_OPTION == result) {
                throw new Exception("Cancellation by user.");
            }
            Logger.TestObjectBuilder.logState("Deleting already existing TestObject: " + testObjectDir.getAbsolutePath());
            FileUtils.deleteDir(testObjectDir);
        }

        if (!testObjectDir.exists()) {
            if (testObjectDir.mkdirs()) {
                // create test object directory
                testObjectTestsDir.mkdirs();

                // copy ICS xml
                FileUtils.copyFile(icsFile, new File(testObjectDir, GlobalSettings.getICSFileName()), true);

                // create client.properties for some global settings
                Properties clientProperties = new Properties();
                clientProperties.setProperty(CLIENT_NAME_ELEMENT, to_client_name);
                clientProperties.setProperty(CLIENT_VENDOR_ELEMENT, to_client_vendor);
                clientProperties.setProperty(CLIENT_VERSION_ELEMENT, to_client_version);
                clientProperties.setProperty(CLIENT_PLATFORM_ELEMENT, to_client_platform);

                FileWriter writer = new FileWriter(new File(testObjectDir, GlobalSettings.getClientPropertiesFileName()));
                clientProperties.store(writer, null);
            } else {
                throw new Exception("Unable to create TestObject: " + testObjectDir.getAbsolutePath());
            }

            // set testobject as default
            if (setDefault) {
                File configDir = new File(settings.getTestbedDir(), GlobalSettings.getConfigDir());
                File sessionProperties = new File(configDir, GlobalSettings.getSessionPropertiesFileName());
                // don't check if file exists. If not file will be created.
                FileUtils.updatePropertiesFile(sessionProperties, PROP_TO_DIR, GlobalSettings.getTOsDir() + File.separator + testObjectDir.getName());
                Logger.TestObjectBuilder.logState("TestObject set as default.");
            }
        }
    }
    
    /**
     * Sets testcase files up. Copies base templates and merges test object specific templates.
     * 
     * @throws FileNotFoundException
     * @throws IOException
     */
    private void setupTestcaseFiles() throws FileNotFoundException, IOException {
    	File testObjectTestsDir = new File(settings.getTestObjectDir(), GlobalSettings.getTOTestsDir());
    	
        if (testObjectTestsDir != null && testObjectTestsDir.exists()) {
            // first copy default templates to new test object
            File defaultDir = new File(settings.getTestbedDir(), GlobalSettings.getTestcaseTemplatesDir());
            if (defaultDir.exists() && defaultDir.isDirectory()) {
                FileUtils.copyDir(defaultDir, testObjectTestsDir, true);
                Logger.TestObjectBuilder.logState("Default configuration imported.");
            }
            
            // integrate TestObject specific configuration
            File templateDir = settings.getTemplateDir();
            if (templateDir != null && templateDir.exists() && templateDir.isDirectory()) {
                FileUtils.copyDir(templateDir, testObjectTestsDir, true, true);
                Logger.TestObjectBuilder.logState("Template integrated.");
            }
        }
    }
}
