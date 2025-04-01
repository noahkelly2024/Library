package com.pinetree;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import java.util.List;
import java.io.File;

public class BookManagementTest {
    
    private static final String TEST_ISBN = "9781234567897";
    private static final String TEST_TITLE = "Test Book";
    private static final String TEST_AUTHOR = "Test Author";
    
    @Before
    public void setup() {
        // Clean up any test data from previous runs
        File booksFile = new File("books.json");
        if (booksFile.exists()) {
            booksFile.delete();
        }
    }
    
    @Test
    public void testBookProperties() {
        // Create a book and verify its properties
        Book book = new Book(TEST_ISBN, TEST_TITLE, TEST_AUTHOR, 1);
        
        assertEquals(TEST_ISBN, book.getIsbn());
        assertEquals(TEST_TITLE, book.getTitle());
        assertEquals(TEST_AUTHOR, book.getAuthor());
        assertNotNull(book.getCopies());
        assertFalse(book.getCopies().isEmpty());
        assertTrue(book.getCopies().get(0).isAvailable());
    }
    
    @Test
    public void testAddAndListBooks() {
        // Add a book
        Book.addBook(TEST_ISBN, TEST_TITLE, TEST_AUTHOR, 3);
        
        // Get all books and verify the book was added
        List<Book> books = Book.allBooks();
        assertNotNull(books);
        assertFalse(books.isEmpty());
        
        Book addedBook = books.get(0);
        assertEquals(TEST_ISBN, addedBook.getIsbn());
        assertEquals(TEST_TITLE, addedBook.getTitle());
        assertEquals(TEST_AUTHOR, addedBook.getAuthor());
        assertEquals(3, addedBook.getCopies().size());
    }
    
    @Test
    public void testFindBookByIsbn() {
        // Add a book
        Book.addBook(TEST_ISBN, TEST_TITLE, TEST_AUTHOR, 1);
        
        // Find the book by ISBN
        Book foundBook = Book.findBookByIsbn(TEST_ISBN);
        
        // Verify book was found
        assertNotNull(foundBook);
        assertEquals(TEST_ISBN, foundBook.getIsbn());
        assertEquals(TEST_TITLE, foundBook.getTitle());
        assertEquals(TEST_AUTHOR, foundBook.getAuthor());
    }
    
    @Test
    public void testRemoveBook() {
        // Add a book
        Book.addBook(TEST_ISBN, TEST_TITLE, TEST_AUTHOR, 3);
        
        // Verify book exists
        assertNotNull(Book.findBookByIsbn(TEST_ISBN));
        
        // Remove all copies of the book
        Book.removeBook(TEST_ISBN, 3);
        
        // Verify book is gone
        assertNull(Book.findBookByIsbn(TEST_ISBN));
    }
    
    @Test
    public void testRemovePartialCopies() {
        // Add a book with 3 copies
        Book.addBook(TEST_ISBN, TEST_TITLE, TEST_AUTHOR, 3);
        
        // Remove 2 copies
        Book.removeBook(TEST_ISBN, 2);
        
        // Verify only 1 copy remains
        Book book = Book.findBookByIsbn(TEST_ISBN);
        assertNotNull(book);
        assertEquals(1, book.getCopies().size());
    }
    
    @Test
    public void testPersistence() {
        // Add a book
        Book.addBook(TEST_ISBN, TEST_TITLE, TEST_AUTHOR, 1);
        
        // Get a fresh list of books (forces a read from storage)
        List<Book> books = Book.readBooksFromJson();
        
        // Verify book exists in storage
        assertFalse(books.isEmpty());
        assertEquals(TEST_ISBN, books.get(0).getIsbn());
    }
}