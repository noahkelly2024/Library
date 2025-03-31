public class Book {
    private String ID;
    private String title;
    private String author;
    private Boolean status;

    Book(String ID, String title, String author, Boolean status) {
        this.ID = ID;
        this.title = title;
        this.author = author;
        this.status = status;
    }

    public void addBook(String ID, String title, String author, Boolean status) {
        this.ID = ID;
        this.title = title;
        this.author = author;
        this.status = status;
    }

    public void removeBook(String ID, String title, String author, Boolean status) {
        this.ID = ID;
        this.title = title;
        this.author = author;
        this.status = status;
    }
    
    public void updateBook(String ID, String title, String author, Boolean status) {
        this.ID = ID;
        this.title = title;
        this.author = author;
        this.status = status;
    }
    public void listAll() {
        System.out.println("ID: " + title);
        System.out.println("Title: " + title);
        System.out.println("Author: " + author);
        System.out.println("Availble: " + status);
    }
}