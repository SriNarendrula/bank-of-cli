package model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public final class Transaction {
    private final int transactionID;
    private final int accountID;
    private final String transactionType;
    private final BigDecimal amount;
    private final Integer relatedAccountId;
    private final LocalDateTime createdAt;

    public Transaction(int transactionID, int accountID, String transactionType,
                       BigDecimal amount, Integer relatedAccountId, LocalDateTime createdAt) {
        this.transactionID = transactionID;
        this.accountID = accountID;
        this.transactionType = transactionType;
        this.amount = amount;
        this.relatedAccountId = relatedAccountId;
        this.createdAt = createdAt;
    }

    public int getTransactionID() {
        return transactionID;
    }

    public int getAccountID() {
        return accountID;
    }

    public String getTransactionType() {
        return transactionType;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public Integer getRelatedAccountId() {
        return relatedAccountId;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}