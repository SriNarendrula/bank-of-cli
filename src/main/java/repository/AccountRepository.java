package repository;

import model.Account;

import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDateTime;

public class AccountRepository {

    public Account createAccount(String pinHash, BigDecimal initialBalance) throws SQLException {
        String sql = "INSERT INTO accounts (pin_hash, balance) VALUES (?, ?) " +
                "RETURNING account_id, created_at";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, pinHash);
            stmt.setBigDecimal(2, initialBalance);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    int accountId = rs.getInt("account_id");
                    LocalDateTime createdAt = rs.getTimestamp("created_at").toLocalDateTime();
                    return new Account(accountId, pinHash, initialBalance, createdAt);
                }
            }
        }
        throw new SQLException("Insert did not return a generated account_id");
    }

    public Account findById(Connection conn, int accountId) throws SQLException {
        String sql = "SELECT * FROM accounts WHERE account_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, accountId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    BigDecimal balance = rs.getBigDecimal("balance");
                    String pinHash = rs.getString("pin_hash");
                    LocalDateTime createdAt = rs.getTimestamp("created_at").toLocalDateTime();
                    return new Account(accountId, pinHash, balance, createdAt);
                }
                return null;
            }
        }
    }

    public Account findById(int accountId) throws SQLException {
        try (Connection conn = DatabaseConnection.getConnection()) {
            return findById(conn, accountId);
        }
    }

    public void updateBalance(Connection conn, int accountId, BigDecimal newBalance) throws SQLException {
        String sql = "UPDATE accounts SET balance = ? WHERE account_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setBigDecimal(1, newBalance);
            stmt.setInt(2, accountId);
            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected == 0) {
                throw new SQLException("No account found with ID " + accountId);
            }
        }
    }

    public void updateBalance(int accountId, BigDecimal newBalance) throws SQLException {
        try (Connection conn = DatabaseConnection.getConnection()) {
            updateBalance(conn, accountId, newBalance);
        }
    }
}