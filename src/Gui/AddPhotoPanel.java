package Gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class AddPhotoPanel extends JPanel {
    private JTextField photoNameField;   // Field for photo name
    private JTextField usernameField;    // Field for username
    private JButton addButton;           // Button to submit the photo

    private DefaultListModel<String> photoCollection; 

    public AddPhotoPanel(DefaultListModel<String> photoCollection) {
        this.photoCollection = photoCollection;
        
        setLayout(new GridLayout(3, 2, 10, 10));

        // Label and field for photo name
        JLabel photoNameLabel = new JLabel("Photo Name:");
        photoNameField = new JTextField();

        // Label and field for username
        JLabel usernameLabel = new JLabel("Username:");
        usernameField = new JTextField();

        // Add button
        addButton = new JButton("Add Photo");
        addButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                addPhoto();
            }
        });

        // Adding components to the panel
        add(photoNameLabel);
        add(photoNameField);
        add(usernameLabel);
        add(usernameField);
        add(new JLabel()); 
        add(addButton);
    }

    private void addPhoto() {
        String photoName = photoNameField.getText().trim();
        String username = usernameField.getText().trim();

        if (photoName.isEmpty() || username.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter both the photo name and your username.");
            return;
        }

        String photoDetails = "Photo: " + photoName + " (Owner: " + username + ")";
        photoCollection.addElement(photoDetails); // Add photo to the collection

        JOptionPane.showMessageDialog(this, "Photo added successfully!");
        photoNameField.setText(""); 
        usernameField.setText("");
    }
}