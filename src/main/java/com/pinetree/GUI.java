package com.pinetree;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

public class GUI extends JFrame {
    private JPanel mainPanel;
    private CardLayout cardLayout;

    // Panels
    private JPanel transactionsPanel;
    private JPanel userPanel;
    private JPanel bookPanel;

    // Tables
    private JTable transactionsTable;
    private JTable userTable;
    private JTable bookTable;

    public GUI() {
        setTitle("Library Management System");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);

        setJMenuBar(createMenuBar());

        initTransactionsPanel();
        initUserPanel();
        initBookPanel();

        mainPanel.add(transactionsPanel, "transactions");
        mainPanel.add(userPanel, "users");
        mainPanel.add(bookPanel, "books");

        add(mainPanel);
        cardLayout.show(mainPanel, "transactions");

        refreshTransactions(); // Load transactions initially
        refreshUsers();        // Load users initially
        refreshBooks();        // Load books initially
    }

    private JMenuBar createMenuBar() {
        JMenuBar menuBar = new JMenuBar();
        JMenu viewMenu = new JMenu("View");

        JMenuItem transactionsMenuItem = new JMenuItem("Transactions");
        JMenuItem usersMenuItem = new JMenuItem("Users");
        JMenuItem booksMenuItem = new JMenuItem("Books");

        transactionsMenuItem.addActionListener(e -> {
            cardLayout.show(mainPanel, "transactions");
            refreshTransactions();
        });

        usersMenuItem.addActionListener(e -> {
            cardLayout.show(mainPanel, "users");
            refreshUsers(); // Refresh user data
        });

        booksMenuItem.addActionListener(e -> {
            cardLayout.show(mainPanel, "books");
            refreshBooks(); // Refresh book data
        });

        viewMenu.add(transactionsMenuItem);
        viewMenu.add(usersMenuItem);
        viewMenu.add(booksMenuItem);

        menuBar.add(viewMenu);
        return menuBar;
    }

    private void initTransactionsPanel() {
        transactionsPanel = new JPanel(new BorderLayout());

        String[] columns = {"Transaction ID", "User ID", "Book ISBN", "Transaction Type", "Transaction Date", "Fine Amount"};
        DefaultTableModel model = new DefaultTableModel(columns, 0);
        transactionsTable = new JTable(model);
        JScrollPane scrollPane = new JScrollPane(transactionsTable);

        JPanel buttonsPanel = new JPanel();
        JButton addButton = new JButton("Add Transaction");
        JButton editButton = new JButton("Edit Transaction");
        JButton deleteButton = new JButton("Delete Transaction");
        JButton refreshButton = new JButton("Refresh");

        buttonsPanel.add(addButton);
        buttonsPanel.add(editButton);
        buttonsPanel.add(deleteButton);
        buttonsPanel.add(refreshButton);

        addButton.addActionListener(e -> addTransaction());
        editButton.addActionListener(e -> editTransaction());
        deleteButton.addActionListener(e -> deleteTransaction());
        refreshButton.addActionListener(e -> refreshTransactions());

        transactionsPanel.add(scrollPane, BorderLayout.CENTER);
        transactionsPanel.add(buttonsPanel, BorderLayout.SOUTH);
    }

    private void initUserPanel() {
        userPanel = new JPanel(new BorderLayout());

        String[] columns = {"User ID", "Name", "Borrowed ISBNs"};
        DefaultTableModel model = new DefaultTableModel(columns, 0);
        userTable = new JTable(model);
        JScrollPane scrollPane = new JScrollPane(userTable);

        JPanel searchPanel = new JPanel();
        JTextField searchField = new JTextField(20);
        JButton searchButton = new JButton("Search User");
        searchPanel.add(new JLabel("User Name or ID:"));
        searchPanel.add(searchField);
        searchPanel.add(searchButton);

        JPanel buttonsPanel = new JPanel();
        JButton addButton = new JButton("Add User");
        JButton editButton = new JButton("Edit User");
        JButton deleteButton = new JButton("Delete User");
        JButton viewHistoryButton = new JButton("View History");

        buttonsPanel.add(addButton);
        buttonsPanel.add(editButton);
        buttonsPanel.add(deleteButton);
        buttonsPanel.add(viewHistoryButton);

        // Placeholder action listeners
        searchButton.addActionListener(e -> searchUser(searchField.getText()));
        addButton.addActionListener(e -> addUser());
        editButton.addActionListener(e -> editUser());
        deleteButton.addActionListener(e -> deleteUser());
        viewHistoryButton.addActionListener(e -> viewUserHistory());

        userPanel.add(searchPanel, BorderLayout.NORTH);
        userPanel.add(scrollPane, BorderLayout.CENTER);
        userPanel.add(buttonsPanel, BorderLayout.SOUTH);
    }

    private void initBookPanel() {
        bookPanel = new JPanel(new BorderLayout());

        String[] columns = {"ISBN", "Title", "Author", "Quantity"};
        DefaultTableModel model = new DefaultTableModel(columns, 0);
        bookTable = new JTable(model);
        JScrollPane scrollPane = new JScrollPane(bookTable);

        JPanel searchPanel = new JPanel();
        JTextField searchField = new JTextField(20);
        JButton searchButton = new JButton("Search Book");
        searchPanel.add(new JLabel("Book Title or ID:"));
        searchPanel.add(searchField);
        searchPanel.add(searchButton);

        JPanel buttonsPanel = new JPanel();
        JButton addButton = new JButton("Add Book");
        JButton editButton = new JButton("Edit Book");
        JButton deleteButton = new JButton("Delete Book");
        JButton viewHistoryButton = new JButton("View History");

        buttonsPanel.add(addButton);
        buttonsPanel.add(editButton);
        buttonsPanel.add(deleteButton);
        buttonsPanel.add(viewHistoryButton);

        // Placeholder action listeners
        searchButton.addActionListener(e -> searchBook(searchField.getText()));
        addButton.addActionListener(e -> addBook());
        editButton.addActionListener(e -> editBook());
        deleteButton.addActionListener(e -> deleteBook());
        viewHistoryButton.addActionListener(e -> viewBookHistory());

        bookPanel.add(searchPanel, BorderLayout.NORTH);
        bookPanel.add(scrollPane, BorderLayout.CENTER);
        bookPanel.add(buttonsPanel, BorderLayout.SOUTH);
    }

    private void refreshTransactions() {
        DefaultTableModel model = (DefaultTableModel) transactionsTable.getModel();
        model.setRowCount(0);

        try (Connection conn = DatabaseHelper.getConnection();
             PreparedStatement stmt = conn.prepareStatement("SELECT * FROM transactions");
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Object[] row = {
                    rs.getInt("transaction_id"),
                    rs.getInt("user_id"),
                    rs.getInt("book_isbn"),
                    rs.getString("transaction_type"),
                    rs.getString("transaction_date"),
                    rs.getString("fine_amount")
                };
                model.addRow(row);
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error loading transactions: " + e.getMessage());
        }
    }

    private void refreshBooks() {
        DefaultTableModel model = (DefaultTableModel) bookTable.getModel();
        model.setRowCount(0);

        try (Connection conn = DatabaseHelper.getConnection();
             PreparedStatement stmt = conn.prepareStatement("SELECT * FROM books");
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Object[] row = {
                    rs.getString("isbn"),
                    rs.getString("title"),
                    rs.getString("author"),
                    rs.getInt("quantity")
                };
                model.addRow(row);
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error loading books: " + e.getMessage());
        }
    }

    private void refreshUsers() {
        DefaultTableModel model = (DefaultTableModel) userTable.getModel();
        model.setRowCount(0);

        try (Connection conn = DatabaseHelper.getConnection();
             PreparedStatement stmt = conn.prepareStatement("SELECT * FROM users");
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Object[] row = {
                    rs.getString("user_id"),
                    rs.getString("name"),
                    rs.getString("borrowed_isbns") // Assuming this is a comma-separated list of borrowed ISBNs
                };
                model.addRow(row);
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error loading users: " + e.getMessage());
        }
    }

    // Stub methods
    private void addTransaction() {
        JOptionPane.showMessageDialog(this, "Add Transaction - TODO");
    }

    private void editTransaction() {
        JOptionPane.showMessageDialog(this, "Edit Transaction - TODO");
    }

    private void deleteTransaction() {
        JOptionPane.showMessageDialog(this, "Delete Transaction - TODO");
    }

    private void searchUser(String query) {
        JOptionPane.showMessageDialog(this, "Search User - TODO: " + query);
    }

    private void addUser() {}
    private void editUser() {}
    private void deleteUser() {}
    private void viewUserHistory() {}

    private void searchBook(String query) {
        JOptionPane.showMessageDialog(this, "Search Book - TODO: " + query);
    }

    private void addBook() {
        JTextField isbnField = new JTextField();
        JTextField titleField = new JTextField();
        JTextField authorField = new JTextField();
        JTextField quantityField = new JTextField();

        Object[] message = {
            "ISBN:", isbnField,
            "Title:", titleField,
            "Author:", authorField,
            "Quantity:", quantityField
        };

        int option = JOptionPane.showConfirmDialog(this, message, "Add Book", JOptionPane.OK_CANCEL_OPTION);
        if (option == JOptionPane.OK_OPTION) {
            String isbn = isbnField.getText();
            String title = titleField.getText();
            String author = authorField.getText();
            int quantity;

            try {
                quantity = Integer.parseInt(quantityField.getText());
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "Quantity must be a valid number.");
                return;
            }

            try (Connection conn = DatabaseHelper.getConnection();
                 PreparedStatement stmt = conn.prepareStatement("INSERT INTO books (isbn, title, author, quantity) VALUES (?, ?, ?, ?)")) {

                stmt.setString(1, isbn);
                stmt.setString(2, title);
                stmt.setString(3, author);
                stmt.setInt(4, quantity);
                stmt.executeUpdate();

                JOptionPane.showMessageDialog(this, "Book added successfully.");
                refreshBooks();

            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Error adding book: " + e.getMessage());
            }
        }
    }

    private void editBook() {}

    private void deleteBook() {
        int selectedRow = bookTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a book to delete.");
            return;
        }

        String isbn = (String) bookTable.getValueAt(selectedRow, 0);

        int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete the selected book?", "Confirm Delete", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            try (Connection conn = DatabaseHelper.getConnection();
                 PreparedStatement stmt = conn.prepareStatement("DELETE FROM books WHERE isbn = ?")) {

                stmt.setString(1, isbn);
                stmt.executeUpdate();

                JOptionPane.showMessageDialog(this, "Book deleted successfully.");
                refreshBooks();

            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Error deleting book: " + e.getMessage());
            }
        }
    }

    private void viewBookHistory() {}

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new GUI().setVisible(true));
    }

    // SQLite connection helper
    public static class DatabaseHelper {
        private static final String DB_URL = "jdbc:sqlite:Library.db";

        public static Connection getConnection() throws SQLException {
            return DriverManager.getConnection(DB_URL);
        }
    }
}
