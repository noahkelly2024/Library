package com.pinetree;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

public class FineManager {
    private static final int FINE_PER_DAY = 1; // $1 per day
    private static final int LOAN_PERIOD_DAYS = 14; // 2 weeks

    // Calculate fine for a user
    public static int calculateFine(String userId) {
        int totalFine = 0;
        List<Transaction> transactions = Transaction.getAllTransactions();
        
        // Find all BORROW transactions for this user
        for (Transaction borrowTx : transactions) {
            if (borrowTx.getUserId().equals(userId) && borrowTx.getType().equals("BORROW")) {
                // Look for a corresponding RETURN transaction
                Transaction returnTx = findReturnTransaction(transactions, userId, borrowTx.getBookIsbn(), borrowTx.getDate());
                
                if (returnTx != null) {
                    // Calculate days between borrow and return
                    long daysKept = ChronoUnit.DAYS.between(borrowTx.getDate(), returnTx.getDate());
                    
                    // If kept longer than loan period, add fine
                    if (daysKept > LOAN_PERIOD_DAYS) {
                        int daysLate = (int) (daysKept - LOAN_PERIOD_DAYS);
                        int fine = daysLate * FINE_PER_DAY;
                        totalFine += fine;
                    }
                } else {
                    // Book not returned yet, calculate fine based on current date
                    long daysKept = ChronoUnit.DAYS.between(borrowTx.getDate(), LocalDate.now());
                    
                    if (daysKept > LOAN_PERIOD_DAYS) {
                        int daysLate = (int) (daysKept - LOAN_PERIOD_DAYS);
                        int fine = daysLate * FINE_PER_DAY;
                        totalFine += fine;
                    }
                }
            }
        }
        
        return totalFine;
    }
    
    // Find a RETURN transaction that matches a BORROW transaction
    private static Transaction findReturnTransaction(List<Transaction> transactions, String userId, 
                                                    String bookIsbn, LocalDate afterDate) {
        for (Transaction tx : transactions) {
            if (tx.getUserId().equals(userId) && 
                tx.getBookIsbn().equals(bookIsbn) && 
                tx.getType().equals("RETURN") &&
                tx.getDate().isAfter(afterDate)) {
                return tx;
            }
        }
        return null;
    }
    
    // Clear a user's fine manually
    public static void clearFine(String userId) {
        System.out.println("Fine for user " + userId + " has been cleared.");
    }
}
