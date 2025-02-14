package Gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.security.SecureRandom;
import java.util.Base64;
import javax.crypto.spec.IvParameterSpec;
import merrimackutil.json.parser.JSONParser;
import merrimackutil.json.types.JSONArray;
import merrimackutil.json.types.JSONObject;
import merrimackutil.json.JSONSerializable;
import json.Photo;

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
            JSONArray jsonArray = JSONParser.readArray(new File(PHOTOS_FILE_PATH));
            if (jsonArray != null) {
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = jsonArray.getObject(i);
                    String photoName = jsonObject.getString("fileName");
                    String owner = jsonObject.getString("owner");
                    photoCollection.addElement("Photo: " + photoName + " (Owner: " + owner + ")");
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error loading photos: File not found.");
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
            JSONArray jsonArray = JSONParser.readArray(new File(PHOTOS_FILE_PATH));
            if (jsonArray == null) {
                jsonArray = new JSONArray();
            }

            // Generate IV
            IvParameterSpec ivSpec = generateIv();
            String iv = Base64.getEncoder().encodeToString(ivSpec.getIV());

            // Assuming encryptedFilePath is set elsewhere, placeholder here
            String encryptedFilePath = "path/to/encrypted/" + photoName;

            // Create Photo object
            Photo newPhoto = new Photo(userName, photoName, iv, encryptedFilePath);

            // Convert Photo object to JSON object
            JSONObject newPhotoJson = new JSONObject();
            newPhotoJson.put("fileName", newPhoto.getFileName());
            newPhotoJson.put("owner", newPhoto.getOwner());
            newPhotoJson.put("iv", newPhoto.getIv());
            newPhotoJson.put("encryptedFilePath", newPhoto.getEncryptedFilePath());

            jsonArray.add(newPhotoJson);

            JSONParser.writeFormattedObject(new JSONSerializable(jsonArray), new File(PHOTOS_FILE_PATH));
            photoCollection.addElement("Photo: " + photoName + " (Owner: " + userName + ")");
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error adding photo.");
        }
    }
}
