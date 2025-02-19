package Gui;

import java.awt.*;
import javax.crypto.*;
import javax.crypto.spec.*;
import javax.swing.*;
import java.awt.event.ActionListener;
import java.io.*;
import java.nio.file.Files;
import java.security.*;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Base64;
import EncryptionAndDecryption.Encryption.ElGamalUtil;
import EncryptionAndDecryption.Encryption.AESUtil;
import merrimackutil.json.JsonIO;
import merrimackutil.json.types.JSONObject;
import merrimackutil.json.types.JSONType;
import json.Photo;
import json.Photos;

public class ExportPhotoPanel extends JPanel {
    private JTextField userNameField;
    private JComboBox<String> photoDropdown;
    private JLabel privateKeyLabel;
    private JButton choosePrivateKeyButton, shareButton, returnButton;
    private File privateKeyFile;
    private static final String PHOTOS_FILE_PATH = "src/json/photos.json";

    public ExportPhotoPanel() {
        setLayout(new BorderLayout(10, 10));

        JPanel inputPanel = new JPanel(new GridBagLayout());
        inputPanel.setBorder(BorderFactory.createTitledBorder("Export Photo"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;

        userNameField = new JTextField(20);
        photoDropdown = new JComboBox<>(loadPhotoNames());
        privateKeyLabel = new JLabel("No file chosen");
        choosePrivateKeyButton = new JButton("Choose Private Key");
        shareButton = new JButton("Export Photo");
        returnButton = new JButton("Return to Main Menu");

        gbc.gridx = 0; gbc.gridy = 0;
        inputPanel.add(new JLabel("User Name:"), gbc);
        gbc.gridx = 1;
        inputPanel.add(userNameField, gbc);
        
        gbc.gridx = 0; gbc.gridy = 1;
        inputPanel.add(new JLabel("Private Key File:"), gbc);
        gbc.gridx = 1;
        inputPanel.add(choosePrivateKeyButton, gbc);
        
        gbc.gridx = 1; gbc.gridy = 2;
        inputPanel.add(privateKeyLabel, gbc);
        
        gbc.gridx = 0; gbc.gridy = 3;
        inputPanel.add(new JLabel("Photo Name:"), gbc);
        gbc.gridx = 1;
        inputPanel.add(photoDropdown, gbc);
        
        add(inputPanel, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(shareButton);
        buttonPanel.add(returnButton);
        add(buttonPanel, BorderLayout.SOUTH);

        choosePrivateKeyButton.addActionListener(e -> choosePrivateKey());
        shareButton.addActionListener(e -> exportPhoto());
        returnButton.addActionListener(e -> returnToMainMenu());
    }

    private void choosePrivateKey() {
        JFileChooser fileChooser = new JFileChooser();
        if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            privateKeyFile = fileChooser.getSelectedFile();
            privateKeyLabel.setText("Selected: " + privateKeyFile.getName());
        }
    }

    private void exportPhoto() {
        String userName = userNameField.getText();
        String photoName = (String) photoDropdown.getSelectedItem();
    
        if (userName.isEmpty() || privateKeyFile == null || photoName == null) {
            JOptionPane.showMessageDialog(this, "Please fill in all fields.");
            return;
        }
        try {
            PrivateKey privateKey = loadPrivateKey(privateKeyFile);
            Photo photo = getPhoto(photoName);
            if (photo == null) {
                JOptionPane.showMessageDialog(this, "Photo not found.");
                return;
            }

            String encryptedAesKey = getEncryptedAesKey(photo, userName);
            if (encryptedAesKey == null) {
                JOptionPane.showMessageDialog(this, "No encryption key found for this user.");
                return;
            }
            SecretKey aesKey = ElGamalUtil.decryptKey(encryptedAesKey, privateKey);
    
            // Search for the encrypted file (.enc)
            File encryptedFile = new File("imgdir/" + photoName + ".enc");
            if (!encryptedFile.exists()) {
                JOptionPane.showMessageDialog(this, "Encrypted photo file not found: " + encryptedFile.getAbsolutePath());
                return;
            }
    
            // Read the encrypted photo file as bytes
            byte[] encryptedData = Files.readAllBytes(encryptedFile.toPath());
    
            // Decrypt the photo using AES
            byte[] decryptedData = AESUtil.decryptAES(encryptedData, aesKey, getIvSpec(photo));
    
            // Ensure decrypted directory exists
            File decryptedDir = new File("imgdir/decrypted");
            if (!decryptedDir.exists()) {
                decryptedDir.mkdirs();
            }
    
            // Write the decrypted data to output file
            File outputFile = new File(decryptedDir, photo.getFileName() + ".dec");
            Files.write(outputFile.toPath(), decryptedData);
    
            JOptionPane.showMessageDialog(this, "Photo exported successfully! File: " + outputFile.getName());
        } catch (Exception ex) {
            ex.printStackTrace();  // Print the stack trace to help identify the issue
            JOptionPane.showMessageDialog(this, "Error exporting photo: " + ex.getMessage());
        }
    }
    
    private void returnToMainMenu() {
        JFrame parentFrame = (JFrame) SwingUtilities.getWindowAncestor(this);
        parentFrame.getContentPane().removeAll();
        parentFrame.getContentPane().add(new MainMenuPanel());
        parentFrame.revalidate();
        parentFrame.repaint();
    }

    private PrivateKey loadPrivateKey(File keyFile) throws Exception {
        byte[] keyBytes = Files.readAllBytes(keyFile.toPath());
        String privateKeyContent = new String(keyBytes).replaceAll("\\r|\\n", "")
                .replace("-----BEGIN PRIVATE KEY-----", "")
                .replace("-----END PRIVATE KEY-----", "").trim();
        return KeyFactory.getInstance("ElGamal", "BC")
                .generatePrivate(new PKCS8EncodedKeySpec(Base64.getDecoder().decode(privateKeyContent)));
    }

    private String[] loadPhotoNames() {
        try {
            File photosFile = new File(PHOTOS_FILE_PATH);
            if (photosFile.exists()) {
                JSONType jsonType = JsonIO.readObject(photosFile);
                if (jsonType instanceof JSONObject) {
                    Photos photos = new Photos();
                    photos.deserialize(jsonType);
                    return photos.getPhotos().stream().map(Photo::getFileName).toArray(String[]::new);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new String[]{};  // Return an empty array if no photos are found
    }

    private Photo getPhoto(String photoName) throws Exception {
        File photosFile = new File(PHOTOS_FILE_PATH);
        if (photosFile.exists()) {
            JSONType jsonType = JsonIO.readObject(photosFile);
            if (jsonType instanceof JSONObject) {
                Photos photos = new Photos();
                photos.deserialize(jsonType);
                return photos.getPhotos().stream()
                        .filter(photo -> photo.getFileName().equals(photoName))
                        .findFirst().orElse(null);
            }
        }
        return null;
    }

    private String getEncryptedAesKey(Photo photo, String userName) {
        userName = userName.trim();  // Trim any extra spaces from the entered username
        System.out.println("Looking for key for user: " + userName);
        
        for (JSONObject keyEntry : photo.getKeyBlock()) {
            String storedUser = keyEntry.getString("user").trim();
            System.out.println("Checking key entry for user: " + storedUser);
    
            if (storedUser.equalsIgnoreCase(userName)) {
                System.out.println("Found encrypted AES key for user: " + storedUser);
                return keyEntry.getString("keyData");
            }
        }
    
        System.out.println("No key found for user: " + userName);
        return null;
    }
    
    private IvParameterSpec getIvSpec(Photo photo) {
        return new IvParameterSpec(Base64.getDecoder().decode(photo.getIv()));
    }
}
