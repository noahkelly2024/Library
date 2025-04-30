package com.pinetree;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

/**
 * The graphical user interface (GUI) for the library system.
 * Provides panels for managing transactions, users, and books.
 */
public class LibraryGUI extends JFrame {
    private JPanel mainPanel;
    private CardLayout cardLayout;

    // Panels for different sections of the library system
    private JPanel transactionsPanel;
    private JPanel userPanel;
    private JPanel bookPanel;

    // Tables for displaying data
    private JTable transactionsTable;
    private JTable userTable;
    private JTable bookTable;

    public LibraryGUI() {
        try {
            UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
        } catch (Exception e) {
            System.err.println("Failed to set Look and Feel: " + e.getMessage());
        }

        setGlobalFont(new Font("Arial", Font.PLAIN, 14)); // Set global font

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

    /**
     * Initializes the Transactions Panel.
     * This panel allows users to manage transactions (add, edit, delete, refresh, and upload CSV).
     */
    private void initTransactionsPanel() {
        transactionsPanel = new JPanel(new BorderLayout());
        transactionsPanel.setBackground(new Color(245, 245, 245)); // Light gray background

        // Define table columns for transactions
        String[] columns = {"Transaction ID", "User ID", "Book ISBN", "Transaction Type", "Transaction Date", "Fine Amount"};
        DefaultTableModel model = new DefaultTableModel(columns, 0);
        transactionsTable = new JTable(model);
        transactionsTable.setFont(new Font("Arial", Font.PLAIN, 14));
        transactionsTable.setRowHeight(25);
        transactionsTable.setGridColor(Color.LIGHT_GRAY);
        JScrollPane scrollPane = new JScrollPane(transactionsTable);

        // Create buttons for managing transactions
        JPanel buttonsPanel = new JPanel();
        buttonsPanel.setBackground(new Color(224, 255, 255)); // Light cyan
        JButton addButton = new JButton("Add Transaction");
        JButton editButton = new JButton("Edit Transaction");
        JButton deleteButton = new JButton("Delete Transaction");
        JButton refreshButton = new JButton("Refresh");
        JButton uploadCsvButton = new JButton("Mass Upload Transactions");

        // Style buttons
        JButton[] buttons = {addButton, editButton, deleteButton, refreshButton, uploadCsvButton};
        for (JButton button : buttons) {
            button.setBackground(new Color(70, 130, 180)); // Steel blue
            button.setForeground(Color.WHITE);
            button.setFont(new Font("Arial", Font.BOLD, 12));
        }

        // Add buttons to the panel
        buttonsPanel.add(addButton);
        buttonsPanel.add(editButton);
        buttonsPanel.add(deleteButton);
        buttonsPanel.add(refreshButton);
        buttonsPanel.add(uploadCsvButton);

        // Add action listeners for button functionality
        addButton.addActionListener(e -> addTransaction());
        editButton.addActionListener(e -> editTransaction());
        deleteButton.addActionListener(e -> deleteTransaction());
        refreshButton.addActionListener(e -> refreshTransactions());
        uploadCsvButton.addActionListener(e -> {
            MassUpload massUpload = new MassUpload();
            massUpload.uploadCsv("transactions");
            refreshTransactions();
        });

        // Add components to the transactions panel
        transactionsPanel.add(scrollPane, BorderLayout.CENTER);
        transactionsPanel.add(buttonsPanel, BorderLayout.SOUTH);
    }

    /**
     * Initializes the Users Panel.
     * This panel allows users to manage library users (add, edit, delete, refresh, and upload CSV).
     */
    private void initUserPanel() {
        userPanel = new JPanel(new BorderLayout());
        userPanel.setBackground(new Color(245, 245, 245)); // Light gray background

        // Define table columns for users
        String[] columns = {"User ID", "Name", "Borrowed ISBNs"};
        DefaultTableModel model = new DefaultTableModel(columns, 0);
        userTable = new JTable(model);
        userTable.setFont(new Font("Arial", Font.PLAIN, 14));
        userTable.setRowHeight(25);
        userTable.setGridColor(Color.LIGHT_GRAY);
        JScrollPane scrollPane = new JScrollPane(userTable);

        // Create search panel for users
        JPanel searchPanel = new JPanel();
        searchPanel.setBackground(new Color(211, 211, 211)); // Light gray
        JTextField searchField = new JTextField(20);
        JButton searchButton = new JButton("Search User");
        searchButton.setBackground(new Color(100, 149, 237)); // Cornflower blue
        searchButton.setForeground(Color.WHITE);
        searchButton.setFont(new Font("Arial", Font.BOLD, 12));
        searchPanel.add(new JLabel("User Name or ID:"));
        searchPanel.add(searchField);
        searchPanel.add(searchButton);

        // Create buttons for managing users
        JPanel buttonsPanel = new JPanel();
        buttonsPanel.setBackground(new Color(211, 211, 211));
        JButton addButton = new JButton("Add User");
        JButton editButton = new JButton("Edit User");
        JButton deleteButton = new JButton("Delete User");
        JButton uploadCsvButton = new JButton("Mass Upload Users");
        JButton refreshButton = new JButton("Refresh");

        // Style buttons
        JButton[] buttons = {addButton, editButton, deleteButton, uploadCsvButton, refreshButton};
        for (JButton button : buttons) {
            button.setBackground(new Color(70, 130, 180)); // Steel blue
            button.setForeground(Color.WHITE);
            button.setFont(new Font("Arial", Font.BOLD, 12));
        }

        // Add buttons to the panel
        buttonsPanel.add(addButton);
        buttonsPanel.add(editButton);
        buttonsPanel.add(deleteButton);
        buttonsPanel.add(uploadCsvButton);
        buttonsPanel.add(refreshButton);

        // Add action listeners for button functionality
        searchButton.addActionListener(e -> searchUser(searchField.getText()));
        addButton.addActionListener(e -> addUser());
        editButton.addActionListener(e -> editUser());
        deleteButton.addActionListener(e -> deleteUser());
        uploadCsvButton.addActionListener(e -> {
            MassUpload massUpload = new MassUpload();
            massUpload.uploadCsv("users");
            refreshUsers();
        });
        refreshButton.addActionListener(e -> refreshUsers());

        // Add components to the users panel
        userPanel.add(searchPanel, BorderLayout.NORTH);
        userPanel.add(scrollPane, BorderLayout.CENTER);
        userPanel.add(buttonsPanel, BorderLayout.SOUTH);
    }

    private void initBookPanel() {
        bookPanel = new JPanel(new BorderLayout());
        bookPanel.setBackground(new Color(245, 245, 245)); // Light gray background

        String[] columns = {"ISBN", "Title", "Author", "Quantity"};
        DefaultTableModel model = new DefaultTableModel(columns, 0);
        bookTable = new JTable(model);
        bookTable.setFont(new Font("Arial", Font.PLAIN, 14));
        bookTable.setRowHeight(25);
        bookTable.setGridColor(Color.LIGHT_GRAY);
        JScrollPane scrollPane = new JScrollPane(bookTable);

        JPanel searchPanel = new JPanel();
        searchPanel.setBackground(new Color(211, 211, 211)); // Light gray
        JTextField searchField = new JTextField(20);
        JButton searchButton = new JButton("Search Book");
        searchButton.setBackground(new Color(100, 149, 237)); // Cornflower blue
        searchButton.setForeground(Color.WHITE);
        searchButton.setFont(new Font("Arial", Font.BOLD, 12));
        searchPanel.add(new JLabel("Book Title or ID:"));
        searchPanel.add(searchField);
        searchPanel.add(searchButton);

        JPanel buttonsPanel = new JPanel();
        buttonsPanel.setBackground(new Color(211, 211, 211)); // Light cyan
        JButton addButton = new JButton("Add Book");
        JButton editButton = new JButton("Edit Book");
        JButton deleteButton = new JButton("Delete Book");
        JButton uploadCsvButton = new JButton("Mass Upload Books");
        JButton refreshButton = new JButton("Refresh");

        // Style buttons
        JButton[] buttons = {addButton, editButton, deleteButton, uploadCsvButton, refreshButton};
        for (JButton button : buttons) {
            button.setBackground(new Color(70, 130, 180)); // Steel blue
            button.setForeground(Color.WHITE);
            button.setFont(new Font("Arial", Font.BOLD, 12));
        }

        buttonsPanel.add(addButton);
        buttonsPanel.add(editButton);
        buttonsPanel.add(deleteButton);
        buttonsPanel.add(uploadCsvButton);
        buttonsPanel.add(refreshButton);

        searchButton.addActionListener(e -> searchBook(searchField.getText()));
        addButton.addActionListener(e -> addBook());
        editButton.addActionListener(e -> editBook());
        deleteButton.addActionListener(e -> deleteBook());
        uploadCsvButton.addActionListener(e -> {
            MassUpload massUpload = new MassUpload();
            massUpload.uploadCsv("books");
            refreshBooks();
        });
        refreshButton.addActionListener(e -> refreshBooks());

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

    private void searchUser(String query) {
        DefaultTableModel model = (DefaultTableModel) userTable.getModel();
        model.setRowCount(0); // Clear the table

        try (Connection conn = DatabaseHelper.getConnection();
             PreparedStatement stmt = conn.prepareStatement("SELECT * FROM users WHERE user_id LIKE ? OR name LIKE ?")) {

            stmt.setString(1, "%" + query + "%");
            stmt.setString(2, "%" + query + "%");
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Object[] row = {
                    rs.getString("user_id"),
                    rs.getString("name"),
                    rs.getString("borrowed_isbns") // Assuming this is a comma-separated list of borrowed ISBNs
                };
                model.addRow(row);
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error searching users: " + e.getMessage());
        }
    }

    private void addTransaction() {
        JOptionPane.showMessageDialog(this, "Add Transaction - TODO");
    }

    private void editTransaction() {
        JOptionPane.showMessageDialog(this, "Edit Transaction - TODO");
    }

    private void deleteTransaction() {
        JOptionPane.showMessageDialog(this, "Delete Transaction - TODO");
    }

    private void addUser() {
        String generatedUserId = "U" + System.currentTimeMillis(); // Generate a unique User ID
        JTextField nameField = new JTextField();
        JTextField borrowedBooksField = new JTextField(); // Optional: Comma-separated list of borrowed ISBNs

        Object[] message = {
            "Generated User ID: " + generatedUserId,
            "Name:", nameField,
            "Borrowed Books (comma-separated ISBNs):", borrowedBooksField
        };

        int option = JOptionPane.showConfirmDialog(this, message, "Add User", JOptionPane.OK_CANCEL_OPTION);
        if (option == JOptionPane.OK_OPTION) {
            String name = nameField.getText().trim();
            String borrowedBooks = borrowedBooksField.getText().trim();

            if (name.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Name cannot be empty.");
                return;
            }

            try (Connection conn = DatabaseHelper.getConnection();
                 PreparedStatement stmt = conn.prepareStatement("INSERT INTO users (user_id, name, borrowed_isbns) VALUES (?, ?, ?)")) {

                stmt.setString(1, generatedUserId);
                stmt.setString(2, name);
                stmt.setString(3, borrowedBooks.isEmpty() ? null : borrowedBooks);
                stmt.executeUpdate();

                JOptionPane.showMessageDialog(this, "User added successfully.");
                refreshUsers();

            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Error adding user: " + e.getMessage());
            }
        }
    }

    private void editUser() {
        int selectedRow = userTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a user to edit.");
            return;
        }

        String userId = (String) userTable.getValueAt(selectedRow, 0);
        String name = (String) userTable.getValueAt(selectedRow, 1);
        String borrowedBooks = (String) userTable.getValueAt(selectedRow, 2);

        JTextField nameField = new JTextField(name);
        JTextField borrowedBooksField = new JTextField(borrowedBooks);

        Object[] message = {
            "User ID (cannot be edited): " + userId,
            "Name:", nameField,
            "Borrowed Books (comma-separated ISBNs):", borrowedBooksField
        };

        int option = JOptionPane.showConfirmDialog(this, message, "Edit User", JOptionPane.OK_CANCEL_OPTION);
        if (option == JOptionPane.OK_OPTION) {
            String newName = nameField.getText().trim();
            String newBorrowedBooks = borrowedBooksField.getText().trim();

            if (newName.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Name cannot be empty.");
                return;
            }

            try (Connection conn = DatabaseHelper.getConnection();
                 PreparedStatement stmt = conn.prepareStatement("UPDATE users SET name = ?, borrowed_isbns = ? WHERE user_id = ?")) {

                stmt.setString(1, newName);
                stmt.setString(2, newBorrowedBooks.isEmpty() ? null : newBorrowedBooks);
                stmt.setString(3, userId);
                stmt.executeUpdate();

                JOptionPane.showMessageDialog(this, "User updated successfully.");
                refreshUsers();

            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Error updating user: " + e.getMessage());
            }
        }
    }

    private void deleteUser() {
        int selectedRow = userTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a user to delete.");
            return;
        }

        String userId = (String) userTable.getValueAt(selectedRow, 0);

        int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete the selected user?", "Confirm Delete", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            try (Connection conn = DatabaseHelper.getConnection();
                 PreparedStatement stmt = conn.prepareStatement("DELETE FROM users WHERE user_id = ?")) {

                stmt.setString(1, userId);
                stmt.executeUpdate();

                JOptionPane.showMessageDialog(this, "User deleted successfully.");
                refreshUsers();

            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Error deleting user: " + e.getMessage());
            }
        }
    }

    private void searchBook(String query) {
        DefaultTableModel model = (DefaultTableModel) bookTable.getModel();
        model.setRowCount(0); // Clear the table

        try (Connection conn = DatabaseHelper.getConnection();
             PreparedStatement stmt = conn.prepareStatement("SELECT * FROM books WHERE isbn LIKE ? OR title LIKE ? OR author LIKE ?")) {

            stmt.setString(1, "%" + query + "%");
            stmt.setString(2, "%" + query + "%");
            stmt.setString(3, "%" + query + "%");
            ResultSet rs = stmt.executeQuery();

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
            JOptionPane.showMessageDialog(this, "Error searching books: " + e.getMessage());
        }
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
            String isbn = isbnField.getText().trim();
            String title = titleField.getText().trim();
            String author = authorField.getText().trim();
            String quantityText = quantityField.getText().trim();

            if (isbn.isEmpty() || title.isEmpty() || author.isEmpty() || quantityText.isEmpty()) {
                JOptionPane.showMessageDialog(this, "All fields are required and cannot be empty.");
                return;
            }

            int quantity;
            try {
                quantity = Integer.parseInt(quantityText);
                if (quantity < 0) {
                    JOptionPane.showMessageDialog(this, "Quantity must be a non-negative number.");
                    return;
                }
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

    private void editBook() {
        int selectedRow = bookTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a book to edit.");
            return;
        }

        String isbn = (String) bookTable.getValueAt(selectedRow, 0);
        String title = (String) bookTable.getValueAt(selectedRow, 1);
        String author = (String) bookTable.getValueAt(selectedRow, 2);
        int quantity = (int) bookTable.getValueAt(selectedRow, 3);

        JTextField titleField = new JTextField(title);
        JTextField authorField = new JTextField(author);
        JTextField quantityField = new JTextField(String.valueOf(quantity));

        Object[] message = {
            "Title:", titleField,
            "Author:", authorField,
            "Quantity:", quantityField
        };

        int option = JOptionPane.showConfirmDialog(this, message, "Edit Book", JOptionPane.OK_CANCEL_OPTION);
        if (option == JOptionPane.OK_OPTION) {
            String newTitle = titleField.getText();
            String newAuthor = authorField.getText();
            int newQuantity;

            try {
                newQuantity = Integer.parseInt(quantityField.getText());
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "Quantity must be a valid number.");
                return;
            }

            try (Connection conn = DatabaseHelper.getConnection();
                 PreparedStatement stmt = conn.prepareStatement("UPDATE books SET title = ?, author = ?, quantity = ? WHERE isbn = ?")) {

                stmt.setString(1, newTitle);
                stmt.setString(2, newAuthor);
                stmt.setInt(3, newQuantity);
                stmt.setString(4, isbn);
                stmt.executeUpdate();

                JOptionPane.showMessageDialog(this, "Book updated successfully.");
                refreshBooks();

            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Error updating book: " + e.getMessage());
            }
        }
    }

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

    public void initialize() {
        // Initialize the GUI components and load data
        setGlobalFont(new Font("Arial", Font.PLAIN, 14));
        refreshTransactions();
        refreshUsers();
        refreshBooks();
        SwingUtilities.invokeLater(() -> new LibraryGUI().setVisible(true));
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new LibraryGUI().setVisible(true));
    }

    private void setGlobalFont(Font font) {
        UIManager.put("Label.font", font);
        UIManager.put("Button.font", font);
        UIManager.put("Table.font", font);
        UIManager.put("TableHeader.font", font);
        UIManager.put("TextField.font", font);
        UIManager.put("ComboBox.font", font);
        UIManager.put("Panel.font", font);
    }

    // SQLite connection helper
    public static class DatabaseHelper {
        private static final String DB_URL = "jdbc:sqlite:Library.db";

        public static Connection getConnection() throws SQLException {
            return DriverManager.getConnection(DB_URL);
        }
    }
        private void addTransaction() {
        JTextField userIdField = new JTextField();
        JTextField bookIsbnField = new JTextField();
        JComboBox<String> transactionTypeComboBox = new JComboBox<>(new String[]{"BORROW", "RETURN"});
        JTextField transactionDateField = new JTextField(); // Format: YYYY-MM-DD
        JTextField fineAmountField = new JTextField();
    
        Object[] message = {
            "User ID:", userIdField,
            "Book ISBN:", bookIsbnField,
            "Transaction Type:", transactionTypeComboBox,
            "Transaction Date (YYYY-MM-DD):", transactionDateField,
            "Fine Amount:", fineAmountField
        };
    
        int option = JOptionPane.showConfirmDialog(this, message, "Add Transaction", JOptionPane.OK_CANCEL_OPTION);
        if (option == JOptionPane.OK_OPTION) {
            String userId = userIdField.getText().trim();
            String bookIsbn = bookIsbnField.getText().trim();
            String transactionType = (String) transactionTypeComboBox.getSelectedItem();
            String transactionDate = transactionDateField.getText().trim();
            String fineAmountText = fineAmountField.getText().trim();
    
            if (userId.isEmpty() || bookIsbn.isEmpty() || transactionDate.isEmpty()) {
                JOptionPane.showMessageDialog(this, "User ID, Book ISBN, and Transaction Date are required.");
                return;
            }
    
            double fineAmount = 0.0;
            if (!fineAmountText.isEmpty()) {
                try {
                    fineAmount = Double.parseDouble(fineAmountText);
                } catch (NumberFormatException e) {
                    JOptionPane.showMessageDialog(this, "Fine Amount must be a valid number.");
                    return;
                }
            }
    
            try (Connection conn = DatabaseHelper.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(
                         "INSERT INTO transactions (user_id, book_isbn, transaction_type, transaction_date, fine_amount) VALUES (?, ?, ?, ?, ?)")) {
    
                stmt.setString(1, userId);
                stmt.setString(2, bookIsbn);
                stmt.setString(3, transactionType);
                stmt.setString(4, transactionDate);
                stmt.setDouble(5, fineAmount);
                stmt.executeUpdate();
    
                JOptionPane.showMessageDialog(this, "Transaction added successfully.");
                refreshTransactions();
    
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Error adding transaction: " + e.getMessage());
            }
        }
    }
    
    private void editTransaction() {
        int selectedRow = transactionsTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a transaction to edit.");
            return;
        }
    
        String transactionId = transactionsTable.getValueAt(selectedRow, 0).toString();
        String userId = transactionsTable.getValueAt(selectedRow, 1).toString();
        String bookIsbn = transactionsTable.getValueAt(selectedRow, 2).toString();
        String transactionType = transactionsTable.getValueAt(selectedRow, 3).toString();
        String transactionDate = transactionsTable.getValueAt(selectedRow, 4).toString();
        String fineAmount = transactionsTable.getValueAt(selectedRow, 5).toString();
    
        JTextField userIdField = new JTextField(userId);
        JTextField bookIsbnField = new JTextField(bookIsbn);
        JComboBox<String> transactionTypeComboBox = new JComboBox<>(new String[]{"BORROW", "RETURN"});
        transactionTypeComboBox.setSelectedItem(transactionType);
        JTextField transactionDateField = new JTextField(transactionDate);
        JTextField fineAmountField = new JTextField(fineAmount);
    
        Object[] message = {
            "User ID:", userIdField,
            "Book ISBN:", bookIsbnField,
            "Transaction Type:", transactionTypeComboBox,
            "Transaction Date (YYYY-MM-DD):", transactionDateField,
            "Fine Amount:", fineAmountField
        };
    
        int option = JOptionPane.showConfirmDialog(this, message, "Edit Transaction", JOptionPane.OK_CANCEL_OPTION);
        if (option == JOptionPane.OK_OPTION) {
            String newUserId = userIdField.getText().trim();
            String newBookIsbn = bookIsbnField.getText().trim();
            String newTransactionType = (String) transactionTypeComboBox.getSelectedItem();
            String newTransactionDate = transactionDateField.getText().trim();
            String newFineAmountText = fineAmountField.getText().trim();
    
            if (newUserId.isEmpty() || newBookIsbn.isEmpty() || newTransactionDate.isEmpty()) {
                JOptionPane.showMessageDialog(this, "User ID, Book ISBN, and Transaction Date are required.");
                return;
            }
    
            double newFineAmount = 0.0;
            if (!newFineAmountText.isEmpty()) {
                try {
                    newFineAmount = Double.parseDouble(newFineAmountText);
                } catch (NumberFormatException e) {
                    JOptionPane.showMessageDialog(this, "Fine Amount must be a valid number.");
                    return;
                }
            }
    
            try (Connection conn = DatabaseHelper.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(
                         "UPDATE transactions SET user_id = ?, book_isbn = ?, transaction_type = ?, transaction_date = ?, fine_amount = ? WHERE transaction_id = ?")) {
    
                stmt.setString(1, newUserId);
                stmt.setString(2, newBookIsbn);
                stmt.setString(3, newTransactionType);
                stmt.setString(4, newTransactionDate);
                stmt.setDouble(5, newFineAmount);
                stmt.setString(6, transactionId);
                stmt.executeUpdate();
    
                JOptionPane.showMessageDialog(this, "Transaction updated successfully.");
                refreshTransactions();
    
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Error updating transaction: " + e.getMessage());
            }
        }
    }
    
    private void deleteTransaction() {
        int selectedRow = transactionsTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a transaction to delete.");
            return;
        }
    
        String transactionId = transactionsTable.getValueAt(selectedRow, 0).toString();
    
        int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete the selected transaction?", "Confirm Delete", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            try (Connection conn = DatabaseHelper.getConnection();
                 PreparedStatement stmt = conn.prepareStatement("DELETE FROM transactions WHERE transaction_id = ?")) {
    
                stmt.setString(1, transactionId);
                stmt.executeUpdate();
    
                JOptionPane.showMessageDialog(this, "Transaction deleted successfully.");
                refreshTransactions();
    
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Error deleting transaction: " + e.getMessage());
            }
        }
    }  
    
}
