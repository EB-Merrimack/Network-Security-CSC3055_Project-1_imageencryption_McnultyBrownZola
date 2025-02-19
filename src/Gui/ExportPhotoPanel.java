package Gui;

import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.security.PrivateKey;
import java.security.Security;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.KeyFactory;
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
    private JButton exportButton, returnButton, importKeyButton;
    private JLabel keyFileLabel;
    private Photos photos;
    private Users users;
    private File privateKeyFile = null;  // Store the selected private key file

    static {
        Security.addProvider(new BouncyCastleProvider());
    }

    public ExportPhotoPanel(Photos photos, Users users) {
        this.photos = photos;
        this.users = users;
        setLayout(new BorderLayout(10, 10));

        ownerNameField = new JTextField();
        photoDropdown = new JComboBox<>(photos.getPhotos().stream().map(Photo::getFileName).toArray(String[]::new));
        exportButton = new JButton("Export Photo");
        returnButton = new JButton("Return to Main Menu");
        importKeyButton = new JButton("Import Private Key");
        keyFileLabel = new JLabel("No key selected");

        JPanel inputPanel = new JPanel(new GridLayout(3, 2, 10, 10));
        inputPanel.add(new JLabel("User Name:"));
        inputPanel.add(ownerNameField);
        inputPanel.add(new JLabel("Select Photo:"));
        inputPanel.add(photoDropdown);
        inputPanel.add(new JLabel("Private Key:"));
        
        // Panel for key import button and label
        JPanel keyPanel = new JPanel(new BorderLayout());
        keyPanel.add(importKeyButton, BorderLayout.WEST);
        keyPanel.add(keyFileLabel, BorderLayout.CENTER);
        
        inputPanel.add(keyPanel);

        add(inputPanel, BorderLayout.NORTH);
        add(exportButton, BorderLayout.CENTER);
        add(returnButton, BorderLayout.SOUTH);

        importKeyButton.addActionListener(e -> selectPrivateKeyFile());
        exportButton.addActionListener(e -> exportPhoto());
        returnButton.addActionListener(e -> returnToMainMenu());
    }

    private void selectPrivateKeyFile() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Select Private Key File");
        int returnValue = fileChooser.showOpenDialog(this);

        if (returnValue == JFileChooser.APPROVE_OPTION) {
            privateKeyFile = fileChooser.getSelectedFile();
            keyFileLabel.setText(privateKeyFile.getName());
        } else {
            privateKeyFile = null;
            keyFileLabel.setText("No key selected");
        }
    }

    private void exportPhoto() {
        String enteredUserName = ownerNameField.getText().trim();
        String selectedPhotoName = (String) photoDropdown.getSelectedItem();

        if (enteredUserName.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter your user name.");
            return;
        }

        if (selectedPhotoName == null) {
            JOptionPane.showMessageDialog(this, "Please select a photo to export.");
            return;
        }

        if (privateKeyFile == null) {
            JOptionPane.showMessageDialog(this, "Please import your private key.");
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

        if (selectedPhoto == null) {
            JOptionPane.showMessageDialog(this, "Error: Unable to find photo.");
            return;
        }

        try {
            // Check if the user has access to the photo
            String encryptedAesKey = selectedPhoto.getKeyBlock().stream()
                    .filter(entry -> entry.getString("user").equals(currentUser.getId()))
                    .map(entry -> entry.getString("keyData"))
                    .findFirst().orElse(null);

            if (encryptedAesKey == null) {
                JOptionPane.showMessageDialog(this, "You don't have access to this photo.");
                return;
            }

            // Load the user's private key from file
            PrivateKey privateKey = loadPrivateKeyFromFile(privateKeyFile);
            if (privateKey == null) {
                JOptionPane.showMessageDialog(this, "Error: Could not load private key.");
                return;
            }

            // Decrypt AES key using private key
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
                Files.write(exportFile.toPath(), finalData, StandardOpenOption.CREATE);
                JOptionPane.showMessageDialog(this, "Photo exported successfully!");
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error exporting photo: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private PrivateKey loadPrivateKeyFromFile(File keyFile) {
        try {
            byte[] keyBytes = Files.readAllBytes(keyFile.toPath());
            PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(keyBytes);
            KeyFactory keyFactory = KeyFactory.getInstance("ElGamal", "BC");
            return keyFactory.generatePrivate(keySpec);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private void returnToMainMenu() {
        JFrame parentFrame = (JFrame) SwingUtilities.getWindowAncestor(this);
        parentFrame.getContentPane().removeAll();
        parentFrame.getContentPane().add(new MainMenuPanel());
        parentFrame.revalidate();
        parentFrame.repaint();
    }
}
