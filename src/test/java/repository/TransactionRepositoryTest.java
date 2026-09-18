package repository;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;

import model.Account;

import model.Transaction;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TransactionRepositoryTest {
     private final TransactionRepository repo = new TransactionRepository();
     private final AccountRepository repo2 = new AccountRepository();

    @Test
    void recordTransaction_savesAndReturnsGeneratedFields() throws SQLException {
        Account account = repo2.createAccount("hashed123", new BigDecimal("100.00"));
        Account related = repo2.createAccount("hashed123", new BigDecimal("100.00"));

        Transaction transaction = repo.recordTransaction(
                account.getAccountID(), "deposit", new BigDecimal("100.00"), related.getAccountID());

        assertTrue(transaction.getTransactionID() > 0);
        assertEquals("deposit", transaction.getTransactionType());
        assertEquals(new BigDecimal("100.00"), transaction.getAmount());
        assertEquals(related.getAccountID(), transaction.getRelatedAccountId());
    }

    @Test
    void recordTransaction_rejectsNonexistentAccountId() {
        assertThrows(SQLException.class, () ->
                repo.recordTransaction(-1, "deposit", new BigDecimal("100.00"), null));
    }

    @Test
    void findByAccountId_returnsTransactionsForAccount() throws SQLException {
        Account account = repo2.createAccount("hashed123", new BigDecimal("100.00"));
        repo.recordTransaction(account.getAccountID(), "deposit", new BigDecimal("50.00"), null);
        repo.recordTransaction(account.getAccountID(), "withdraw", new BigDecimal("20.00"), null);

        List<Transaction> history = repo.findByAccountId(account.getAccountID());

        assertEquals(2, history.size());
    }

    @Test
    void findByAccountId_returnsEmptyListWhenNoTransactions() throws SQLException {
        Account account = repo2.createAccount("hashed123", new BigDecimal("100.00"));

        List<Transaction> history = repo.findByAccountId(account.getAccountID());

        assertTrue(history.isEmpty());
    }
 }
