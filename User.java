import java.util.ArrayList;
import java.util.List;

public class User {
    private String ID;
    private String name;
    private List<Book> booksBorrowed;
    private static String filepath = "users.csv";
    
    public User(String name) {
        this.ID = "U" + (int) (Math.random() * 1000000000); // Generate a random ID
        this.name = name;
        this.booksBorrowed = new ArrayList<>();
    }

    public String getID() {
        return ID;
    }

    public String getName() {
        return name;
    }

    public List<Book> getBooksBorrowed() {
        return booksBorrowed;
    }
    public void addUser(String name) {
        this.name = name;
        this.ID = "U" + (int) (Math.random() * 1000000000);
        this.booksBorrowed = new ArrayList<>();

        // Convert List to a properly formatted string for CSV
        String booksString = booksBorrowed.toString(); // Converts List to "[Book1, Book2]"

        String[] userData = {this.ID, this.name, booksBorrowed.toString()};
        CSVUtil.writeToCSV(filepath, new String[][]{userData});
    }

    public static void removeUser(String userId) {
        List<String[]> usersData = CSVUtil.readFromCSV(filepath);
        
        // Create a list to store updated users
        List<String[]> updatedUsers = new ArrayList<>();

        // Loop through all users and keep those who don't match the userId
        for (String[] user : usersData) {
            if (!user[0].equals(userId)) {  // user[0] is the user ID in the CSV file
                updatedUsers.add(user); // Add user to the updated list if IDs don't match
            }
        }

        CSVUtil.writeToCSV(filepath, updatedUsers.toArray(new String[0][]));
    }
    

}