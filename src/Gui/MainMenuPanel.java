package Gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class MainMenuPanel extends JPanel {
    private CardLayout cardLayout;
    private JPanel contentPanel;
    private DefaultListModel<String> photoCollection; 

    public MainMenuPanel() {
        setLayout(new BorderLayout());

        photoCollection = new DefaultListModel<>();

        // Dropdown menu with choices
        String[] options = {"Select an option", "Add Photo", "Share Photo", "Export Photo", "List All Photos", "Exit"};
        JComboBox<String> menuDropdown = new JComboBox<>(options);

        // Create a CardLayout for internal panels
        cardLayout = new CardLayout();
        contentPanel = new JPanel(cardLayout);

        // Create the sub-panels
        JPanel blankPanel = new JPanel(); // Empty default panel
        JPanel addPhotoPanel = new AddPhotoPanel(photoCollection);
        JPanel sharePhotoPanel = createPlaceholderPanel("Share Photo Panel");
        JPanel exportPhotoPanel = createPlaceholderPanel("Export Photo Panel");
        JPanel listPhotosPanel = createPlaceholderPanel("List All Photos Panel");

        // Add sub-panels to the CardLayout
        contentPanel.add(blankPanel, "Blank");
        contentPanel.add(addPhotoPanel, "Add Photo");
        contentPanel.add(sharePhotoPanel, "Share Photo");
        contentPanel.add(exportPhotoPanel, "Export Photo");
        contentPanel.add(listPhotosPanel, "List All Photos");

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