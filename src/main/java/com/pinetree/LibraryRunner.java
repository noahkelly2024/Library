package com.pinetree;
import java.time.LocalDate;
import java.util.List;
import java.util.ArrayList;
import java.util.UUID;
import java.sql.*;

public class LibraryRunner {
    public static int borrowLimit = 3; // Default limit
    private static List<Transaction> transactionHistory = new ArrayList<>();
    public static void main(String[] args) {
        // Step 1: Setup Database
        LibraryDatabase.setupDatabase();

        // // Step 2: Add a book to the database for testing
        // Book book1 = new Book("1234567890", "The Great Adventure", "John Doe", 5);
        // LibraryDatabase.addBook(book1);

        // Step 3: Add a user to the system
        LibraryDatabase.borrowBookByUserId("U23930694", "1234567890");

        
        // // Step 4: Borrow a book (by ISBN)
        // user.borrowBook("1234567890"); // ISBN of the book

        // // Step 5: List the transactions for the user
        // System.out.println("User transactions:");
        // Transaction.displayUserTransactions(user.getID());

        // // Step 6: Return the borrowed book
        // user.returnBook("1234567890");


        // // Step 7: List the transactions again for the user
        // System.out.println("User transactions after return:");
        // Transaction.displayUserTransactions(user.getID());
    }
}