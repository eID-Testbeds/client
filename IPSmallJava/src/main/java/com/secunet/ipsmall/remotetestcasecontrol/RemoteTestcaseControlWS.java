package com.secunet.ipsmall.remotetestcasecontrol;

import com.secunet.ipsmall.IPSmallManager;
import com.secunet.ipsmall.log.IModuleLogger;
import com.secunet.ipsmall.log.Logger;
import com.secunet.ipsmall.test.FileBasedTestData;
import com.secunet.ipsmall.test.ITestData;
import com.secunet.ipsmall.test.ITestProtocolCallback.TestResult;
import com.secunet.ipsmall.util.FileUtils;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;

/**
 * Web-Service for running testcases remotely.
 */
@WebService(targetNamespace = "http://secunet.com/ipsmall/rtc")
public class RemoteTestcaseControlWS {

    private String latestError = null;

    /**
     * Starts given testcase.
     *
     * @param testcaseName - Name of testcase.
     * @return <i>True</i>, if testcase was startet successfully. If testcase
     * already running, <i>false</i> will be returned.
     */
    @WebMethod()
    public boolean startTestcase(@WebParam(name = "testcaseName") String testcaseName) {
        if (testcaseName == null || testcaseName.isEmpty()) {
            logError("Testcase name not set.");
            return false;
        }

        if (IPSmallManager.getInstance().isTestCaseRunning()) {
            logError("Testcase " + IPSmallManager.getInstance().getProject().getRunningTestcaseId() + " currently running.");
            return false;
        }

        List<ITestData> testcases = IPSmallManager.getInstance().getTestcases();
        for (ITestData testcase : testcases) {
            if (testcase.getTestName().endsWith(testcaseName)) {
                try {
                    IPSmallManager.getInstance().getMainFrame().selectTestcase(testcase);
                    IPSmallManager.getInstance().getMainFrame().setUITestcaseStarted();

                    IPSmallManager.getInstance().startTestcase(testcase);
                    return true;
                } catch (Exception ex) {
                    IPSmallManager.getInstance().getMainFrame().setUITestcaseStopped();
                    logError("Error while starting testcase " + testcaseName + ": " + ex.getMessage());
                    return false;
                }
            }
        }

        logError("Testcase " + testcaseName + " not found!");
        return false;
    }
    
    /**
     * Stops current running testcase.
     *
     * @return <i>True</i>, if testcase was stopped successfully. If no testcase
     * is running, <i>false</i> will be returned.
     */
    @WebMethod()
    public boolean stopTestcase() {
        if (IPSmallManager.getInstance().isTestCaseRunning()) {
            try {
                IPSmallManager.getInstance().cancelTestcase();
                IPSmallManager.getInstance().getMainFrame().setUITestcaseStopped();
            } catch (Exception ex) {
                logError("Error while stopping testcase: " + ex.getMessage());
                return false;
            }
        } else {
            logError("No testcase running.");
            return false;
        }

        return true;
    }
    
    /**
     * Starts given testcase and waits until it was terminated.
     * 
     * @param testcaseName - Name of testcase.
     * @return Result of testcase. If an error occurred <i>null</i> will be returned.
     */
    public String runTestcase(@WebParam(name = "testcaseName") String testcaseName) {
        if (!startTestcase(testcaseName)) {
            return null;
        }

        TestResult result = IPSmallManager.getInstance().getResult().waitForResult();
        if (result == null) {
            return "";
        }
        
        return result.name();
    }

    /**
     * Gets configuration value for given testcase.
     *
     * @param testcaseName - Name of the testcase.
     * @param configurationKey - Name of the configuration parameter.
     * @return The value of configuration parameter. If value is a binary file,
     * it will be Base64 encoded.
     */
    @WebMethod()
    public String getConfigurationValue(@WebParam(name = "testcaseName") String testcaseName, @WebParam(name = "configurationKey") String configurationKey) {
        if (testcaseName == null || testcaseName.isEmpty()) {
            logError("Testcase name not set.");
            return null;
        }

        List<ITestData> testcases = IPSmallManager.getInstance().getTestcases();
        for (ITestData testcase : testcases) {
            if (testcase.getTestName().endsWith(testcaseName)) {
                return ((FileBasedTestData) testcase).getValue(configurationKey);
            }
        }

        logError("Testcase " + testcaseName + " not found!");
        return null;
    }

    /**
     * Gets log content of latest log file for given testcase.
     * 
     * @param testcaseName - Name of the testcase.
     * @return Content of latest log file.
     */
    @WebMethod()
    public String getLog(@WebParam(name = "testcaseName") String testcaseName) {
        if (testcaseName == null || testcaseName.isEmpty()) {
            logError("Testcase name not set.");
            return null;
        }

        List<ITestData> testcases = IPSmallManager.getInstance().getTestcases();
        for (ITestData testcase : testcases) {
            if (testcase.getTestName().endsWith(testcaseName)) {
                String logFilePath = testcase.getLogTestcasesFilepath();
                File logFile = new File(logFilePath);
                if (logFile != null && logFile.isFile()) {
                    try {
                        String content = FileUtils.getLogContent(logFile);
                        return content;
                    } catch (IOException ex) {
                        logError("Error while loading log file: " + ex.getMessage());
                        return null;
                    }
                } else {
                    logError("Log file not found: " + logFilePath);
                    return null;
                }
            }
        }

        logError("Testcase " + testcaseName + " not found!");
        return null;
    }

    /**
     * Gets latest error message.
     *
     * @return Latest error message if exists. Otherwise <i>null</i> will be
     * returned.
     */
    @WebMethod()
    public String getError() {
        String result = latestError;
        latestError = null;
        return result;
    }

    /**
     * Stores and logs error a message.
     *
     * @param message - The error message.
     */
    private void logError(String message) {
        latestError = message;
        Logger.Global.logState("RemoteTestcaseControl: " + message, IModuleLogger.LogLevel.Error);
    }
}
