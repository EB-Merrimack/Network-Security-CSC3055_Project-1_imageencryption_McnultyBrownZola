/*package Gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.HashMap;

public class LoginPanel extends JPanel {
    private HashMap<String, String> users;
    private JButton loginButton, signUpButton;
    private LoginListener loginListener;  // Listener for login success

    public LoginPanel(HashMap<String, String> users) {
        this.users = users;
        setLayout(new GridLayout(4, 2, 10, 10));

        JLabel usernameLabel = new JLabel("Username:");
        JTextField usernameField = new JTextField();
        JLabel passwordLabel = new JLabel("Password:");
        JPasswordField passwordField = new JPasswordField();

        loginButton = new JButton("Log In");
        signUpButton = new JButton("Create Account");

        add(usernameLabel);
        add(usernameField);
        add(passwordLabel);
        add(passwordField);
        add(loginButton);
        add(signUpButton);

        loginButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String username = usernameField.getText();
                String password = new String(passwordField.getPassword());

                if (users.containsKey(username) && users.get(username).equals(password)) {
                    // Notify the parent frame that login was successful
                    if (loginListener != null) {
                        loginListener.onLoginSuccess();
                    }
                } else {
                    JOptionPane.showMessageDialog(null, "Invalid login details.");
                }
            }
        });

        signUpButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                showSignUpDialog();
            }
        });
    }

    // Interface for login success event
    public interface LoginListener {
        void onLoginSuccess();
    }

    // Setter for the login listener
    public void setLoginListener(LoginListener listener) {
        this.loginListener = listener;
    }

    private void showSignUpDialog() {
        JDialog signUpDialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Create User", true);
        signUpDialog.setLayout(new GridLayout(4, 2, 10, 10));
        signUpDialog.setSize(300, 200);
        signUpDialog.setLocationRelativeTo(this);

        JLabel userLabel = new JLabel("Username:");
        JTextField usernameField = new JTextField();
        JLabel passwordLabel = new JLabel("Password:");
        JPasswordField passwordField = new JPasswordField();
        JLabel confirmPasswordLabel = new JLabel("Confirm Password:");
        JPasswordField confirmPasswordField = new JPasswordField();
        JButton signUpButton = new JButton("Sign Up");

        signUpDialog.add(userLabel);
        signUpDialog.add(usernameField);
        signUpDialog.add(passwordLabel);
        signUpDialog.add(passwordField);
        signUpDialog.add(confirmPasswordLabel);
        signUpDialog.add(confirmPasswordField);
        signUpDialog.add(new JLabel()); // Empty cell
        signUpDialog.add(signUpButton);

        signUpButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String username = usernameField.getText();
                String password = new String(passwordField.getPassword());
                String confirmPassword = new String(confirmPasswordField.getPassword());

                if (username.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
                    JOptionPane.showMessageDialog(null, "All fields are required.");
                    return;
                }

                if (!password.equals(confirmPassword)) {
                    JOptionPane.showMessageDialog(null, "Passwords do not match.");
                    return;
                }

                if (!isValidPassword(password)) {
                    JOptionPane.showMessageDialog(null, "Password must contain:\n" +
                            "- At least one uppercase letter\n" +
                            "- At least one lowercase letter\n" +
                            "- At least one number\n" +
                            "- At least one special character\n" +
                            "- Minimum length of 8 characters");
                    return;
                }

                if (users.containsKey(username)) {
                    JOptionPane.showMessageDialog(null, "Username already exists.");
                    return;
                }

                users.put(username, password);
                JOptionPane.showMessageDialog(null, "User created successfully!");
                signUpDialog.dispose();

                // Notify parent that login/signup was successful
                if (loginListener != null) {
                    loginListener.onLoginSuccess();
                }
            }
        });

        signUpDialog.setVisible(true);
    }

    private boolean isValidPassword(String password) {
        if (password.length() < 8) return false;
        if (!password.matches(".*[A-Z].*")) return false;
        if (!password.matches(".*[a-z].*")) return false;
        if (!password.matches(".*[0-9].*")) return false;
        if (!password.matches(".*[!@#$%^&*(),.?\":{}|<>].*")) return false;
        return true;
    }
}*/