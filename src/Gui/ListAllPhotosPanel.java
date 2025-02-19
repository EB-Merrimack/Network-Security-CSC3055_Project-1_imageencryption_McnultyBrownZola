package Gui;

import javax.swing.*;
import json.Photo;
import json.Photos;
import java.awt.*;
import java.util.List;
import java.util.stream.Collectors;

public class ListAllPhotosPanel extends JPanel {
    private JList<String> photoList;
    private JButton returnButton;

    public ListAllPhotosPanel(Photos photoCollection) {
        setLayout(new BorderLayout(10, 10));

        // Convert Photo objects to a list of filenames
        List<String> photoNames = photoCollection.getPhotos().stream()
                                    .map(Photo::getFileName)
                                    .collect(Collectors.toList());

        // Initialize JList with the photo names
        photoList = new JList<>(photoNames.toArray(new String[0]));

        // Create return button
        returnButton = new JButton("Return to Main Menu");

        // Add components to the panel
        add(new JScrollPane(photoList), BorderLayout.CENTER);
        add(returnButton, BorderLayout.SOUTH);

        // Return button action to navigate back to the main menu
        returnButton.addActionListener(e -> returnToMainMenu());
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
