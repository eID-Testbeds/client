package com.secunet.ipsmall.report;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.w3c.dom.ProcessingInstruction;
import org.xml.sax.SAXException;

import com.secunet.ipsmall.GlobalInfo;
import com.secunet.ipsmall.GlobalSettings;
import com.secunet.ipsmall.log.Logger;
import com.secunet.ipsmall.log.IModuleLogger.LogLevel;
import com.secunet.ipsmall.util.FileUtils;

/**
 * Generates a report from all logfiles.
 */
public class ReportGenerator {
    
    private enum TestCaseResult {
        NotPerformed,
        NotApplicable,
        Passed,
        Failed;
    }
 
    private static final String c_ActivatedAttribute = "ecard.testcase.load";
    
    private File testObjectDir;
    
    
    /**
     * Initializes the generator.
     * @param testObjectDir Directory of TestObject.
     * @throws FileNotFoundException if TestObject directory is not valid.
     */
    public ReportGenerator(final File testObjectDir) throws FileNotFoundException {
        if (testObjectDir.exists() && testObjectDir.isDirectory()) {
            this.testObjectDir = testObjectDir;
        }
        else
            throw new FileNotFoundException(testObjectDir.getAbsolutePath() + " is no vaild directory.");
    }
    
    /**
     * Generates the report.
     * @param filename Name of report.
     */
    public File generateReport(final String filename) {
        File reportFile = new File(new File(testObjectDir, GlobalSettings.getTOReportDir()), filename);
        
        reportFile.getParentFile().mkdirs();
        
        File logDir = new File(testObjectDir, GlobalSettings.getTOLogDir());
        
        // get logfiles
        List<File> logFiles = new ArrayList<File>();
        if (logDir.exists() && logDir.isDirectory()) {
            for (File logFile : logDir.listFiles())
                if (logFile.isFile())
                    logFiles.add(logFile);
        }
        
        // get testcases
        final List<TestCase> testcases = getTestcases();
        
        // initialize report document
        Document reportDoc = null;
        try {
            DocumentBuilder docBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            reportDoc = docBuilder.newDocument();
        } catch (ParserConfigurationException e) {
            Logger.Global.logState("Unable to initialize report: " + e.getMessage(), LogLevel.Error);
            return null;
        }
        
        if (reportDoc == null)
            return null;
        
        // create root element <report />        
        Element rootElement = reportDoc.createElement("report");
        reportDoc.appendChild(rootElement);
        ProcessingInstruction pi = reportDoc.createProcessingInstruction("xml-stylesheet", "type=\"text/xsl\" href=\"" + GlobalSettings.getReportStyleFileName() + "\"");
        reportDoc.insertBefore(pi, rootElement);
        rootElement.setAttributeNS("http://www.w3.org/2001/XMLSchema-instance", "xsi:schemaLocation", "http://www.secunet.com " + GlobalSettings.getReportSchemaFileName());
        rootElement.setAttribute("software", GlobalInfo.Title.getValue());
        rootElement.setAttribute("sw_version", GlobalInfo.SoftwareVersion.getValue());
        
        // analyze logfiles
        for (TestCase testcase : testcases) {
            // get events from logfiles for each activated testcase
            List<ConformityEvent> conformityEvents = new ArrayList<ConformityEvent>();
            if (testcase.isActivated())
                for (File logFile : logFiles) {
                    if (logFile.getName().startsWith(testcase.getModule() + " " + testcase.getName()))
                        conformityEvents.addAll(getConformityEventsFromLogFile(logFile, testcase));
            }
            
            // analyze results
            TestCaseResult result = analyseTestcase(testcase, conformityEvents);
            
            // create testcase element <testcase />
            Element testcaseElement = reportDoc.createElement("testcase");
            rootElement.appendChild(testcaseElement);
            testcaseElement.setAttribute("module", testcase.getModule());
            testcaseElement.setAttribute("name", testcase.getName());
            testcaseElement.setAttribute("result", result.name());
            
            // create event elements <event />
            for (ConformityEvent event : conformityEvents) {
                Element eventElement = reportDoc.createElement("event");
                testcaseElement.appendChild(eventElement);
                eventElement.setAttribute("result", event.getResult());
                eventElement.setAttribute("mode", event.getMode());
                eventElement.setAttribute("timestamp", event.getTimestamp());
                eventElement.setAttribute("module", event.getModule());
                eventElement.setAttribute("logfile", event.getLogfile().getAbsolutePath());
                eventElement.appendChild(reportDoc.createCDATASection(event.getMessage()));
            }
        }

        // write report
        try {
            Transformer transformer = TransformerFactory.newInstance().newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            DOMSource domSource = new DOMSource(reportDoc);
            StreamResult streamResult = new StreamResult(reportFile);        
            transformer.transform(domSource, streamResult);
            
            copyConfigFileToReportDir(GlobalSettings.getReportSchemaFileName());
            copyConfigFileToReportDir(GlobalSettings.getReportStyleFileName());
        } catch (TransformerFactoryConfigurationError | TransformerException e) {
            Logger.Global.logState("Unable to write report: " + e.getMessage(), LogLevel.Error);
            return null;
        }
        
        return reportFile;
    }
    
