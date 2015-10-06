package com.secunet.ipsmall;

import java.awt.EventQueue;
import java.awt.Rectangle;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.swing.JOptionPane;
import javax.swing.UIManager;

import com.secunet.ipsmall.cardsimulation.GTCardSimCtrl;
import com.secunet.ipsmall.exception.GeneralException;
import com.secunet.ipsmall.log.DualLogOutputStream;
import com.secunet.ipsmall.log.IModuleLogger.ConformityResult;
import com.secunet.ipsmall.log.IModuleLogger.EnvironmentClassification;
import com.secunet.ipsmall.log.IModuleLogger.EventType;
import com.secunet.ipsmall.log.IModuleLogger.LogLevel;
import com.secunet.ipsmall.log.Logger;
import com.secunet.ipsmall.test.FileBasedTestData;
import com.secunet.ipsmall.test.ITestData;
import com.secunet.ipsmall.test.ITestData.ExpectedTestStepKey;
import com.secunet.ipsmall.test.ITestProtocolCallback;
import com.secunet.ipsmall.test.TestProject;
import com.secunet.ipsmall.test.TestState;
import com.secunet.ipsmall.tobuilder.ics.TR031242ICS;
import com.secunet.ipsmall.ui.MainFrame;
import com.secunet.ipsmall.ui.UIUtils;
import com.secunet.ipsmall.util.CommonUtil;
import com.secunet.ipsmall.util.FileUtils;
import com.secunet.testbedutils.cvc.cvcertificate.DataBuffer;
import com.secunet.testbedutils.utilities.JaxBUtil;
import com.sun.org.apache.xerces.internal.impl.xpath.regex.ParseException;

/**
 * Main Class of the Application and Managment Interface for the UI to
 * start/stop and access testcases with the matching servers and services
 *
 * @author olischlaeger.dennis
 */
public class IPSmallManager implements ITestProtocolCallback {

    public static final String c_TESTOBJECT_DIRECTORY = "application.directory.testobject";

    public static final String c_LOG_PROFILE = "application.logging.profile";

    public static final String c_LOG_PROFILE_NAME = "logging.profile.name";
    public static final String c_LOG_LEVEL_STATE = "logging.level.STATE";
    public static final String c_LOG_LEVEL_CONFORMITY = "logging.level.CONFORMITY";
    public static final String c_LOG_LEVEL_PROTOCOL = "logging.level.PROTOCOL";
    public static final String c_LOG_LEVEL_ENVIRONMENT = "logging.level.ENVIRONMENT";

    public static final String c_CLIENT_NAME = "name";
    public static final String c_CLIENT_VENDOR = "vendor";
    public static final String c_CLIENT_VERSION = "version";
    public static final String c_CLIENT_PLATFORM = "platform";

    public static final String c_SIM = "application.cardsimulation";
    public static final String c_SIM_GT_WORKSPACE = "application.cardsimulation.globaltester.workspace";
    public static final String c_SIM_GT_HOST = "application.cardsimulation.globaltester.host";
    public static final String c_SIM_GT_PORT_SERVICE = "application.cardsimulation.globaltester.port.service";
    public static final String c_SIM_GT_PORT_RESULTS = "application.cardsimulation.globaltester.port.results";
    public static final String c_SIM_GT_PORT_APDU = "application.cardsimulation.globaltester.port.apdu";
    public static final String c_SIM_REMOTEPCSC_HOST = "application.cardsimulation.remotepcsc.host";
    public static final String c_SIM_REMOTEPCSC_PORT = "application.cardsimulation.remotepcsc.port";

    /**
     * Filename to store project state information
     */
    public static final String PROJECT_FILENAME = "project.csv";

    /**
     * the directory which contains the Common and the TestCases directories
     */
    /**
     * the directory which contains the Log and testobject folders
     */
    public static File testobjectDirectory = null;

    /**
     * The main manager instance
     */
    private static IPSmallManager instance = new IPSmallManager();

    /**
     * the UI
     */
    private MainFrame mainFrame = null;
    /**
     * the main test object, which contains the different servers and
     * connections
     */
    private IPSmallTester testServer = null;
    /**
     * the project contains the testdata and meta information and informs all
     * listeners about changes
     */
    private TestProject project = null;

    private TR031242ICS ics = null;

    /**
     * configuration of the test application
     */
    private Properties applicationProperties = null;

    /**
     * card simulation
     */
    private GTCardSimCtrl cardSimulator = null;

    /**
     * flag to indicate initialization state
     */
    private boolean initialized = false;

    /**
     * singleton
     */
    private IPSmallManager() {
    }

    /**
     * Singleton Manager
     */
    public static synchronized IPSmallManager getInstance() {
        return instance;
    }

