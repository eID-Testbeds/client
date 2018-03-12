package com.secunet.ipsmall.log;

import org.apache.logging.log4j.*;
import org.apache.logging.log4j.message.Message;
import org.apache.logging.log4j.message.MessageFactory;

/**
 * Wrapper to provide Log4J interface for IModuleLogger.
 */
public class Log4JWrapper implements org.apache.logging.log4j.Logger {

    protected IModuleLogger moduleLogger;

    protected String prefix = null;

    /**
     * Creates a new Log4J logger for IModuleLogger.
     *
     * @param moduleLogger Wrapped IModuleLogger.
     */
    public Log4JWrapper(IModuleLogger moduleLogger) {
        this(moduleLogger, null);
    }

    /**
     * Creates a new Log4J logger for IModuleLogger.
     *
     * @param moduleLogger Wrapped IModuleLogger.
     * @param prefix Prefix to show at begin of each log message.
     */
    public Log4JWrapper(IModuleLogger moduleLogger, String prefix) {
        this.moduleLogger = moduleLogger;
        this.prefix = prefix;
    }

    @Override
    public void catching(Level level, Throwable t) {

    }

    @Override
    public void catching(Throwable t) {

    }

    @Override
    public void debug(Marker marker, Message msg) {
        log(Level.DEBUG, marker, msg);
    }

    @Override
    public void debug(Marker marker, Message msg, Throwable t) {
        log(Level.DEBUG, marker, msg, t);
    }

    @Override
    public void debug(Marker marker, Object message) {
        log(Level.DEBUG, marker, message);
    }

    @Override
    public void debug(Marker marker, Object message, Throwable t) {
        log(Level.DEBUG, marker, message, t);
    }

    @Override
    public void debug(Marker marker, String message) {
        log(Level.DEBUG, marker, message);
    }

    @Override
    public void debug(Marker marker, String message, Object... params) {
        log(Level.DEBUG, marker, message, params);
    }

    @Override
    public void debug(Marker marker, String message, Throwable t) {
        log(Level.DEBUG, marker, message, t);
    }

    @Override
    public void debug(Message msg) {
        log(Level.DEBUG, msg);
    }

    @Override
    public void debug(Message msg, Throwable t) {
        log(Level.DEBUG, msg, t);
    }

    @Override
    public void debug(Object message) {
        log(Level.DEBUG, message);
    }

    @Override
    public void debug(Object message, Throwable t) {
        log(Level.DEBUG, message, t);
    }

    @Override
    public void debug(String message) {
        log(Level.DEBUG, message);
    }

    @Override
    public void debug(String message, Object... params) {
        log(Level.DEBUG, message, params);
    }

    @Override
    public void debug(String message, Throwable t) {
        log(Level.DEBUG, message, t);
    }

    @Override
    public void entry() {

    }

    @Override
    public void entry(Object... params) {

    }

    @Override
    public void error(Marker marker, Message msg) {
        log(Level.ERROR, msg);
    }

    @Override
    public void error(Marker marker, Message msg, Throwable t) {
        log(Level.ERROR, marker, msg, t);
    }

    @Override
    public void error(Marker marker, Object message) {
        log(Level.ERROR, marker, message);
    }

    @Override
    public void error(Marker marker, Object message, Throwable t) {
        log(Level.ERROR, marker, message, t);
    }

    @Override
    public void error(Marker marker, String message) {
        log(Level.ERROR, marker, message);
    }

    @Override
    public void error(Marker marker, String message, Object... params) {
        log(Level.ERROR, marker, message, params);
    }

    @Override
    public void error(Marker marker, String message, Throwable t) {
        log(Level.ERROR, marker, message, t);
    }

    @Override
    public void error(Message msg) {
        log(Level.ERROR, msg);
    }

    @Override
    public void error(Message msg, Throwable t) {
        log(Level.ERROR, msg, t);
    }

    @Override
    public void error(Object message) {
        log(Level.ERROR, message);
    }

    @Override
    public void error(Object message, Throwable t) {
        log(Level.ERROR, message, t);
    }

    @Override
    public void error(String message) {
        log(Level.ERROR, message);
    }

    @Override
    public void error(String message, Object... params) {
        log(Level.ERROR, params);
    }

    @Override
    public void error(String message, Throwable t) {
        log(Level.ERROR, message, t);
    }

    @Override
    public void exit() {

    }

    @Override
    public <R> R exit(R result) {
        return null;
    }

    @Override
    public void fatal(Marker marker, Message msg) {
        log(Level.FATAL, marker, msg);
    }

    @Override
    public void fatal(Marker marker, Message msg, Throwable t) {
        log(Level.FATAL, marker, msg, t);
    }

    @Override
    public void fatal(Marker marker, Object message) {
        log(Level.FATAL, marker, message);
    }

