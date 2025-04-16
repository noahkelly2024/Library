package com.pinetree;
import java.time.LocalDate;
import java.util.List;
import java.util.ArrayList;

public class Library {
    public static int borrowLimit = 3; // Default limit
    private static List<Transaction> transactionHistory = new ArrayList<>();
    
    // Get the borrow limit
    public static int getBorrowLimit() {
        return borrowLimit;
    }
    
    // Set the borrow limit
    public static void setBorrowLimit(int limit) {
        borrowLimit = limit;
    }
    

    public static void main(String[] args) {

        // Add books
        Book.addBook("123456789012", "The Hobbit", "J.R.R. Tolkien", 5);
        Book.addBook("987654321098", "The Great Gatsby", "F. Scott Fitzgerald", 3);
        

    }
}