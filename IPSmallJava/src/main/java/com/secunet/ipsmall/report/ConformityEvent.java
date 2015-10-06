package com.secunet.ipsmall.report;

import java.io.File;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Node;

/**
 * Represents a conformity event.
 */
public class ConformityEvent {
    private final File logfile;
    private final TestCase testcase;
    private String result;
    private String mode;
    private String timestamp;
    private String message;
    private String module;
    
    public ConformityEvent(final Node event, final TestCase testcase, final File logfile) {
        this.testcase = testcase;
        this.logfile = logfile;
        
        XPathFactory factory = XPathFactory.newInstance();
        XPath xpath = factory.newXPath();
        
        try {
            // check if event is conformity event
            String eventType = xpath.evaluate("@type", event);
            if (!eventType.equals("CONFORMITY"))
                throw new IllegalArgumentException("Event was no CONFORMITY event!");
            
            // get values from event
            result = xpath.evaluate("attribute[@key='result']/@value", event);
            mode = xpath.evaluate("attribute[@key='mode']/@value", event);
            timestamp = xpath.evaluate("timestamp/@raw_ms", event);
            message = xpath.evaluate("message", event);
            module = xpath.evaluate("@module", event);
            
        } catch (XPathExpressionException e) {
            throw new IllegalArgumentException("Unable to parse CONFORMITY event: " + e.getMessage());
        }
    }
    
    /**
     * Gets logfile in which the event occurred.
     * @return The logfile.
     */
    public File getLogfile() {
        return logfile;
    }
    
    /**
     * Gets testcase in which the event occurred.
     * @return The testcase.
     */
    public TestCase getTestcase() {
        return testcase;
    }
    
    /**
     * Gets result of the event.
     * @return The result.
     */
    public String getResult() {
        return result;
    }
    
    /**
     * Gets mode of the event.
     * @return The mode.
     */
    public String getMode() {
        return mode;
    }
    
    /**
     * Gets timestamp of the event.
     * @return The timestamp (in ms).
     */
    public String getTimestamp() {
        return timestamp;
    }
    
    /**
     * Gets message of the event.
     * @return The message.
     */
    public String getMessage() {
        return message;
    }
    
    /**
     * Gets module in which event was generated.
     * @return The module.
     */
    public String getModule() {
        return module;
    }
}
