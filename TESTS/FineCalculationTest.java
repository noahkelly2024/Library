package com.pinetree;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import java.io.File;
import java.time.LocalDate;
import java.util.List;

public class FineCalculationTest {
    
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
        
        // Add a test book
        Book.addBook(TEST_ISBN, TEST_TITLE, TEST_AUTHOR, 1);
    }
    
    @Test
    public void testNoFineForOnTimeReturn() {
        // Add a BORROW transaction for 7 days ago (within 14-day limit)
        LocalDate borrowDate = LocalDate.now().minusDays(7);
        Transaction borrowTx = new Transaction(testUser.getID(), TEST_ISBN, "BORROW", borrowDate);
        Library.addTransaction(borrowTx);
        
        // Add a RETURN transaction for today
        Transaction returnTx = new Transaction(testUser.getID(), TEST_ISBN, "RETURN", LocalDate.now());
        Library.addTransaction(returnTx);
        
        // Calculate fine - should be 0
        int fine = FineManager.calculateFine(testUser.getID());
        assertEquals(0, fine);
    }
    
    @Test
    public void testFineForLateReturn() {
        // Add a BORROW transaction for 20 days ago (6 days overdue)
        LocalDate borrowDate = LocalDate.now().minusDays(20);
        Transaction borrowTx = new Transaction(testUser.getID(), TEST_ISBN, "BORROW", borrowDate);
        Library.addTransaction(borrowTx);
        
        // Add a RETURN transaction for today
        Transaction returnTx = new Transaction(testUser.getID(), TEST_ISBN, "RETURN", LocalDate.now());
        Library.addTransaction(returnTx);
        
        // Calculate fine - should be $6 (6 days late)
        int fine = FineManager.calculateFine(testUser.getID());
        assertEquals(6, fine);
    }
    
    @Test
    public void testFineForCurrentlyOverdueBook() {
        // Add a BORROW transaction for 20 days ago (6 days overdue)
        LocalDate borrowDate = LocalDate.now().minusDays(20);
        Transaction borrowTx = new Transaction(testUser.getID(), TEST_ISBN, "BORROW", borrowDate);
        Library.addTransaction(borrowTx);
        
        // No RETURN transaction - book is still out
        
        // Calculate fine - should be $6 (6 days late)
        int fine = FineManager.calculateFine(testUser.getID());
        assertEquals(6, fine);
    }
    
    @Test
    public void testMultipleOverdueBooks() {
        // Add a second book
        Book.addBook("9789876543210", "Second Test Book", TEST_AUTHOR, 1);
        
        // First book - borrowed 20 days ago (6 days overdue)
        LocalDate borrowDate1 = LocalDate.now().minusDays(20);
        Transaction borrowTx1 = new Transaction(testUser.getID(), TEST_ISBN, "BORROW", borrowDate1);
        Library.addTransaction(borrowTx1);
        
        // Second book - borrowed 25 days ago (11 days overdue)
        LocalDate borrowDate2 = LocalDate.now().minusDays(25);
        Transaction borrowTx2 = new Transaction(testUser.getID(), "9789876543210", "BORROW", borrowDate2);
        Library.addTransaction(borrowTx2);
        
        // No RETURN transactions - books are still out
        
        // Calculate fine - should be $17 ($6 + $11)
        int fine = FineManager.calculateFine(testUser.getID());
        assertEquals(17, fine);
    }
}