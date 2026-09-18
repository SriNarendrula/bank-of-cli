package service;

import model.Account;
import repository.AccountRepository;
import org.mindrot.jbcrypt.BCrypt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.math.BigDecimal;
import java.sql.SQLException;
public class AccountService {
    private static final Logger logger = LoggerFactory.getLogger(AccountService.class);

    private final AccountRepository accountRepository;

    public AccountService(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    public Account register(String pin, BigDecimal initialBalance) throws SQLException {
        if (pin == null || pin.length() < 4) {
            throw new IllegalArgumentException("PIN must be at least 4 digits");
        }
        if (initialBalance == null || initialBalance.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Initial balance cannot be negative");
        }

        String pinHash = BCrypt.hashpw(pin, BCrypt.gensalt());
        return accountRepository.createAccount(pinHash, initialBalance);
    }

    public Account login(int accountId, String pin) throws SQLException {
        Account account = accountRepository.findById(accountId);

        if (account == null) {
            throw new IllegalArgumentException("Account not found");
        }
        if (!BCrypt.checkpw(pin, account.getPinHash())) {
            logger.error("Failed login attempt for account {} — incorrect PIN", accountId);
            throw new SecurityException("Incorrect PIN");
        }
        logger.info("Account {} logged in successfully", accountId);
        return account;
    }
    public Account getAccount(int accountId) throws SQLException {
        Account account = accountRepository.findById(accountId);
        if (account == null) throw new IllegalArgumentException("Account not found");
        return account;
    }
}