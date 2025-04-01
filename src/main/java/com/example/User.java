import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class User {
    private String ID;
    private String name;
    private List<Book> booksBorrowed;
    private static final String filepath = "users.json"; // Use JSON file instead of CSV
    
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

    // Method to add a user (writing to JSON)
    public void addUser(String name) {
        this.name = name;
        this.ID = "U" + (int) (Math.random() * 1000000000);
        this.booksBorrowed = new ArrayList<>();

        // Read existing users
        List<User> users = readUsersFromJson();
        
        // Add the new user to the list
        users.add(this);

        // Save the updated list to the JSON file
        writeUsersToJson(users);
    }

    // Method to remove a user by ID (from JSON file)
    public static void removeUser(String userId) {
        List<User> users = readUsersFromJson();
        
        // Remove the user by matching the ID
        users.removeIf(user -> user.getID().equals(userId));

        // Write the updated list of users back to the JSON file
        writeUsersToJson(users);
    }

    // Read users from the JSON file and return a list of User objects
    private static List<User> readUsersFromJson() {
        List<User> users = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(filepath))) {
            Gson gson = new Gson();
            // Read the list of users from the JSON file
            users = gson.fromJson(reader, new TypeToken<List<User>>() {}.getType());
        } catch (FileNotFoundException e) {
            // File doesn't exist, so we'll create a new one when writing
        } catch (IOException e) {
            e.printStackTrace();
        }
        return users;
    }

    // Write the list of users to the JSON file
    private static void writeUsersToJson(List<User> users) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filepath))) {
            Gson gson = new Gson();
            // Convert the list of users to JSON and write it to the file
            gson.toJson(users, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Method to list all users
    public static List<User> listUsers() {
        return readUsersFromJson();
    }
}
