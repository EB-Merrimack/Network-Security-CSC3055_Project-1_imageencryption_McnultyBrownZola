package Gui;

import java.awt.*;
import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;

import javax.crypto.CipherOutputStream;

public class ExportPhotoPanel extends JPanel {
    private JTextField userNameField;
    private JTextField privateKeyField;
    private JTextField photoNameField;
    private JButton shareButton;
    private JButton returnButton;

    public ExportPhotoPanel() {
        setLayout(new BorderLayout(10, 10));

        userNameField = new JTextField(20);
        privateKeyField = new JTextField(20); // Now added to UI
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
                String photoName = photoNameField.getText();

                if (userName.isEmpty() || photoName.isEmpty()) {
                    JOptionPane.showMessageDialog(ExportPhotoPanel.this, "Please fill in all fields.");
                } else {
                    JOptionPane.showMessageDialog(ExportPhotoPanel.this, "Photo exported successfully!");

                    userNameField.setText("");
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

    public static void decryptFile(File inputFile, File outputFile, SecretKey secretKey, IvParameterSpec ivSpec) throws Exception {
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(Cipher.DECRYPT_MODE, secretKey, ivSpec);

        try (FileInputStream fis = new FileInputStream(inputFile);
             FileOutputStream fos = new FileOutputStream(outputFile);
             CipherOutputStream cos = new CipherOutputStream(fos, cipher)) {

            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = fis.read(buffer)) != -1) {
                cos.write(buffer, 0, bytesRead);
            }
        }
    }
}
