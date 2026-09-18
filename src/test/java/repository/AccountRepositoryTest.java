package repository;
import model.Account;

import java.math.BigDecimal;
import java.sql.SQLException;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class AccountRepositoryTest {
    private final AccountRepository repo = new AccountRepository();

    @Test
    void createAccount_savesAndReturnsGeneratedFields() throws SQLException {
        Account account = repo.createAccount("hashed123", new BigDecimal("100.00"));

        assertTrue(account.getAccountID() > 0);
        assertEquals(new BigDecimal("100.00"), account.getBalance());
        assertNotNull(account.getCreatedAt());
    }

    @Test
    void createAccount_rejectsNullPinHash() {
        assertThrows(SQLException.class, () ->
                repo.createAccount(null, new BigDecimal("100.00")));
    }

    @Test
    void findById_returnsAccountWhenItExists() throws SQLException {
        Account created = repo.createAccount("hashed123", new BigDecimal("50.00"));

        Account found = repo.findById(created.getAccountID());

        assertNotNull(found);
        assertEquals(created.getAccountID(), found.getAccountID());
        assertEquals(new BigDecimal("50.00"), found.getBalance());
    }

    @Test
    void findById_returnsNullWhenAccountDoesNotExist() throws SQLException {
        Account found = repo.findById(-1);

        assertNull(found);
    }

    @Test
    void updateBalance_returnsUpdatedBalance() throws SQLException {
        Account created = repo.createAccount("hashed123", new BigDecimal("50.00"));

        repo.updateBalance(created.getAccountID(), new BigDecimal("75.00"));
        Account updated = repo.findById(created.getAccountID());

        assertEquals(new BigDecimal("75.00"), updated.getBalance());
    }

    @Test
    void updateBalance_throwsWhenAccountDoesNotExist() {
        assertThrows(SQLException.class, () ->
                repo.updateBalance(-1, new BigDecimal("100.00")));
    }
}