    /**
     * Load the configuration of the application.
     *
     * @throws GeneralException on error during loading and parsing of the
     * propertiesfile
     * @throws IllegalArgumentException if path not accessible
     */
    public synchronized void initialize() throws GeneralException {

        Properties defaultProperties = new Properties();
        try (FileInputStream input = new FileInputStream(new File(GlobalSettings.getConfigDir(), GlobalSettings.getDefaultPropertiesFileName()))) {
            defaultProperties.load(input);
        } catch (Exception ex) {
            defaultProperties = new Properties();
            Logger.Global.logState("Unable to load " + GlobalSettings.getDefaultPropertiesFileName() + ": " + ex.getMessage(), LogLevel.Warn);
        }

        Properties sessionProperties = new Properties();
        try (FileInputStream input = new FileInputStream(new File(GlobalSettings.getConfigDir(), GlobalSettings.getSessionPropertiesFileName()))) {
            defaultProperties.load(input);
        } catch (Exception ex) {
            sessionProperties = new Properties();
            Logger.Global.logState("Unable to load " + GlobalSettings.getSessionPropertiesFileName() + ": " + ex.getMessage(), LogLevel.Warn);
        }

        // merge properties
        applicationProperties = new Properties();
        applicationProperties.putAll(defaultProperties);
        applicationProperties.putAll(sessionProperties);

        // read
        String testObjectDirPath = applicationProperties.getProperty(c_TESTOBJECT_DIRECTORY, "");
        testobjectDirectory = new File(testObjectDirPath);

        loadLoggingProfile(getLoggingProfileFileName());

        String simulationType = applicationProperties.getProperty(c_SIM, "none");

        String gtWorkspacePath = applicationProperties.getProperty(c_SIM_GT_WORKSPACE, "");

        String gtHost = applicationProperties.getProperty(c_SIM_GT_HOST, "gt-simulator.secunet.de");
        int gtPortService = Integer.parseInt(applicationProperties.getProperty(c_SIM_GT_PORT_SERVICE, "6789"));
        int gtPortResults = Integer.parseInt(applicationProperties.getProperty(c_SIM_GT_PORT_RESULTS, "6788"));
        int gtPortAPDU = Integer.parseInt(applicationProperties.getProperty(c_SIM_GT_PORT_APDU, "9876"));

        String remotePCSCHost = applicationProperties.getProperty(c_SIM_REMOTEPCSC_HOST, "pcscemulator.secunet.de");
        int remotePCSCPort = Integer.parseInt(applicationProperties.getProperty(c_SIM_REMOTEPCSC_PORT, "1234"));

        switch (simulationType) {
            case "GT_PersonalizeOnly":
                if (!CommonUtil.isDirectoryAccessible(gtWorkspacePath)) {
                    throw new GeneralException("Can't load directory: " + c_SIM_GT_WORKSPACE + "=" + gtWorkspacePath);
                }
                cardSimulator = new GTCardSimCtrl(gtWorkspacePath);

                break;

            case "GT_HW":
                if (!CommonUtil.isDirectoryAccessible(gtWorkspacePath)) {
                    throw new GeneralException("Can't load directory: " + c_SIM_GT_WORKSPACE + "=" + gtWorkspacePath);
                }
                cardSimulator = new GTCardSimCtrl(gtWorkspacePath, gtHost, gtPortService, gtPortResults);

                break;
            case "GT_RemotePCSC":
                if (!CommonUtil.isDirectoryAccessible(gtWorkspacePath)) {
                    throw new GeneralException("Can't load directory: " + c_SIM_GT_WORKSPACE + "=" + gtWorkspacePath);
                }
                cardSimulator = new GTCardSimCtrl(gtWorkspacePath, gtHost, gtPortService, gtPortResults, gtPortAPDU, remotePCSCHost, remotePCSCPort);

                break;
            case "GT_OnlyRemotePCSC":
                cardSimulator = new GTCardSimCtrl(gtHost, gtPortAPDU, remotePCSCHost, remotePCSCPort);

                break;
            case "none":
            default:
                cardSimulator = null;
                break;
        }

        initialized = true;
    }

    public void setLoggingProfileFileName(String filename) {
        String profileName = filename;

        applicationProperties.put(c_LOG_PROFILE, profileName);
        saveProperties();
    }

    public String getLoggingProfileFileName() {
        return applicationProperties.getProperty(c_LOG_PROFILE);
    }

    public void loadLoggingProfile(String filename) {
        Properties profileProperties = new Properties();
        try (FileInputStream input = new FileInputStream(new File(GlobalSettings.getLogProfilesDir(), filename))) {
            profileProperties.load(input);

            Map<EventType, LogLevel> levels = new HashMap<EventType, LogLevel>();
            levels.put(EventType.State, LogLevel.valueOf(profileProperties.getProperty(c_LOG_LEVEL_STATE, "Debug")));
            levels.put(EventType.Conformity, LogLevel.valueOf(profileProperties.getProperty(c_LOG_LEVEL_CONFORMITY, "Debug")));
            levels.put(EventType.Protocol, LogLevel.valueOf(profileProperties.getProperty(c_LOG_LEVEL_PROTOCOL, "Debug")));
            levels.put(EventType.Environment, LogLevel.valueOf(profileProperties.getProperty(c_LOG_LEVEL_ENVIRONMENT, "Debug")));
            Logger.setAllLogLevels(levels);

        } catch (Exception ex) {
            Logger.Global.logState("Unable to load logging profile: " + ex.getMessage(), LogLevel.Error);
        }
    }

