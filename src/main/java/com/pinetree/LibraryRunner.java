package com.pinetree;

public class LibraryRunner {
    public static int borrowLimit = 3; // Default limit

    public static void main(String[] args) {

        LibraryDatabase.setupDatabase();
        LibraryGUI libraryGUI = new LibraryGUI();
        libraryGUI.initialize(); // Ensure LibraryGUI has an initialize() method implemented

    }
}