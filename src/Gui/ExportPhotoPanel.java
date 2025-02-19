package Gui;

import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.nio.charset.StandardCharsets;
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

            // Load the user's private key from file (PEM-formatted)
            PrivateKey privateKey = loadPrivateKeyFromFile(privateKeyFile);
            if (privateKey == null) {
                JOptionPane.showMessageDialog(this, "Error: Could not load private key.");
                return;
            }

            // Decrypt AES key using the private key
            SecretKey aesKey = ElGamalUtil.decryptKey(encryptedAesKey, privateKey);
            if (aesKey == null) {
                JOptionPane.showMessageDialog(this, "Error: AES key decryption failed.");
                return;
            }

            // Decode IV and read the encrypted photo data as a String (Base64-encoded)
            IvParameterSpec ivSpec = new IvParameterSpec(Base64.getDecoder().decode(selectedPhoto.getIv()));
            String encryptedDataStr = new String(Files.readAllBytes(Paths.get(selectedPhoto.getEncryptedFilePath())), StandardCharsets.UTF_8);
            
            // Log lengths for debugging
            System.out.println("IV Length: " + ivSpec.getIV().length);
            byte[] encryptedDataBytes = Base64.getDecoder().decode(encryptedDataStr);
            System.out.println("Encrypted Data Length (Base64 decoded): " + encryptedDataBytes.length);
            
            // Decrypt the photo data (this returns a byte array)
            byte[] decryptedData = AESUtil.decryptAES(encryptedDataStr, aesKey, ivSpec);
            System.out.println("Decrypted Data Length: " + decryptedData.length);

            // Determine the output file name (remove .enc extension and add .jpg)
            String outputFileName = getJpgFileName(selectedPhoto.getFileName());

            // Prompt user to save the file
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setDialogTitle("Save Photo");
            fileChooser.setSelectedFile(new File(outputFileName));
            int returnValue = fileChooser.showSaveDialog(this);
            if (returnValue == JFileChooser.APPROVE_OPTION) {
                File exportFile = fileChooser.getSelectedFile();
                Files.write(exportFile.toPath(), decryptedData, StandardOpenOption.CREATE);
                JOptionPane.showMessageDialog(this, "Photo exported successfully!");
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error exporting photo: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // Helper method to convert encrypted filename to a JPG filename.
    private String getJpgFileName(String encryptedFileName) {
        if (encryptedFileName.toLowerCase().endsWith(".enc")) {
            return encryptedFileName.substring(0, encryptedFileName.length() - 4) + ".jpg";
        } else {
            return encryptedFileName + ".jpg";
        }
    }

    private PrivateKey loadPrivateKeyFromFile(File keyFile) {
        try {
            // Read the PEM file content as UTF-8 text
            String keyContent = new String(Files.readAllBytes(keyFile.toPath()), StandardCharsets.UTF_8);
            // Remove the PEM header and footer and any whitespace
            String privateKeyPEM = keyContent.replace("-----BEGIN PRIVATE KEY-----", "")
                                             .replace("-----END PRIVATE KEY-----", "")
                                             .replaceAll("\\s", "");
            // Decode the Base64 encoded key
            byte[] encoded = Base64.getDecoder().decode(privateKeyPEM);
            // Create a KeyFactory instance for ElGamal using BouncyCastle and generate the PrivateKey
            KeyFactory keyFactory = KeyFactory.getInstance("ElGamal", "BC");
            PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(encoded);
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
