package Gui;

import javax.swing.*;

import json.Photos;

import java.awt.*;

public class MainMenuPanel extends JPanel {
    private CardLayout cardLayout;
    private JPanel contentPanel;
    private Photos photos;

    public MainMenuPanel() {
        setLayout(new BorderLayout());

        photos = new Photos();

        // Dropdown menu with choices
        String[] options = {"Select an option", "Add Photo", "Share Photo", "Export Photo", "List Accessable Photos", "List All Photos", "Exit"};
        JComboBox<String> menuDropdown = new JComboBox<>(options);

        // Create a CardLayout for internal panels
        cardLayout = new CardLayout();
        contentPanel = new JPanel(cardLayout);

        // Create the sub-panels
        JPanel blankPanel = new JPanel(); // Empty default panel
        JPanel addPhotoPanel = new AddPhotoPanel(photos);
        JPanel sharePhotoPanel = new SharePhotoPanel();
        JPanel exportPhotoPanel = createPlaceholderPanel("Export Photo Panel");
        JPanel listAccessablePhotosPanel = createPlaceholderPanel("List Accessable Photos Panel");
        //JPanel listPhotosPanel = new ListAllPhotosPanel(photos);

        // Add sub-panels to the CardLayout
        contentPanel.add(blankPanel, "Blank");
        contentPanel.add(addPhotoPanel, "Add Photo");
        contentPanel.add(sharePhotoPanel, "Share Photo");
        contentPanel.add(exportPhotoPanel, "Export Photo");
        contentPanel.add(listAccessablePhotosPanel, "List Accessable Photos");
        //contentPanel.add(listPhotosPanel, "List All Photos");

        // Action listener to switch between internal panels
        menuDropdown.addActionListener(e -> {
            String selected = (String) menuDropdown.getSelectedItem();
            if ("Exit".equals(selected)) {
                System.exit(0);
            } else {
                cardLayout.show(contentPanel, selected.equals("Select an option") ? "Blank" : selected);
            }
        });

        // Add components to MainMenuPanel
        add(new JLabel("Main Menu", SwingConstants.CENTER), BorderLayout.NORTH);
        add(menuDropdown, BorderLayout.NORTH);
        add(contentPanel, BorderLayout.CENTER);
    }

    private JPanel createPlaceholderPanel(String text) {
        JPanel panel = new JPanel();
        panel.add(new JLabel(text));
        return panel;
    }
}