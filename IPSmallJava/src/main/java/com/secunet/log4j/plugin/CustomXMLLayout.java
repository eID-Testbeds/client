package com.secunet.log4j.plugin;

import java.nio.charset.Charset;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.Layout;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.config.Node;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.config.plugins.PluginAttribute;
import org.apache.logging.log4j.core.config.plugins.PluginFactory;
import org.apache.logging.log4j.core.layout.AbstractStringLayout;
import org.apache.logging.log4j.message.Message;
import org.apache.logging.log4j.message.StructuredDataMessage;

import com.secunet.ipsmall.GlobalInfo;
import com.secunet.ipsmall.GlobalSettings;

@Plugin(name = "CustomXMLLayout", category = Node.CATEGORY, elementType = Layout.ELEMENT_TYPE, printObject = true)
public class CustomXMLLayout extends AbstractStringLayout {
    
    private static final long serialVersionUID = 1L;
    
    private Level locationInfoLevel;
    private boolean envelopeMessage;
    private boolean resolveTime;
    private DateFormat dfDate;
    private DateFormat dfTime;
    
    private static final String ROOT_TAG = "events";
    private static final String EVENT_TAG = "event";
    private static final String TIMESTAMP_TAG = "timestamp";
    private static final String MESSAGE_TAG = "message";
    private static final String ATTRIBUTE_TAG = "attribute";
    private static final String DEBUG_TAG = "debug";
    private static final String TRACE_TAG = "trace";
    
    protected CustomXMLLayout(final String locationInfoLevel, final boolean envelopeMessage, final boolean resolveTime, final String datePattern, final String timePattern, final Charset charset) {
        super(charset);
        this.locationInfoLevel = Level.getLevel(locationInfoLevel);
        this.envelopeMessage = envelopeMessage;
        this.resolveTime = resolveTime;
        this.dfDate = new SimpleDateFormat(datePattern);
        this.dfTime = new SimpleDateFormat(timePattern);
    }

    @Override
    public String toSerializable(final LogEvent event) {
        
        Message message = event.getMessage();
        
        long timestamp = event.getTimeMillis();
        Date date = new Date(timestamp);
        
        StackTraceElement[] source = null;
        if (message instanceof CustomDataMessage)  {
            CustomDataMessage msg = (CustomDataMessage) message;
            source = msg.getSource();
        } else {
            StackTraceElement[] trace = { event.getSource() };
            source = trace;
        }
        
        String id = "";
        String type = "";
        String text = message.getFormat();
        Map<String,String> attributes = null; 
        
        if (message instanceof StructuredDataMessage) {
            StructuredDataMessage msg = (StructuredDataMessage) message;
            id = msg.getId().getName();
            type = msg.getType();
            attributes = msg.getData();
        }

        final StringBuilder buf = new StringBuilder();
        //  <event ...>
        buf.append("\t<" + EVENT_TAG);
        buf.append(" logger=\"" + event.getLoggerName() + "\"");
        if (!id.isEmpty())
            buf.append(" module=\"" + id + "\"");
        if (!type.isEmpty())
            buf.append(" type=\"" + type + "\"");
        buf.append(" loglevel=\"" + event.getLevel().name() + "\"");
        buf.append(">" + System.lineSeparator());
        
        //      <timestamp ... />
        buf.append("\t\t<" + TIMESTAMP_TAG);
        if (this.resolveTime) {
            buf.append(" date=\"" +  this.dfDate.format(date) + "\"");
            buf.append(" time=\"" +  this.dfTime.format(date) + "\"");
        }
        buf.append(" raw_ms=\"" +  timestamp + "\"");
        buf.append("/>" + System.lineSeparator());
                
        //      <debug ... />        
        if (this.locationInfoLevel.intLevel() <= LogManager.getLogger(event.getLoggerName()).getLevel().intLevel()) {
            buf.append("\t\t<" + DEBUG_TAG);
            buf.append(" thread=\"" + escapeCharacters(event.getThreadName()) + "\"");
            buf.append(">" + System.lineSeparator());
            
            //          <trace ... />
            if (source != null)
                for (int i = source.length - 1; i >= 0 ; i--) {
                    buf.append("\t\t\t<" + TRACE_TAG);
                    buf.append(" class=\"" + escapeCharacters(source[i].getClassName()) + "\"");
                    buf.append(" method=\"" + escapeCharacters(source[i].getMethodName()) + "\"");
                    if (source[i].getFileName() != null)
                        buf.append(" file=\"" + source[i].getFileName() + "\"");
                    if (source[i].getLineNumber() > 0)
                        buf.append(" line=\"" +  source[i].getLineNumber() + "\"");
                    buf.append("/>" + System.lineSeparator());
                }
            
            buf.append("\t\t</" + DEBUG_TAG + ">" + System.lineSeparator());
        }

        //      <message> ... </message>
        buf.append("\t\t<" + MESSAGE_TAG + ">");
        if (envelopeMessage)
            buf.append("<![CDATA[");
        buf.append(text);
        if (envelopeMessage)
            buf.append("]]>");
        buf.append("</" + MESSAGE_TAG + ">" + System.lineSeparator());
        
        //      <attribute ... />
        if (attributes != null) {
            for (int i = 0; i < attributes.size(); i++) {
                buf.append("\t\t<" + ATTRIBUTE_TAG);
                buf.append(" key=\"" + escapeCharacters(attributes.keySet().toArray()[i].toString()) + "\"");
                buf.append(" value=\"" + escapeCharacters(attributes.values().toArray()[i].toString()) + "\"");
                buf.append("/>" + System.lineSeparator());
            }
        }

        //  </event>
        buf.append("\t</" + EVENT_TAG + ">" + System.lineSeparator());
        return buf.toString();
    }
    
