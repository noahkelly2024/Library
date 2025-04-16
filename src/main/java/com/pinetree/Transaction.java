package com.pinetree;

import java.time.LocalDate;

public class Transaction {
    private String userId;
    private String bookIsbn;
    private String type; // "BORROW" or "RETURN"
    private LocalDate date;

    // Constructor
    public Transaction(String userId, String bookIsbn, String type, LocalDate date) {
        this.userId = userId;
        this.bookIsbn = bookIsbn;
        this.type = type;
        this.date = date;
    }

    // Getters
    public String getUserId() { return userId; }
    public String getBookIsbn() { return bookIsbn; }
    public String getType() { return type; }
    public LocalDate getDate() { return date; }

    // Add a transaction to the database
    public void addTransaction() {
        LibraryDatabase.addTransaction(userId, bookIsbn, type, date);
    }

    // Display all transactions for a user
    public static void displayUserTransactions(String userId) {
        LibraryDatabase.displayUserTransactions(userId);
    }
}
