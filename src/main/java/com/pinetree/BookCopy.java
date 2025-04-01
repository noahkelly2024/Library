package com.pinetree;

public class BookCopy {
    private String copyId;
    private String borrowedBy; // Null if available
    private boolean onLoanToAnotherLibrary = false;

    public BookCopy(String copyId) {
        this.copyId = copyId;
        this.borrowedBy = null;
    }

    public String getCopyId() { return copyId; }
    public String getBorrowedBy() { return borrowedBy; }
    public boolean isAvailable() { return borrowedBy == null && !onLoanToAnotherLibrary; }

    public void borrow(String userId) {
        this.borrowedBy = userId;
    }

    public void returnCopy() {
        this.borrowedBy = null;
    }

    public boolean isOnLoanToAnotherLibrary() {
        return onLoanToAnotherLibrary;
    }

    public void setOnLoanToAnotherLibrary(boolean onLoanToAnotherLibrary) {
        this.onLoanToAnotherLibrary = onLoanToAnotherLibrary;
    }
}