    public String getLoggingProfileName(String filename) {
        String result = "";
        Properties profileProperties = new Properties();
        try (FileInputStream input = new FileInputStream(new File(GlobalSettings.getLogProfilesDir(), filename))) {
            profileProperties.load(input);
            result = profileProperties.getProperty(c_LOG_PROFILE_NAME, "");
        } catch (Exception ex) {
            Logger.Global.logState("Unable to load logging profile: " + ex.getMessage(), LogLevel.Error);
        }

        return result;
    }

    public Properties getProperties() {
        return applicationProperties;
    }

    public TestProject getProject() {
        return project;
    }

    public TR031242ICS getIcs() {
        return ics;
    }

    public MainFrame getMainFrame() {
        return mainFrame;
    }

    public GTCardSimCtrl getCardSimulator() {
        return cardSimulator;
    }

    /**
     * Saves properties.
     *
     * @return True if save was successful.
     */
    private boolean saveProperties() {
        boolean result = false;
        try {
    		// Only works if file already exists.
        	/*URL resourceUrl = Thread.currentThread().getContextClassLoader().getResource(GlobalSettings.getSessionPropertiesFileName());
             OutputStream output = new FileOutputStream(new File(resourceUrl.toURI()));
             applicationProperties.store(output, null);*/

            FileWriter writer = new FileWriter(new File(GlobalSettings.getConfigDir(), GlobalSettings.getSessionPropertiesFileName()));
            applicationProperties.store(writer, null);

            result = true;
        } catch (Exception e) {
            Logger.Global.logException(e);
        }

        return result;
    }

    /**
     * Currently this method does not check anything, it will just trigger
     * <code>System.exit(0)</code>.
     */
    public void shutdown() {
        applicationProperties.put("MainFrame.X", "" + getMainFrame().getBounds().x);
        applicationProperties.put("MainFrame.Y", "" + getMainFrame().getBounds().y);
        applicationProperties.put("MainFrame.H", "" + getMainFrame().getBounds().height);
        applicationProperties.put("MainFrame.W", "" + getMainFrame().getBounds().width);
        saveProperties();

        Logger.Global.logState("Shutting down!", LogLevel.Info);
        System.exit(0);
    }

    /**
     * Checks if testcase is running. Does NOT check if project is unsaved.
     *
     * @return true if no testcase is running
     */
    public synchronized boolean canShutdown() {
        return !isTestCaseRunning();
    }

    /**
     * Check if testcase is running
     *
     * @return checks if project exists and has a running testcase
     */
    public synchronized boolean isTestCaseRunning() {
        return project != null && project.isTestcaseRunning();
    }

    /**
     * Checks if project is unsaved
     *
     * @return true if project has changed after last save
     */
    public synchronized boolean isProjectUnsaved() {
        return getProject().isUnsaved();
    }

    public synchronized boolean startTestcase() throws Exception {
        return startTestcase(true);
    }

    /**
     * Checks if the queue contains a testcase which can be autostarted and
     * starts it
     *
     * @return if testcase could be started
     * @throws Exception on error
     */
    public synchronized boolean startTestcase(final boolean askUserForStart) throws Exception {
        Logger.Global.logState("Start testcase triggered, "
                + (getProject().hasAutostartInQueue() ? "and found testcase to start in queue" : "but no startable testcase in queue"), LogLevel.Debug);
        if (getProject().hasAutostartInQueue()) {
            ITestData nextTest = getProject().getTopFromQueue();
            int answer = JOptionPane.YES_OPTION;
            if (askUserForStart) {
                answer = JOptionPane.showConfirmDialog(getMainFrame(), "Ready to start testcase: " + nextTest.getTestName(), "Start next?",
                        JOptionPane.YES_NO_OPTION);
            }

            if (JOptionPane.YES_OPTION == answer) {
                startTestcase(nextTest);
                return true;
            }
        }

        return false;
    }

