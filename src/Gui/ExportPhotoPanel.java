package Gui;

import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.nio.file.Files;
import java.security.PrivateKey;
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

public class ExportPhotoPanel extends JPanel {
    private JTextField ownerNameField;
    private JComboBox<String> photoDropdown;
    private JButton exportButton, returnButton;
    private JList<String> userList;
    private Photos photos;
    private Users users;
    //private static final String PHOTOS_FILE_PATH = "src/json/photos.json";
    private static final String USERS_FILE_PATH = "src/json/users.json";
    //private static final String UPLOAD_DIR = "imgdir/";

    static {
        Security.addProvider(new BouncyCastleProvider());
    }

    public ExportPhotoPanel(Photos photos, Users users) {
        this.photos = photos;
        this.users = users;
        setLayout(new BorderLayout(10, 10));

        ownerNameField = new JTextField();
        userList = new JList<>(loadUsernames());
        photoDropdown = new JComboBox<>(photos.getPhotos().stream().map(Photo::getFileName).toArray(String[]::new));
        exportButton = new JButton("Export Photo");
        returnButton = new JButton("Return to Main Menu");

        JPanel inputPanel = new JPanel(new GridLayout(3, 2, 10, 10));
        inputPanel.add(new JLabel("User Name:"));
        inputPanel.add(ownerNameField);
        inputPanel.add(new JLabel("Select Photo:"));
        inputPanel.add(photoDropdown);
        inputPanel.add(new JLabel("Select User:"));
        inputPanel.add(new JScrollPane(userList));

        add(inputPanel, BorderLayout.NORTH);
        add(exportButton, BorderLayout.CENTER);
        add(returnButton, BorderLayout.SOUTH);

        exportButton.addActionListener(e -> exportPhoto());
        returnButton.addActionListener(e -> returnToMainMenu());
    }

    private String[] loadUsernames() {
        try {
            File file = new File(USERS_FILE_PATH);
            if (!file.exists()) return new String[] {};
    
            JSONType jsonType = JsonIO.readObject(file);
            if (jsonType instanceof JSONObject) {
                users.deserialize(jsonType);
                
                // Use a HashSet to ensure unique usernames
                return users.getKeys().stream()
                        .map(User::getId)
                        .distinct() // Ensures only unique values are returned
                        .toArray(String[]::new);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new String[] {};
    }
    
    private void exportPhoto() {
        String enteredUserName = ownerNameField.getText().trim();
        String selectedPhotoName = (String) photoDropdown.getSelectedItem();
        String selectedUserName = userList.getSelectedValue();

        if (enteredUserName.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter your user name.");
            return;
        }

        if (selectedPhotoName == null || selectedUserName == null) {
            JOptionPane.showMessageDialog(this, "Please select a photo and a user to export to.");
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
            String encryptedAesKey = selectedPhoto.getKeyBlock().stream()
                    .filter(entry -> entry.getString("user").equals(selectedUser.getId()))
                    .map(entry -> entry.getString("keyData"))
                    .findFirst().orElse(null);

            if (encryptedAesKey == null) {
                JOptionPane.showMessageDialog(this, "You don't have access to this photo.");
                return;
            }

            // Decrypt AES key using user's private key
            PrivateKey privateKey = getUserPrivateKey(currentUser);
            SecretKey aesKey = ElGamalUtil.decryptKey(encryptedAesKey, privateKey);

            // Decode IV and read encrypted data
            IvParameterSpec ivSpec = new IvParameterSpec(Base64.getDecoder().decode(selectedPhoto.getIv()));
            byte[] encryptedData = Files.readAllBytes(new File(selectedPhoto.getEncryptedFilePath()).toPath());

            // Decrypt the photo data
            String decryptedData = AESUtil.decryptAES(Base64.getEncoder().encodeToString(encryptedData), aesKey, ivSpec);
            byte[] finalData = decryptedData.getBytes();

            // Prompt user to save the file
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setDialogTitle("Save Photo");
            fileChooser.setSelectedFile(new File(selectedPhoto.getFileName()));
            int returnValue = fileChooser.showSaveDialog(this);

            if (returnValue == JFileChooser.APPROVE_OPTION) {
                File exportFile = fileChooser.getSelectedFile();
                Files.write(exportFile.toPath(), finalData);
                JOptionPane.showMessageDialog(this, "Photo exported successfully!");
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error exporting photo: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private PrivateKey getUserPrivateKey(User user) {
        // Implement logic to retrieve the user's private key
        return null; // Replace with actual implementation
    }

    private void returnToMainMenu() {
        JFrame parentFrame = (JFrame) SwingUtilities.getWindowAncestor(this);
        parentFrame.getContentPane().removeAll();
        parentFrame.getContentPane().add(new MainMenuPanel());
        parentFrame.revalidate();
        parentFrame.repaint();
    }
}