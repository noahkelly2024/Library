package com.pinetree;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import java.util.List;
import java.io.File;

public class UserManagementTest {
    
    private static final String TEST_USER_NAME = "Test User";
    
    @Before
    public void setup() {
        // Clean up any test data from previous runs
        File usersFile = new File("users.json");
        if (usersFile.exists()) {
            usersFile.delete();
        }
    }
    
    @Test
    public void testUserProperties() {
        // Create a user
        User user = new User(TEST_USER_NAME);
        
        // Verify properties
        assertNotNull(user.getID());
        assertEquals(TEST_USER_NAME, user.getName());
        assertNotNull(user.getBorrowedCopyIds());
        assertTrue(user.getBorrowedCopyIds().isEmpty());
    }
    
    @Test
    public void testAddAndListUsers() {
        // Add a user
        User addedUser = User.addUser(TEST_USER_NAME);
        
        // Get all users
        List<User> users = User.listUsers();
        
        // Verify user was added
        assertNotNull(users);
        assertFalse(users.isEmpty());
        
        User listedUser = users.get(0);
        assertEquals(addedUser.getID(), listedUser.getID());
        assertEquals(TEST_USER_NAME, listedUser.getName());
    }
    
    @Test
    public void testRemoveUser() {
        // Add a user
        User user = User.addUser(TEST_USER_NAME);
        String userId = user.getID();
        
        // Verify user exists
        List<User> users = User.listUsers();
        assertFalse(users.isEmpty());
        
        // Remove the user
        User.removeUser(userId);
        
        // Verify user is gone
        users = User.listUsers();
        assertTrue(users.isEmpty() || users.stream().noneMatch(u -> u.getID().equals(userId)));
    }
    
    @Test
    public void testPersistence() {
        // Add a user
        User addedUser = User.addUser(TEST_USER_NAME);
        
        // Get a fresh list of users (forces a read from storage)
        List<User> users = User.readUsersFromJson();
        
        // Verify user exists in storage
        assertFalse(users.isEmpty());
        assertEquals(addedUser.getID(), users.get(0).getID());
        assertEquals(TEST_USER_NAME, users.get(0).getName());
    }
}