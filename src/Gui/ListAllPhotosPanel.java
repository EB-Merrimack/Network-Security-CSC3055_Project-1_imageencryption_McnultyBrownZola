package Gui;

import javax.swing.*;

import json.Photos;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ListAllPhotosPanel extends JPanel{
    private JList<String> photoList;
    private JButton returnButton;


    public ListAllPhotosPanel(Photos photoCollection) {
        setLayout(new BorderLayout(10, 10));

        returnButton = new JButton("Return to Main Menu");
        //photoList = new JList<>(photoCollection);

        add(new JScrollPane(photoList), BorderLayout.CENTER);



    // Return to main menu button action
        returnButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                JFrame parentFrame = (JFrame) SwingUtilities.getWindowAncestor(ListAllPhotosPanel.this);
                parentFrame.getContentPane().removeAll(); 
                parentFrame.getContentPane().add(new MainMenuPanel());
                parentFrame.revalidate();
                parentFrame.repaint();
            }
        });
    }


 
    
}