    @Override
    public void fatal(Marker marker, Object message, Throwable t) {
        log(Level.FATAL, marker, message, t);
    }

    @Override
    public void fatal(Marker marker, String message) {
        log(Level.FATAL, marker, message);
    }

    @Override
    public void fatal(Marker marker, String message, Object... params) {
        log(Level.FATAL, marker, message, params);
    }

    @Override
    public void fatal(Marker marker, String message, Throwable t) {
        log(Level.FATAL, marker, message, t);
    }

    @Override
    public void fatal(Message msg) {
        log(Level.FATAL, msg);
    }

    @Override
    public void fatal(Message msg, Throwable t) {
        log(Level.FATAL, msg, t);
    }

    @Override
    public void fatal(Object message) {
        log(Level.FATAL, message);
    }

    @Override
    public void fatal(Object message, Throwable t) {
        log(Level.FATAL, message, t);
    }

    @Override
    public void fatal(String message) {
        log(Level.FATAL, message);
    }

    @Override
    public void fatal(String message, Object... params) {
        log(Level.FATAL, message, params);
    }

    @Override
    public void fatal(String message, Throwable t) {
        log(Level.FATAL, message, t);
    }

    @Override
    public Level getLevel() {
        return Log4J2ModuleLogger.toLog4J2Level(moduleLogger.getLogLevel(IModuleLogger.EventType.State));
    }

    @Override
    public MessageFactory getMessageFactory() {
        return null;
    }

    @Override
    public String getName() {
        return moduleLogger.getModuleName();
    }

    @Override
    public void info(Marker marker, Message msg) {
        log(Level.INFO, marker, msg);
    }

    @Override
    public void info(Marker marker, Message msg, Throwable t) {
        log(Level.INFO, marker);
    }

    @Override
    public void info(Marker marker, Object message) {
        log(Level.INFO, marker, message);
    }

    @Override
    public void info(Marker marker, Object message, Throwable t) {
        log(Level.INFO, marker, message, t);
    }

    @Override
    public void info(Marker marker, String message) {
        log(Level.INFO, marker, message);
    }

    @Override
    public void info(Marker marker, String message, Object... params) {
        log(Level.INFO, marker, message, params);
    }

    @Override
    public void info(Marker marker, String message, Throwable t) {
        log(Level.INFO, marker, message, t);
    }

    @Override
    public void info(Message msg) {
        log(Level.INFO, msg);
    }

    @Override
    public void info(Message msg, Throwable t) {
        log(Level.INFO, msg, t);
    }

    @Override
    public void info(Object message) {
        log(Level.INFO, message);
    }

    @Override
    public void info(Object message, Throwable t) {
        log(Level.INFO, message, t);
    }

    @Override
    public void info(String message) {
        log(Level.INFO, message);
    }

    @Override
    public void info(String message, Object... params) {
        log(Level.INFO, message, params);
    }

    @Override
    public void info(String message, Throwable t) {
        log(Level.INFO, message, t);
    }

    @Override
    public boolean isDebugEnabled() {
        return isEnabled(Level.DEBUG);
    }

    @Override
    public boolean isDebugEnabled(Marker marker) {
        return isEnabled(Level.DEBUG, marker);
    }

    @Override
    public boolean isEnabled(Level level) {
        return true;
    }

    @Override
    public boolean isEnabled(Level level, Marker marker) {
        return isEnabled(level);
    }

    @Override
    public boolean isErrorEnabled() {
        return isEnabled(Level.ERROR);
    }

    @Override
    public boolean isErrorEnabled(Marker marker) {
        return isEnabled(Level.ERROR, marker);
    }

    @Override
    public boolean isFatalEnabled() {
        return isEnabled(Level.FATAL);
    }

    @Override
    public boolean isFatalEnabled(Marker marker) {
        return isEnabled(Level.FATAL, marker);
    }

    @Override
    public boolean isInfoEnabled() {
        return isEnabled(Level.INFO);
    }

    @Override
    public boolean isInfoEnabled(Marker marker) {
        return isEnabled(Level.INFO, marker);
    }

    @Override
    public boolean isTraceEnabled() {
        return isEnabled(Level.TRACE);
    }

    @Override
    public boolean isTraceEnabled(Marker marker) {
        return isEnabled(Level.TRACE, marker);
    }

    @Override
    public boolean isWarnEnabled() {
        return isEnabled(Level.WARN);
    }

    @Override
    public boolean isWarnEnabled(Marker marker) {
        return isEnabled(Level.WARN, marker);
    }

    @Override
    public void log(Level level, Marker marker, Message msg) {
        log(level, msg.getFormattedMessage());
    }

    @Override
    public void log(Level level, Marker marker, Message msg, Throwable t) {
        log(level, msg.getFormattedMessage());
    }

