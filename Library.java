import java.util.List;

public class Library {

    public static void main(String[] args) {
        // Create a new book

        // Create a new user
        User john = new User("John");
        john.addUser("John");

        // Create a new user
        User noah = new User("Noah");
        noah.addUser("Noah");

        // Create a new user
        User alice = new User("Alice");
        alice.addUser("Alice");

        // Remove User by ID
        System.out.println("Removing Alice from the system...");
        User.removeUser(alice.getID());


    }
}

