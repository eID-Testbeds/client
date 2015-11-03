package com.secunet.ipsmall.log;

import com.secunet.ipsmall.log.IModuleLogger.LogLevel;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.LogRecord;
import java.util.logging.Level;

/**
 * Wrapper to provide Java util logger for IModuleLogger.
 */
public class JavaLogWrapper extends java.util.logging.Logger {
    protected IModuleLogger moduleLogger;

    protected String prefix = null;

    /**
     * Creates a new Java util logger for IModuleLogger.
     *
     * @param moduleLogger Wrapped IModuleLogger.
     */
    public JavaLogWrapper(IModuleLogger moduleLogger) {
        this(moduleLogger, null);
    }

    /**
     * Creates a new Java util logger for IModuleLogger.
     *
     * @param moduleLogger Wrapped IModuleLogger.
     * @param prefix Prefix to show at begin of each log message.
     */
    public JavaLogWrapper(IModuleLogger moduleLogger, String prefix) {
        super(moduleLogger.getModuleName(), null);
        
        this.moduleLogger = moduleLogger;
        this.prefix = prefix;
    }
    
    @Override
    public void log(LogRecord record) {
        String msg = "";
        if (prefix != null && !prefix.isEmpty()) {
            msg += prefix + ": ";
        }
        msg += record.getMessage();

        moduleLogger.logState(msg, fromJavaLogLevel(record.getLevel()));
    }
    
    @Override
    public boolean isLoggable(java.util.logging.Level level) {
        // log all, filtering is done by |ModuleLogger.
        return true;
    }
    
    @Override
    public Level getLevel() {
        return toJavaLogLevel(moduleLogger.getLogLevel(IModuleLogger.EventType.State));
    }
    
    
    private static Map<LogLevel, Level> getLogLevelMapping() {
        Map<LogLevel, Level> map = new HashMap<>();
        
        map.put(LogLevel.Off, Level.OFF);
        map.put(LogLevel.Fatal, Level.SEVERE);
        map.put(LogLevel.Error, Level.SEVERE);
        map.put(LogLevel.Warn, Level.WARNING);
        map.put(LogLevel.Info, Level.INFO);
        map.put(LogLevel.Debug, Level.FINEST);
        map.put(LogLevel.All, Level.ALL);
        
        return map;
    }
    
    public static Level toJavaLogLevel(LogLevel level) {
        Level result =  getLogLevelMapping().get(level);
        if (result == null)
            result = Level.ALL;
        
        return result;
    }
    
    public static LogLevel fromJavaLogLevel(Level level) {
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
}