    /**
     * Copies file from config to report directory.
     * @param fileName File to copy.
     */
    private void copyConfigFileToReportDir(String fileName) {
    	File source = new File(GlobalSettings.getConfigDir(), fileName);
    	if (source.exists()) {
    		File dest = new File(new File(testObjectDir, GlobalSettings.getTOReportDir()), fileName);
    		// copy only if not exists
    		if (!dest.exists()) {
    			try {
					FileUtils.copyFile(source, dest, true);
				} catch (IOException e) {
					Logger.Global.logState(e.getMessage(), LogLevel.Error);
				}
    		}
    	} else {
    		Logger.Global.logState(fileName + " not found.", LogLevel.Error);
    	}
    }
    
    /**
     * Searches all testcases and returns them.
     * 
     * @return List of testcases.
     */
    private List<TestCase> getTestcases() {
        List<TestCase> result = new ArrayList<TestCase>();
        
        File tests = new File(testObjectDir, "Tests");
        
        if (tests.exists() && tests.isDirectory())
            for (File moduleDir : tests.listFiles())
                if (moduleDir.isDirectory() && moduleDir.getName().startsWith("Module_"))
                    for (File testcaseDir : moduleDir.listFiles())
                        if (testcaseDir.isDirectory()) {
                            File testcaseConfigFile = new File(testcaseDir, "config.properties");
                            if (testcaseConfigFile.exists() && testcaseConfigFile.isFile()) {
                                try {
                                    FileReader testcaseConfigReader = new FileReader(testcaseConfigFile);
                                    Properties testcaseConfigProperties = new Properties();
                                    testcaseConfigProperties.load(testcaseConfigReader);
                                    
                                    String value = testcaseConfigProperties.getProperty(c_ActivatedAttribute, "true");
                                    TestCase testcase = new TestCase(testcaseDir.getName(), moduleDir.getName(), Boolean.parseBoolean(value));
                                    result.add(testcase);
                                    
                                } catch (Exception ex) {
                                    Logger.Global.logState("Unable to load testcase configuration: " + ex.getMessage(), LogLevel.Error);
                                }
                            }
                        }
        
        return result;
    }
    
    /**
     * Gets all conformity events from logfile.
     * @param logfile The logfile.
     * @param testcase The testcase.
     * @return List of ConformityEvents in logfile.
     */
    private List<ConformityEvent> getConformityEventsFromLogFile(final File logfile, final TestCase testcase) {
        List<ConformityEvent> result = new ArrayList<ConformityEvent>();
        
        // parse xml logfile
        Document logDoc = null;
        try {
            DocumentBuilder docBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            logDoc = docBuilder.parse(logfile);
        } catch (SAXException | IOException | ParserConfigurationException e) {
            Logger.Global.logState("Unable to parse logfile (" + logfile.getAbsolutePath() + "): " + e.getMessage(), LogLevel.Error);
        }
        
        if (logDoc == null)
            return result;
        
        XPathFactory factory = XPathFactory.newInstance();
        XPath xpath = factory.newXPath();
        
        
        // validate logfile
        File xsd = null;
        // get logging version to select correct schema
        try {
            String logVersion = xpath.evaluate("attribute[@key='result']/@value", logDoc);
            switch (logVersion) {
                case "1":
                    xsd = new File(GlobalSettings.getConfigDir(), "logging_v1.xsd");
                    break;
            }
        } catch (XPathExpressionException e) {
            Logger.Global.logState("Unable to determine log version: " + e.getMessage(), LogLevel.Error);
        }
        // validate now
        if (xsd != null && xsd.exists()) {
            try {
                Schema schema = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI).newSchema(xsd);
                Source xml = new StreamSource(logfile);
                schema.newValidator().validate(xml);
                // no exception = validation successful
            } catch (SAXException | IOException e) {
                Logger.Global.logState("Unable to determine log version: " + e.getMessage(), LogLevel.Error);
                return result;
            }
        }
        
        // get conformity events
        NodeList events = null;
        try {
            events = (NodeList)xpath.evaluate("/events/event[@type='CONFORMITY']", logDoc, XPathConstants.NODESET);
        } catch (XPathExpressionException e) {
            Logger.Global.logState("Unable to get events from logfile: " + e.getMessage(), LogLevel.Error);
        }
        
        if (events == null)
            return result;
        
        // parse events
        for (int i = 0; i < events.getLength(); i++)
            result.add(new ConformityEvent(events.item(i), testcase, logfile));
        
        return result;
    }
    
    /**
     * Analyzes all given events to rate testcase.
     * @param testcase The test case.
     * @param events The events.
     * @return Result of testcase.
     */
    private TestCaseResult analyseTestcase(final TestCase testcase, final List<ConformityEvent> events) {
        // only analyse testcase if activated
        if (!testcase.isActivated())
            return TestCaseResult.NotApplicable;
        
        TestCaseResult result = TestCaseResult.NotPerformed;
        
        for (ConformityEvent event : events)
            if (testcase.equals(event.getTestcase())) {
                // testcase is failed if even one event is failed
                if (event.getResult().equals("passed"))
                    result = TestCaseResult.Passed;
                else if(event.getResult().equals("failed")) {
                    result = TestCaseResult.Failed;
                    break;
                }
            }
        
        return result;
    }
}
