package service;
import model.Account;

import java.math.BigDecimal;
import java.sql.SQLException;

import org.junit.jupiter.api.Test;
import repository.AccountRepository;
import repository.TransactionRepository;

import static org.junit.jupiter.api.Assertions.*;

public class TransactionServiceTest {
    private final AccountRepository repo = new AccountRepository();
    private final TransactionRepository repo2 = new TransactionRepository();
    private final  TransactionService service = new TransactionService(repo,repo2);

    @Test
    void deposit_CheckIfDepositIsMade() throws SQLException{
        Account account = repo.createAccount("hashed123", new BigDecimal("100.00"));

        Account account2= service.deposit(account.getAccountID(), new BigDecimal("100.00"));

        assertEquals(account2.getBalance(),new BigDecimal("200.00"));
    }

    @Test
    void deposit_HowItHandlesNullID(){
        assertThrows(IllegalArgumentException.class, () ->
                service.deposit(-1, new BigDecimal("200.00")));
    }

    @Test
    void deposit_HowItHandlesNegatives() throws SQLException{
        Account account = repo.createAccount("hashed123", new BigDecimal("100.00"));

        assertThrows(IllegalArgumentException.class, () ->
                service.deposit(account.getAccountID(), new BigDecimal("-200.00")));
    }

    @Test
    void withdraw_DoesItWithdraw() throws SQLException{
        Account account = repo.createAccount("hashed123", new BigDecimal("100.00"));

        Account account2= service.withdraw(account.getAccountID(), new BigDecimal("100.00"));

        assertEquals(account2.getBalance(),new BigDecimal("0.00"));
    }

    @Test
    void withdraw_HowItHandlesNullID(){
        assertThrows(IllegalArgumentException.class, () ->
                service.withdraw(-1, new BigDecimal("200.00")));
    }

    @Test
    void withdraw_HowItHandlesOverdraft() throws SQLException{
        Account account = repo.createAccount("hashed123", new BigDecimal("100.00"));

        assertThrows(IllegalStateException.class, () ->
                service.withdraw(account.getAccountID(), new BigDecimal("200.00")));
    }

    @Test
    void transfer_MovesFundsBetweenAccounts() throws SQLException {
        Account from = repo.createAccount("1234", new BigDecimal("100.00"));
        Account to = repo.createAccount("1234", new BigDecimal("50.00"));

        service.transfer(from.getAccountID(), to.getAccountID(), new BigDecimal("30.00"));

        assertEquals(new BigDecimal("70.00"), repo.findById(from.getAccountID()).getBalance());
        assertEquals(new BigDecimal("80.00"), repo.findById(to.getAccountID()).getBalance());
    }

    @Test
    void transfer_RollsBackBothBalancesOnInsufficientFunds() throws SQLException {
        Account from = repo.createAccount("1234", new BigDecimal("10.00"));
        Account to = repo.createAccount("1234", new BigDecimal("50.00"));

        assertThrows(IllegalStateException.class, () ->
                service.transfer(from.getAccountID(), to.getAccountID(), new BigDecimal("100.00")));

        assertEquals(new BigDecimal("10.00"), repo.findById(from.getAccountID()).getBalance());
        assertEquals(new BigDecimal("50.00"), repo.findById(to.getAccountID()).getBalance());
    }
}