    /**
     * Tries to start a testcase. first it initializes the logging, than starts
     * the main testserver component and than updates the testcase state in the
     * project. on error it stops and nulls the server. <b>Note:</b> currently
     * this method does only support tests of the type
     * <code>FileBasedTestData</code>
     *
     * @param testdata must not be null
     * @throws UnsupportedOperationException if testdata not instance of
     * FileBasedTestData; or any other exception on error
     */
    public synchronized void startTestcase(ITestData testdata) throws Exception {
        if (!(testdata instanceof FileBasedTestData)) {
            throw new UnsupportedOperationException("Currently only testcases of type FileBasedTestData are supported");
        }
        FileBasedTestData fileBasedTestData = (FileBasedTestData) testdata;
        if (fileBasedTestData == null || project.getTestcase(fileBasedTestData.getTestName()) == null) {
            throw new IllegalArgumentException("Unkown testcase or null");
        }

        // starting simulator if exists
        if (cardSimulator != null) {
            cardSimulator.initCard(new DataBuffer(testdata.getSimulatedCard_CVCA()), testdata.getSimulatedCardDate(),
                    "GT Scripts ePA EAC2 Reader BSI/testsuites/Data/CFG.CERTS.TA/CFG.DFLT.EAC.IS",
                    "GT Scripts ePA EAC2 Reader BSI/testsuites/Data/CFG.CERTS.TA/CFG.DFLT.EAC.AT", "");
            cardSimulator.start();
        }

        try {
            testdata.generateNewTestcaseLogfile();
            Logger.setTestCasefile(testdata.getLogTestcasesFilepath());

            // publish some client information
            Properties clientProperties = new Properties();
            clientProperties.load(new FileReader(new File(fileBasedTestData.getRelativeTestObjectFolder(), GlobalSettings.getClientPropertiesFileName())));

            Logger.TestRunner.logEnvironment(EnvironmentClassification.Environment,
                    "TestObject name: " + clientProperties.getProperty(c_CLIENT_NAME));
            Logger.TestRunner.logEnvironment(EnvironmentClassification.Environment,
                    "TestObject vendor: " + clientProperties.getProperty(c_CLIENT_VENDOR));
            Logger.TestRunner.logEnvironment(EnvironmentClassification.Environment,
                    "TestObject version: " + clientProperties.getProperty(c_CLIENT_VERSION));
            Logger.TestRunner.logEnvironment(EnvironmentClassification.Environment,
                    "TestObject platform: " + clientProperties.getProperty(c_CLIENT_PLATFORM));

            // starting servers
            Logger.TestRunner.logState("Starting servers ...");
            project.updateState(fileBasedTestData.getTestName(), TestState.Running);
            testServer = new IPSmallTester(fileBasedTestData);

            UIUtils.showInfoDialogs(fileBasedTestData.getTestMessagesBegin());

            testServer.start();

        } catch (Exception ex) {
            stopAndDismissServer();
            Logger.TestRunner.logState("Couldn't start server:" + ex, LogLevel.Fatal);
            project.updateState(fileBasedTestData.getTestName(), TestState.Idle);
            throw ex;
        }
    }

    /**
     * will return null if testcase is running. so caller should call
     * isTestCaseRunning before calling this method. Currently the IO is done in
     * the calling thread, which is likely to be the EDT. Change to SwingWorker
     * if loading takes to long.
     *
     * @param testobjectDir the root directory containing the Log and TestsData
     * folders
     * @return null if testcase is running
     * @throws Exception on error
     */
    public synchronized boolean loadTestcases(File testobjectDir) throws Exception {
        if (!isTestCaseRunning()) {
            File tests = new File(testobjectDir, "Tests");
            File copiedTests = new File(testobjectDir, "CopiedTests");

            boolean success = true;
            if (tests.exists() && tests.isDirectory()) {

                Logger.Global.logState("Loading testcases from: " + tests.getAbsolutePath(), LogLevel.Info);
	            // TODO: if this takes to long in the calling Swing EDT, move it
                // into a swing worker

                try {
                    List<ITestData> testcases = new ArrayList<>();

                    List<String> testcasePaths = new ArrayList<String>();
                    testcasePaths.addAll(getTestcasePaths(tests));
                    if (copiedTests.exists() && copiedTests.isDirectory()) {
                        testcasePaths.addAll(getTestcasePaths(copiedTests));
                    }
                    for (String path : testcasePaths) {
                        FileBasedTestData data = loadTestcaseFromFile(path, testobjectDir);
                        if (data != null) {
                            testcases.add(data);
                        }
                    }

                    project = TestProject.loadTestProject(new File(testobjectDir, PROJECT_FILENAME), testcases);

                    // load ICS
                    File icsFile = new File(testobjectDir, GlobalSettings.getICSFileName());
                    InputStream xsd = Thread.currentThread().getContextClassLoader().getResourceAsStream(GlobalSettings.getICSSchemaFileName());
                    ics = JaxBUtil.unmarshal(icsFile, TR031242ICS.class, xsd);
                    xsd.close();
                    if (ics == null) {
                        throw new Exception("Unable to load ICS file " + icsFile.getAbsolutePath());
                    }

                    // save testobjectDir
                    applicationProperties.put(c_TESTOBJECT_DIRECTORY, testobjectDir.getAbsolutePath());
                    saveProperties();

                } catch (Exception ex) {
                    Logger.Global.logException(ex);
                    throw ex;
                }
            } else {
                Logger.Global.logState("Unable to load testcase from: " + tests.getAbsolutePath(), LogLevel.Warn);
                project = TestProject.loadTestProject(null, new ArrayList<>());
            }

            return success;
        } else {
            throw new IllegalStateException("Testcase is currently running");
        }
    }

