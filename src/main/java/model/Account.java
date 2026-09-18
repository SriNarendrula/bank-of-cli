package model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class Account {
    private final int accountID;
    private final String pinHash;
    private final BigDecimal balance;
    private final LocalDateTime createdAt;

    public Account(int aID,String pHash, BigDecimal bal, LocalDateTime cAt){
        this.accountID=aID ;
        this.pinHash=pHash;
        this.balance=bal;
        this.createdAt=cAt;
    }

    public int getAccountID() {
        return accountID;
    }

    public String getPinHash() {
        return pinHash;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}
