package Gui;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import json.Users;

public class GUIBuilder extends JFrame {
    private JPanel mainPanel;
    private CardLayout cardLayout;
    private HashMap<String, String> users;
    private Users userManager;
    public GUIBuilder() {
        users = new HashMap<>();
        userManager = new Users();
        setTitle("Application");
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
    
        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);
    
        // Create login panel and main menu panel
        LoginPanel loginPanel = new LoginPanel(users, userManager);
        MainMenuPanel mainMenuPanel = new MainMenuPanel(); // Your main menu panel
    
        // Set listener for login success
        loginPanel.setLoginListener(new LoginPanel.LoginListener() {
            @Override
            public void onLoginSuccess() {
                // Show the main menu when login is successful
                cardLayout.show(mainPanel, "Main Menu");
            }
        });
    
        // Add panels to the card layout
        mainPanel.add(loginPanel, "Login");  // Add the login panel
        mainPanel.add(mainMenuPanel, "Main Menu");  // Add the main menu panel
    
        // Add the main panel to the frame
        add(mainPanel);
    
        // Initially show the Login panel
        cardLayout.show(mainPanel, "Login");
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(GUIBuilder::new);
    }
}
