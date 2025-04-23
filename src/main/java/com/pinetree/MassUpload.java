package com.pinetree;

import java.io.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

import com.pinetree.LibraryGUI.DatabaseHelper;

public class MassUpload {

    public void uploadCsv(String tableName) {
        JFileChooser fileChooser = new JFileChooser();
        int result = fileChooser.showOpenDialog(null); // Use null instead of 'this' since this is not a GUI class

        if (result == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();

            try (BufferedReader br = new BufferedReader(new FileReader(file))) {
                String line;
                boolean isFirstLine = true;

                try (Connection conn = DatabaseHelper.getConnection()) {
                    while ((line = br.readLine()) != null) {
                        if (isFirstLine) {
                            isFirstLine = false; // Skip the header row
                            continue;
                        }

                        String[] values = line.split(",");
                        if (tableName.equals("books")) {
                            updateBookFromCsv(conn, values);
                        } else if (tableName.equals("users")) {
                            updateUserFromCsv(conn, values);
                        } else if (tableName.equals("transactions")) {
                            updateTransactionFromCsv(conn, values);
                        }
                    }

                    JOptionPane.showMessageDialog(null, "CSV uploaded successfully.");
                }

            } catch (IOException e) {
                JOptionPane.showMessageDialog(null, "Error reading CSV file: " + e.getMessage());
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(null, "Error updating database: " + e.getMessage());
            }
        }
    }

    public void updateBookFromCsv(Connection conn, String[] values) throws SQLException {
        if (values.length < 4) {
            throw new IllegalArgumentException("Invalid CSV format for books.");
        }

        String isbn = values[0].trim();
        String title = values[1].trim();
        String author = values[2].trim();
        int quantity = Integer.parseInt(values[3].trim());

        try (PreparedStatement stmt = conn.prepareStatement(
                "INSERT INTO books (isbn, title, author, quantity) VALUES (?, ?, ?, ?) " +
                "ON CONFLICT(isbn) DO UPDATE SET title = excluded.title, author = excluded.author, quantity = excluded.quantity")) {
            stmt.setString(1, isbn);
            stmt.setString(2, title);
            stmt.setString(3, author);
            stmt.setInt(4, quantity);
            stmt.executeUpdate();
        }
    }

    public void updateUserFromCsv(Connection conn, String[] values) throws SQLException {
        if (values.length < 2) {
            throw new IllegalArgumentException("Invalid CSV format for users.");
        }

        String userId = values[0].trim();
        String name = values[1].trim();

        // Combine all remaining columns into the borrowed_isbns field
        StringBuilder borrowedIsbnsBuilder = new StringBuilder();
        for (int i = 2; i < values.length; i++) {
            if (borrowedIsbnsBuilder.length() > 0) {
                borrowedIsbnsBuilder.append(",");
            }
            borrowedIsbnsBuilder.append(values[i].trim());
        }
        String borrowedIsbns = borrowedIsbnsBuilder.toString();

        try (PreparedStatement stmt = conn.prepareStatement(
                "INSERT INTO users (user_id, name, borrowed_isbns) VALUES (?, ?, ?) " +
                "ON CONFLICT(user_id) DO UPDATE SET name = excluded.name, borrowed_isbns = excluded.borrowed_isbns")) {
            stmt.setString(1, userId);
            stmt.setString(2, name);
            stmt.setString(3, borrowedIsbns.isEmpty() ? null : borrowedIsbns);
            stmt.executeUpdate();
        }
    }

    public void updateTransactionFromCsv(Connection conn, String[] values) throws SQLException {
        if (values.length < 5) { // Ensure there are at least 5 columns
            throw new IllegalArgumentException("Invalid CSV format for transactions.");
        }

        String transactionId = values[0].trim();
        String userId = values[1].trim();
        String isbn = values[2].trim();
        String transactionType = values[3].trim();
        String transactionDate = values[4].trim(); // Keep as a string for now
        double fineAmount = values.length > 5 ? Double.parseDouble(values[5].trim()) : 0.0;

        try (PreparedStatement stmt = conn.prepareStatement(
                "INSERT INTO transactions (transaction_id, user_id, book_isbn, transaction_type, transaction_date, fine_amount) " +
                "VALUES (?, ?, ?, ?, ?, ?) " +
                "ON CONFLICT(transaction_id) DO UPDATE SET user_id = excluded.user_id, book_isbn = excluded.book_isbn, " +
                "transaction_type = excluded.transaction_type, transaction_date = excluded.transaction_date, fine_amount = excluded.fine_amount")) {
            stmt.setString(1, transactionId);
            stmt.setString(2, userId);
            stmt.setString(3, isbn);
            stmt.setString(4, transactionType);
            stmt.setString(5, transactionDate); // Use the date string directly
            stmt.setDouble(6, fineAmount);
            stmt.executeUpdate();
        }
    }
}