    /**
     * Gets all modules and testcases in given directory.
     *
     * @param location The directory.
     * @return List for all testcases: 'location/module/testcase'
     */
    public synchronized List<String> getTestcasePaths(File location) {
        List<String> testcasePaths = new ArrayList<String>();

        for (File module : location.listFiles()) {
            if (module.isDirectory()) {
                for (File testcase : module.listFiles()) {
                    if (CommonUtil.isFilebasedTestcaseFolder(testcase)) {
                        testcasePaths.add(location.getName() + "/" + module.getName() + "/" + testcase.getName());
                    }
                }
            }
        }

        return testcasePaths;
    }

    /**
     * Loads testcase configuration from file.
     *
     * @param relTestPath Sublocation of testcases.
     * @param testobjectDir Directory of testobject.
     * @return Test configuration if loaded successfully.
     */
    public synchronized FileBasedTestData loadTestcaseFromFile(String relTestPath, File testobjectDir) {
        FileBasedTestData data = null;
        try {
            String newRelTestPath = relTestPath.replaceAll("\\\\", "/");
            String commonName = null;
            String testLocation = CommonUtil.getSubstringBefore(newRelTestPath, "/", false);
            String testName = CommonUtil.getSubstringAfter(newRelTestPath, "/", false);
            if (testLocation.equals(new File(GlobalSettings.getTOTestsDir()).getName())) {
                commonName = (new File(GlobalSettings.getTOTestsCommonDir())).getName();
            }
            data = new FileBasedTestData(testName, testobjectDir, testLocation, commonName);

            data.load();
            if (data.getTestLoad()) {
                data.addTestProtocolCallback(this);
                Logger.Global.logState("Loaded testcase " + testName + " from " + testLocation);
            } else {
                Logger.Global.logState("Testcase was deactivated: " + data.getTestName());
                data = null;
            }
        } catch (Exception ex) {
            if (data != null) {
                Logger.Global.logState(
                        "Could not load Testcase " + data.getTestName() + ": " + ex.getClass().getSimpleName() + "@" + ex.getMessage(),
                        LogLevel.Error);
            } else {
                Logger.Global.logState("Could not load Testcase: " + ex.getMessage(), LogLevel.Error);
            }
            data = null;
        }

        return data;
    }

    public synchronized void addTestcase(ITestData toAdd) {
        project.addTestcase(toAdd);
    }

    /**
     * Reloads a single test from the file system. Will return an unmodified
     * node and log an error if the test is not file based.
     *
     * @param toUpdate The testcase that has to be reloaded
     */
    public synchronized ITestData reloadTestcase(ITestData toUpdate) {
        if (toUpdate instanceof FileBasedTestData) {
            FileBasedTestData selTestData = (FileBasedTestData) toUpdate;
            FileBasedTestData data = null;
            try {
                data = new FileBasedTestData(selTestData.getTestModuleName() + "/" + selTestData.getTestName(), new File(
                        selTestData.getRelativeTestObjectFolder()));
                data.load();
                if (!data.getTestLoad()) {
                    Logger.Global.logState("Testcase was deactivated: " + data.getTestName());
                } else {
                    // backup state of testcase
                    TestState state = getProject().getState(data.getTestName());
                    if (state == TestState.Running) { // if state is running, set to idle
                        state = TestState.Idle;
                    }

                    for (ITestData testData : getProject().getTestcases()) {
                        if (testData.getTestName().equals(data.getTestName())) {
                            testData.removeTestProtocolCallback(this);
                            getProject().getTestcases().remove(testData);
                            break;
                        }
                    }
                    for (ITestData testData : getProject().getQueue()) {
                        if (testData.getTestName().equals(data.getTestName())) {
                            getProject().removeFromQueue(testData);
                            List<ITestData> queueItems = new ArrayList<>(getProject().getQueue().size());
                            queueItems.addAll(getProject().getQueue());
                            queueItems.add(data);
                            getProject().setQueue(queueItems);
                            break;
                        }
                    }
                    getProject().addTestcase(data);
                    data.addTestProtocolCallback(this);
                    
                    // restore state of testcase
                    getProject().updateState(data.getTestName(), state);
                }
            } catch (Exception ex) {
                Logger.Global.logState("Could not load Testcase " + data.getTestName() + ": " + ex.getClass().getSimpleName() + "@" + ex.getMessage(),
                        LogLevel.Error);
            }
            return data;
        } else {
            Logger.Global.logState("Could not load Testcase " + toUpdate.getTestName() + ": not a file based test case", LogLevel.Error);
            return toUpdate;
        }
    }

    public synchronized List<ITestData> getTestcases() {
        return getProject().getTestcases();
    }

    /**
     * Resets the meta information about the test results.
     * {@link com.secunet.ipsmall.test.TestProject#resetTestcases()}
     */
    public void resetTestcases() {
        Logger.Global.logState("Resetting testcase states", LogLevel.Debug);
        getProject().resetTestcases();
    }

