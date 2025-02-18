package Gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.security.SecureRandom;
import java.util.Base64;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;

import merrimackutil.json.JsonIO;
import merrimackutil.json.types.JSONObject;
import merrimackutil.json.types.JSONType;
import json.Photo;
import json.Photos;
import EncryptionAndDecryption.Encryption.AESUtil;

public class AddPhotoPanel extends JPanel {
    private DefaultListModel<String> photoCollection;
    private JTextField userNameField;
    private JButton uploadButton, returnButton, selectPhotoButton;
    private JList<String> photoList;
    private File selectedFile;
    private Photos photos;
    private static final String PHOTOS_FILE_PATH = "src/json/photos.json";
    private static final String UPLOAD_DIR = "uploads/";

    public AddPhotoPanel(DefaultListModel<String> photoCollection, Photos photos) {
        this.photoCollection = photoCollection;
        this.photos = photos;
        setLayout(new BorderLayout(10, 10));

        new File(UPLOAD_DIR).mkdirs(); // Ensure upload directory exists

        userNameField = new JTextField(20);
        uploadButton = new JButton("Upload Photo");
        returnButton = new JButton("Return to Main Menu");
        selectPhotoButton = new JButton("Choose File");
        photoList = new JList<>(photoCollection);

        loadExistingPhotos();

        JPanel inputPanel = new JPanel(new GridLayout(2, 2, 10, 10));
        inputPanel.add(new JLabel("User Name:"));
        inputPanel.add(userNameField);
        inputPanel.add(new JLabel("Selected Photo:"));
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

    private void choosePhoto() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        int returnValue = fileChooser.showOpenDialog(this);
        if (returnValue == JFileChooser.APPROVE_OPTION) {
            selectedFile = fileChooser.getSelectedFile();
            JOptionPane.showMessageDialog(this, "Selected file: " + selectedFile.getName());
        }
    }

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
    }

    private void addPhoto(String userName, String photoName, String filePath) {
        try {
            SecretKey aesKey = AESUtil.generateAESKey();
            IvParameterSpec ivSpec = generateIv();
            String iv = Base64.getEncoder().encodeToString(ivSpec.getIV());

            String encryptedPhotoPath = UPLOAD_DIR + photoName + ".enc";
            byte[] fileData = Files.readAllBytes(new File(filePath).toPath());
            String encryptedData = AESUtil.encryptAES(fileData, aesKey, ivSpec);
            Files.write(new File(encryptedPhotoPath).toPath(), encryptedData.getBytes());

            Photo newPhoto = new Photo(userName, photoName, iv, encryptedPhotoPath);
            photos.getPhotos().add(newPhoto);
            JsonIO.writeFormattedObject(photos, new File(PHOTOS_FILE_PATH));

            photoCollection.addElement("Photo: " + photoName + " (Owner: " + userName + ")");
            JOptionPane.showMessageDialog(null, "Photo uploaded and encrypted successfully!");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error adding photo.");
            e.printStackTrace();
        }
    }

    private void loadExistingPhotos() {
        try {
            File file = new File(PHOTOS_FILE_PATH);
            if (!file.exists()) return;
            JSONType jsonType = JsonIO.readObject(file);
            if (jsonType instanceof JSONObject) {
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

    private IvParameterSpec generateIv() {
        byte[] iv = new byte[16];
        new SecureRandom().nextBytes(iv);
        return new IvParameterSpec(iv);
    }

    private void returnToMainMenu() {
        JFrame parentFrame = (JFrame) SwingUtilities.getWindowAncestor(this);
        parentFrame.getContentPane().removeAll();
        parentFrame.getContentPane().add(new MainMenuPanel());
        parentFrame.revalidate();
        parentFrame.repaint();
    }
}
