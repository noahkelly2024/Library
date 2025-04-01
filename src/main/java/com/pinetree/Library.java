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
    
    // Add a transaction to history
    public static void addTransaction(Transaction transaction) {
        transactionHistory.add(transaction);
        Transaction.saveTransactions(transactionHistory);
    }
    
    // Upload existing transaction history
    public static void uploadTransactionHistory(List<Transaction> history) {
        transactionHistory.addAll(history);
        Transaction.saveTransactions(transactionHistory);
    }

    public static void main(String[] args) {
        // Example usage
        User noah = User.addUser("Noah");
        
        // Add books
        Book.addBook("123456789012", "The Hobbit", "J.R.R. Tolkien", 5);
        Book.addBook("987654321098", "The Great Gatsby", "F. Scott Fitzgerald", 3);
        
        // Display book info
        Book.displayBookByIsbn("123456789012");
        
        // Borrow a book with transaction recording
        noah.borrowBookByIsbn("123456789012");
        
        // Display user info
        noah.displayUserInfo();
        
        // Get borrowed copy ID and return it
        String borrowedCopyId = noah.getBorrowedCopyIds().get(0);
        noah.returnBookAndRecordTransaction(borrowedCopyId);
        
        // Display transaction history
        Transaction.displayUserTransactions(noah.getID());
        
        // Check and display fines
        int fine = FineManager.calculateFine(noah.getID());
        System.out.println("Fine for " + noah.getName() + ": $" + fine);
    }
}