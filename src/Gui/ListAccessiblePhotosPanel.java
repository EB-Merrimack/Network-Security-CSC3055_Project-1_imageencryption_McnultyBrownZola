package Gui;

import javax.swing.*;
import json.Photo;
import json.Photos;
import json.User;
import json.Users;
import merrimackutil.json.JsonIO;
import merrimackutil.json.types.JSONObject;
import merrimackutil.json.types.JSONType;

import java.awt.*;
import java.io.File;
import java.util.List;
import java.util.stream.Collectors;

public class ListAccessiblePhotosPanel extends JPanel {
    private static final String USERS_FILE_PATH = "src/json/users.json";
        private JComboBox<String> userDropdown;
        private JList<String> photoList;
        private JButton returnButton;
        private Photos photos;
        private Users users;
    
        public ListAccessiblePhotosPanel(Photos photos, Users users) {
            this.photos = photos;
            this.users = users;
            setLayout(new BorderLayout(10, 10));
    
            userDropdown = new JComboBox<>(loadUsernames());
            photoList = new JList<>();
            returnButton = new JButton("Return to Main Menu");
    
            // Top Panel with User Selection
            JPanel topPanel = new JPanel(new BorderLayout());
            topPanel.add(new JLabel("Select User: "), BorderLayout.WEST);
            topPanel.add(userDropdown, BorderLayout.CENTER);
            add(topPanel, BorderLayout.NORTH);
    
            // Photo List in Scroll Pane
            add(new JScrollPane(photoList), BorderLayout.CENTER);
            add(returnButton, BorderLayout.SOUTH);
    
            // Load photos when user is selected
            userDropdown.addActionListener(e -> updatePhotoList());
    
            // Return button action
            returnButton.addActionListener(e -> returnToMainMenu());
    
            // Initial load for the first user in dropdown
            if (userDropdown.getItemCount() > 0) {
                updatePhotoList();
            }
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


    private void updatePhotoList() {
        String selectedUser = (String) userDropdown.getSelectedItem();
        if (selectedUser == null) return;

        // Get accessible photos with ownership markers
        List<String> accessiblePhotos = photos.getPhotos().stream()
            .filter(photo -> photo.getOwner().equals(selectedUser) || 
                             photo.getKeyBlock().stream().anyMatch(entry -> entry.getString("user").equals(selectedUser)))
            .map(photo -> {
                if (photo.getOwner().equals(selectedUser)) {
                    return "OWNED: " + photo.getFileName(); // Mark as owned
                } else {
                    return "SHARED: " + photo.getFileName(); // Mark as shared
                }
            })
            .collect(Collectors.toList());

        // Update JList
        photoList.setListData(accessiblePhotos.toArray(new String[0]));
    }

    private void returnToMainMenu() {
        JFrame parentFrame = (JFrame) SwingUtilities.getWindowAncestor(this);
        if (parentFrame != null) {
            parentFrame.getContentPane().removeAll();
            parentFrame.getContentPane().add(new MainMenuPanel());
            parentFrame.revalidate();
            parentFrame.repaint();
        }
    }
}
