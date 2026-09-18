package repository;

import model.Transaction;

import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class TransactionRepository {

    public Transaction recordTransaction(Connection conn, int accountId, String transactionType,
                                         BigDecimal amount, Integer relatedAccountId) throws SQLException {
        String sql = "INSERT INTO transactions (account_id, transaction_type, amount, related_account_id) " +
                "VALUES (?, ?, ?, ?) RETURNING transaction_id, created_at";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, accountId);
            stmt.setString(2, transactionType);
            stmt.setBigDecimal(3, amount);
            if (relatedAccountId == null) stmt.setNull(4, Types.INTEGER);
            else stmt.setInt(4, relatedAccountId);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    int transactionId = rs.getInt("transaction_id");
                    LocalDateTime createdAt = rs.getTimestamp("created_at").toLocalDateTime();
                    return new Transaction(transactionId, accountId, transactionType,
                            amount, relatedAccountId, createdAt);
                }
            }
        }
        throw new SQLException("Insert did not return a generated transaction_id");
    }

    public Transaction recordTransaction(int accountId, String transactionType,
                                         BigDecimal amount, Integer relatedAccountId) throws SQLException {
        try (Connection conn = DatabaseConnection.getConnection()) {
            return recordTransaction(conn, accountId, transactionType, amount, relatedAccountId);
        }
    }

    public List<Transaction> findByAccountId(int accountId) throws SQLException {
        String sql = "SELECT * FROM transactions WHERE account_id = ? ORDER BY created_at DESC";
        List<Transaction> results = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, accountId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    int transactionId = rs.getInt("transaction_id");
                    String type = rs.getString("transaction_type");
                    BigDecimal amount = rs.getBigDecimal("amount");
                    int relatedRaw = rs.getInt("related_account_id");
                    Integer relatedAccountId = rs.wasNull() ? null : relatedRaw;
                    LocalDateTime createdAt = rs.getTimestamp("created_at").toLocalDateTime();
                    results.add(new Transaction(transactionId, accountId, type,
                            amount, relatedAccountId, createdAt));
                }
            }
        }
        return results;
    }
}