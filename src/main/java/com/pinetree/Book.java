package com.pinetree;

import java.io.*;
import java.util.*;
import com.google.gson.*;
import com.google.gson.reflect.TypeToken;

public class Book {
    private String isbn;
    private String title;
    private String author;
    private List<BookCopy> copies; // Track individual copies
    private static final String filepath = "books.json";

    // Constructor
    public Book(String isbn, String title, String author, int quantity) {
        this.isbn = isbn;
        this.title = title;
        this.author = author;
        this.copies = new ArrayList<>();
        
        // Create the specified number of copies
        for (int i = 0; i < quantity; i++) {
            this.copies.add(new BookCopy(this.isbn));
        }
    }

    // Getters
    public String getIsbn() { 
        return isbn; }

    public String getTitle() { 
        return title; }

    public String getAuthor() { 
        return author; }

    public List<BookCopy> getCopies() { 
        return copies; }

    // Add book to JSON (with duplicate check)
    public static void addBook(String isbn, String title, String author, int quantity) {
        List<Book> books = readBooksFromJson();
        
        // Check if book with this ISBN already exists
        boolean exists = books.stream().anyMatch(book -> book.getIsbn().equals(isbn));
        
        if (!exists) {
            books.add(new Book(isbn, title, author, quantity));
            writeBooksToJson(books);
            System.out.println("Book added: " + title + " by " + author);
        } else {
            System.out.println("Book with ISBN " + isbn + " already exists.");
        }
    }

    // Remove book by ISBN
    public static void removeBook(String isbn, int quantity) {
        List<Book> books = readBooksFromJson();
        // books.removeIf(book -> book.getIsbn().equals(isbn));
        
        for (Book book : books) {
            if (book.getIsbn().equals(isbn)) {
                List<BookCopy> copies = book.getCopies();
                if (copies.size() > quantity) {
                    for (int i = 0; i < quantity; i++) {
                        copies.remove(copies.size() - 1); // Remove the last copy
                    }
                } else if (copies.size() == quantity) {
                    books.remove(book); // Remove the book entirely
                } else {
                    System.out.println("Not enough copies to remove.");
                }
                break; // Exit loop after finding the book
            }
        }
        writeBooksToJson(books); // Save updated list
    }

    


    // Borrow a book copy
    public boolean borrowBook(String userId) {
        for (BookCopy copy : copies) {
            if (copy.isAvailable()) {
                copy.borrow(userId);
                writeBooksToJson(readBooksFromJson()); // Save state
                return true;
            }
        }
        System.out.println("No available copies of " + title);
        return false;
    }

    // Return a book copy
    public boolean returnBook(String userId) {
        for (BookCopy copy : copies) {
            if (userId.equals(copy.getBorrowedBy())) {
                copy.returnCopy();
                writeBooksToJson(readBooksFromJson()); // Save state
                return true;
            }
        }
        System.out.println("User " + userId + " did not borrow " + title);
        return false;
    }

        // List all copies of a book given its ISBN
        public static List<BookCopy> listBookCopies(String isbn) {
            List<Book> books = readBooksFromJson();
            for (Book book : books) {
            if (book.getIsbn().equals(isbn)) {
                System.out.println("Book: " + book.getTitle() + " by " + book.getAuthor());
                if (book.getCopies() != null && !book.getCopies().isEmpty()) {
                System.out.println("Copies:");
                for (BookCopy copy : book.getCopies()) {
                    System.out.println("  - Copy ID: " + copy.getCopyId());
                }
                return book.getCopies();
                } else {
                System.out.println("No copies available.");
                return new ArrayList<>();
                }
            }
            }
            System.out.println("Book with ISBN " + isbn + " not found.");
            return new ArrayList<>();
        }

        // List all books
        public static List<Book> allBooks() {
            List<Book> books = readBooksFromJson();
            for (Book book : books) {
                // Check if the book has copies
                if (book.getCopies() != null && !book.getCopies().isEmpty()) {
                    for (BookCopy copy : book.getCopies()) {
                    }
                } else {
                }
            }
            return readBooksFromJson();
        }

        // List all books
        public static List<Book> listAllBooks() {
            List<Book> books = readBooksFromJson();
            for (Book book : books) {
                System.out.println("ISBN: " + book.getIsbn());
                System.out.println("Title: " + book.getTitle());
                System.out.println("Author: " + book.getAuthor());
    
                // Check if the book has copies
                if (book.getCopies() != null && !book.getCopies().isEmpty()) {
                    System.out.println("Copies:");
                    for (BookCopy copy : book.getCopies()) {
                        System.out.println("  - Copy ID: " + copy.getCopyId());
                    }
                } else {
                    System.out.println("No copies available.");
                }
                System.out.println("----------------------------------");
            }
            return readBooksFromJson();
        }

    // Read books from JSON
    private static List<Book> readBooksFromJson() {
        try (BufferedReader reader = new BufferedReader(new FileReader(filepath))) {
            return new Gson().fromJson(reader, new TypeToken<List<Book>>() {}.getType());
        } catch (IOException e) {
            return new ArrayList<>();
        }
    }

    // Write books to JSON with pretty printing
    private static void writeBooksToJson(List<Book> books) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filepath))) {
            Gson gson = new GsonBuilder()
                .setPrettyPrinting()
                .create();
            gson.toJson(books, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Update books in JSON
    public static void updateBooks(List<Book> books) {
        writeBooksToJson(books);
    }

    /**
     * Find a book by its ISBN
     * @param isbn The ISBN to search for
     * @return The Book object if found, null otherwise
     */
    public static Book findBookByIsbn(String isbn) {
        if (isbn == null || isbn.isEmpty()) {
            System.out.println("Error: ISBN is null or empty");
            return null;
        }
        
        List<Book> books = allBooks();
        
        for (Book book : books) {
            if (book != null && isbn.equals(book.getIsbn())) {
                return book;
            }
        }
        
        System.out.println("Book with ISBN " + isbn + " not found");
        return null;
    }

    public static void massUploadUsers(String fileName) {

    }

    /**
     * Display detailed information about a book
     */
    public void displayBookInfo() {
        System.out.println("ISBN: " + this.isbn);
        System.out.println("Title: " + this.title);
        System.out.println("Author: " + this.author);
        
        if (this.copies != null && !this.copies.isEmpty()) {
            int available = 0;
            for (BookCopy copy : this.copies) {
                if (copy.isAvailable()) {
                    available++;
                }
            }
            System.out.println("Copies: " + this.copies.size() + " (Available: " + available + ")");
        } else {
            System.out.println("No copies available.");
        }
    }

    /**
     * Static method to display book information by ISBN
     * @param isbn The ISBN of the book to display
     * @return true if book was found and displayed, false otherwise
     */
    public static boolean displayBookByIsbn(String isbn) {
        Book book = findBookByIsbn(isbn);
        if (book != null) {
            book.displayBookInfo();
            return true;
        }
        return false;
    }

}