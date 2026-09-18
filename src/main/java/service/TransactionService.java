package service;

import model.Account;
import model.Transaction;
import repository.AccountRepository;
import repository.TransactionRepository;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TransactionService {

    private static final Logger logger = LoggerFactory.getLogger(TransactionService.class);

    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;

    public TransactionService(AccountRepository accountRepository, TransactionRepository transactionRepository) {
        this.accountRepository = accountRepository;
        this.transactionRepository = transactionRepository;
    }

    public Account deposit(int accountId, BigDecimal amount) throws SQLException {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Deposit amount must be positive");
        }
        Account account = accountRepository.findById(accountId);
        if (account == null) throw new IllegalArgumentException("Account not found");

        accountRepository.updateBalance(accountId, account.getBalance().add(amount));
        transactionRepository.recordTransaction(accountId, "DEPOSIT", amount, null);
        logger.info("Deposit of {} succeeded for account {}", amount, accountId);
        return accountRepository.findById(accountId);
    }

    public Account withdraw(int accountId, BigDecimal amount) throws SQLException {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Withdrawal amount must be positive");
        }
        Account account = accountRepository.findById(accountId);
        if (account == null) throw new IllegalArgumentException("Account not found");

        if (account.getBalance().compareTo(amount) < 0) {
            logger.error("Withdrawal of {} rejected for account {} — insufficient funds", amount, accountId);
            throw new IllegalStateException("Insufficient funds");
        }

        accountRepository.updateBalance(accountId, account.getBalance().subtract(amount));
        transactionRepository.recordTransaction(accountId, "WITHDRAW", amount, null);
        logger.info("Withdrawal of {} succeeded for account {}", amount, accountId);
        return accountRepository.findById(accountId);
    }

    public void transfer(int fromAccountId, int toAccountId, BigDecimal amount) throws SQLException {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Transfer amount must be positive");
        }
        if (fromAccountId == toAccountId) {
            throw new IllegalArgumentException("Cannot transfer to the same account");
        }

        try (Connection conn = repository.DatabaseConnection.getConnection()) {
            conn.setAutoCommit(false);
            try {
                Account from = accountRepository.findById(conn, fromAccountId);
                Account to = accountRepository.findById(conn, toAccountId);
                if (from == null || to == null) throw new IllegalArgumentException("Account not found");
                if (from.getBalance().compareTo(amount) < 0) {
                    logger.error("Transfer of {} from account {} rejected — insufficient funds", amount, fromAccountId);
                    throw new IllegalStateException("Insufficient funds");
                }

                accountRepository.updateBalance(conn, fromAccountId, from.getBalance().subtract(amount));
                accountRepository.updateBalance(conn, toAccountId, to.getBalance().add(amount));
                transactionRepository.recordTransaction(conn, fromAccountId, "TRANSFER_OUT", amount, toAccountId);
                transactionRepository.recordTransaction(conn, toAccountId, "TRANSFER_IN", amount, fromAccountId);

                conn.commit();
                logger.info("Transfer of {} from account {} to {} succeeded", amount, fromAccountId, toAccountId);
            } catch (Exception e) {
                conn.rollback();
                logger.error("Transfer from {} to {} failed and was rolled back: {}", fromAccountId, toAccountId, e.getMessage());
                throw e;
            }
        }
    }

    public List<Transaction> getHistory(int accountId) throws SQLException {
        return transactionRepository.findByAccountId(accountId);
    }
}