    /**
     * Trys to cancel a running testcase and stops all servers.*
     *
     * @throws Exception on error
     */
    public void cancelTestcase() throws Exception {
        if (project.getRunningTestcaseId() != null) {
            Logger.TestRunner.logState("Canceling execution of testcase " + project.getRunningTestcaseId());
            stopAndDismissServer();
            getProject().updateState(project.getRunningTestcaseId(), TestState.Idle);
        } else {
            throw new IllegalArgumentException("Given testcase is not currently running");
        }
    }

    /**
     * tries to stop the test server TODO: Currently the http servers joins the
     * daemon listener threads and can block. if this happens the complete
     * thread dies.
     *
     */
    protected void stopAndDismissServer() {
        Logger.TestRunner.logState("Trying to stop servers...");
        // stopping simulator if exists
        if (cardSimulator != null) {
            cardSimulator.stop();
        }

        if (testServer != null) {
            testServer.stop(); // TODO this could block the current main thread
        }
    }

    /**
     * @return true if initialized ;)
     */
    public boolean isInitialized() {
        return initialized;
    }

    @Override
    /**
     * This method will be called from different Thread! Swing Objects must be
     * called from the EDT!
     *
     * Test protocol callbacks may evaluate passed data to decide on
     * success/fail of a test case. As this callback is just the UI, do NOT do
     * any evaluation here, but let the testRunner of the testbed decide whether
     * a testcase is successful. Instead, do only manage UI elements here, i.e.
     * set the UI-status to passed/green, failed/red and maybe enable/disable
     * buttons depending on the state of a test.
     */
    public void testProtocolCallback(final ITestEvent event, Object data, SourceComponent sourceComponent, Object source) {
        String entry = ((sourceComponent != null) ? sourceComponent.toString() + " " : "");
        entry += ((event != null) ? "\t" + event.toString() : "");
        if (event instanceof TestEvent) {
            TestEvent testEvent = (TestEvent) event;
            if (testEvent == TestEvent.TLS_HANDSHAKE_DONE) {
                if (testEvent.getInterpretation() != null) {
                    entry += ((data != null && data instanceof String) ? " @(" + ((String) data) + ")" : "");
                    entry += (" -> " + testEvent.getInterpretation());
                } else {
                    entry += ((data != null && data instanceof String) ? "\t" + ((String) data) : "");
                }
            }
        }

        int currentStep = -1;
        int lastStep = -1;

        ITestData testData = getProject().getTestcase(getProject().getRunningTestcaseId());
        if (testData != null) {
            if (event instanceof TestStep) {
                currentStep = ((TestStep) event).getStepOrder();
            }

            // get last step = expect.finalDefaultFlowStep
            String lastEvent = testData.getExpectedFinalDefaultFlowStep().get(ITestData.ExpectedTestStepKey.STEP);

            // if there are additional steps given, the last of them is the really last step
            List<HashMap<ExpectedTestStepKey, String>> addSteps = testData.getExpectedAdditionalSteps();
            if (addSteps != null && addSteps.size() > 0) {
                lastEvent = addSteps.get(addSteps.size() - 1).get(ITestData.ExpectedTestStepKey.STEP);
            }

            try {
                lastStep = TestStep.valueOf(lastEvent).getStepOrder();
            } catch (Exception e) {
            }
        }

        addProtocollEntry(entry, currentStep, lastStep);

        if (event instanceof TestError) {
            TestError error = (TestError) event;
            if (error.isCritical()) {
                final String errorMsg = "Testcase " + getProject().getRunningTestcaseId() + " encountered a critical error: " + error + " - " + data;
                Logger.TestRunner.logConformity(ConformityResult.failed, errorMsg, LogLevel.Error);
                stopAndDismissServer(); // TODO: remove this hard stopping

                getProject().removeFromQueue(getProject().getTestcase(getProject().getRunningTestcaseId()));
                getProject().updateState(getProject().getRunningTestcaseId(), TestState.Idle);

                invokeInEDThread(new Runnable() {
                    @Override
                    public void run() {
                        JOptionPane.showMessageDialog(null, errorMsg, "Error", JOptionPane.ERROR_MESSAGE);
                        IPSmallManager.getInstance().getMainFrame().switchUIOperational(true);
                        IPSmallManager.getInstance().getMainFrame().setTriggerToStart();
                    }
                });
            }

        } else if (event instanceof TestResult) {
            String runningID = getProject().getRunningTestcaseId();
            TestResult result = (TestResult) event;
            switch (result) {
                case PASSED:
                    Logger.TestRunner.logConformity(ConformityResult.passed, "Testcase " + getProject().getRunningTestcaseId()
                            + " has finished with result: Passed", LogLevel.Info);
                    getProject().updateState(getProject().getRunningTestcaseId(), TestState.Passed);
                    break;
                case FAILED:
                    Logger.TestRunner.logConformity(ConformityResult.failed, "Testcase " + getProject().getRunningTestcaseId()
                            + " has finished with result: Failed", LogLevel.Error);
                    getProject().updateState(getProject().getRunningTestcaseId(), TestState.Failed);
                    break;
                case UNDETERMINED:
                    Logger.TestRunner.logConformity(ConformityResult.undetermined, "Testcase " + getProject().getRunningTestcaseId()
                            + " has finished with result: Undetermined", LogLevel.Warn);
                    getProject().updateState(getProject().getRunningTestcaseId(), TestState.Undetermined);
                    break;
            }

            UIUtils.showInfoDialogs(testData.getTestMessagesEnd());

            // cleanup
            getProject().removeFromQueue(getProject().getTestcase(runningID));
            stopAndDismissServer();

            try {
                if (!startTestcase()) {
                    invokeInEDThread(new Runnable() {
                        @Override
                        public void run() {
                            IPSmallManager.getInstance().getMainFrame().switchUIOperational(true);
                            IPSmallManager.getInstance().getMainFrame().setTriggerToStart();
                        }
                    });
                }
            } catch (Exception exc) {
                final String message = exc.getMessage();
                Logger.TestRunner.logState("Error while trying to start new testcase:" + exc.getMessage(), LogLevel.Error);

                invokeInEDThread(new Runnable() {
                    final String m = message;

                    @Override
                    public void run() {
                        JOptionPane.showMessageDialog(null, "Error:" + m, "error", JOptionPane.ERROR_MESSAGE);
                        IPSmallManager.getInstance().getMainFrame().switchUIOperational(true);
                        IPSmallManager.getInstance().getMainFrame().setTriggerToStart();
                    }
                });
            }

            // whenever testcase-state changes:
            // save new project state to prevent lost test-results after crash
            // EIDCLIENTC-244:
            try {
                IPSmallManager.getInstance().saveProject();
            } catch (IOException ex) {
                IPSmallManager.getInstance().getMainFrame().displayErrorMessage(ex);
                return;
            }

        }
    }

