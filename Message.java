package group3;
import java.io.Serializable;

import homeworks.HW5.HW5_status;
import homeworks.HW5.HW5_type;

public class Message implements Serializable {
	private static int count = 0;
	private final int id;
	private final MessageType type; 	  // login, text, logout, undefined
    private final MessageStatus status;  // success, error, request, lowercase, uppercase
    private final String text;
    
    // messages with no parameters
    public Message() {
    	this.id = ++count;
        this.type = MessageType.undefined;
        this.status = MessageStatus.undefined;
        this.text = "undefined"; 
    }
    
    public Message(MessageType type, MessageStatus status, String text) {
    	this.type = type;
    	this.status = status;
    	this.text = text;
    	this.id = ++count;
    }
}
