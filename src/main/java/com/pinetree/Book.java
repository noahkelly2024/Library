package com.pinetree;

import java.sql.*;

public class Book {
    private String isbn;
    private String title;
    private String author;
    private int quantity;

    // Constructor
    public Book(String isbn, String title, String author, int quantity) {
        this.isbn = isbn;
        this.title = title;
        this.author = author;
        this.quantity = quantity;
    }

    // Getters
    public String getIsbn() {
        return isbn;
    }

    public String getTitle() {
        return title;
    }

    public String getAuthor() {
        return author;
    }

    public int getQuantity() {
        return quantity;
    }

    // Setter for Quantity
    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    // Static method to find a book by ISBN
    public static Book findBookByIsbn(String isbn) {
        String sql = "SELECT * FROM books WHERE isbn = ?";
        Book book = null;

        try (Connection conn = DriverManager.getConnection(LibraryDatabase.DB_URL);
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, isbn);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                String title = rs.getString("title");
                String author = rs.getString("author");
                int quantity = rs.getInt("quantity");

                book = new Book(isbn, title, author, quantity);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return book;
    }

}
