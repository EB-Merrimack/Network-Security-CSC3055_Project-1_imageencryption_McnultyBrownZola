package Gui;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import json.Users;
import json.User;
import EncryptionAndDecryption.Encryption.KeyManager;
import java.io.File;

public class LoginPanel extends JPanel {
    private HashMap<String, String> users;
    private Users userManager;
    private JButton returningUserButton, newUserButton;
    private LoginListener loginListener;

    public LoginPanel(HashMap<String, String> users, Users userManager) {
        this.users = users;
        this.userManager = userManager;
        setLayout(new BorderLayout(10, 10));

        JLabel welcomeLabel = new JLabel("Welcome to the Photo Encryption GUI", SwingConstants.CENTER);
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 18));

        returningUserButton = new JButton("Returning User");
        newUserButton = new JButton("New User");

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(returningUserButton);
        buttonPanel.add(newUserButton);

        add(welcomeLabel, BorderLayout.NORTH);
        add(buttonPanel, BorderLayout.CENTER);

        returningUserButton.addActionListener(e -> {
            if (loginListener != null) {
                loginListener.onLoginSuccess();
            }
        });

        newUserButton.addActionListener(e -> showNewUserDialog());
    }

    public interface LoginListener {
        void onLoginSuccess();
    }

    public void setLoginListener(LoginListener listener) {
        this.loginListener = listener;
    }

    private void showNewUserDialog() {
        String username = JOptionPane.showInputDialog(this, "Enter your username:", "New User Registration", JOptionPane.PLAIN_MESSAGE);
        if (username != null && !username.trim().isEmpty()) {
            if (isUsernameExists(username)) {
                JOptionPane.showMessageDialog(this, "Username already exists. Please log in as a returning user or choose a different username.");
            } else {
                addUser(username);
                if (checkPrivateKeyExists(username)) {
                    JOptionPane.showMessageDialog(this, "User created successfully! Private key saved. Proceeding to main menu.");
                } else {
                    JOptionPane.showMessageDialog(this, "User created, but private key was not generated. Please try again.", "Error", JOptionPane.ERROR_MESSAGE);
                }
                if (loginListener != null) {
                    loginListener.onLoginSuccess();
                }
            }
        }
    }

    private boolean isUsernameExists(String username) {
        // Check in the users HashMap
        if (users.containsKey(username)) {
            return true;
        }

        // Check in userManager's list of keys
        for (User user : userManager.getKeys()) {
            if (user.getId().equalsIgnoreCase(username)) {
                return true;
            }
        }
        return false;
    }

    private void addUser(String username) {
        users.put(username, ""); // No password for now
        User user = new User();
        user.setId(username);
        userManager.getKeys().add(user);

        // Generate and store the user's keys
        try {
            KeyManager.generateAndStoreKeys(username, userManager);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error generating keys for the user: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private boolean checkPrivateKeyExists(String username) {
        File privateKeyFile = new File("./key_data/" + username + "_private.key");
        return privateKeyFile.exists();
    }
}
