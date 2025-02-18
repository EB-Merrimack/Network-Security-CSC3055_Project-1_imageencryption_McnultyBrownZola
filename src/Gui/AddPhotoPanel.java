package Gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.nio.file.*;
import java.security.*;
import javax.crypto.*;
import javax.crypto.spec.*;
import java.util.*;
import java.util.List;

import merrimackutil.json.JsonIO;
import merrimackutil.json.types.JSONObject;
import merrimackutil.json.types.JSONType;
import json.Photo;
import json.Photos;
import json.User;
import json.Users;
import EncryptionAndDecryption.Encryption.AESUtil;

public class AddPhotoPanel extends JPanel {
    private DefaultListModel<String> photoCollection;
    private JTextField userNameField;
    private JButton uploadButton, returnButton, selectPhotoButton;
    private JList<String> photoList;
    private File selectedFile;
    private Photos photos;
    private static final String PHOTOS_FILE_PATH = "src/json/photos.json";
    private static final String UPLOAD_DIR = "imgdir/";

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
            File destinationFile = new File(UPLOAD_DIR + selectedFile.getName() + ".enc");
            encryptAndSaveFile(selectedFile, destinationFile);

            String userName = userNameField.getText();
            User user = getUser(userName); // Retrieve User object
            addPhoto(user, selectedFile.getName() + ".enc", destinationFile.getAbsolutePath());
            userNameField.setText("");
            selectedFile = null;
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error saving the encrypted file.");
            e.printStackTrace();
        }
    }

    private void encryptAndSaveFile(File inputFile, File outputFile) throws Exception {
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        SecretKey secretKey = KeyGenerator.getInstance("AES").generateKey();
        IvParameterSpec ivSpec = generateIv();
        cipher.init(Cipher.ENCRYPT_MODE, secretKey, ivSpec);

        try (FileInputStream fis = new FileInputStream(inputFile);
             FileOutputStream fos = new FileOutputStream(outputFile);
             CipherOutputStream cos = new CipherOutputStream(fos, cipher)) {
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = fis.read(buffer)) != -1) {
                cos.write(buffer, 0, bytesRead);
            }
        }
    }

    private void addPhoto(User user, String photoName, String filePath) {
        try {
            SecretKey aesKey = AESUtil.generateAESKey();
            IvParameterSpec ivSpec = generateIv();
            String iv = Base64.getEncoder().encodeToString(ivSpec.getIV());

            String encryptedPhotoPath = UPLOAD_DIR + photoName;
            byte[] fileData = Files.readAllBytes(new File(filePath).toPath());
            String encryptedData = AESUtil.encryptAES(fileData, aesKey, ivSpec);
            Files.write(new File(encryptedPhotoPath).toPath(), encryptedData.getBytes());

            // Create the keyblock with photo info
            JSONObject keyBlockEntry = user.getPublicKeyJSON(user);
            List<JSONObject> keyBlock = new ArrayList<>();
            keyBlock.add(keyBlockEntry);

            Photo newPhoto = new Photo(user.getId(), photoName, iv, encryptedPhotoPath, keyBlock);
            photos.getPhotos().add(newPhoto);
            JsonIO.writeFormattedObject(photos, new File(PHOTOS_FILE_PATH));

            photoCollection.addElement("Photo: " + photoName + " (Owner: " + user.getId() + ")");
            JOptionPane.showMessageDialog(null, "Photo uploaded and encrypted successfully!");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error adding photo.");
            e.printStackTrace();
        }
    }

    private User getUser(String userName) {
        try {
            File usersFile = new File("src/json/users.json");
            if (usersFile.exists()) {
                JSONType jsonType = JsonIO.readObject(usersFile);
                if (jsonType instanceof JSONObject) {
                    Users users = new Users();
                    users.deserialize(jsonType);
                    for (User user : users.getKeys()) {
                        if (user.getId().equals(userName)) {
                            return user;
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;  // Or handle the case where the user is not found
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
        } catch (FileNotFoundException e) {
            JOptionPane.showMessageDialog(this, "Error loading photos: File not found.");
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Error creating photos.json.");
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
