//Student Name: Lindsay Torres
//Student ID: 110130322
/**
 * TransactionSQL.java
 * -> This class represents the SQL layer for managing transactions in the finance tracker application.
 *  It handles the connection to the SQLite database, creating the transactions table, adding transactions, and retrieving all transactions.
 */

import java.sql.*;
import java.util.ArrayList;

public class TransactionSQL {
    private Connection conn;

    public TransactionSQL() {
        try {
            conn = DriverManager.getConnection("jdbc:sqlite:finance.db");
            createTable();
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    public void createTable() {
        String sql = "CREATE TABLE IF NOT EXISTS transactions (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "type TEXT NOT NULL," + 
                "amount REAL NOT NULL," +
                "date TEXT NOT NULL," +
                "category TEXT NOT NULL" +
                ");";
        try (Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void addTransaction(Transaction transaction) {
    String sql = "INSERT INTO transactions (type, amount, date, category) VALUES (?, ?, ?, ?)";
    try {
        PreparedStatement stmt = conn.prepareStatement(sql);
        stmt.setString(1, transaction.getType());
        stmt.setDouble(2, transaction.getAmount());
        stmt.setString(3, transaction.getDate());
        stmt.setString(4, transaction.getCategory());
        stmt.executeUpdate();
    } catch (SQLException e) {
        e.printStackTrace();
    }
}

public ArrayList<Transaction> getAllTransactions() {
    ArrayList<Transaction> transactions = new ArrayList<>();
    String sql = "SELECT * FROM transactions";
    try {
        ResultSet rs = conn.createStatement().executeQuery(sql);
        while (rs.next()) {
            String type = rs.getString("type");
            double amount      = rs.getDouble("amount");
            String date     = rs.getString("date");
            String category    = rs.getString("category");

            if (type.equals("expense")) {
                transactions.add(new Expense(type, amount, date, category));
            } else {
                transactions.add(new Income(type, amount, date, category));
            }
        }
    } catch (SQLException e) {
        e.printStackTrace();
    }
    return transactions;
}

    public void close() {
        try {
            if (conn != null)
                conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}
