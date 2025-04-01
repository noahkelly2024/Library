package com.pinetree;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import java.io.*;
import java.time.LocalDate;
import java.util.*;

public class User {
    private String ID;
    private String name;
    private List<String> borrowedCopyIds; // Track borrowed book copy IDs
    private static final String filepath = "users.json";

    public User(String name) {
        this.ID = generateUniqueID();
        this.name = name;
        this.borrowedCopyIds = new ArrayList<>();
    }

    // Getters can be simplified to one-liners
    public String getID() { return ID; }
    public String getName() { return name; }
    public List<String> getBorrowedCopyIds() { return borrowedCopyIds; }


    // Generate a unique ID for each user
    private static String generateUniqueID() {
        return "U" + UUID.randomUUID().toString().substring(0, 8);
    }

    // Register a new user
    public static User addUser(String name) {
        List<User> users = readUsersFromJson();
        User newUser = new User(name);
        users.add(newUser);
        writeUsersToJson(users);
        return newUser;
    }

    // Remove a user by ID
    public static void removeUser(String userId) {
        List<User> users = readUsersFromJson();
        users.removeIf(user -> user.getID().equals(userId));
        writeUsersToJson(users);
    }

    // Borrow a book (by copy ID)
    public boolean borrowBook(Book book) {
        // Check if user has reached borrow limit
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
                        
                        // Save updates
                        Book.updateBooks(books);
                        updateUsers();
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

    /**
     * Return a borrowed book
     * @param copyId The ID of the book copy to return
     * @return true if successful, false otherwise
     */
    public boolean returnBook(String copyId) {
        if (copyId == null || copyId.isEmpty()) {
            System.out.println("Error: Copy ID is null or empty");
            return false;
        }

        // Make sure the user has actually borrowed this book
        if (this.borrowedCopyIds == null || !this.borrowedCopyIds.contains(copyId)) {
            System.out.println("Error: User " + this.getName() + " hasn't borrowed book with copy ID " + copyId);
            return false;
        }

        List<Book> books = Book.allBooks();  // Get all books from the system
        if (books == null) {
            System.out.println("Error: Failed to retrieve books from the system");
            return false;
        }
        
        for (Book book : books) {
            if (book == null) continue;
            
            List<BookCopy> copies = book.getCopies();
            if (copies == null) continue;
            
            for (BookCopy copy : copies) {
                if (copy == null) continue;
                
                // Check if this is the copy we're looking for
                if (copyId.equals(String.valueOf(copy.getCopyId()))) {
                    // Check if this copy was borrowed by this user
                    String borrowedBy = copy.getBorrowedBy();
                    if (borrowedBy != null && borrowedBy.equals(this.ID)) {
                        // Found the right copy, now return it
                        copy.returnCopy();
                        this.borrowedCopyIds.remove(copyId);
                        
                        // Save the changes
                        Book.updateBooks(books);
                        updateUsers();
                        return true;
                    }
                }
            }
        }

        System.out.println("Error: Could not find borrowed book with copy ID " + copyId);
        return false;
    }

    // List all users
    public static List<User> listUsers() {
        List<User> users = readUsersFromJson();
        for (User user : users) {
            System.out.println("ID: " + user.getID());
            System.out.println("Name: " + user.getName());
            System.out.println("Books Borrowed: " + user.getBorrowedCopyIds());
            System.out.println("----------------------------------");
        }
        return users; // Return the list we already read
    }

    // Read users from JSON
    private static List<User> readUsersFromJson() {
        try (BufferedReader reader = new BufferedReader(new FileReader(filepath))) {
            return new Gson().fromJson(reader, new TypeToken<List<User>>() {}.getType());
        } catch (IOException e) {
            return new ArrayList<>();
        }
    }

    // Write users to JSON with pretty printing
    private static void writeUsersToJson(List<User> users) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filepath))) {
            Gson gson = new GsonBuilder()
                .setPrettyPrinting()
                .create();
            gson.toJson(users, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Update user data in JSON
    private void updateUsers() {
        List<User> users = readUsersFromJson();
        for (User user : users) {
            if (user.getID().equals(this.ID)) {
                user.borrowedCopyIds = this.borrowedCopyIds;
            }
        }
        writeUsersToJson(users);
    }

    public List<Book> getBooksBorrowed() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getBooksBorrowed'");
    }

    /**
     * Borrow a book by ISBN
     * @param isbn The ISBN of the book to borrow
     * @return true if successful, false otherwise
     */
    public boolean borrowBookByIsbn(String isbn) {
        // Check if user has reached borrow limit
        if (this.borrowedCopyIds.size() >= Library.borrowLimit) {
            System.out.println("User has reached the borrow limit of " + Library.borrowLimit + " books.");
            return false;
        }
        
        Book book = Book.findBookByIsbn(isbn);
        if (book == null) {
            System.out.println("Could not find book with ISBN: " + isbn);
            return false;
        }
        
        System.out.println("Found book: " + book.getTitle());
        List<BookCopy> copies = book.getCopies();
        System.out.println("Book has " + (copies != null ? copies.size() : 0) + " copies");
        
        boolean success = borrowBook(book);
        if (success) {
            // Create a transaction record
            Transaction transaction = new Transaction(this.ID, isbn, "BORROW", LocalDate.now());
            Library.addTransaction(transaction);
            System.out.println("Successfully borrowed book: " + book.getTitle());
        } else {
            System.out.println("Failed to borrow book: " + book.getTitle());
        }
        return success;
    }

    /**
     * Return a book by copy ID and record the transaction
     * @param copyId The ID of the book copy to return
     * @return true if successful, false otherwise
     */
    public boolean returnBookAndRecordTransaction(String copyId) {
        // Check if copyId is null or empty
        if (copyId == null || copyId.isEmpty()) {
            System.out.println("Error: Copy ID is null or empty");
            return false;
        }
        
        // Check if user has actually borrowed this copy
        if (borrowedCopyIds == null || !borrowedCopyIds.contains(copyId)) {
            System.out.println("Error: User " + this.getName() + " hasn't borrowed book with copy ID " + copyId);
            return false;
        }
        
        // Find which book this copy belongs to
        String isbn = null;
        List<Book> allBooks = Book.allBooks();
        
        if (allBooks == null || allBooks.isEmpty()) {
            System.out.println("Error: No books in the system");
            return false;
        }
        
        for (Book book : allBooks) {
            if (book == null) continue;
            
            List<BookCopy> copies = book.getCopies();
            if (copies == null || copies.isEmpty()) continue;
            
            for (BookCopy copy : copies) {
                if (copy == null) continue;
                
                String currentCopyId = copy.getCopyId();
                if (currentCopyId != null && copyId.equals(currentCopyId)) {
                    isbn = book.getIsbn();
                    break;
                }
            }
            if (isbn != null) break; // Found the ISBN, exit the loop
        }
        
        // If we couldn't find the book, return false
        if (isbn == null) {
            System.out.println("Error: Could not find book for copy ID " + copyId);
            return false;
        }
        
        // Attempt to return the book
        boolean success = returnBook(copyId);
        if (success) {
            // Create a transaction record
            Transaction transaction = new Transaction(this.ID, isbn, "RETURN", LocalDate.now());
            Library.addTransaction(transaction);
            System.out.println("Successfully returned book copy " + copyId);
        } else {
            System.out.println("Failed to return book copy " + copyId);
        }
        
        return success;
    }

    /**
     * Get all books borrowed by this user
     * @return List of books currently borrowed by this user
     */
    public List<Book> getBorrowedBooks() {
        List<Book> borrowedBooks = new ArrayList<>();
        List<Book> allBooks = Book.allBooks();
        
        if (borrowedCopyIds == null || borrowedCopyIds.isEmpty()) {
            return borrowedBooks; // Return empty list if no books borrowed
        }
        
        for (String copyId : borrowedCopyIds) {
            if (copyId == null) continue; // Skip null copyIds
            
            for (Book book : allBooks) {
                if (book == null) continue; // Skip null books
                
                List<BookCopy> copies = book.getCopies();
                if (copies == null || copies.isEmpty()) continue; // Skip books with no copies
                
                for (BookCopy copy : copies) {
                    if (copy == null) continue; // Skip null copies
                    
                    String currentCopyId = copy.getCopyId();
                    if (currentCopyId != null && copyId.equals(currentCopyId)) {
                        borrowedBooks.add(book);
                        break; // Found this book, move to next copyId
                    }
                }
            }
        }
        
        return borrowedBooks;
    }

    /**
     * Display info about this user and their borrowed books
     */
    public void displayUserInfo() {
        System.out.println("User ID: " + this.ID);
        System.out.println("Name: " + this.name);
        System.out.println("Books borrowed: " + (this.borrowedCopyIds != null ? this.borrowedCopyIds.size() : 0));
        
        if (borrowedCopyIds != null && !borrowedCopyIds.isEmpty()) {
            System.out.println("Borrowed books:");
            try {
                List<Book> borrowedBooks = getBorrowedBooks();
                if (borrowedBooks.isEmpty()) {
                    System.out.println("  (No book information available)");
                } else {
                    for (Book book : borrowedBooks) {
                        if (book != null) {
                            System.out.println("  - " + book.getTitle() + " by " + book.getAuthor());
                        } else {
                            System.out.println("  - (Unknown book)");
                        }
                    }
                }
            } catch (Exception e) {
                System.out.println("  Error retrieving borrowed books: " + e.getMessage());
            }
        }
    }
}
