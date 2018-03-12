package com.secunet.ipsmall.log;

import java.util.HashMap;
import java.util.Map;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.config.AbstractConfiguration;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.LoggerConfig;

import com.secunet.log4j.plugin.CustomDataMessage;

public class Log4J2ModuleLogger implements IModuleLogger {         
    private Map<EventType, Logger> eventLogger = new HashMap<EventType, Logger>();
    
    private final String moduleName;
    private final String loggerPrefix;
    private String stackTraceIdentifier = Log4J2ModuleLogger.class.getName();
    
    public Log4J2ModuleLogger(String moduleName) {
        this(moduleName, "");
    }
    
    public Log4J2ModuleLogger(String moduleName, String loggerPrefix) {
        this.moduleName = moduleName;
        if (!loggerPrefix.isEmpty() && !loggerPrefix.endsWith("."))
            this.loggerPrefix = loggerPrefix + ".";
        else
            this.loggerPrefix = loggerPrefix;
        
        // create loggers
        createNewLogger(EventType.None, this.loggerPrefix + this.moduleName);
        createNewLogger(EventType.State, this.loggerPrefix + this.moduleName + "." + EventType.State.getValue());
        createNewLogger(EventType.Conformity, this.loggerPrefix + this.moduleName + "." + EventType.Conformity.getValue());
        createNewLogger(EventType.Protocol, this.loggerPrefix + this.moduleName + "." + EventType.Protocol.getValue());
        createNewLogger(EventType.Environment, this.loggerPrefix + this.moduleName + "." + EventType.Environment.getValue());
    }
    
    public void setStackTraceIdentifier(String identifier) {
        stackTraceIdentifier = identifier;
    }
    
    private void createNewLogger(EventType type, String name) {
        // create configuration for new logger if doesn't exist
        LoggerContext ctx = (LoggerContext) LogManager.getContext(false);
        Configuration conf = ctx.getConfiguration();
        
        // check if configuration exists already
        if (((AbstractConfiguration)conf).getLogger(name) == null) {
            // get parent config
            LoggerConfig parentConfig = conf.getLoggerConfig(name); // use same name, put if logger doesn't exist it get parent config
            LoggerConfig logConf = new LoggerConfig(name, parentConfig.getLevel(), parentConfig.isAdditive());
            conf.addLogger(name, logConf);
        }
        
        // store new logger
        eventLogger.put(type, ctx.getLogger(name));
    }
    
    public static void setGlobalLogfile(String file) {
        setSystemProperty("logGlobalLogfile", file);
    }
    
    public static String getGlobalLogfile() {        
        return getSystemProperty("logGlobalLogfile");
    }
    
    public static void setTestCasefile(String file) {        
        setSystemProperty("logTestCaseLogfile", file);
    }
    
    public static String getTestCasefile() {        
        return getSystemProperty("logTestCaseLogfile");
    }
    
    private static void setSystemProperty(String varName, String value) {
        System.setProperty(varName, value);
        LoggerContext ctx = (LoggerContext) LogManager.getContext(false);
        ctx.reconfigure();
    }
    
    private static String getSystemProperty(String varName) {
        return System.getProperty(varName);
    }
    
    private static Map<LogLevel, Level> getLogLevelMapping() {
        Map<LogLevel, Level> map = new HashMap<LogLevel, Level>();
        
        map.put(LogLevel.Off, Level.OFF);
        map.put(LogLevel.Fatal, Level.FATAL);
        map.put(LogLevel.Error, Level.ERROR);
        map.put(LogLevel.Warn, Level.WARN);
        map.put(LogLevel.Info, Level.INFO);
        map.put(LogLevel.Debug, Level.DEBUG);
        map.put(LogLevel.All, Level.ALL);
        
        return map;
    }
    
    public static Level toLog4J2Level(LogLevel level) {
        Level result =  getLogLevelMapping().get(level);
        if (result == null)
            result = Level.ALL;
        
        return result;
    }
    
    public static LogLevel fromLog4J2Level(Level level) {
        LogLevel result = LogLevel.All;
        Object[] levels = getLogLevelMapping().values().toArray();
        for (int i = 0; i < levels.length; i++) {
            if (levels[i].equals(level)) {
                result = (LogLevel) getLogLevelMapping().keySet().toArray()[i];
                break;
            }
        }
        
        return result;
    }
    
    @Override
    public String getModuleName() {
        return this.moduleName;
    }
    
    @Override
    public LogLevel setLogLevel(LogLevel level) {
        return setLogLevel(level, EventType.None);
    }
    
    @Override
    public LogLevel setLogLevel(LogLevel level, EventType eventType) {
        // get logger configuration
        LoggerContext ctx = (LoggerContext) LogManager.getContext(false);
        
        String loggerName = eventLogger.get(eventType).getName();
        Level oldLevel = ctx.getLogger(loggerName).getLevel();
        ctx.getLogger(loggerName).setLevel(toLog4J2Level(level));
        //Level newLevel = ctx.getLogger(loggerName).getLevel();
        return fromLog4J2Level(oldLevel);
    }
    
