package Gui;


import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
//import java.security.PublicKey;
import java.security.SecureRandom;
import java.util.Base64;

import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;

import merrimackutil.json.JSONSerializable;
import merrimackutil.json.JsonIO;
import merrimackutil.json.types.JSONArray;
import merrimackutil.json.types.JSONObject;
//import merrimackutil.json.types.JSONType;
import json.Photo;
import json.Photos;
import EncryptionAndDecryption.Encryption.AESUtil;  // Assuming AES encryption utilities are in this class
import EncryptionAndDecryption.Encryption.ElGamalUtil;

public class AddPhotoPanel extends JPanel {
    private Photos photos;
    private JTextField photoField;
    private JTextField userNameField;
    private JButton addButton;
    private JButton returnButton;
    private JList<String> photoList;
    //private static final String PHOTOS_FILE_PATH = "src/json/photos.json";

    public AddPhotoPanel(Photos photos) {
        this.photos = photos;
        setLayout(new BorderLayout(10, 10));

        // Create components
        photoField = new JTextField(20);
        userNameField = new JTextField(20);
        addButton = new JButton("Add Photo");
        returnButton = new JButton("Return to Main Menu");
        //photoList = new Photos(photos);

        // Existing photos
        //loadExistingPhotos();

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

    /*private void loadExistingPhotos() {
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