    private String escapeCharacters(String txt)
    {
        String result = txt;
        result = result.replace("&", "&amp;");
        result = result.replace("\"", "&quot;");
        result = result.replace("'", "&apos;");
        result = result.replace("<", "&lt;");
        result = result.replace(">", "&gt;");
        return result;
    }
    
    @Override
    public byte[] getHeader() {
        final StringBuilder buf = new StringBuilder();
        buf.append("<?xml version=\"1.0\" encoding=\"" + this.getCharset().name() + "\"?>" + System.lineSeparator());
        buf.append("<?xml-stylesheet type=\"text/xsl\" href=\"" + GlobalSettings.getLogStyleFileName() + "\"?>" + System.lineSeparator());        
        buf.append("<" + ROOT_TAG);
        // schema definition
        buf.append(" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:schemaLocation=\"http://www.secunet.com " + GlobalSettings.getLogSchemaFileName() + "\"");
        buf.append(" software=\"" + GlobalInfo.Title.getValue() + "\"");
        buf.append(" sw_version=\"" + GlobalInfo.SoftwareVersion.getValue() + "\"");
        buf.append(" log_version=\"" + GlobalInfo.LogVersion.getValue() + "\"");
        buf.append(">" + System.lineSeparator());        
        
        return buf.toString().getBytes(this.getCharset());
    }
    
    @Override
    public byte[] getFooter() {
        final StringBuilder buf = new StringBuilder();
        buf.append("</" + ROOT_TAG + ">" + System.lineSeparator());
        return buf.toString().getBytes(this.getCharset());
    }
    
    @PluginFactory
    public static CustomXMLLayout createLayout(
            @PluginAttribute(value = "locationInfoLevel", defaultString = "DEBUG") final String locationInfoLevel,
            @PluginAttribute(value = "envelopeMessage", defaultBoolean = true) final boolean envelopeMessage,
            @PluginAttribute(value = "resolveTime", defaultBoolean = true) final boolean resolveTime,
            @PluginAttribute(value = "datePattern", defaultString = "yyyy-MM-dd") final String datePattern,
            @PluginAttribute(value = "timePattern", defaultString = "HH:mm:ss.SSS") final String timePattern,
            @PluginAttribute(value = "charset", defaultString = "UTF-8") final Charset charset)
    {
        return new CustomXMLLayout(locationInfoLevel, envelopeMessage, resolveTime, datePattern, timePattern, charset);
    }
}
