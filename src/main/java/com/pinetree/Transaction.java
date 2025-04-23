package com.pinetree;

import java.time.LocalDate;

public class Transaction {  
    private String userId;
    private String bookIsbn;
    private String type; // "BORROW" or "RETURN"
    private LocalDate date;
    private LocalDate returnDate;

    // Constructor
    public Transaction(String userId, String bookIsbn, String type, LocalDate date, LocalDate returnDate) {
        this.userId = userId;
        this.bookIsbn = bookIsbn;
        this.type = type;
        this.date = date;
        this.returnDate = returnDate;
    }

    // Add a transaction to the database
    public void addTransaction() {
        LibraryDatabase.addTransaction(userId, bookIsbn, type, date, returnDate);
    }

    // Display all transactions for a user
    public static void displayUserTransactions(String userId) {
        LibraryDatabase.displayUserTransactions(userId);
    }
}
