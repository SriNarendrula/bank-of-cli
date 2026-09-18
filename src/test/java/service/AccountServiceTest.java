package service;
import model.Account;

import java.math.BigDecimal;
import java.sql.SQLException;

import org.junit.jupiter.api.Test;
import repository.AccountRepository;

import static org.junit.jupiter.api.Assertions.*;
public class AccountServiceTest {
    private final AccountRepository repo = new AccountRepository();
    private final  AccountService service = new AccountService(repo);
    @Test
    void register_CheckInitialBalance() throws  SQLException{
        Account account= service.register("1234", new BigDecimal("100.00"));
        assertEquals(account.getBalance(),new BigDecimal("100.00"));
        assertNotNull(account.getCreatedAt());
    }

    @Test
    void register_CheckNegativeBalance() throws  SQLException{
        assertThrows(IllegalArgumentException.class, () ->
                service.register("1234", new BigDecimal("-100.00")));
    }

    @Test
    void login_SucceedsWithCorrectPin() throws SQLException {
        Account registered = service.register("1234", new BigDecimal("100.00"));

        Account loggedIn = service.login(registered.getAccountID(), "1234");

        assertEquals(registered.getAccountID(), loggedIn.getAccountID());
    }

    @Test
    void login_AccountNotFound(){
        assertThrows(IllegalArgumentException.class, () ->
                service.login(-1, "1234"));
    }

    @Test
    void login_ThrowsOnWrongPin() throws SQLException {
        Account registered = service.register("1234", new BigDecimal("100.00"));

        assertThrows(SecurityException.class, () ->
                service.login(registered.getAccountID(), "9999"));
    }
}
