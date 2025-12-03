package testing;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import group3.Account;
import group3.AccountType;

public class AccountTesting {

    private Account checkingAccount;
    private Account locAccount;

    @BeforeEach
    public void setup() {
        checkingAccount = new Account(12345678, 1234, AccountType.checking, 500);
        locAccount = new Account(12312312, 4329, AccountType.lineOfCredit, 1000);
    }

    @Test
    public void testDepositChecking() {
        checkingAccount.deposit(200);
        assertEquals(700, checkingAccount.getBalance(), 0.01);
    }

    @Test
    public void testWithdrawCheckingSufficientFunds() {
        checkingAccount.withdraw(300);
        assertEquals(200, checkingAccount.getBalance(), 0.01); //Account class withdraw method still in development
    }

    @Test
    public void testWithdrawCheckingInsufficientFunds() {
        checkingAccount.withdraw(600);
        assertEquals(500, checkingAccount.getBalance(), 0.01);
    }

    @Test
    public void testDepositLOCBalanceEqualsInitial() {
        locAccount.deposit(100);
        assertEquals(1000, locAccount.getBalance(), 0.01);
        //Initial Balance is 1000 so can't deposit more for LOC accounts
    }

    @Test
    public void testDepositLOCBalanceBelowInitial() {
        locAccount.setBalance(800); 
        locAccount.deposit(150); //The deposit should work in this case since 950 is not greater than the initial balance of 1000
        assertEquals(950, locAccount.getBalance(), 0.01);
    }

    @Test
    public void testGettersAndSetters() {
        checkingAccount.setAccountNumber(3001);
        checkingAccount.setPin(4321);
        checkingAccount.setBalance(800.0);

        assertEquals(3001, checkingAccount.getNum());
        assertEquals(4321, checkingAccount.getPin());
        assertEquals(800, checkingAccount.getBalance(), 0.01);
        assertEquals(AccountType.checking, checkingAccount.getType());
    }

    @Test
    public void testGetLog() {
        String log = checkingAccount.getLog();
        assertNotNull(log);
        assertEquals("", log);
    }
}
