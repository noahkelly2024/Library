package com.pinetree;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class LibraryDatabase {

    private static final String DB_URL = "jdbc:sqlite:library.db";

    // Setup the database (creates tables if they don't exist)
    public static void setupDatabase() {
        try (Connection conn = DriverManager.getConnection(DB_URL);
             Statement stmt = conn.createStatement()) {

            // Create users table if not exists
            String createUsersTableSql = "CREATE TABLE IF NOT EXISTS users (" +
                                         "user_id TEXT PRIMARY KEY, " +
                                         "name TEXT NOT NULL, " +
                                         "borrowed_copy_ids TEXT)";
            stmt.executeUpdate(createUsersTableSql);
            System.out.println("Users table created or already exists.");

            // Create transactions table if not exists
            String createTransactionsTableSql = "CREATE TABLE IF NOT EXISTS transactions (" +
                                                 "transaction_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                                                 "user_id TEXT NOT NULL, " +
                                                 "book_isbn TEXT NOT NULL, " +
                                                 "transaction_type TEXT NOT NULL, " +
                                                 "transaction_date TEXT NOT NULL, " +
                                                 "FOREIGN KEY(user_id) REFERENCES users(user_id), " +
                                                 "FOREIGN KEY(book_isbn) REFERENCES books(isbn))";
            stmt.executeUpdate(createTransactionsTableSql);
            System.out.println("Transactions table created or already exists.");

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Add a user to the database
    public static void addUser(User user) {
        String sql = "INSERT INTO users (user_id, name) VALUES (?, ?)";

        try (Connection conn = DriverManager.getConnection(DB_URL);
            PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, user.getID());
            stmt.setString(2, user.getName());
            stmt.executeUpdate();

            System.out.println("User added: " + user.getName());
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Remove a user from the database
    public static void removeUser(String userId) {
        String sql = "DELETE FROM users WHERE user_id = ?";

        try (Connection conn = DriverManager.getConnection(DB_URL);
            PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, userId);
            stmt.executeUpdate();

            System.out.println("User removed: " + userId);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Update a user's information (e.g., borrowed books)
    public static void updateUser(User user) {
        String sql = "UPDATE users SET borrowed_copy_ids = ? WHERE user_id = ?";

        try (Connection conn = DriverManager.getConnection(DB_URL);
            PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, String.join(",", user.getBorrowedCopyIds()));
            stmt.setString(2, user.getID());
            stmt.executeUpdate();

            System.out.println("User updated: " + user.getID());
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // List all users from the database
    public static List<User> listUsers() {
        List<User> users = new ArrayList<>();
        String sql = "SELECT * FROM users";

        try (Connection conn = DriverManager.getConnection(DB_URL);
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                String userId = rs.getString("user_id");
                String name = rs.getString("name");

                User user = new User(name);
                user.ID = userId;
                // You could add borrowed copy ids here if needed
                users.add(user);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return users;
    }


    // Method to add a transaction
    public static void addTransaction(String userId, String bookIsbn, String type, LocalDate date) {
        String sql = "INSERT INTO transactions (user_id, book_isbn, transaction_type, transaction_date) VALUES (?, ?, ?, ?)";

        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, userId);
            stmt.setString(2, bookIsbn);
            stmt.setString(3, type);
            stmt.setString(4, date.toString());
            stmt.executeUpdate();
            System.out.println("Transaction added: " + type + " " + bookIsbn + " for user " + userId);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Get all transactions for a user
    public static void displayUserTransactions(String userId) {
        String sql = "SELECT * FROM transactions WHERE user_id = ?";

        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, userId);
            ResultSet rs = stmt.executeQuery();

            boolean found = false;
            while (rs.next()) {
                found = true;
                String bookIsbn = rs.getString("book_isbn");
                String transactionType = rs.getString("transaction_type");
                String date = rs.getString("transaction_date");

                Book book = Book.findBookByIsbn(bookIsbn);
                String bookTitle = book != null ? book.getTitle() : "Unknown book";
                System.out.println(date + ": " + transactionType + " " + bookTitle);
            }

            if (!found) {
                System.out.println("No transactions found for this user.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Get all transactions in the system
    public static void getAllTransactions() {
        String sql = "SELECT * FROM transactions";

        try (Connection conn = DriverManager.getConnection(DB_URL);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                String userId = rs.getString("user_id");
                String bookIsbn = rs.getString("book_isbn");
                String transactionType = rs.getString("transaction_type");
                String date = rs.getString("transaction_date");

                System.out.println("Transaction: " + date + " - " + transactionType + " - " + bookIsbn + " - User: " + userId);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
