package Gui;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;

public class GUIBuilder extends JFrame{
    private JPanel mainPanel;
    private CardLayout cardLayout;
    private HashMap<String, String> users;


    public GUIBuilder() {
        users = new HashMap<>();
        setTitle("Application");
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);

        //LoginPanel loginPanel = new LoginPanel(users);
        MainMenuPanel mainMenuPanel = new MainMenuPanel(); // Your main menu panel

        //loginPanel.setLoginListener(new LoginPanel.LoginListener() {
         //   @Override
         //   public void onLoginSuccess() {
                // Show the main menu when login is successful
               // cardLayout.show(mainPanel, "Main Menu");
           // }
      //  });

       // mainPanel.add(loginPanel, "Login");
        mainPanel.add(mainMenuPanel, "Main Menu");

        add(mainPanel);
        cardLayout.show(mainPanel, "Login");
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(GUIBuilder::new);
    }
}