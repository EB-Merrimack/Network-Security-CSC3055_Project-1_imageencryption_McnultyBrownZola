package Gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.HashMap;
import json.Users;
import json.User;
import EncryptionAndDecryption.Encryption.KeyManager;

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
                JOptionPane.showMessageDialog(this, "Username already exists. Please log in as a returning user.");
            } else {
                addUser(username);
                JOptionPane.showMessageDialog(this, "User created successfully! Proceeding to main menu.");
                if (loginListener != null) {
                    loginListener.onLoginSuccess();
                }
            }
        }
    }

    private boolean isUsernameExists(String username) {
        return users.containsKey(username);
    }

    private void addUser(String username) {
        users.put(username, ""); // No password for now
        User user = new User();
        user.setId(username);
        userManager.getKeys().add(user);

        // Now generate and store the user's keys
        try {
            KeyManager.generateAndStoreKeys(username, userManager);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error generating keys for the user: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
