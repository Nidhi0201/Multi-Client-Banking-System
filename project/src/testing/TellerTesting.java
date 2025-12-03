package testing;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import group3.Profile;
import group3.Teller;

public class TellerTesting {

    @Test
    public void testIsConnectedFalseInitially() {
        // Use an invalid IP so the constructor fails immediately
        Teller teller = new Teller("256.256.256.256");
        assertFalse(teller.isConnected(), "Teller should not connect with an invalid IP");
    }

    @Test
    public void testSetAndGetCurrentProfile() {
        Teller teller = new Teller("256.256.256.256");
        Profile profile = new Profile("Mark Brown", "mbrown", "secure456", 7778888, "456 Example Rd", "mark@mail.com");

        teller.setCurrentProfile(profile);

        assertEquals(profile, teller.getCurrentProfile(), "Profile getter/setter should return the same object");
    }

    @Test
    public void testSetCurrentProfileNull() {
        Teller teller = new Teller("256.256.256.256");

        teller.setCurrentProfile(null);

        assertNull(teller.getCurrentProfile(), "Profile should be null after setting it to null");
    }
}
