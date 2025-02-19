package Gui;

import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.nio.file.Files;
import java.security.PublicKey;
import java.security.Security;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import merrimackutil.json.JsonIO;
import merrimackutil.json.types.JSONObject;
import merrimackutil.json.types.JSONType;
import json.Photo;
import json.Photos;
import json.User;
import json.Users;
import EncryptionAndDecryption.Encryption.AESUtil;
import EncryptionAndDecryption.Encryption.ElGamalUtil;
import java.util.Base64;

public class SharePhotoPanel extends JPanel {
    private JTextField ownerNameField;
    private JComboBox<String> userDropdown;
    private JButton shareButton, returnButton;
    private JList<String> photoList;
    private Photos photos;
    private Users users;
    private static final String PHOTOS_FILE_PATH = "src/json/photos.json";
    private static final String USERS_FILE_PATH = "src/json/users.json";
    private static final String UPLOAD_DIR = "imgdir/";
    static {
        Security.addProvider(new BouncyCastleProvider());
    }

    public SharePhotoPanel(Photos photos, Users users) {
        this.photos = photos;
        this.users = users;
        setLayout(new BorderLayout(10, 10));

        ownerNameField = new JTextField();

        photoList = new JList<>(photos.getPhotos().stream().map(Photo::getFileName).toArray(String[]::new));
        userDropdown = new JComboBox<>(loadUsernames());
        shareButton = new JButton("Share Photo");
        returnButton = new JButton("Return to Main Menu");

        JPanel inputPanel = new JPanel(new GridLayout(3, 2, 10, 10));
        inputPanel.add(new JLabel("User Name:"));
        inputPanel.add(ownerNameField);
        inputPanel.add(new JLabel("Select Photo:"));
        inputPanel.add(new JScrollPane(photoList));
        inputPanel.add(new JLabel("Select User:"));
        inputPanel.add(userDropdown);

        add(inputPanel, BorderLayout.NORTH);
        add(shareButton, BorderLayout.CENTER);
        add(returnButton, BorderLayout.SOUTH);

        shareButton.addActionListener(e -> sharePhoto());
        returnButton.addActionListener(e -> returnToMainMenu());
    }

    private String[] loadUsernames() {
        try {
            File file = new File(USERS_FILE_PATH);
            if (!file.exists()) return new String[] {};
            JSONType jsonType = JsonIO.readObject(file);
            if (jsonType instanceof JSONObject) {
                users.deserialize(jsonType);
                return users.getKeys().stream().map(User::getId).toArray(String[]::new);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new String[] {};
    }

    private void sharePhoto() {
        String enteredUserName = ownerNameField.getText().trim();
        String selectedPhotoName = photoList.getSelectedValue();
        String selectedUserName = (String) userDropdown.getSelectedItem();
    
        if (enteredUserName.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter your user name.");
            return;
        }
    
        if (selectedPhotoName == null || selectedUserName == null) {
            JOptionPane.showMessageDialog(this, "Please select a photo and a user to share with.");
            return;
        }
    
        // Get the current user
        User currentUser = users.getKeys().stream()
                .filter(user -> user.getId().equals(enteredUserName))
                .findFirst().orElse(null);
    
        if (currentUser == null) {
            JOptionPane.showMessageDialog(this, "Error: User not found.");
            return;
        }
    
        // Get the selected photo
        Photo selectedPhoto = photos.getPhotos().stream()
                .filter(photo -> selectedPhotoName.equals(photo.getFileName()))
                .findFirst().orElse(null);
    
        // Get the selected user
        User selectedUser = users.getKeys().stream()
                .filter(user -> user.getId().equals(selectedUserName))
                .findFirst().orElse(null);
    
        if (selectedPhoto == null || selectedUser == null) {
            JOptionPane.showMessageDialog(this, "Error: Unable to find photo or user.");
            return;
        }
    
        try {
            // Check if the selected user exists in the key block of the photo
            String encryptionKey = selectedPhoto.getKeyBlock().stream()
                    .filter(entry -> entry.getString("user").equals(selectedUser.getId()))
                    .map(entry -> entry.getString("keyData"))
                    .findFirst().orElse(null);
    
            // If no key exists, encrypt the AES key and add it to the key block
            if (encryptionKey == null) {
                // Generate AES key and IV
                SecretKey aesKey = AESUtil.generateAESKey();
                IvParameterSpec ivSpec = AESUtil.generateIV();
                String iv = Base64.getEncoder().encodeToString(ivSpec.getIV());
    
                // Get the public key from the keyring of the selected user
                PublicKey publicKey = AddPhotoPanel.getUserPublicKey(selectedUser); // Ensure this method works as expected
    
                if (publicKey == null) {
                    JOptionPane.showMessageDialog(this, "Error: Public key not found for selected user.");
                    return;
                }
    
                // Encrypt the AES key with the user's public key
                String encryptedAesKey = ElGamalUtil.encryptKey(aesKey, publicKey);
    
                // Encrypt the photo with the AES key
                File photoFile = new File(selectedPhoto.getEncryptedFilePath());
                if (!photoFile.exists()) {
                    JOptionPane.showMessageDialog(this, "Error: Photo file does not exist.");
                    return;
                }
    
                byte[] fileData = Files.readAllBytes(photoFile.toPath());
                String encryptedData = AESUtil.encryptAES(fileData, aesKey, ivSpec);
                String encryptedPhotoPath = UPLOAD_DIR + selectedPhotoName;
                Files.write(new File(encryptedPhotoPath).toPath(), encryptedData.getBytes());
    
                // Create the key block with photo info and encrypted AES key
                JSONObject keyBlockEntry = new JSONObject();
                keyBlockEntry.put("user", selectedUser.getId());
                keyBlockEntry.put("keyData", encryptedAesKey);
                selectedPhoto.getKeyBlock().add(keyBlockEntry);
            } else {
                // If the user already exists, just update the key data
                SecretKey secretKey = convertStringToSecretKey(encryptionKey);
    
                // Encrypt the updated AES key with the user's public key
                PublicKey publicKey = AddPhotoPanel.getUserPublicKey(selectedUser); // Ensure this method works as expected
                String encryptedAesKey = ElGamalUtil.encryptKey(secretKey, publicKey);
    
                // Update the existing key block entry
                selectedPhoto.getKeyBlock().stream()
                    .filter(entry -> entry.getString("user").equals(selectedUser.getId()))
                    .forEach(entry -> entry.put("keyData", encryptedAesKey));
            }
    
            // Write the updated photos object back to the JSON file
            JsonIO.writeFormattedObject(photos, new File(PHOTOS_FILE_PATH));
    
            JOptionPane.showMessageDialog(this, "Photo shared successfully!");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error sharing photo: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private SecretKey convertStringToSecretKey(String keyString) throws Exception {
        byte[] decodedKey = Base64.getDecoder().decode(keyString);
        return new SecretKeySpec(decodedKey, 0, decodedKey.length, "AES");
    }

    private void returnToMainMenu() {
        JFrame parentFrame = (JFrame) SwingUtilities.getWindowAncestor(this);
        parentFrame.getContentPane().removeAll();
        parentFrame.getContentPane().add(new MainMenuPanel());
        parentFrame.revalidate();
        parentFrame.repaint();
    }
}