import javax.swing.JFrame;
import Gui.GUIBuilder;
import json.Photos;
import json.Users;
import merrimackutil.json.JsonIO;
import java.io.File;
import java.io.IOException;

public class Main {
    private static final String PHOTOS_FILE_PATH = "src/json/photos.json";
    private static final String USERS_FILE_PATH = "src/json/users.json";

    public static void main(String[] args) {
        // Ensure the photos.json file exists
        File photosFile = new File(PHOTOS_FILE_PATH);
        if (!photosFile.exists()) {
            try {
                Photos photos = new Photos();
                JsonIO.writeFormattedObject(photos, photosFile);
                System.out.println("Created photos.json file.");
            } catch (IOException e) {
                e.printStackTrace();
                System.out.println("Error creating photos.json file.");
            }
        }

        // Ensure the users.json file exists
        File usersFile = new File(USERS_FILE_PATH);
        if (!usersFile.exists()) {
            try {
                Users users = new Users();
                JsonIO.writeFormattedObject(users, usersFile);
                System.out.println("Created users.json file.");
            } catch (IOException e) {
                e.printStackTrace();
                System.out.println("Error creating users.json file.");
            }
        }

        GUIBuilder gui = new GUIBuilder();
        // What to do when the window closes:
        gui.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        // Size of the window, in pixels
        gui.setSize(800, 600);
        // Make the window "visible"
        gui.setVisible(true);
    }
}
