package testing;

import static org.junit.Assert.*;
import org.junit.Test;

import group3.*;

public class MessageTesting {

    @Test
    public void testDefaultConstructor() {
    	
        Message m = new Message();

        assertEquals(MessageType.undefined, m.getType());
        assertEquals(MessageStatus.undefined, m.getStatus());
        assertEquals("undefined", m.getText());
        assertEquals("", m.getNum());
        assertEquals(Application.undefined, m.getSender());
        
    }

    @Test
    public void testCustomConstructorType() {
    	
        Message m = new Message(MessageStatus.confirmation, MessageType.deposit, Application.ATM, "123", "OK");
        assertEquals(MessageType.deposit, m.getType());
        
    }
    @Test
    public void testIDIncrements() {
    	
        Message m1 = new Message();
        Message m2 = new Message();
        
        assertTrue(m2.getID() > m1.getID());
        
    }

    @Test
    public void testCustomConstructorStatus() {
    	
        Message m = new Message(MessageStatus.request, MessageType.withdrawal, Application.ATM, "20", "withdraw");
        assertEquals(MessageStatus.request, m.getStatus());
        
    }
    

    @Test
    public void testCustomConstructorText() {
    	
        Message m = new Message(MessageStatus.confirmation, MessageType.customerLogin, Application.teller, "42", "login success");
        assertEquals("login success", m.getText());
        
    }

    @Test
    public void testCustomConstructorNumber() {
    	
        Message m = new Message(MessageStatus.denial, MessageType.logout, Application.ATM, "452", "denied");
        assertEquals("452", m.getNum());
        
    }
}

    
