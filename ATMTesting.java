package testing;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import group3.ATM;
import group3.Account;
import group3.AccountType;
import group3.Profile;

public class ATMTesting {

    @Test
    public void testIsConnectedFalseInitially() {
        // Use an invalid IP so the constructor fails immediately
        ATM atm = new ATM("256.256.256.256");
        assertFalse(atm.isConnected(), "ATM should not connect with an invalid IP");
    }

    @Test
    public void testSetAndGetCurrentProfile() {
        ATM atm = new ATM("256.256.256.256");
        Profile profile = new Profile("Mark Brown", "Mark", "MarkBrownIsTheBest", 1234567890, "Lombard St", "markbrown@gmail.com");

        atm.setCurrentProfile(profile);

        assertEquals(profile, atm.getCurrentProfile(), "Profile getter/setter should return the same object");
    }

    @Test
    public void testSetAndGetCurrentAccount() {
        ATM atm = new ATM("256.256.256.256");
        Account acc = new Account(1234, 1111, AccountType.checking, 500.0);

        atm.setCurrentAccount(acc);

        assertEquals(acc, atm.getCurrentAccount(), "Account getter/setter should return the same object");
    }

    @Test
    public void testSetCurrentAccountNull() {
        ATM atm = new ATM("256.256.256.256");

        atm.setCurrentAccount(null);

        assertNull(atm.getCurrentAccount(), "Account should be null after setting it to null");
    }

    @Test
    public void testSetCurrentProfileNull() {
        ATM atm = new ATM("256.256.256.256");

        atm.setCurrentProfile(null);

        assertNull(atm.getCurrentProfile(), "Profile should be null after setting it to null");
    }
}
