package com.secunet.ipsmall.log;

public interface IModuleLogger {
    
    public enum LogLevel {
        Off,
        Fatal,
        Error,
        Warn,
        Info,
        Debug,
        All
    }
    
    public enum EventType {
        State("STATE"),
        Conformity("CONFORMITY"),
        Protocol("PROTOCOL"),
        Environment("ENVIRONMENT"),
        None("");
        
        private String value;
        
        private EventType(String value) {
            this.value = value;
        }
        
        public String getValue() {
            return this.value;
        }
    }
    
    public enum ConformityMode {
        auto,
        manual
    }
    
    public enum ConformityResult {
        passed,
        failed,
        undetermined
    }
    
    public enum ProtocolDirection {
        sent,
        received
    }
    
    public enum EnvironmentClassification {
        Environment,
        GlobalConfig,
        TestCaseConfig
    }
        
    public String getModuleName();
    
    public LogLevel setLogLevel(LogLevel level);
    
    // returns old level
    public LogLevel setLogLevel(LogLevel level, EventType eventType);
    
    public LogLevel getLogLevel(EventType eventType);
    
    public void log(String messageText);
    public void log(String messageText, LogLevel level);
    
    public void logState(String messageText);
    public void logState(String messageText, LogLevel level);
    
    public void logConformity(ConformityResult result, String messageText);
    public void logConformity(ConformityResult result, String messageText, LogLevel level);
    public void logConformity(ConformityResult result, ConformityMode mode, String messageText);
    public void logConformity(ConformityResult result, ConformityMode mode, String messageText, LogLevel level);
    
    public void logProtocol(String name, ProtocolDirection direction, String sender, String receiver, String messageText);
    public void logProtocol(String name, ProtocolDirection direction, String sender, String receiver, String messageText, LogLevel level);
    public void logProtocol(String name, ProtocolDirection direction, String sender, String receiver, String messageText, String rawData);
    public void logProtocol(String name, ProtocolDirection direction, String sender, String receiver, String messageText, String rawData, LogLevel level);
    
    public void logEnvironment(EnvironmentClassification classification, String messageText);
    public void logEnvironment(EnvironmentClassification classification, String messageText, LogLevel level);
    
    public void logException(Exception e);
}
