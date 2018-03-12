package com.secunet.ipsmall.ecard;

public class Message {
    
    protected MessageHandler m_handler;
    
    public Message(MessageHandler handler) {
        m_handler = handler;
    }
    
    public MessageHandler getMessageHandler() {
        return m_handler;
    }
    
}
