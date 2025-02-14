package Gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.InvalidObjectException;
import java.io.IOException;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import javax.crypto.spec.IvParameterSpec;

import merrimackutil.json.JSONSerializable;
import merrimackutil.json.JsonIO;
import merrimackutil.json.types.JSONArray;
import merrimackutil.json.types.JSONObject;
import merrimackutil.json.types.JSONType;
import json.Photo;
import json.Photos;

public class AddPhotoPanel extends JPanel {
    private DefaultListModel<String> photoCollection;
    private JTextField photoField;
    private JTextField userNameField;
    private JButton addButton;
    private JButton returnButton;
    private JList<String> photoList;
    private static final String PHOTOS_FILE_PATH = "src/json/photos.json";

    public AddPhotoPanel(DefaultListModel<String> photoCollection) {
        this.photoCollection = photoCollection;
        setLayout(new BorderLayout(10, 10));

        // Create components
        photoField = new JTextField(20);
        userNameField = new JTextField(20);
        addButton = new JButton("Add Photo");
        returnButton = new JButton("Return to Main Menu");
        photoList = new JList<>(photoCollection);

        // Existing photos
        loadExistingPhotos();

        // Photo and Username
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
                String photoName = photoField.getText();
                String userName = userNameField.getText();
                if (!photoName.isEmpty() && !userName.isEmpty()) {
                    addPhoto(userName, photoName);
                    photoField.setText("");
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
                parentFrame.getContentPane().removeAll();
                parentFrame.getContentPane().add(new MainMenuPanel());
                parentFrame.revalidate();
                parentFrame.repaint();
            }
        });
    }

    private void loadExistingPhotos() {
        try {
            File file = new File(PHOTOS_FILE_PATH);
            if (!file.exists()) {
                Photos photos = new Photos();
                JsonIO.writeFormattedObject((JSONSerializable) photos.toJSONType(), file);
            }

            JSONType jsonType = JsonIO.readObject(file);
            if (jsonType instanceof JSONObject) {
                Photos photos = new Photos();
                photos.deserialize(jsonType);
                for (Photo photo : photos.getPhotos()) {
                    photoCollection.addElement("Photo: " + photo.getFileName() + " (Owner: " + photo.getOwner() + ")");
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error loading photos: File not found.");
        } catch (IOException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error creating photos.json.");
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error loading photos.");
        }
    }

    private IvParameterSpec generateIv() {
        byte[] iv = new byte[16];
        new SecureRandom().nextBytes(iv);
        return new IvParameterSpec(iv);
    }

    private void addPhoto(String userName, String photoName) {
        try {
            File file = new File(PHOTOS_FILE_PATH);
            JSONType jsonType = JsonIO.readObject(file);
            Photos photos;
            if (jsonType instanceof JSONObject) {
                photos = new Photos();
                photos.deserialize(jsonType);
            } else {
                photos = new Photos();
            }

            // Generate IV
            IvParameterSpec ivSpec = generateIv();
            String iv = Base64.getEncoder().encodeToString(ivSpec.getIV());

            // Assuming encryptedFilePath is set elsewhere, placeholder here
            String encryptedFilePath = "path/to/encrypted/" + photoName;

            // Create Photo object
            Photo newPhoto = new Photo(userName, photoName, iv, encryptedFilePath);
            photos.getPhotos().add(newPhoto);

            // Write updated Photos object to file
            JsonIO.writeFormattedObject(photos, file);

            photoCollection.addElement("Photo: " + photoName + " (Owner: " + userName + ")");
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error adding photo.");
        }
    }
}
