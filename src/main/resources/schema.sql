CREATE DATABASE bank;

CREATE TABLE accounts(
    account_id SERIAL PRIMARY KEY,
    pin_hash VARCHAR(255),
    balance NUMERIC(12,2),
    created_at TIMESTAMP DEFAULT NOW()
);

CREATE TABLE transactions(
    transaction_id SERIAL PRIMARY KEY,
    account_id INTEGER REFERENCES accounts(account_id),
    transaction_type VARCHAR(20),
    amount NUMERIC(12,2),
    related_account_id INTEGER NULL,
    created_at TIMESTAMP DEFAULT NOW()
);