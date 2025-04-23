package com.pinetree;

import java.sql.*;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

public class LibraryDatabase {

    static final String DB_URL = "jdbc:sqlite:library.db";
    private static final int LOAN_PERIOD_DAYS = 14;
    private static final double DAILY_FINE = 1.0;

    // Setup the database (creates tables if they don't exist)
    public static void setupDatabase() {
        try (Connection conn = DriverManager.getConnection(DB_URL);
            Statement stmt = conn.createStatement()) {

            // Create books table if not exists
            String createBooksTableSql = "CREATE TABLE IF NOT EXISTS books (" +
                                        "isbn TEXT PRIMARY KEY, " +
                                        "title TEXT NOT NULL, " +
                                        "author TEXT NOT NULL, " +
                                        "quantity INTEGER NOT NULL DEFAULT 0)";
            stmt.executeUpdate(createBooksTableSql);
            System.out.println("Books table created or already exists.");

            // Create users table if not exists
            String createUsersTableSql = "CREATE TABLE IF NOT EXISTS users (" +
                                        "user_id TEXT PRIMARY KEY, " +
                                        "name TEXT NOT NULL, " +
                                        "borrowed_isbns TEXT)";
            stmt.executeUpdate(createUsersTableSql);
            System.out.println("Users table created or already exists.");

            // Create transactions table if not exists
            String createTransactionsTableSql = "CREATE TABLE IF NOT EXISTS transactions (" +
                                                "transaction_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                                                "user_id TEXT NOT NULL, " +
                                                "book_isbn TEXT NOT NULL, " +
                                                "transaction_type TEXT NOT NULL, " +
                                                "transaction_date TEXT NOT NULL, " +
                                                "fine_amount REAL DEFAULT 0," +
                                                "FOREIGN KEY(user_id) REFERENCES users(user_id), " +
                                                "FOREIGN KEY(book_isbn) REFERENCES books(isbn))";
            stmt.executeUpdate(createTransactionsTableSql);
            System.out.println("Transactions table created or already exists.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    public static User findUserById(String userId) {
        String sql = "SELECT * FROM users WHERE user_id = ?";
        
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, userId);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                String name = rs.getString("name");
                String borrowedIsbns = rs.getString("borrowed_isbns");
                
                User user = new User(name);
                user.ID = userId;
                
                // Parse borrowed ISBNs if any
                if (borrowedIsbns != null && !borrowedIsbns.isEmpty()) {
                    String[] isbns = borrowedIsbns.split(",");
                    for (String isbn : isbns) {
                        user.getBorrowedIsbns().add(isbn);
                    }
                }
                
                return user;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return null;
    }

    public static void borrowBookByUserId(String userId, String isbn) {
        // Step 1: Find the user by ID
        User user = findUserById(userId);
        if (user == null) {
            System.out.println("User not found with ID: " + userId);
            return;
        }
        
        // Step 2: Find the book by ISBN
        Book book = findBookByIsbn(isbn);
        if (book == null) {
            System.out.println("Book not found with ISBN: " + isbn);
            return;
        }
        
        // Step 3: Check if there are copies available
        if (book.getQuantity() <= 0) {
            System.out.println("No available copies of " + book.getTitle());
            return;
        }
        
        // Step 4: Update book quantity
        book.setQuantity(book.getQuantity() - 1);
        LibraryDatabase.updateBook(book);
        
        // Step 5: Add book to user's borrowed books
        user.getBorrowedIsbns().add(isbn);
        LibraryDatabase.updateUser(user);
        
        // Step 6: Record the transaction
        LocalDate borrowDate = LocalDate.now();
        LibraryDatabase.addTransaction(userId, isbn, "BORROW", borrowDate, null);
        
        System.out.println(user.getName() + " successfully borrowed: " + book.getTitle());
    }

    // Add a book to the database
    public static void addBook(Book book) {
        // Check if the book already exists by ISBN
        if (findBookByIsbn(book.getIsbn()) != null) {
            System.out.println("Book with ISBN " + book.getIsbn() + " already exists. Updating quantity...");
            updateBookQuantity(book);
            return;
        }
    
        // If the book doesn't exist, add a new one
        String sql = "INSERT INTO books (isbn, title, author, quantity) VALUES (?, ?, ?, ?)";
    
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement stmt = conn.prepareStatement(sql)) {
    
            stmt.setString(1, book.getIsbn());
            stmt.setString(2, book.getTitle());
            stmt.setString(3, book.getAuthor());
            stmt.setInt(4, book.getQuantity());
    
            stmt.executeUpdate();
            System.out.println("Book added: " + book.getTitle());
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    // Method to update the quantity if the book already exists
    private static void updateBookQuantity(Book book) {
        String sql = "UPDATE books SET quantity = quantity + ? WHERE isbn = ?";
    
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement stmt = conn.prepareStatement(sql)) {
    
            stmt.setInt(1, book.getQuantity());
            stmt.setString(2, book.getIsbn());
    
            stmt.executeUpdate();
            System.out.println("Quantity updated for book: " + book.getTitle());
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Find a book by ISBN
    public static Book findBookByIsbn(String isbn) {
        String sql = "SELECT * FROM books WHERE isbn = ?";
    
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement stmt = conn.prepareStatement(sql)) {
    
            stmt.setString(1, isbn);
            ResultSet rs = stmt.executeQuery();
    
            if (rs.next()) {
                String title = rs.getString("title");
                String author = rs.getString("author");
                int quantity = rs.getInt("quantity");
    
                return new Book(isbn, title, author, quantity);
            } else {
                System.out.println("Book not found: " + isbn);
                return null;
            }
    
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
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
        String sql = "UPDATE users SET borrowed_isbns = ? WHERE user_id = ?";

        try (Connection conn = DriverManager.getConnection(DB_URL);
            PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, String.join(",", user.getBorrowedIsbns()));  // Storing ISBNs as comma-separated values
            stmt.setString(2, user.getID());
            stmt.executeUpdate();

            System.out.println("User updated: " + user.getID());
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Update book information
    public static void updateBook(Book book) {
        String sql = "UPDATE books SET title = ?, author = ?, quantity = ? WHERE isbn = ?";
    
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement stmt = conn.prepareStatement(sql)) {
    
            stmt.setString(1, book.getTitle());
            stmt.setString(2, book.getAuthor());
            stmt.setInt(3, book.getQuantity());
            stmt.setString(4, book.getIsbn());
            stmt.executeUpdate();
            System.out.println("Book updated: " + book.getTitle());
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
                String borrowedIsbns = rs.getString("borrowed_isbns");

                User user = new User(name);
                user.ID = userId;

                // Parse the comma-separated ISBNs (if any)
                if (borrowedIsbns != null && !borrowedIsbns.isEmpty()) {
                    String[] isbns = borrowedIsbns.split(",");
                    for (String isbn : isbns) {
                        user.getBorrowedIsbns().add(isbn);
                    }
                }

                users.add(user);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return users;
    }

    // Display all transactions for a user
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

                // Find the book by ISBN (you can modify this to return book details if needed)
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

    // Method to calculate the fine
    public static double calculateFine(LocalDate borrowDate, LocalDate returnDate) {
        long daysOverdue = ChronoUnit.DAYS.between(borrowDate.plusDays(LOAN_PERIOD_DAYS), returnDate);

        if (daysOverdue > 0) {
            return daysOverdue * DAILY_FINE;
        } else {
            return 0;  // No fine if not overdue
        }
    }

    // Automatically update fines for overdue books
    public static void updateFines() {
        String sql = "SELECT user_id, book_isbn, transaction_date FROM transactions WHERE transaction_type = 'BORROW'";

        try (Connection conn = DriverManager.getConnection(DB_URL);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                String userId = rs.getString("user_id");
                String bookIsbn = rs.getString("book_isbn");
                LocalDate borrowDate = LocalDate.parse(rs.getString("transaction_date"));
                LocalDate today = LocalDate.now();

                double fine = calculateFine(borrowDate, today);

                if (fine > 0) {
                    String updateSql = "UPDATE transactions SET fine_amount = ? WHERE user_id = ? AND book_isbn = ? AND transaction_type = 'BORROW'";
                    try (PreparedStatement updateStmt = conn.prepareStatement(updateSql)) {
                        updateStmt.setDouble(1, fine);
                        updateStmt.setString(2, userId);
                        updateStmt.setString(3, bookIsbn);
                        updateStmt.executeUpdate();
                    }
                }
            }
            System.out.println("Fines updated for overdue books.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Clear a user's fine (this would typically be called after payment)
    public static void clearUserFine(String userId) {
        String sql = "UPDATE transactions SET fine_amount = 0 WHERE user_id = ? AND fine_amount > 0";

        try (Connection conn = DriverManager.getConnection(DB_URL);
            PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, userId);
            stmt.executeUpdate();
            System.out.println("Fines cleared for user " + userId);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Add a transaction to the database
    public static void addTransaction(String userId, String bookIsbn, String type, LocalDate date, LocalDate returnDate) {
        // Calculate fine if it's a return and the book is overdue
        double fineAmount = 0;
        if (type.equals("RETURN")) {
            fineAmount = calculateFine(date, returnDate);
        }

        String sql = "INSERT INTO transactions (user_id, book_isbn, transaction_type, transaction_date, fine_amount) VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, userId);
            stmt.setString(2, bookIsbn);
            stmt.setString(3, type);
            stmt.setString(4, date.toString());
            stmt.setDouble(5, fineAmount);
            stmt.executeUpdate();
            System.out.println("Transaction added: " + type + " " + bookIsbn + " for user " + userId + " with fine: $" + fineAmount);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static LocalDate getBorrowDate(String userId, String bookIsbn) {
        String sql = "SELECT transaction_date FROM transactions WHERE user_id = ? AND book_isbn = ? AND transaction_type = 'BORROW' ORDER BY transaction_id DESC LIMIT 1";
    
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement stmt = conn.prepareStatement(sql)) {
    
            stmt.setString(1, userId);
            stmt.setString(2, bookIsbn);
            ResultSet rs = stmt.executeQuery();
    
            if (rs.next()) {
                String dateStr = rs.getString("transaction_date");
                return LocalDate.parse(dateStr);
            } else {
                // If no borrow transaction is found, return a default date or handle the error
                System.out.println("No borrow record found for this book and user.");
                return LocalDate.now(); // This is a fallback that might not be appropriate
            }
    
        } catch (SQLException e) {
            e.printStackTrace();
            return LocalDate.now(); // Again, this is a fallback that might not be appropriate
        }
    }


}