    /**
     * single point for invoking thread into edt
     */
    public void invokeInEDThread(final Runnable runnable) {
        // logging?
        EventQueue.invokeLater(runnable);
    }

    /**
     * Saves the project as a csv file in the set TestData base directory
     *
     * @throws IOException on error
     */
    public void saveProject() throws IOException {
        if (testobjectDirectory.exists() && testobjectDirectory.isDirectory()) {
            Logger.Global.logState("Saving project file to: " + testobjectDirectory + " into " + PROJECT_FILENAME, LogLevel.Debug);
            getProject().saveTestProject(new File(testobjectDirectory, PROJECT_FILENAME));
        }
    }

    /**
     * Adds an entry to the UI protocol. This method can be called in any
     * thread, due to the fact, that it will create a new runnable and invokes
     * it in the Event-Dispatcher-Thread.
     *
     * @param entry must not be null
     */
    private void addProtocollEntry(final String entry, final int currentStep, final int maxSteps) {
        invokeInEDThread(new Runnable() {
            @Override
            public void run() {
                IPSmallManager.getInstance().getMainFrame().addProtocollTestStep(entry, currentStep, maxSteps);
            }
        });
    }

    /**
     * Generates a completely independent working copy of given testcase.
     *
     * @param data Testcase to copy.
     */
    public synchronized FileBasedTestData copyTestCase(ITestData data) {
        FileBasedTestData result = null;
        if (data instanceof FileBasedTestData) {
            FileBasedTestData fileData = (FileBasedTestData) data;
            Logger.Global.logState("Starting copying testcase: " + fileData.getTestName() + " ...", LogLevel.Debug);

            // generates name of copy
            File copiedTestcase = null;
            int numberOfCopy = 1;
            do {
                copiedTestcase = new File(new File(testobjectDirectory, GlobalSettings.getTOCopiedTestsDir()),
                        fileData.getTestModuleName() + File.separator + fileData.getTestName() + "_" + String.format("%02d", numberOfCopy));
                numberOfCopy++;
            } while (copiedTestcase.exists());

            // copy common to destination
            File commonConfig = new File(testobjectDirectory, GlobalSettings.getTOTestsCommonDir());
            try {
                FileUtils.copyDir(commonConfig, copiedTestcase, false);
            } catch (IOException e) {
                Logger.Global.logState("Unable to copy testcase: " + fileData.getTestName() + ": " + e.getMessage(), LogLevel.Error);
                return null;
            }

            // copy testcase and merge config.properties
            File sourceTestcase = new File(new File(testobjectDirectory, GlobalSettings.getTOTestsDir()),
                    fileData.getTestModuleName() + File.separator + fileData.getTestName());
            try {
                FileUtils.copyDir(sourceTestcase, copiedTestcase, true, true);
            } catch (IOException e) {
                Logger.Global.logState("Unable to copy testcase: " + fileData.getTestName() + ": " + e.getMessage(), LogLevel.Error);
                return null;
            }

            Logger.Global.logState("Copied testcase: " + fileData.getTestName() + ".", LogLevel.Info);

            result = loadTestcaseFromFile("CopiedTests" + File.separator
                    + fileData.getTestModuleName() + File.separator + copiedTestcase.getName(), new File(fileData.getRelativeTestObjectFolder()));
        } else {
            Logger.Global.logState("Unable to copy testcase: " + data.getTestName() + ": " + "no file based testcase.", LogLevel.Error);
        }

        return result;
    }

