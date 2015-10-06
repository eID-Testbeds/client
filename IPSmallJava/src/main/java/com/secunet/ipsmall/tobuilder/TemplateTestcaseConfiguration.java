package com.secunet.ipsmall.tobuilder;

import com.secunet.ipsmall.GlobalSettings;
import com.secunet.ipsmall.log.IModuleLogger;
import com.secunet.ipsmall.log.Logger;
import com.secunet.ipsmall.util.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Contains configuration for a template testcase.
 */
public class TemplateTestcaseConfiguration {
    private String baseTestcaseName;

    private Map<String,String> configValues = new HashMap<String,String>();

    /**
     * Creates configuration for a template testcase.
     * @param baseTestcaseName
     */
    public TemplateTestcaseConfiguration(String baseTestcaseName) {
        this.baseTestcaseName = baseTestcaseName;
    }

    /**
     * Adds value for writing in configuration.
     * @param key Key name.
     * @param value The value.
     */
    public void addValueToConfig(String key, String value) {
        if (key != null && !key.isEmpty()) {
            String configValue = "";
            if (value != null) {
                configValue = value;
            }
            configValues.put(key, configValue);
        }
    }

    /**
     * Creates the testcase.
     * @param testcaseName Name of the testcase.
     * @param settings Test object generation settings.
     */
    public void create(String testcaseName, TestObjectSettings settings) {
        Logger.TestObjectBuilder.logState("Generating testcase " + testcaseName + " ...");

        // create testcase
        File testcaseSource = new File(new File(settings.getTestbedDir(), GlobalSettings.getTestcaseTemplatesDir()),
                settings.getModule(baseTestcaseName) + File.separator + baseTestcaseName + File.separator + GlobalSettings.getTestcasePropertiesFileName());

        File testcaseDest = new File(new File(settings.getTestObjectDir(), GlobalSettings.getTOTestsDir()),
                settings.getModule(baseTestcaseName) + File.separator + testcaseName + File.separator + GlobalSettings.getTestcasePropertiesFileName());

        try {
            FileUtils.copyFile(testcaseSource, testcaseDest, true);
        } catch (IOException e) {
            Logger.TestObjectBuilder.logState("Unable to copy Testcase " + testcaseName + ": " + e.getMessage(), IModuleLogger.LogLevel.Error);
        }

        // update config of created testcase
        for (String key : configValues.keySet()) {
            String value = configValues.get(key);
            settings.updateTestcase(testcaseName, settings.getModule(baseTestcaseName), key, value);
        }

        Logger.TestObjectBuilder.logState("Testcase " + testcaseName + " generated.");
    }
}
