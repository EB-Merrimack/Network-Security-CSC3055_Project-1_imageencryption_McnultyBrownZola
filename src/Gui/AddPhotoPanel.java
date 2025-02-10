package Gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;


public class AddPhotoPanel extends JPanel {
    private DefaultListModel<String> photoCollection;
    private JTextField photoField;
    private JTextField userNameField;
    private JButton addButton;
    private JButton returnButton;
    private JList<String> photoList;

    public AddPhotoPanel(DefaultListModel<String> photoCollection) {
        this.photoCollection = photoCollection;
        setLayout(new BorderLayout(10, 10));

        // Create components
        photoField = new JTextField(20);
        userNameField = new JTextField(20);
        addButton = new JButton("Add Photo");
        returnButton = new JButton("Return to Main Menu");
        photoList = new JList<>(photoCollection);

        // Layout for photo and user name entry
        JPanel inputPanel = new JPanel(new GridLayout(2, 2, 10, 10));
        inputPanel.add(new JLabel("Photo Name:"));
        inputPanel.add(photoField);
        inputPanel.add(new JLabel("User Name:"));
        inputPanel.add(userNameField);

        // Add components to panel
        add(inputPanel, BorderLayout.NORTH);
        add(new JScrollPane(photoList), BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(addButton);
        buttonPanel.add(returnButton);
        add(buttonPanel, BorderLayout.SOUTH);

        // Add button action to add photo
        addButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String photoUrl = photoField.getText();
                String userName = userNameField.getText();
                if (!photoUrl.isEmpty() && !userName.isEmpty()) {
                    // Add photo to collection (for now, we just store the URL + username)
                    photoCollection.addElement("Photo: " + photoUrl + " (Owner: " + userName + ")");
                    photoField.setText("");  // Clear text fields
                    userNameField.setText("");
                } else {
                    JOptionPane.showMessageDialog(AddPhotoPanel.this, "Please fill in both fields.");
                }
            }
        });

        // Return to main menu button action
        returnButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                JFrame parentFrame = (JFrame) SwingUtilities.getWindowAncestor(AddPhotoPanel.this);
                parentFrame.getContentPane().removeAll(); // Remove the current panel
                parentFrame.getContentPane().add(new MainMenuPanel()); // Add the main menu panel
                parentFrame.revalidate();
                parentFrame.repaint();
            }
        });
    }
}