package com.pinetree;

import java.time.LocalDate;
import java.util.*;

public class User {
    String ID;
    private String name;
    private List<String> borrowedCopyIds;

    // Constructor
    public User(String name) {
        this.ID = generateUniqueID();
        this.name = name;
        this.borrowedCopyIds = new ArrayList<>();
    }

    // Getters
    public String getID() { return ID; }
    public String getName() { return name; }
    public List<String> getBorrowedCopyIds() { return borrowedCopyIds; }

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

    // Borrow a book (by copy ID)
    public boolean borrowBook(Book book) {
        if (this.borrowedCopyIds.size() >= Library.borrowLimit) {
            System.out.println("User has reached the borrow limit of " + Library.borrowLimit + " books.");
            return false;
        }

        List<Book> books = Book.listAllBooks();
        for (Book b : books) {
            if (b.getIsbn().equals(book.getIsbn())) {
                for (BookCopy copy : b.getCopies()) {
                    if (copy.isAvailable()) {
                        copy.borrow(this.ID);
                        this.borrowedCopyIds.add(copy.getCopyId());

                        // Log the transaction
                        Transaction transaction = new Transaction(this.ID, book.getIsbn(), "BORROW", LocalDate.now());
                        transaction.addTransaction();  // Add the transaction via the Transaction class

                        // Save updates to the database
                        LibraryDatabase.updateUser(this);
                        return true;
                    }
                }
                System.out.println("No available copies of " + book.getTitle());
                return false;
            }
        }
        System.out.println("Book not found: " + book.getTitle());
        return false;
    }

    // Return a borrowed book and log the transaction
    public boolean returnBook(String copyId) {
        if (copyId == null || copyId.isEmpty()) {
            System.out.println("Error: Copy ID is null or empty");
            return false;
        }

        // Make sure the user has actually borrowed this book
        if (!this.borrowedCopyIds.contains(copyId)) {
            System.out.println("Error: User " + this.getName() + " hasn't borrowed book with copy ID " + copyId);
            return false;
        }

        List<Book> books = Book.allBooks();
        if (books == null) {
            System.out.println("Error: Failed to retrieve books from the system");
            return false;
        }

        for (Book book : books) {
            for (BookCopy copy : book.getCopies()) {
                if (copy.getCopyId().equals(copyId) && copy.getBorrowedBy().equals(this.ID)) {
                    // Found the right copy, now return it
                    copy.returnCopy();
                    this.borrowedCopyIds.remove(copyId);

                    // Log the transaction
                    Transaction transaction = new Transaction(this.ID, book.getIsbn(), "RETURN", LocalDate.now());
                    transaction.addTransaction();  // Add the transaction via the Transaction class

                    // Save the changes to the database
                    LibraryDatabase.updateUser(this);
                    return true;
                }
            }
        }

        System.out.println("Error: Could not find borrowed book with copy ID " + copyId);
        return false;
    }

    // List all users
    public static List<User> listUsers() {
        return LibraryDatabase.listUsers();  // Use the LibraryDatabase class to list users
    }
}
