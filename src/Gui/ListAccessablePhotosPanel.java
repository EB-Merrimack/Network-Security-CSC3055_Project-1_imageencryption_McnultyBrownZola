package Gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ListAccessablePhotosPanel extends JPanel{
    private JTextField userNameField;
    private JButton viewButton;
    private JButton returnButton;
    private DefaultListModel<String> photoCollection;
    private DefaultListModel<String> sharedPhotos;
    private JList<String> ownedList;
    private JList<String> sharedList;





    public ListAccessablePhotosPanel(DefaultListModel<String> photoCollection, DefaultListModel<String> sharedPhotos) {
        this.photoCollection = photoCollection;
        this.sharedPhotos = sharedPhotos;
        setLayout(new BorderLayout(10, 10));

        JPanel inputPanel = new JPanel();
        inputPanel.add(new JLabel("Enter Username:"));
        userNameField = new JTextField(15);
        inputPanel.add(userNameField);

        viewButton = new JButton("View Photos");
        returnButton = new JButton("Return to Main Menu");
        inputPanel.add(viewButton);

        add(inputPanel, BorderLayout.NORTH);


        ownedList = new JList<>(new DefaultListModel<>());
        sharedList = new JList<>(new DefaultListModel<>());

        JPanel listsPanel = new JPanel(new GridLayout(2, 1, 10, 10));
        listsPanel.add(createLabeledPanel("Photos You Own", ownedList));
        listsPanel.add(createLabeledPanel("Photos Shared With You", sharedList));

        add(listsPanel, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(returnButton);
        add(buttonPanel, BorderLayout.SOUTH);


        viewButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String userName = userNameField.getText().trim();
                if (userName.isEmpty()) {
                    JOptionPane.showMessageDialog(ListAccessablePhotosPanel.this, "Please enter a username.");
                    return;
                }

                // Filter owned photos
                DefaultListModel<String> ownedModel = (DefaultListModel<String>) ownedList.getModel();
                ownedModel.clear();
                for (int i = 0; i < photoCollection.size(); i++) {
                    String photoEntry = photoCollection.get(i);
                    if (photoEntry.contains("(Owner: " + userName + ")")) {
                        ownedModel.addElement(photoEntry);
                    }
                }

                // Filter shared photos
                DefaultListModel<String> sharedModel = (DefaultListModel<String>) sharedList.getModel();
                sharedModel.clear();
                for (int i = 0; i < sharedPhotos.size(); i++) {
                    String sharedEntry = sharedPhotos.get(i);
                    if (sharedEntry.contains("Shared With: " + userName)) {
                        sharedModel.addElement(sharedEntry);
                    }
                }

                if (ownedModel.isEmpty() && sharedModel.isEmpty()) {
                    JOptionPane.showMessageDialog(ListAccessablePhotosPanel.this, "No photos found for this user.");
                }
            }
        });

    // Return to main menu button action
        returnButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                JFrame parentFrame = (JFrame) SwingUtilities.getWindowAncestor(ListAccessablePhotosPanel.this);
                parentFrame.getContentPane().removeAll(); 
                parentFrame.getContentPane().add(new MainMenuPanel());
                parentFrame.revalidate();
                parentFrame.repaint();
            }
        });
    }

    private JPanel createLabeledPanel(String title, JList<String> list) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.add(new JLabel(title, SwingConstants.CENTER), BorderLayout.NORTH);
        panel.add(new JScrollPane(list), BorderLayout.CENTER);
        return panel;
    }
    
}
