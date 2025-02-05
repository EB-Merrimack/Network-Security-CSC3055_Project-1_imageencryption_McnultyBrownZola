package Gui;

import java.awt.*;
import javax.swing.*;
import java.awt.event.*;



public class GUIBuilder extends JFrame {
    private JComboBox<String> stringBox; // Dropdown menu
    private JPanel dropdownPanel; // Panel to hold dropdown

    private static final String[] labels = { 
        "Add Photo", "Share Photo", "Export Photo", "List All Photos", "Exit" 
    };

    public GUIBuilder() {
        super("Main Menu");
        setLayout(new BorderLayout(10, 10));

        // Label above dropdown
        JLabel menuLabel = new JLabel("Select an option:");
        menuLabel.setFont(new Font("Arial", Font.BOLD, 14));

        // Dropdown menu
        stringBox = new JComboBox<>(labels);
        stringBox.setMaximumRowCount(5);

        // Panel for dropdown
        dropdownPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        dropdownPanel.add(menuLabel);
        dropdownPanel.add(stringBox);

        // Handle selection change
        stringBox.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String selected = (String) stringBox.getSelectedItem();
                removeDropdown(); // Hide dropdown BEFORE showing message

                // Delay pop-up so UI updates first
                SwingUtilities.invokeLater(() -> {
                    handleSelection(selected);
                    restoreDropdown(); // Bring back dropdown after pop-up closes
                });
            }
        });

        // Add elements to the frame
        add(dropdownPanel, BorderLayout.NORTH);

        // Set window properties
        setSize(300, 150);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null); // Center window
        setVisible(true);
    }

    private void handleSelection(String option) {
        switch (option) {
            case "Add Photo":
                JOptionPane.showMessageDialog(this, "Feature: Add Photo (To be implemented)");
                break;
            case "Share Photo":
                JOptionPane.showMessageDialog(this, "Feature: Share Photo (To be implemented)");
                break;
            case "Export Photo":
                JOptionPane.showMessageDialog(this, "Feature: Export Photo (To be implemented)");
                break;
            case "List All Photos":
                JOptionPane.showMessageDialog(this, "Feature: List All Photos (To be implemented)");
                break;
            case "Exit":
                System.exit(0);
                break;
        }
    }

    private void removeDropdown() {
        dropdownPanel.setVisible(false); // Hide the dropdown panel
    }

    private void restoreDropdown() {
        dropdownPanel.setVisible(true); // Show the dropdown panel again
    }

    public static void main(String[] args) {
        new GUIBuilder();
    }
}