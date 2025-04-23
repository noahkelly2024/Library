package com.pinetree; // Defines the package for this class

/**
 * The entry point of the library system application.
 */
public class LibraryRunner {
    public static int borrowLimit = 3; // Default limit for the number of books a user can borrow

    /**
     * The main method to start the library system.
     * @param args Command-line arguments (not used in this application).
     */
    public static void main(String[] args) {

        // Set up the database by creating tables and initializing data if necessary
        LibraryDatabase.setupDatabase();

        // Create and initialize the graphical user interface (GUI) for the library system
        LibraryGUI libraryGUI = new LibraryGUI();
        libraryGUI.initialize(); // Ensure LibraryGUI has an initialize() method implemented

    }
}