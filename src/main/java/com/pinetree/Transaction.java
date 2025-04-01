package com.pinetree;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import java.io.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Transaction {
    private String userId;
    private String bookIsbn;
    private String type; // "BORROW" or "RETURN"
    private LocalDate date;
    private static final String filepath = "transactions.json";

    public Transaction(String userId, String bookIsbn, String type, LocalDate date) {
        this.userId = userId;
        this.bookIsbn = bookIsbn;
        this.type = type;
        this.date = date;
    }

    // Getters
    public String getUserId() { return userId; }
    public String getBookIsbn() { return bookIsbn; }
    public String getType() { return type; }
    public LocalDate getDate() { return date; }

    // Static method to add a transaction
    public static void addTransaction(Transaction transaction) {
        List<Transaction> transactions = readTransactionsFromJson();
        transactions.add(transaction);
        saveTransactions(transactions);
    }

    // Static method to get all transactions
    public static List<Transaction> getAllTransactions() {
        return readTransactionsFromJson();
    }

    // Save transactions to JSON
    public static void saveTransactions(List<Transaction> transactions) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filepath))) {
            Gson gson = new GsonBuilder()
                .setPrettyPrinting()
                .create();
            gson.toJson(transactions, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Read transactions from JSON
    private static List<Transaction> readTransactionsFromJson() {
        try (BufferedReader reader = new BufferedReader(new FileReader(filepath))) {
            return new Gson().fromJson(reader, new TypeToken<List<Transaction>>() {}.getType());
        } catch (IOException e) {
            return new ArrayList<>();
        }
    }

    /**
     * Display all transactions for a specific user
     * @param userId The ID of the user
     */
    public static void displayUserTransactions(String userId) {
        List<Transaction> transactions = getAllTransactions();
        System.out.println("Transaction history for user " + userId + ":");
        
        boolean found = false;
        for (Transaction tx : transactions) {
            if (tx.getUserId().equals(userId)) {
                found = true;
                Book book = Book.findBookByIsbn(tx.getBookIsbn());
                String bookTitle = book != null ? book.getTitle() : "Unknown book";
                System.out.println("  - " + tx.getDate() + ": " + tx.getType() + " " + bookTitle);
            }
        }
        
        if (!found) {
            System.out.println("No transactions found for this user.");
        }
    }
}