    public synchronized void editTestCaseInExternalEditor(ITestData data) {
        if (data instanceof FileBasedTestData) {
            FileBasedTestData fileData = (FileBasedTestData) data;
            Logger.Global.logState("Starting editing testcase: " + fileData.getTestName() + " ...", LogLevel.Debug);

            File file = new File(testobjectDirectory, fileData.getTestLocation() + System.getProperty("file.separator") + fileData.getTestModuleName() + System.getProperty("file.separator") + fileData.getTestName() + System.getProperty("file.separator") + "config.properties");
            if (!file.exists()) {
                Logger.Global.logState("Error editing testcase: " + file.getAbsolutePath() + " does not exist.", LogLevel.Error);
                return;
            }

            try {
                Runtime.getRuntime().exec("notepad.exe " + file.getAbsolutePath());
            } catch (IOException e) {
                Logger.Global.logState("Error editing testcase: " + file.getAbsolutePath() + " does not exist.", LogLevel.Error);
                Logger.Global.logException(e);
                return;
            }

        }
    }

    /**
     * Launch the application.
     *
     * @param args currently all args are being ignored
     */
    public static void main(String[] args) {
        System.out.println("Starting eCard Conformity Tester...");

        if (args != null) {
            if (Arrays.asList(args).contains("-h")) {
                System.out.println("Help:");
                System.out.println("arguments:");
                System.out.println("-h \t\tPrints this help.");
                System.out.println("");
                System.out.println("java -jar <name-of-jar>.jar");
                System.out.println("");
                System.out.println("Additional hint: Do not forget to add the needed hostnames in your hosts file!");
            }
        } else {
            System.out.println("Using default application properties");
        }

        try {
            Logger.setGlobalLogfile("debug.log");
            Logger.Global.logState("STARTING IPSmallManager...\n");

            Logger.Global.logState("initializing...");
            getInstance().initialize();

            Logger.Global.logState("loading testcases...");
            final boolean success = getInstance().loadTestcases(testobjectDirectory);

            // update system out and error stream #ECARDCONF-236
            // log information on ssl handshakes for Java JDK (Java7SocketFactory)
            System.setProperty("javax.net.debug", "ssl");
            System.out.println("Changing System.out to log out based on 'CR LF': javax.net.debug=ssl");
            System.setOut(new PrintStream(new DualLogOutputStream(System.out)));
            // System.setErr(new PrintStream(new DualLogOutputStream(System.err)));

            UIManager.LookAndFeelInfo plaf[] = UIManager.getInstalledLookAndFeels();
            for (int i = 0, n = plaf.length; i < n; i++) {
                // System.out.println("Name: " + plaf[i].getName() +
                // "  Class name: " + plaf[i].getClassName());
                if ("Windows".equals(plaf[i].getName())) {
                    try {
                        UIManager.setLookAndFeel(plaf[i].getClassName());
                    } catch (Exception ignore) {
                    }
                    break;
                }
            }

            EventQueue.invokeLater(new Runnable() {
                @Override
                public void run() {
                    getInstance().mainFrame = new MainFrame();
                    if (getInstance().applicationProperties != null && getInstance().applicationProperties.containsKey("MainFrame.X")) {
                        Properties sessProp = getInstance().applicationProperties;
                        try {
                            Rectangle r = new Rectangle(Integer.parseInt(sessProp.getProperty("MainFrame.X", "0")), Integer.parseInt(sessProp.getProperty(
                                    "MainFrame.Y", "0")), Integer.parseInt(sessProp.getProperty("MainFrame.W", ""
                                                    + getInstance().mainFrame.getPreferredSize().width)), Integer.parseInt(sessProp.getProperty("MainFrame.H", ""
                                                    + getInstance().mainFrame.getPreferredSize().height)));
                            getInstance().mainFrame.setBounds(r);
                        } catch (ParseException ex) {
                            Logger.Global.logException(ex);
                        }
                    }

                    getInstance().mainFrame.setVisible(true);
                    getInstance().mainFrame.setProject(getInstance().getProject());
                    if (!success) {
                        getInstance().mainFrame.setLogContent("Could not load all testcases see log.");
                        // JOptionPane.showMessageDialog(getInstance().mainFrame, "Could not load all testcases see log.",
                        // "Loading Error", JOptionPane.OK_OPTION);
                    }
                }
            });
        } catch (Exception exc) {
            Logger.Global.logException(exc);
        }
    }

}
