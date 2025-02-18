package Gui;


import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;

//import java.security.PublicKey;
import java.security.SecureRandom;
import java.util.Base64;

import javax.crypto.SecretKey;

import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.security.SecureRandom;
import java.util.Base64;

import javax.crypto.spec.IvParameterSpec;

import merrimackutil.json.JSONSerializable;
import merrimackutil.json.JsonIO;
import merrimackutil.json.types.JSONObject;
//import merrimackutil.json.types.JSONType;
import json.Photo;
import json.Photos;
import EncryptionAndDecryption.Encryption.AESUtil;  // Assuming AES encryption utilities are in this class
import EncryptionAndDecryption.Encryption.ElGamalUtil;

public class AddPhotoPanel extends JPanel {
 private Photos photos;
    private JTextField photoField;

    private DefaultListModel<String> photoCollection;

    private JTextField userNameField;
    private JButton uploadButton;
    private JButton returnButton;
    private JList<String> photoList;

    //private static final String PHOTOS_FILE_PATH = "src/json/photos.json";

    private File selectedFile;
    private static final String PHOTOS_FILE_PATH = "src/json/photos.json";
    private static final String UPLOAD_DIR = "uploads/"; 


    public AddPhotoPanel(Photos photos) {
        this.photos = photos;
        setLayout(new BorderLayout(10, 10));

        // Ensure the upload directory exists
        new File(UPLOAD_DIR).mkdirs();

        userNameField = new JTextField(20);
        uploadButton = new JButton("Upload Photo");
        returnButton = new JButton("Return to Main Menu");
        //photoList = new Photos(photos);


        // Existing photos
        //loadExistingPhotos();

        loadExistingPhotos();


        JPanel inputPanel = new JPanel(new GridLayout(2, 2, 10, 10));
        inputPanel.add(new JLabel("User Name:"));
        inputPanel.add(userNameField);
        inputPanel.add(new JLabel("Selected Photo:"));
        JButton selectPhotoButton = new JButton("Choose File");
        inputPanel.add(selectPhotoButton);

        add(inputPanel, BorderLayout.NORTH);
        add(new JScrollPane(photoList), BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(uploadButton);
        buttonPanel.add(returnButton);
        add(buttonPanel, BorderLayout.SOUTH);

        selectPhotoButton.addActionListener(e -> choosePhoto());
        uploadButton.addActionListener(e -> uploadPhoto());
        returnButton.addActionListener(e -> returnToMainMenu());
    }

    /*private void loadExistingPhotos() {

    // Opens a file chooser to select a photo from the computer
    private void choosePhoto() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        int returnValue = fileChooser.showOpenDialog(this);
        if (returnValue == JFileChooser.APPROVE_OPTION) {
            selectedFile = fileChooser.getSelectedFile();
            JOptionPane.showMessageDialog(this, "Selected file: " + selectedFile.getName());
        }
    }

    // Uploads the selected photo and stores it in the designated folder
    private void uploadPhoto() {
        if (selectedFile == null || userNameField.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please select a file and enter a username.");
            return;
        }

        try {
            File destinationFile = new File(UPLOAD_DIR + selectedFile.getName());
            Files.copy(selectedFile.toPath(), destinationFile.toPath(), StandardCopyOption.REPLACE_EXISTING);

            addPhoto(userNameField.getText(), selectedFile.getName(), destinationFile.getAbsolutePath());
            userNameField.setText("");
            selectedFile = null;
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Error saving the file.");
            e.printStackTrace();
        }
    }*/



    public void addPhoto(String userName, String photoName) {
        try {
            // Ensure the photo directory exists
            ensurePhotoDirectoryExists();

            // Load the photo data (assuming 'photoName' is the path to the image)
            File photoFile = new File(photoName);  
            byte[] photoData = new byte[(int) photoFile.length()];
            try (FileInputStream fis = new FileInputStream(photoFile)) {
                fis.read(photoData);
            }

            // Encrypt the photo
            SecretKey aesKey = AESUtil.generateAESKey();

    // Adds a photo entry to the JSON file
    private void addPhoto(String userName, String photoName, String filePath) {
        try {
            File file = new File(PHOTOS_FILE_PATH);
            JSONType jsonType = file.exists() ? JsonIO.readObject(file) : new JSONObject();
            Photos photos = new Photos();
            if (jsonType instanceof JSONObject) {
                photos.deserialize(jsonType);
            }


            IvParameterSpec ivSpec = generateIv();
            String encryptedPhoto = AESUtil.encryptAES(photoData, aesKey, ivSpec);

            // Get the encrypted file path from the Photos object
            String encryptedFilePath = photos.getImgdir() + "/" + photoName + ".enc";

            // Generate the Base64 encoded IV
            String ivString = Base64.getEncoder().encodeToString(ivSpec.getIV());


            // Encrypt AES key with ElGamal (placeholder method)
            //PublicKey publicKey = getUserPublicKey(userName);  // Assume method to get the user's public key
            //String encryptedAESKey = ElGamalUtil.encryptKey(aesKey, publicKey);

            // Create a new photo object
            Photo newPhoto = new Photo();
            newPhoto.setOwner(userName);
            newPhoto.setFileName(photoName);
            newPhoto.setIv(ivString);


            // Add the photo object to the photos list
            photos.getPhotos().add(newPhoto);

            // Update photos.json file
            updatePhotosJson();

            // Optionally, save the encrypted photo to the disk
            try (FileOutputStream fos = new FileOutputStream(encryptedFilePath)) {
                fos.write(encryptedPhoto.getBytes());
            }

            JOptionPane.showMessageDialog(null, "Photo added successfully!");

            Photo newPhoto = new Photo(userName, photoName, iv, filePath);
            photos.getPhotos().add(newPhoto);
            JsonIO.writeFormattedObject(photos, file);


        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error adding photo.");
        }
    }

    // Ensure the photo directory exists
    private void ensurePhotoDirectoryExists() {
        File dir = new File(photos.getImgdir());
        if (!dir.exists()) {
            dir.mkdirs();  // Create the directory if it doesn't exist
        }
    }

    // Method to generate IV for AES encryption
    private IvParameterSpec generateIv() {
        byte[] iv = new byte[16];
        new SecureRandom().nextBytes(iv);
        return new IvParameterSpec(iv);
    }

    // Method to update photos.json with the new photo
    private void updatePhotosJson() {
        try {
            // Serialize the Photos object to JSON
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("imgdir", photos.getImgdir());
    
            JSONArray photosArray = new JSONArray();
            for (Photo photo : photos.getPhotos()) {
                photosArray.add(photo.toJSONType());
            }
            jsonObject.put("photos", photosArray);
    
            // write the JSON to the file
            JsonIO.writeFormattedObject((JSONSerializable) photos, new File("photos.json"));
    
        } catch (IOException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error updating photos.json.");
        }
    }

    // Placeholder method to get the user's public key for ElGamal encryption
   /*  private PublicKey getUserPublicKey(String userName) {
        // You can implement this based on your key management system
        return null;
    }*/
}

            JOptionPane.showMessageDialog(this, "Error adding photo.");
            e.printStackTrace();
        }
    }

    // Loads existing photos from the JSON file into the list
    private void loadExistingPhotos() {
        try {
            File file = new File(PHOTOS_FILE_PATH);
            if (!file.exists()) return;
            JSONType jsonType = JsonIO.readObject(file);
            if (jsonType instanceof JSONObject) {
                Photos photos = new Photos();
                photos.deserialize(jsonType);
                for (Photo photo : photos.getPhotos()) {
                    photoCollection.addElement("Photo: " + photo.getFileName() + " (Owner: " + photo.getOwner() + ")");
                }
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error loading photos.");
            e.printStackTrace();
        }
    }

    // Generates a random IV for encryption
    private IvParameterSpec generateIv() {
        byte[] iv = new byte[16];
        new SecureRandom().nextBytes(iv);
        return new IvParameterSpec(iv);
    }

    // Returns to the main menu by switching the panel
    private void returnToMainMenu() {
        JFrame parentFrame = (JFrame) SwingUtilities.getWindowAncestor(this);
        parentFrame.getContentPane().removeAll();
        parentFrame.getContentPane().add(new MainMenuPanel());
        parentFrame.revalidate();
        parentFrame.repaint();
    }
}

