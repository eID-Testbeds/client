package com.secunet.ipsmall.tobuilder;

import com.secunet.ipsmall.GlobalSettings;
import com.secunet.ipsmall.log.IModuleLogger;
import com.secunet.ipsmall.log.Logger;
import com.secunet.ipsmall.util.FileUtils;
import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Sets testobject specific settings to default testcase.
 */
public class DefaultTestcaseSetup implements ITestObjectSetup {

    private static final String DATE_PATTERN_PARSE = "yyyy-MM-dd";
    private static final String DATE_PATTERN_DEST = "yyyyMMdd";

    private static final String REFERENCE_ATTRIBUTE_CARDSIMDATE = "cardsimulation.date";

    private final TestObjectSettings settings;
    private boolean isSetUp = false;
    private final DateFormat dateFormatParse;
    private final DateFormat dateFormatDest;

    /**
     * Creates setup to sets testobject specific settings to default testcase.
     *
     * @param settings TestObject settings.
     */
    public DefaultTestcaseSetup(TestObjectSettings settings) {
        this.settings = settings;
        this.dateFormatParse = new SimpleDateFormat(DATE_PATTERN_PARSE);
        this.dateFormatDest = new SimpleDateFormat(DATE_PATTERN_DEST);
    }

    @Override
    public void runSetup() throws Exception {
        Logger.TestObjectBuilder.logState("Updating default testcase ...");

        if (settings != null) {
            // update card simulation date
            try {
            File defaultConfig = new File(settings.getTestObjectDir(), GlobalSettings.getTOTestsCommonDir() + File.separator + GlobalSettings.getTestcasePropertiesFileName());
            Date date = dateFormatParse.parse(settings.getTestDate());
            FileUtils.updatePropertiesFile(defaultConfig, REFERENCE_ATTRIBUTE_CARDSIMDATE, dateFormatDest.format(date));
            } catch (ParseException | IOException e) {
                Logger.TestObjectBuilder.logState("Unable to set card simulation date: " + e.getMessage(), IModuleLogger.LogLevel.Error);
            }
            
            
        }

        Logger.TestObjectBuilder.logState("Default testcase updated.");

        isSetUp = true;
    }

    @Override
    public boolean IsSetUp() {
        return isSetUp;
    }
}
