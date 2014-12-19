package com.breakmc.eroc.automessage;

public class Message
{
    String message;
    
    public Message(final String message) {
        super();
        this.message = message;
    }
    
    public String getMessage() {
        return this.message;
    }
    
    public void setMessage(final String message) {
        this.message = message;
    }
}
