package group3;
import java.io.Serializable;

import homeworks.HW5.HW5_status;
import homeworks.HW5.HW5_type;

public class Message implements Serializable {
	private static int count = 0;
	private final int id;
	private final MessageType type;
    private final MessageStatus status;
    private final String text;
    private final String number; // this is a String variable so null can pass, the server will use parseInt
    private final Application sender;
    
    // messages with no parameters
    public Message() {
    	this.id = ++count;
        this.type = MessageType.undefined;
        this.status = MessageStatus.undefined;
        this.text = "undefined";
        this.number = "";
        this.sender = Application.undefined;
    }
    
    public Message(MessageStatus status, MessageType type, Application app, String num, String text) {
    	this.type = type;
    	this.status = status;
    	this.text = text;
    	this.id = ++count;
    	this.number = num;
    	this.sender = app;
    }

    // getters
    public int getID() {
    	return id;
    }
    
    public MessageType getType() {
    	
    	return type;
    }
    
    public MessageStatus getStatus() {
    	
    	return status;
    }
    
    public String getText() {
    	
    	return text;
    }
    
    public String getNum() {
    	return number;
    }
    
    public Application getSender() {
    	return sender;
    	
    }
}
    