    @Override
    public LogLevel getLogLevel(EventType eventType) {
        LoggerContext ctx = (LoggerContext) LogManager.getContext(false);
        
        String loggerName = eventLogger.get(eventType).getName();
        Level level = ctx.getLogger(loggerName).getLevel();
        
        return fromLog4J2Level(level);
    }
    
    @Override
    public void log(String messageText) {
        this.log(messageText, LogLevel.Info);
    }
    
    @Override
    public void log(String messageText, LogLevel level) {
        CustomDataMessage msg = new CustomDataMessage(this.moduleName, messageText, EventType.None.getValue());
        msg.setSource(getSource());
        
        eventLogger.get(EventType.None).log(toLog4J2Level(level), msg);
    }
    
    @Override
    public void logState(String messageText) {
        this.logState(messageText, LogLevel.Info);
    }
    
    @Override
    public void logState(String messageText, LogLevel level) {
        CustomDataMessage msg = new CustomDataMessage(this.moduleName, messageText, EventType.State.getValue());
        msg.setSource(getSource());
        
        eventLogger.get(EventType.State).log(toLog4J2Level(level), msg);
    }
    
    @Override
    public void logConformity(ConformityResult result, String messageText) {
        this.logConformity(result, messageText, LogLevel.Info);
    }
    
    @Override
    public void logConformity(ConformityResult result, String messageText, LogLevel level) {
        this.logConformity(result, ConformityMode.auto, messageText, level);
    }
    
    @Override
    public void logConformity(ConformityResult result, ConformityMode mode, String messageText) {
        this.logConformity(result, mode, messageText, LogLevel.Info);
    }
    
    @Override
    public void logConformity(ConformityResult result, ConformityMode mode, String messageText, LogLevel level) {
        CustomDataMessage msg = new CustomDataMessage(this.moduleName, messageText, EventType.Conformity.getValue());
        msg.setSource(getSource());
        msg.put("result", result.name());
        msg.put("mode", mode.name());
        
        eventLogger.get(EventType.Conformity).log(toLog4J2Level(level), msg);
    }
    
    @Override
    public void logProtocol(String name, ProtocolDirection direction, String sender, String receiver, String messageText) {
        this.logProtocol(name, direction, sender, receiver, messageText, LogLevel.Info);
    }
    
    @Override
    public void logProtocol(String name, ProtocolDirection direction, String sender, String receiver, String messageText, LogLevel level) {
        this.logProtocol(name, direction, sender, receiver, messageText, null, level);
    }
    
    @Override
    public void logProtocol(String name, ProtocolDirection direction, String sender, String receiver, String messageText, String rawData) {
        this.logProtocol(name, direction, sender, receiver, messageText, rawData, LogLevel.Info);
    }
    
    @Override
    public void logProtocol(String name, ProtocolDirection direction, String sender, String receiver, String messageText, String rawData, LogLevel level) {
        CustomDataMessage msg = new CustomDataMessage(this.moduleName, messageText, EventType.Protocol.getValue());
        msg.setSource(getSource());
        msg.put("name", name);
        msg.put("direction", direction.name());
        msg.put("sender", sender);
        msg.put("receiver", receiver);
        if (rawData != null)
            msg.put("raw", rawData);
        
        eventLogger.get(EventType.Protocol).log(toLog4J2Level(level), msg);
    }
    
    @Override
    public void logEnvironment(EnvironmentClassification classification, String messageText) {
        this.logEnvironment(classification, messageText, LogLevel.Info);
    }
    
    @Override
    public void logEnvironment(EnvironmentClassification classification, String messageText, LogLevel level) {
        CustomDataMessage msg = new CustomDataMessage(this.moduleName, messageText, EventType.Environment.getValue());
        msg.setSource(getSource());
        msg.put("classification", classification.name());
        
        eventLogger.get(EventType.Environment).log(toLog4J2Level(level), msg);
    }
    
    @Override
    public void logException(Exception e) {
        String msgText = "Exception occured";
        StackTraceElement[] source =  getSource();
        if (source != null && source.length > 0 && source[0] != null)
            msgText += " (at " + source[0].getMethodName() + " in " + source[0].getClassName() + ")";
        msgText += ": " + e.getMessage();
        
        CustomDataMessage msg = new CustomDataMessage(this.moduleName, msgText, EventType.State.getValue());
        msg.setSource(e.getStackTrace());
        
        eventLogger.get(EventType.State).log(toLog4J2Level(LogLevel.Debug), msg);
    }
    
    private StackTraceElement[] getSource() {
        StackTraceElement result = null;
        StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
        StackTraceElement last = null;
        for (int i = stackTrace.length - 1; i > 0; i--) {
            final String className = stackTrace[i].getClassName();
            if (this.stackTraceIdentifier.equals(className)) {
                result = last;
                break;
            }
            last = stackTrace[i];
        }
        StackTraceElement[] results = { result };
        return results;
    } 
}
