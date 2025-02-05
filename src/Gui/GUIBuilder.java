package Gui;

import java.awt.*;
import javax.swing.*;
import java.awt.event.*;

public class GUIBuilder extends JFrame {
    private JComboBox<String> stringBox; // Dropdown menu
    private JPanel cardPanel;            // Panel that will hold different screens
    private CardLayout cardLayout;       // CardLayout to manage different screens

    private DefaultListModel<String> photoCollection; 

    private static final String[] labels = { 
        "Select an option", "Add Photo", "Share Photo", "Export Photo", "List All Photos", "Exit" 
    };

    public GUIBuilder() {
        super("Main Menu");

        setLayout(new BorderLayout(10, 10));

        // Initialize the photo collection
        photoCollection = new DefaultListModel<>();

        JLabel menuLabel = new JLabel("Select an option:");
        menuLabel.setFont(new Font("Arial", Font.BOLD, 14));

        // Dropdown menu
        stringBox = new JComboBox<>(labels);
        stringBox.setMaximumRowCount(5);

        // Panel for dropdown
        JPanel dropdownPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        dropdownPanel.add(menuLabel);
        dropdownPanel.add(stringBox);

        // Create a card layout container
        cardLayout = new CardLayout();
        cardPanel = new JPanel(cardLayout);

        // Create panels for each feature
        JPanel addPhotoPanel = new AddPhotoPanel(photoCollection); 
        JPanel sharePhotoPanel = createSharePhotoPanel();
        JPanel exportPhotoPanel = createExportPhotoPanel();
        JPanel listPhotosPanel = createListPhotosPanel();
        JPanel exitPanel = new JPanel(); 

        // Add panels to the card layout
        cardPanel.add(new JPanel(), "Select an option");  
        cardPanel.add(addPhotoPanel, "Add Photo");
        cardPanel.add(sharePhotoPanel, "Share Photo");
        cardPanel.add(exportPhotoPanel, "Export Photo");
        cardPanel.add(listPhotosPanel, "List All Photos");
        cardPanel.add(exitPanel, "Exit");

        // Action listener for the dropdown selection
        stringBox.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String selected = (String) stringBox.getSelectedItem();

                removeDropdown(); 

                SwingUtilities.invokeLater(() -> {
                    handleSelection(selected);  
                    restoreDropdown(); 
                });
            }
        });

        // Add elements to the frame
        add(dropdownPanel, BorderLayout.NORTH);
        add(cardPanel, BorderLayout.CENTER);

        setSize(400, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null); // Center window
        setVisible(true);
    }

    // Switch between different panels
    private void handleSelection(String option) {
        cardLayout.show(cardPanel, option);
    }

    // Create the Share Photo panel
    private JPanel createSharePhotoPanel() {
        JPanel panel = new JPanel();
        panel.add(new JLabel("Share Photo (Functionality will be implemented here)"));
        return panel;
    }

    // Create the Export Photo panel
    private JPanel createExportPhotoPanel() {
        JPanel panel = new JPanel();
        panel.add(new JLabel("Export Photo (Functionality will be implemented here)"));
        return panel;
    }

    // Create the List All Photos panel
    private JPanel createListPhotosPanel() {
        JPanel panel = new JPanel();
        panel.add(new JLabel("List All Photos (Functionality will be implemented here)"));
        return panel;
    }

    private void removeDropdown() {
        stringBox.setEnabled(false);
    }

    private void restoreDropdown() {
        stringBox.setEnabled(true);
    }

    public static void main(String[] args) {
        new GUIBuilder();
    }
}