package testing;

import group3.*;

import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;

public class ProfileTesting {

    private Profile profile;
    private File testFile;

    @Before
    public void setup() throws Exception {
    	
        profile = new Profile("Mark Brown", "mbrown", "secure456", 7778888, "456 Example Rd", "mark@mail.com");

        testFile = new File("test_accounts.txt");
        PrintWriter out = new PrintWriter(new FileWriter(testFile));
        out.println("1234,1111,checking,500.0");
        out.println("4567,2222,saving,900.0");
        out.close();

        // Replace Profile.accountFile reflection-style
        var field = Profile.class.getDeclaredField("accountFile");
        field.setAccessible(true);
        field.set(null, testFile);
    }

    @Test
    public void testConstructorValues() {
        assertEquals("Mark Brown", profile.getName());
        assertEquals("mbrown", profile.getUsername());
        assertEquals("secure456", profile.getPassword());
        assertEquals(7778888, profile.getPhone());
        assertEquals("456 Example Rd", profile.getAddress());
        assertEquals("mark@mail.com", profile.getEmail());
        assertEquals(0, profile.getCreditScore());
        assertEquals(0, profile.getAccounts().length);
    }

    @Test
    public void testSetters() {
        profile.setName("New Name");
        profile.setUsername("newuser");
        profile.setPassword("newpass");
        profile.setPhone(1112222);
        profile.setAddress("New Address");
        profile.setEmail("new@mail.com");
        profile.setCreditScore(720);

        assertEquals("New Name", profile.getName());
        assertEquals("newuser", profile.getUsername());
        assertEquals("newpass", profile.getPassword());
        assertEquals(1112222, profile.getPhone());
        assertEquals("New Address", profile.getAddress());
        assertEquals("new@mail.com", profile.getEmail());
        assertEquals(720, profile.getCreditScore());
    }

    @Test
    public void testLoadAccounts() {
        int[] nums = {1234, 4567};
        profile.loadAccounts(nums);
        Account[] loaded = profile.getAccounts();

        assertEquals(2, loaded.length);
        assertNotNull(profile.findAccount(1234));
        assertNotNull(profile.findAccount(4567));
    }

    @Test
    public void testFindAccountByNumber() {
        int[] nums = {1234};
        profile.loadAccounts(nums);

        Account acc = profile.findAccount(1234);
        assertNotNull(acc);
        assertEquals(1234, acc.getNum());
    }

    @Test
    public void testAddAccount() {
        profile.addAccount(AccountType.checking, 250.0);

        Account[] accs = profile.getAccounts();
        assertEquals(1, accs.length);

        Account added = accs[0];
        assertEquals(250.0, added.getBalance(), 0.01);
        assertEquals(AccountType.checking, added.getType());
    }

    @Test
    public void testRemoveAccount() {
        int[] nums = {1234};
        profile.loadAccounts(nums);

        Account acc = profile.findAccount(1234);
        assertNotNull(acc);

        profile.removeAccount(acc);

        assertNull(profile.findAccount(1234));
    }
}
