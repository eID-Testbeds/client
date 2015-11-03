package com.secunet.ipsmall;

import java.io.File;

/**
 * Contains global settings.
 */
public class GlobalSettings {
	private static final String TOGEN_PROPERTIES_FILE_NAME = "testobject.properties";
	private static final String DEFAULT_PROPERTIES_FILE_NAME = "testbed_default.properties";
	private static final String SESSION_PROPERTIES_FILE_NAME = "session.properties";
	private static final String CLIENT_PROPERTIES_FILE_NAME = "client.properties";
	private static final String TESTCASE_PROPERTIES_FILE_NAME = "config.properties";
	
	private static final String LOG_SCHEMA_FILE_NAME = "logging_v1.xsd";
	private static final String LOG_STYLE_FILE_NAME = "logging_v1.xsl";
	
	private static final String REPORT_SCHEMA_FILE_NAME = "report.xsd";
	private static final String REPORT_STYLE_FILE_NAME = "report.xsl";
	
	private static final String ICS_SCHEMA_FILE_NAME = "TR-03124-2_ICS_v1.2.xsd";
	private static final String ICS_FILE_NAME = "ics.xml";
	
	private static final String CONFIG_DIR = "config";
	private static final String LOG_PROFILES_DIR = CONFIG_DIR + File.separator + "profiles";
	
	private static final String TO_GEN_DIR = "TestObjectBuilder";
	private static final String TESTCASE_TEMPLATES_DIR = TO_GEN_DIR + File.separator + "Tests_Template";
	private static final String TESTCASE_TEMPLATES_COMMON_DIR = TESTCASE_TEMPLATES_DIR + File.separator + "Common";
	
	private static final String TOS_DIR = "tests";
	
	private static final String TO_TESTS_DIR = "Tests";
	private static final String TO_TESTS_COMMON_DIR = TO_TESTS_DIR + File.separator + "Common";
	private static final String TO_COPIEDTESTS_DIR = "CopiedTests";
	private static final String TO_LOG_DIR = "Log";
	private static final String TO_REPORT_DIR = "Report";

    private static final String TO_CERTGEN = "certificategeneration";
    private static final String TO_CERTGEN_CERTS = TO_CERTGEN + File.separator + "certs";
	
	private static final String TR03124_P2_XML_DIR = "XML_TR-03124" + File.separator + "Part2";
	
	/**
	 * Gets file name for testobject generation properties.
	 * @return File name for testobject generation properties.
	 */
	public static String getTOGenPropertiesFileName() {
		return TOGEN_PROPERTIES_FILE_NAME;
	}
	
	/**
	 * Gets file name for default properties.
	 * @return File name for default properties.
	 */
	public static String getDefaultPropertiesFileName() {
		return DEFAULT_PROPERTIES_FILE_NAME;
	}
	
	/**
	 * Gets file name for session properties.
	 * @return File name for session properties.
	 */
	public static String getSessionPropertiesFileName() {
		return SESSION_PROPERTIES_FILE_NAME;
	}
	
	/**
	 * Gets file name for client properties.
	 * @return File name for client properties.
	 */
	public static String getClientPropertiesFileName() {
		return CLIENT_PROPERTIES_FILE_NAME;
	}
	
	/**
	 * Gets file name for testcase properties.
	 * @return File name for testcase properties.
	 */
	public static String getTestcasePropertiesFileName() {
		return TESTCASE_PROPERTIES_FILE_NAME;
	}
	
	/**
	 * Gets file name for log schema.
	 * @return File name for log schema.
	 */
	public static String getLogSchemaFileName() {
		return LOG_SCHEMA_FILE_NAME;
	}
	
	/**
	 * Gets file name for log style transformation.
	 * @return File name for log style transformation.
	 */
	public static String getLogStyleFileName() {
		return LOG_STYLE_FILE_NAME;
	}
	
	/**
	 * Gets file name for report schema.
	 * @return File name for report schema.
	 */
	public static String getReportSchemaFileName() {
		return REPORT_SCHEMA_FILE_NAME;
	}
	
	/**
	 * Gets file name for report style transformation.
	 * @return File name for report style transformation.
	 */
	public static String getReportStyleFileName() {
		return REPORT_STYLE_FILE_NAME;
	}
	
	/**
	 * Gets file name for ICS schema.
	 * @return File name for ICS schema.
	 */
	public static String getICSSchemaFileName() {
		return ICS_SCHEMA_FILE_NAME;
	}
	
	/**
	 * Gets file name for ICS XML file.
	 * @return File name for ICS XML file.
	 */
	public static String getICSFileName() {
		return ICS_FILE_NAME;
	}
	
	/**
	 * Gets configuration directory.
	 * @return Configuration directory.
	 */
	public static String getConfigDir() {
		return CONFIG_DIR;
	}
	
	/**
	 * Gets logging profiles directory.
	 * @return Logging profiles directory.
	 */
	public static String getLogProfilesDir() {
		return LOG_PROFILES_DIR;
	}
	
	/**
	 * Gets test object builder directory.
	 * @return Test object builder directory.
	 */
	public static String getTOGenDir() {
		return TO_GEN_DIR;
	}
	
	/**
	 * Gets testcase templates directory.
	 * @return Testcase template directory.
	 */
	public static String getTestcaseTemplatesDir() {
		return TESTCASE_TEMPLATES_DIR;
	}
	
	/**
	 * Gets common test directory in testcase template directory.
	 * @return Common directory.
	 */
	public static String getTestcaseTemplatesCommonDir() {
		return TESTCASE_TEMPLATES_COMMON_DIR;
	}
	
	/**
	 * Gets directory of test objects.
	 * @return Directory of test objects.
	 */
	public static String getTOsDir() {
		return TOS_DIR;
	}
	
	/**
	 * Gets tests directory in test object.
	 * @return Tests directory.
	 */
	public static String getTOTestsDir() {
		return TO_TESTS_DIR;
	}
	
	/**
	 * Gets common test directory in test object.
	 * @return Common directory.
	 */
	public static String getTOTestsCommonDir() {
		return TO_TESTS_COMMON_DIR;
	}
	
	/**
	 * Gets copied tests directory in test object.
	 * @return Copied tests directory.
	 */
	public static String getTOCopiedTestsDir() {
		return TO_COPIEDTESTS_DIR;
	}
	
	/**
	 * Gets log directory in test object.
	 * @return Log directory.
	 */
	public static String getTOLogDir() {
		return TO_LOG_DIR;
	}
	
	/**
	 * Gets report directory in test object.
	 * @return Report directory.
	 */
	public static String getTOReportDir() {
		return TO_REPORT_DIR;
	}

    /**
     * Gets certificate generation directory in test object.
     * @return Certificate generation directory.
     */
    public static String getTOCertificateGenerationDir() {
        return TO_CERTGEN;
    }

    /**
     * Gets certificate directory in test object.
     * @return Certificate directory.
     */
    public static String getTOCertificatesDir() {
        return TO_CERTGEN_CERTS;
    }
	
	/**
	 * TR-03124 part 2 XML directory in test object.
	 * @return TR-03124 part 2 XML directory.
	 */
	public static String getTR03124p2Dir() {
		return TR03124_P2_XML_DIR;
	}
}
