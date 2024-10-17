package controllers;

import java.io.*;
import java.util.Properties;

public class AdminController {

    private static String adminUsername;
    private static String adminPassword;
    private static final String CONFIG_FILE = "admin_config.properties"; // File to store the credentials

    static {
        loadCredentials();
    }

    // Load credentials from the file
    private static void loadCredentials() {
        Properties props = new Properties();
        try (InputStream input = new FileInputStream(CONFIG_FILE)) {
            props.load(input);
            adminUsername = props.getProperty("username", "admin");
            adminPassword = props.getProperty("password", "admin123");
        } catch (IOException e) {
            // If the file does not exist, use default credentials
            adminUsername = "admin";
            adminPassword = "admin123";
        }
    }

    // Save credentials to the file
    private static void saveCredentials() {
        Properties props = new Properties();
        props.setProperty("username", adminUsername);
        props.setProperty("password", adminPassword);
        try (OutputStream output = new FileOutputStream(CONFIG_FILE)) {
            props.store(output, null);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Authenticate method
    public static boolean authenticate(String username, String password) {
        return username.equals(adminUsername) && password.equals(adminPassword);
    }

    // Method to change username and password
    public static void updateCredentials(String newUsername, String newPassword) {
        adminUsername = newUsername;
        adminPassword = newPassword;
        saveCredentials(); // Save the new credentials to the file
    }

    // Optional method to get the current username for the settings screen
    public static String getAdminUsername() {
        return adminUsername;
    }
}
