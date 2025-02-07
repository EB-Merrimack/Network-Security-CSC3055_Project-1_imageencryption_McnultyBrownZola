package Gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.HashMap;

public class GUIBuilder extends JFrame {
    private JComboBox<String> stringBox; // Dropdown menu
    private JPanel cardPanel;            // Panel that will hold different screens
    private CardLayout cardLayout;       // CardLayout to manage different screens

    private DefaultListModel<String> photoCollection; // Model for storing photos

    private static final String[] labels = { 
        "Select an option", "Add Photo", "Share Photo", "Export Photo", "List All Photos", "Exit" 
    };

    private HashMap<String, String> users; // Store users: username -> password

    public GUIBuilder() {
        super("Main Menu");

        setLayout(new BorderLayout(10, 10));

        // Initialize the photo collection and users map
        photoCollection = new DefaultListModel<>();
        users = new HashMap<>();

        // Create a card layout container
        cardLayout = new CardLayout();
        cardPanel = new JPanel(cardLayout);

        // Add the main menu panel
        JPanel addPhotoPanel = new AddPhotoPanel(photoCollection); 
        JPanel sharePhotoPanel = createSharePhotoPanel();
        JPanel exportPhotoPanel = createExportPhotoPanel();
        JPanel listPhotosPanel = createListPhotosPanel();
        JPanel exitPanel = new JPanel(); 

        // Add panels to the card layout container
        cardPanel.add(new JPanel(), "Select an option");  
        cardPanel.add(addPhotoPanel, "Add Photo");
        cardPanel.add(sharePhotoPanel, "Share Photo");
        cardPanel.add(exportPhotoPanel, "Export Photo");
        cardPanel.add(listPhotosPanel, "List All Photos");
        cardPanel.add(exitPanel, "Exit");

        // Add elements to the frame
        add(cardPanel, BorderLayout.CENTER);

        // Set window properties
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null); 
        setVisible(true);

        // Show the user registration dialog
        showUserCreationDialog();
    }

    // Suser creation as a pop up 
    private void showUserCreationDialog() {
        JDialog signUpDialog = new JDialog(this, "Create User", true);
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
        signUpDialog.add(new JLabel()); 
        signUpDialog.add(signUpButton);

        // Action listener for sign-up button
        signUpButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String username = usernameField.getText();
                String password = new String(passwordField.getPassword());
                String confirmPassword = new String(confirmPasswordField.getPassword());

                // Check if username or password is empty
                if (username.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
                    JOptionPane.showMessageDialog(null, "All fields are required.");
                    return;
                }

                // Check if passwords match
                if (!password.equals(confirmPassword)) {
                    JOptionPane.showMessageDialog(null, "Passwords do not match.");
                    return;
                }

                // Check if password meets criteria
                if (!isValidPassword(password)) {
                    JOptionPane.showMessageDialog(null, "Password must contain:\n" +
                            "- At least one uppercase letter\n" +
                            "- At least one lowercase letter\n" +
                            "- At least one number\n" +
                            "- At least one special character\n" +
                            "- Minimum length of 8 characters");
                    return;
                }

                // Check if username already exists
                if (users.containsKey(username)) {
                    JOptionPane.showMessageDialog(null, "Username already exists.");
                    return;
                }

                // Save the new user
                users.put(username, password);
                JOptionPane.showMessageDialog(null, "User created successfully!");

                // Close the sign-up dialog
                signUpDialog.dispose();

                // Switch to the main menu screen
                cardLayout.show(cardPanel, "Select an option");
            }
        });

        signUpDialog.setVisible(true); // Show the dialog
    }

    // Validate password based on the specified criteria
    private boolean isValidPassword(String password) {
        // Password must be longer than 8 characters
        if (password.length() < 8) {
            return false;
        }

        // Check if password contains at least one uppercase letter
        if (!password.matches(".*[A-Z].*")) {
            return false;
        }

        // Check if password contains at least one lowercase letter
        if (!password.matches(".*[a-z].*")) {
            return false;
        }

        // Check if password contains at least one number
        if (!password.matches(".*[0-9].*")) {
            return false;
        }

        // Check if password contains at least one special character
        if (!password.matches(".*[!@#$%^&*(),.?\":{}|<>].*")) {
            return false;
        }

        return true;
    }

    private void handleSelection(String option) {
        switch (option) {
            case "Add Photo":
                openAddPhotoWindow();
                break;
            case "Share Photo":
                JOptionPane.showMessageDialog(this, "Feature: Share Photo (To be implemented)");
                break;
            case "Export Photo":
                JOptionPane.showMessageDialog(this, "Feature: Export Photo (To be implemented)");
                break;
            case "List All Photos":
                JOptionPane.showMessageDialog(this, "Feature: List All Photos (To be implemented)");
                break;
            case "Exit":
                System.exit(0);
                break;
        }
    }

    // Create the Add Photo window
    private void openAddPhotoWindow() {
        // Close the main menu window
        this.setVisible(false);
        this.dispose(); 

        // Create a new Add Photo window
        JFrame addPhotoWindow = new JFrame("Add Photo");
        addPhotoWindow.setSize(400, 300);
        addPhotoWindow.setLayout(new BorderLayout(10, 10));

        // Create and add the AddPhotoPanel
        AddPhotoPanel addPhotoPanel = new AddPhotoPanel(photoCollection);
        addPhotoWindow.add(addPhotoPanel, BorderLayout.CENTER);

        // Set the Add Photo window properties
        addPhotoWindow.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        addPhotoWindow.setLocationRelativeTo(null); 
        addPhotoWindow.setVisible(true);
    }

    // Create the Share Photo panel
    private JPanel createSharePhotoPanel() {
        JPanel panel = new JPanel();
        panel.add(new JLabel("Share Photo (Functionality will be implemented here)"));
        return panel;
    }

    // Create the Export Photo panel
    private JPanel createExportPhotoPanel() {
        JPanel panel = new JPanel();
        panel.add(new JLabel("Export Photo (Functionality will be implemented here)"));
        return panel;
    }

    // Create the List All Photos panel
    private JPanel createListPhotosPanel() {
        JPanel panel = new JPanel();
        panel.add(new JLabel("List All Photos (Functionality will be implemented here)"));
        return panel;
    }

    public static void main(String[] args) {
        new GUIBuilder();
    }
}
