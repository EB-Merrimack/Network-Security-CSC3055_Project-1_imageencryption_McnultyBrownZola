package Gui;

import java.awt.*;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ExportPhotoPanel extends JPanel {
    private JTextField userNameField;
    private JTextField privateKeyField;
    private JTextField photoNameField;
    private JButton shareButton;
    private JButton returnButton;

    public ExportPhotoPanel() {
        setLayout(new BorderLayout(10, 10));

        userNameField = new JTextField(20);
        privateKeyField = new JTextField(20);
        photoNameField = new JTextField(20);
        shareButton = new JButton("Export Photo");
        returnButton = new JButton("Return to Main Menu");

        JPanel inputPanel = new JPanel(new GridLayout(4, 2, 10, 10));
        inputPanel.add(new JLabel("User Name:"));
        inputPanel.add(userNameField);
        inputPanel.add(new JLabel("Photo Name:"));
        inputPanel.add(photoNameField);

        add(inputPanel, BorderLayout.NORTH);

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(shareButton);
        buttonPanel.add(returnButton);
        add(buttonPanel, BorderLayout.SOUTH);

        shareButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String userName = userNameField.getText();
                String privateKey = privateKeyField.getText();
                String photoName = photoNameField.getText();

                if (userName.isEmpty() || privateKey.isEmpty() || photoName.isEmpty()) {
                    JOptionPane.showMessageDialog(ExportPhotoPanel.this, "Please fill in all fields.");
                } else {
                    JOptionPane.showMessageDialog(ExportPhotoPanel.this, "Photo exported successfully!");

                    userNameField.setText("");
                    privateKeyField.setText("");
                    photoNameField.setText("");
                }
            }
        });

        returnButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                JFrame parentFrame = (JFrame) SwingUtilities.getWindowAncestor(ExportPhotoPanel.this);
                parentFrame.getContentPane().removeAll();
                parentFrame.getContentPane().add(new MainMenuPanel());
                parentFrame.revalidate();
                parentFrame.repaint();
            }
        });

    }
}
