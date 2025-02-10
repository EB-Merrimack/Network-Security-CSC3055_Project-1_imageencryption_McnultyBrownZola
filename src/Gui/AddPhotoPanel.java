package Gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.security.SecureRandom;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import com.fasterxml.jackson.databind.ObjectMapper;
import json.Photo;
import json.Photos;
import json.KeyEntry;



public class AddPhotoPanel extends JPanel {
    private DefaultListModel<String> photoCollection;
    private JTextField photoField;
    private JTextField userNameField;
    private JButton addButton;
    private JButton returnButton;
    private JList<String> photoList;
    private static final String PHOTOS_FILE_PATH = "src/json/photos.json";
    private static final ObjectMapper mapper = new ObjectMapper();


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
                String photoUrl = photoField.getText();
                String userName = userNameField.getText();
                if (!photoUrl.isEmpty() && !userName.isEmpty()) {
                    addPhoto(userName, photoUrl);
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

    private void addPhoto(String owner, String fileName) {
        try {
            // Load existing photos
            Photos photos = loadJsonFile(PHOTOS_FILE_PATH, Photos.class);
            if (photos == null) {
                photos = new Photos();
            }

            // Generate encryption-related fields (IV and key)
            IvParameterSpec ivSpec = generateIv();
            String iv = Base64.getEncoder().encodeToString(ivSpec.getIV());
            KeyGenerator keyGen = KeyGenerator.getInstance("AES");
            keyGen.init(256);
            SecretKey secretKey = keyGen.generateKey();
            String keyData = Base64.getEncoder().encodeToString(secretKey.getEncoded());

            // Create KeyEntry and Photo object
            List<KeyEntry> keyBlock = new ArrayList<>();
            keyBlock.add(new KeyEntry(owner, keyData));

            Photo newPhoto = new Photo();
            newPhoto.setOwner(owner);
            newPhoto.setFileName(fileName);
            newPhoto.setIv(iv);
            newPhoto.setKeyBlock(keyBlock);

            // Add new photo and save to file
            photos.getPhotos().add(newPhoto);
            saveJsonFile(PHOTOS_FILE_PATH, photos);

            // Update the GUI list
            photoCollection.addElement("Photo: " + fileName + " (Owner: " + owner + ")");

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error adding photo.");
        }
    }

    private void loadExistingPhotos() {
        try {
            Photos photos = loadJsonFile(PHOTOS_FILE_PATH, Photos.class);
            if (photos != null) {
                for (Photo photo : photos.getPhotos()) {
                    photoCollection.addElement("Photo: " + photo.getFileName() + " (Owner: " + photo.getOwner() + ")");
                }
            }
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

    private static <T> T loadJsonFile(String filePath, Class<T> valueType) {
        try {
            File file = new File(filePath);
            if (file.exists()) {
                return mapper.readValue(file, valueType);
            } else {
                return valueType.getDeclaredConstructor().newInstance();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private static void saveJsonFile(String filePath, Object data) {
        try {
            File file = new File(filePath);
            mapper.writerWithDefaultPrettyPrinter().writeValue(file, data);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