    @Override
    public void log(Level level, Marker marker, Object message) {
        log(level, message.toString());
    }

    @Override
    public void log(Level level, Marker marker, Object message, Throwable t) {
        log(level, message.toString());
    }

    @Override
    public void log(Level level, Marker marker, String message) {
        log(level, message);
    }

    @Override
    public void log(Level level, Marker marker, String message, Object... params) {
        log(level, message);
    }

    @Override
    public void log(Level level, Marker marker, String message, Throwable t) {
        log(level, message);
    }

    @Override
    public void log(Level level, Message msg) {
        log(level, msg.getFormattedMessage());
    }

    @Override
    public void log(Level level, Message msg, Throwable t) {
        log(level, msg.getFormattedMessage());
    }

    @Override
    public void log(Level level, Object message) {
        log(level, message.toString());
    }

    @Override
    public void log(Level level, Object message, Throwable t) {
        log(level, message.toString());
    }

    @Override
    public void log(Level level, String message) {
        String msg = "";
        if (prefix != null && !prefix.isEmpty()) {
            msg += prefix + ": ";
        }
        msg += message;

        moduleLogger.logState(msg, Log4J2ModuleLogger.fromLog4J2Level(level));
    }

    @Override
    public void log(Level level, String message, Object... params) {
        log(level, message);
    }

    @Override
    public void log(Level level, String message, Throwable t) {
        log(level, message);
    }

    @Override
    public void printf(Level level, Marker marker, String format, Object... params) {

    }

    @Override
    public void printf(Level level, String format, Object... params) {

    }

    @Override
    public <T extends Throwable> T throwing(Level level, T t) {
        return null;
    }

    @Override
    public <T extends Throwable> T throwing(T t) {
        return null;
    }

    @Override
    public void trace(Marker marker, Message msg) {
        log(Level.TRACE, marker, msg);
    }

    @Override
    public void trace(Marker marker, Message msg, Throwable t) {
        log(Level.TRACE, marker, msg, t);
    }

    @Override
    public void trace(Marker marker, Object message) {
        log(Level.TRACE, marker, message);
    }

    @Override
    public void trace(Marker marker, Object message, Throwable t) {
        log(Level.TRACE, marker, message, t);
    }

    @Override
    public void trace(Marker marker, String message) {
        log(Level.TRACE, marker, message);
    }

    @Override
    public void trace(Marker marker, String message, Object... params) {
        log(Level.TRACE, marker, message, params);
    }

    @Override
    public void trace(Marker marker, String message, Throwable t) {
        log(Level.TRACE, marker, message, t);
    }

    @Override
    public void trace(Message msg) {
        log(Level.TRACE, msg);
    }

    @Override
    public void trace(Message msg, Throwable t) {
        log(Level.TRACE, msg, t);
    }

    @Override
    public void trace(Object message) {
        log(Level.TRACE, message);
    }

    @Override
    public void trace(Object message, Throwable t) {
        log(Level.TRACE, message, t);
    }

    @Override
    public void trace(String message) {
        log(Level.TRACE, message);
    }

    @Override
    public void trace(String message, Object... params) {
        log(Level.TRACE, message, params);
    }

    @Override
    public void trace(String message, Throwable t) {
        log(Level.TRACE, message, t);
    }

    @Override
    public void warn(Marker marker, Message msg) {
        log(Level.WARN, marker, msg);
    }

    @Override
    public void warn(Marker marker, Message msg, Throwable t) {
        log(Level.WARN, msg, t);
    }

    @Override
    public void warn(Marker marker, Object message) {
        log(Level.WARN, marker, message);
    }

    @Override
    public void warn(Marker marker, Object message, Throwable t) {
        log(Level.WARN, marker, message, t);
    }

    @Override
    public void warn(Marker marker, String message) {
        log(Level.WARN, marker, message);
    }

    @Override
    public void warn(Marker marker, String message, Object... params) {
        log(Level.WARN, marker, message, params);
    }

    @Override
    public void warn(Marker marker, String message, Throwable t) {
        log(Level.WARN, marker, message, t);
    }

    @Override
    public void warn(Message msg) {
        log(Level.WARN, msg);
    }

    @Override
    public void warn(Message msg, Throwable t) {
        log(Level.WARN, msg, t);
    }

    @Override
    public void warn(Object message) {
        log(Level.WARN, message);
    }

    @Override
    public void warn(Object message, Throwable t) {
        log(Level.WARN, message, t);
    }

    @Override
    public void warn(String message) {
        log(Level.WARN, message);
    }

    @Override
    public void warn(String message, Object... params) {
        log(Level.WARN, message, params);
    }

    @Override
    public void warn(String message, Throwable t) {
        log(Level.WARN, message, t);
    }
}
