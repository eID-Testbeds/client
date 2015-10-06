package com.secunet.ipsmall.log;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public enum Logger implements IModuleLogger {
    // Logger for global events
    Global("global", true),
    TestObjectBuilder("TestObjectBuilder", true),
    CertificateGeneration("CertificateGeneration", true),
    SystemOut("SystemOut", false),
    TestRunner("TestRunner", false),
    BrowserSim("BrowserSimulator", false),
    HTTPServer("HTTPServer", false),
    Redirector("Redirector", false),
    eIDServer("eIDServer", false),
    eService("eService", false),
    TLS("TLS", false),
    EAC("EAC", false),
    CardSim("CardSimulation", false),
    XMLEval("XMLEvaluater", false),
    UI("UI", false),
    TCTokenProv("TCTokenProvider", false),
    ;
    
    private static final String prefixTestcase = "testcase";

    private final IModuleLogger logger;
    
    private Logger(String name, boolean global)
    {
        final Log4J2ModuleLogger logger;
        if (global)
            logger = new Log4J2ModuleLogger(name);
        else
            logger = new Log4J2ModuleLogger(name, prefixTestcase);
        
        // set this class name as identifier to find correct element in stacktrace
        logger.setStackTraceIdentifier(Logger.class.getName());
        
        this.logger = logger;
    }

    public Log4JWrapper getLog4JLogger() {
        return new Log4JWrapper(logger);
    }

    public Log4JWrapper getLog4JLogger(String prefix) {
        return new Log4JWrapper(logger, prefix);
    }
    
    public JavaLogWrapper getJavaLogger() {
        return new JavaLogWrapper(logger);
    }

    public JavaLogWrapper getJavaLogger(String prefix) {
        return new JavaLogWrapper(logger, prefix);
    }
    
    public void setLogLevels(Map<EventType,LogLevel> levels) {
        Set<EventType> eventTypes = levels.keySet();
        for (EventType eventType : eventTypes) 
            this.logger.setLogLevel(levels.get(eventType), eventType);
    }
    
    public static void setAllLogLevels(Map<EventType,LogLevel> levels) {
        for(Logger logger : Logger.values()) {
            logger.setLogLevels(levels);
        }
    }
    
    private static Map<Logger,Map<EventType,LogLevel>> backupLogLevels() {
        Map<Logger,Map<EventType,LogLevel>> backup = new HashMap<Logger, Map<EventType,LogLevel>>();
        for(Logger logger : Logger.values()) {
            Map<EventType,LogLevel> levels = new HashMap<EventType,LogLevel>();
            for(EventType eventType : EventType.values()) {
                LogLevel level = logger.getLogLevel(eventType);
                levels.put(eventType, level);
            }
            backup.put(logger, levels);
        }
        
        return backup;
    }
    
    private static void restoreLogLevels(Map<Logger,Map<EventType,LogLevel>> backup) {
        for(Logger logger : Logger.values()) {
            Map<EventType,LogLevel> levels = backup.get(logger);
            for(EventType eventType : EventType.values()) {
                LogLevel level = levels.get(eventType);
                logger.setLogLevel(level, eventType);
            }
            backup.put(logger, levels);
        }
    }
    
    public static void setGlobalLogfile(String file) {
        Map<Logger,Map<EventType,LogLevel>> backup = backupLogLevels();
        Log4J2ModuleLogger.setGlobalLogfile(file);
        restoreLogLevels(backup);
    }
    
    public static String getGlobalLogfile() {
        return Log4J2ModuleLogger.getGlobalLogfile();
    }
    
    public static void setTestCasefile(String file) {  
        Map<Logger,Map<EventType,LogLevel>> backup = backupLogLevels();
        Log4J2ModuleLogger.setTestCasefile(file);
        restoreLogLevels(backup);
    }
    
    public static String getTestCasefile() {  
        return Log4J2ModuleLogger.getTestCasefile();
    }

    @Override
    public String getModuleName() {
        return this.logger.getModuleName();
    }

    @Override
    public LogLevel setLogLevel(LogLevel level) {
        return this.logger.setLogLevel(level);
    }

    @Override
    public LogLevel setLogLevel(LogLevel level, EventType eventType) {
        return this.logger.setLogLevel(level, eventType);
    }

    @Override
    public LogLevel getLogLevel(EventType eventType) {
        return this.logger.getLogLevel(eventType);
    }

    @Override
    public void log(String messageText) {
        this.logger.log(messageText);
    }

    @Override
    public void log(String messageText, LogLevel level) {
        this.logger.log(messageText, level);
    }

    @Override
    public void logState(String messageText) {
        this.logger.logState(messageText);
    }

    @Override
    public void logState(String messageText, LogLevel level) {
        this.logger.logState(messageText, level);
    }

    @Override
    public void logConformity(ConformityResult result, String messageText) {
        this.logger.logConformity(result, messageText);
    }

    @Override
    public void logConformity(ConformityResult result, String messageText, LogLevel level) {
        this.logger.logConformity(result, messageText, level);
    }
    
    @Override
    public void logConformity(ConformityResult result, ConformityMode mode, String messageText) {
        this.logger.logConformity(result, mode, messageText);
    }

    @Override
    public void logConformity(ConformityResult result, ConformityMode mode, String messageText, LogLevel level) {
        this.logger.logConformity(result, mode, messageText, level);
    }

    @Override
    public void logProtocol(String name, ProtocolDirection direction, String sender, String receiver, String messageText) {
        this.logger.logProtocol(name, direction, sender, receiver, messageText);
    }

    @Override
    public void logProtocol(String name, ProtocolDirection direction, String sender, String receiver, String messageText, LogLevel level) {
        this.logger.logProtocol(name, direction, sender, receiver, messageText, level);
    }
    
    @Override
    public void logProtocol(String name, ProtocolDirection direction, String sender, String receiver, String messageText, String rawData) {
        this.logger.logProtocol(name, direction, sender, receiver, messageText, rawData);
    }

    @Override
    public void logProtocol(String name, ProtocolDirection direction, String sender, String receiver, String messageText, String rawData, LogLevel level) {
        this.logger.logProtocol(name, direction, sender, receiver, messageText, rawData, level);
    }

    @Override
    public void logEnvironment(EnvironmentClassification classification, String messageText) {
        this.logger.logEnvironment(classification, messageText);
    }

    @Override
    public void logEnvironment(EnvironmentClassification classification, String messageText, LogLevel level) {
        this.logger.logEnvironment(classification, messageText, level);
    }

    @Override
    public void logException(Exception e) {
        this.logger.logException(e);
    }
}
