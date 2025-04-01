public class LibrarySystem {
    public static void main(String[] args) {
        // Create Users and Add them to JSON
        User alice = new User("Alice");
        alice.addUser("Alice");

        User bob = new User("Bob");
        bob.addUser("Bob");

        // List all users
        System.out.println("Users in the system:");
        for (User user : User.listUsers()) {
            System.out.println(user.getName() + " (" + user.getID() + ")");
        }

        // Remove Alice by ID
        User.removeUser("U123456789");

        // List all users after removal
        System.out.println("Users after removal:");
        for (User user : User.listUsers()) {
            System.out.println(user.getName() + " (" + user.getID() + ")");
        }
    }
}