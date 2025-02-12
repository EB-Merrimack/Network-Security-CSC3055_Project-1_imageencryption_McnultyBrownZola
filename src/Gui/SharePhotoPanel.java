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

public class SharePhotoPanel extends JPanel{
    private JTextField userNameField;
    private JTextField privateKeyField;
    private JTextField shareWithField;
    private JTextField photoNameField;
    private JButton shareButton;
    private JButton returnButton;

    public SharePhotoPanel() {
        setLayout(new BorderLayout(10, 10));

        userNameField = new JTextField(20);
        privateKeyField = new JTextField(20);
        shareWithField = new JTextField(20);
        photoNameField = new JTextField(20);
        shareButton = new JButton("Share Photo");
        returnButton = new JButton("Return to Main Menu");


        JPanel inputPanel = new JPanel(new GridLayout(4, 2, 10, 10));
        inputPanel.add(new JLabel("User Name:"));
        inputPanel.add(userNameField);
        inputPanel.add(new JLabel("Private Key:"));
        inputPanel.add(privateKeyField);
        inputPanel.add(new JLabel("Share With:"));
        inputPanel.add(shareWithField);
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
                String shareWith = shareWithField.getText();
                String photoName = photoNameField.getText();

                if (userName.isEmpty() ||privateKey.isEmpty() || !shareWith.isEmpty() || photoName.isEmpty()) {
                    JOptionPane.showMessageDialog(SharePhotoPanel.this, "Please fill in all fields.");
                } else {
                    JOptionPane.showMessageDialog(SharePhotoPanel.this,  "Photo '" + photoName + "' shared with " + shareWith + " by " + userName + ".");

                    userNameField.setText("");
                    privateKeyField.setText("");
                    shareWithField.setText("");
                    photoNameField.setText("");
                }
            }
        });

          returnButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                JFrame parentFrame = (JFrame) SwingUtilities.getWindowAncestor(SharePhotoPanel.this);
                parentFrame.getContentPane().removeAll(); 
                parentFrame.getContentPane().add(new MainMenuPanel());
                parentFrame.revalidate();
                parentFrame.repaint();
            }
        });
    

    }
    
}
