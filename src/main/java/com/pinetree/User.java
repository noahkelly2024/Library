package com.pinetree;

import java.time.LocalDate;
import java.util.*;

public class User {
    String ID;
    private String name;
    private List<String> borrowedIsbns; // Track ISBNs of borrowed books

    // Constructor
    public User(String name) {
        this.ID = generateUniqueID();
        this.name = name;
        this.borrowedIsbns = new ArrayList<>();
    }

    // Getters
    public String getID() { return ID; }
    public String getName() { return name; }
    public List<String> getBorrowedIsbns() { return borrowedIsbns; }

    // Generate a unique ID for each user
    private static String generateUniqueID() {
        return "U" + UUID.randomUUID().toString().substring(0, 8);
    }

    // Add a new user to the system
    public static User addUser(String name) {
        User newUser = new User(name);
        LibraryDatabase.addUser(newUser);  // Use the LibraryDatabase class to add the user
        return newUser;
    }

    // Remove a user by ID
    public static void removeUser(String userId) {
        LibraryDatabase.removeUser(userId);  // Use the LibraryDatabase class to remove the user
    }

    // Borrow a book (by ISBN)
    public void borrowBook(Book book) {
        if (book.getQuantity() > 0) {
            // Decrease the quantity of the book when borrowed
            book.setQuantity(book.getQuantity() - 1);  // Decreases quantity by 1

            // Record borrow transaction
            LocalDate borrowDate = LocalDate.now();
            LibraryDatabase.addTransaction(this.getID(), book.getIsbn(), "BORROW", borrowDate, null);
            System.out.println("Book borrowed: " + book.getTitle());
        } else {
            System.out.println("No available copies of " + book.getTitle());
        }
    }

    // Overloaded method that accepts an ISBN string
    public void borrowBook(String isbn) {
        Book book = LibraryDatabase.findBookByIsbn(isbn);
        if (book != null) {
            borrowBook(book); // Call the existing method
        } else {
            System.out.println("Book not found with ISBN: " + isbn);
        }
    }
    
    // Return a borrowed book and log the transaction
    public void returnBook(Book book) {
        LocalDate returnDate = LocalDate.now();
        // Fetch the borrow date from the database
        LocalDate borrowDate = LibraryDatabase.getBorrowDate(this.getID(), book.getIsbn());
        
        LibraryDatabase.addTransaction(this.getID(), book.getIsbn(), "RETURN", borrowDate, returnDate);
        
        // Update the quantity of the book when returned
        book.setQuantity(book.getQuantity() + 1);
    
        System.out.println("Book returned: " + book.getTitle());
    }

        // Return a borrowed book and log the transaction
        public void returnBook(String isbn) {
            Book book = LibraryDatabase.findBookByIsbn(isbn);
            LocalDate returnDate = LocalDate.now();
            // Fetch the borrow date from the database
            LocalDate borrowDate = LibraryDatabase.getBorrowDate(this.getID(), book.getIsbn());
            
            LibraryDatabase.addTransaction(this.getID(), book.getIsbn(), "RETURN", borrowDate, returnDate);
            
            // Update the quantity of the book when returned
            book.setQuantity(book.getQuantity() + 1);
        
            System.out.println("Book returned: " + book.getTitle());
        }
    

    // List all users
    public static List<User> listUsers() {
        return LibraryDatabase.listUsers();  // Use the LibraryDatabase class to list users
    }
}
