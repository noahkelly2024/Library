package com.pinetree; // Defines the package for this class

import java.io.*; // For file handling
import java.sql.Connection; // For database connection
import java.sql.PreparedStatement; // For executing SQL queries
import java.sql.SQLException; // For handling SQL exceptions

import javax.swing.JFileChooser; // For file selection dialog
import javax.swing.JOptionPane; // For displaying messages to the user

import com.pinetree.LibraryGUI.DatabaseHelper; // Custom helper class for database connections

/**
 * The MassUpload class provides functionality to upload data from CSV files
 * and update the database for books, users, and transactions.
 */
public class MassUpload {

    /**
     * Uploads a CSV file and updates the specified table in the database.
     * @param tableName The name of the table to update ("books", "users", or "transactions").
     */
    public void uploadCsv(String tableName) {
        JFileChooser fileChooser = new JFileChooser(); // Create a file chooser dialog
        int result = fileChooser.showOpenDialog(null); // Show the dialog and get the user's choice

        if (result == JFileChooser.APPROVE_OPTION) { // If the user selected a file
            File file = fileChooser.getSelectedFile(); // Get the selected file

            try (BufferedReader br = new BufferedReader(new FileReader(file))) { // Open the file for reading
                String line;
                boolean isFirstLine = true; // Flag to skip the header row

                try (Connection conn = DatabaseHelper.getConnection()) { // Get a database connection
                    while ((line = br.readLine()) != null) { // Read each line from the file
                        if (isFirstLine) {
                            isFirstLine = false; // Skip the header row
                            continue;
                        }

                        String[] values = line.split(","); // Split the line into values
                        if (tableName.equals("books")) {
                            updateBookFromCsv(conn, values); // Update the books table
                        } else if (tableName.equals("users")) {
                            updateUserFromCsv(conn, values); // Update the users table
                        } else if (tableName.equals("transactions")) {
                            updateTransactionFromCsv(conn, values); // Update the transactions table
                        }
                    }

                    JOptionPane.showMessageDialog(null, "CSV uploaded successfully."); // Notify the user
                }

            } catch (IOException e) { // Handle file reading errors
                JOptionPane.showMessageDialog(null, "Error reading CSV file: " + e.getMessage());
            } catch (SQLException e) { // Handle database errors
                JOptionPane.showMessageDialog(null, "Error updating database: " + e.getMessage());
            }
        }
    }

    /**
     * Updates the books table in the database using data from a CSV file.
     * @param conn The database connection.
     * @param values The values from the CSV file (ISBN, title, author, quantity).
     * @throws SQLException If a database error occurs.
     */
    public void updateBookFromCsv(Connection conn, String[] values) throws SQLException {
        if (values.length < 4) { // Ensure there are at least 4 columns
            throw new IllegalArgumentException("Invalid CSV format for books.");
        }

        String isbn = values[0].trim(); // Extract and trim the ISBN
        String title = values[1].trim(); // Extract and trim the title
        String author = values[2].trim(); // Extract and trim the author
        int quantity = Integer.parseInt(values[3].trim()); // Parse the quantity as an integer

        // SQL query to insert or update a book record
        try (PreparedStatement stmt = conn.prepareStatement(
                "INSERT INTO books (isbn, title, author, quantity) VALUES (?, ?, ?, ?) " +
                "ON CONFLICT(isbn) DO UPDATE SET title = excluded.title, author = excluded.author, quantity = excluded.quantity")) {
            stmt.setString(1, isbn); // Set the ISBN
            stmt.setString(2, title); // Set the title
            stmt.setString(3, author); // Set the author
            stmt.setInt(4, quantity); // Set the quantity
            stmt.executeUpdate(); // Execute the query
        }
    }

    /**
     * Updates the users table in the database using data from a CSV file.
     * @param conn The database connection.
     * @param values The values from the CSV file (User ID, name, borrowed ISBNs).
     * @throws SQLException If a database error occurs.
     */
    public void updateUserFromCsv(Connection conn, String[] values) throws SQLException {
        if (values.length < 2) { // Ensure there are at least 2 columns
            throw new IllegalArgumentException("Invalid CSV format for users.");
        }

        String userId = values[0].trim(); // Extract and trim the User ID
        String name = values[1].trim(); // Extract and trim the name

        // Combine all remaining columns into the borrowed_isbns field
        StringBuilder borrowedIsbnsBuilder = new StringBuilder();
        for (int i = 2; i < values.length; i++) {
            if (borrowedIsbnsBuilder.length() > 0) {
                borrowedIsbnsBuilder.append(","); // Add a comma separator
            }
            borrowedIsbnsBuilder.append(values[i].trim()); // Append the borrowed ISBN
        }
        String borrowedIsbns = borrowedIsbnsBuilder.toString(); // Convert to a single string

        // SQL query to insert or update a user record
        try (PreparedStatement stmt = conn.prepareStatement(
                "INSERT INTO users (user_id, name, borrowed_isbns) VALUES (?, ?, ?) " +
                "ON CONFLICT(user_id) DO UPDATE SET name = excluded.name, borrowed_isbns = excluded.borrowed_isbns")) {
            stmt.setString(1, userId); // Set the User ID
            stmt.setString(2, name); // Set the name
            stmt.setString(3, borrowedIsbns.isEmpty() ? null : borrowedIsbns); // Set the borrowed ISBNs
            stmt.executeUpdate(); // Execute the query
        }
    }

    /**
     * Updates the transactions table in the database using data from a CSV file.
     * @param conn The database connection.
     * @param values The values from the CSV file (Transaction ID, User ID, Book ISBN, Transaction Type, Transaction Date, Fine Amount).
     * @throws SQLException If a database error occurs.
     */
    public void updateTransactionFromCsv(Connection conn, String[] values) throws SQLException {
        if (values.length < 5) { // Ensure there are at least 5 columns
            throw new IllegalArgumentException("Invalid CSV format for transactions.");
        }

        String transactionId = values[0].trim(); // Extract and trim the Transaction ID
        String userId = values[1].trim(); // Extract and trim the User ID
        String isbn = values[2].trim(); // Extract and trim the Book ISBN
        String transactionType = values[3].trim(); // Extract and trim the Transaction Type
        String transactionDate = values[4].trim(); // Extract and trim the Transaction Date
        double fineAmount = values.length > 5 ? Double.parseDouble(values[5].trim()) : 0.0; // Parse the fine amount

        // SQL query to insert or update a transaction record
        try (PreparedStatement stmt = conn.prepareStatement(
                "INSERT INTO transactions (transaction_id, user_id, book_isbn, transaction_type, transaction_date, fine_amount) " +
                "VALUES (?, ?, ?, ?, ?, ?) " +
                "ON CONFLICT(transaction_id) DO UPDATE SET user_id = excluded.user_id, book_isbn = excluded.book_isbn, " +
                "transaction_type = excluded.transaction_type, transaction_date = excluded.transaction_date, fine_amount = excluded.fine_amount")) {
            stmt.setString(1, transactionId); // Set the Transaction ID
            stmt.setString(2, userId); // Set the User ID
            stmt.setString(3, isbn); // Set the Book ISBN
            stmt.setString(4, transactionType); // Set the Transaction Type
            stmt.setString(5, transactionDate); // Set the Transaction Date
            stmt.setDouble(6, fineAmount); // Set the Fine Amount
            stmt.executeUpdate(); // Execute the query
        }
    }
}