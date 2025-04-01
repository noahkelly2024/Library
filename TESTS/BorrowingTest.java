package com.pinetree;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import java.util.List;
import java.io.File;

public class BorrowingTest {
    
    private static final String TEST_ISBN = "9781234567897";
    private static final String TEST_TITLE = "Test Book";
    private static final String TEST_AUTHOR = "Test Author";
    private static final String TEST_USER_NAME = "Test User";
    
    private User testUser;
    
    @Before
    public void setup() {
        // Clean up any test data from previous runs
        File booksFile = new File("books.json");
        if (booksFile.exists()) {
            booksFile.delete();
        }
        
        File usersFile = new File("users.json");
        if (usersFile.exists()) {
            usersFile.delete();
        }
        
        File transactionsFile = new File("transactions.json");
        if (transactionsFile.exists()) {
            transactionsFile.delete();
        }
        
        // Create a test user
        testUser = User.addUser(TEST_USER_NAME);
    }
    
    @Test
    public void testBorrowLimit() {
        // Reset borrow limit to default
        Library.setBorrowLimit(3);
        
        // Add 4 books
        Book.addBook("9781111111111", "Book 1", TEST_AUTHOR, 1);
        Book.addBook("9782222222222", "Book 2", TEST_AUTHOR, 1);
        Book.addBook("9783333333333", "Book 3", TEST_AUTHOR, 1);
        Book.addBook("9784444444444", "Book 4", TEST_AUTHOR, 1);
        
        // Borrow 3 books (should succeed)
        assertTrue(testUser.borrowBookByIsbn("9781111111111"));
        assertTrue(testUser.borrowBookByIsbn("9782222222222"));
        assertTrue(testUser.borrowBookByIsbn("9783333333333"));
        
        // Try to borrow 4th book (should fail)
        assertFalse(testUser.borrowBookByIsbn("9784444444444"));
        
        // Increase limit
        Library.setBorrowLimit(4);
        
        // Now we should be able to borrow the 4th book
        assertTrue(testUser.borrowBookByIsbn("9784444444444"));
    }
    
    @Test
    public void testBookAvailability() {
        // Add a book with 1 copy
        Book.addBook(TEST_ISBN, TEST_TITLE, TEST_AUTHOR, 1);
        
        // First user borrows the book
        assertTrue(testUser.borrowBookByIsbn(TEST_ISBN));
        
        // Create a second user
        User secondUser = User.addUser("Second User");
        
        // Second user tries to borrow the same book (should fail)
        assertFalse(secondUser.borrowBookByIsbn(TEST_ISBN));
        
        // First user returns the book
        String copyId = testUser.getBorrowedCopyIds().get(0);
        assertTrue(testUser.returnBookAndRecordTransaction(copyId));
        
        // Now second user should be able to borrow it
        assertTrue(secondUser.borrowBookByIsbn(TEST_ISBN));
    }
    
    @Test
    public void testMultipleCopies() {
        // Add a book with 3 copies
        Book.addBook(TEST_ISBN, TEST_TITLE, TEST_AUTHOR, 3);
        
        // First user borrows a copy
        assertTrue(testUser.borrowBookByIsbn(TEST_ISBN));
        
        // Create 2 more users and have them borrow copies
        User user2 = User.addUser("User 2");
        User user3 = User.addUser("User 3");
        
        assertTrue(user2.borrowBookByIsbn(TEST_ISBN));
        assertTrue(user3.borrowBookByIsbn(TEST_ISBN));
        
        // Create a 4th user - should not be able to borrow
        User user4 = User.addUser("User 4");
        assertFalse(user4.borrowBookByIsbn(TEST_ISBN));
    }
    
    @Test
    public void testTransactionHistory() {
        // Add a book
        Book.addBook(TEST_ISBN, TEST_TITLE, TEST_AUTHOR, 1);
        
        // Borrow the book
        assertTrue(testUser.borrowBookByIsbn(TEST_ISBN));
        
        // Get transactions for this user
        List<Transaction> transactions = Transaction.getAllTransactions();
        
        // Should have one transaction
        assertFalse(transactions.isEmpty());
        Transaction tx = transactions.get(0);
        assertEquals(testUser.getID(), tx.getUserId());
        assertEquals(TEST_ISBN, tx.getBookIsbn());
        assertEquals("BORROW", tx.getType());
        
        // Return the book
        String copyId = testUser.getBorrowedCopyIds().get(0);
        assertTrue(testUser.returnBookAndRecordTransaction(copyId));
        
        // Should now have two transactions
        transactions = Transaction.getAllTransactions();
        assertEquals(2, transactions.size());
        
        // Last one should be a RETURN
        tx = transactions.get(1);
        assertEquals(testUser.getID(), tx.getUserId());
        assertEquals(TEST_ISBN, tx.getBookIsbn());
        assertEquals("RETURN", tx.getType());
    }
}