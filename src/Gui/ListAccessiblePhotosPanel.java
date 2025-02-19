package Gui;

import javax.swing.*;
import json.Photo;
import json.Photos;
import json.User;
import json.Users;
import java.awt.*;
import java.util.List;
import java.util.stream.Collectors;

public class ListAccessiblePhotosPanel extends JPanel {
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
        return users.getKeys().stream().map(User::getId).toArray(String[]::new);
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
