package com.secunet.log4j.plugin;

import org.apache.logging.log4j.message.StructuredDataMessage;

public class CustomDataMessage extends StructuredDataMessage {
    private static final long serialVersionUID = 1L;
    
    private StackTraceElement[] source = null;
    
    /**
     * Creates a StructuredDataMessage using an ID (max 32 characters), message, and type (max 32 characters).
     * @param id The String id.
     * @param msg The message.
     * @param type The message type.
     */
    public CustomDataMessage(final String id, final String msg, final String type) {
        super(id, msg, type);
    }
    
    public void setSource(StackTraceElement[] source)
    {
        this.source = source;
    }
    
    public StackTraceElement[] getSource()
    {
        return this.source;
    }
}
