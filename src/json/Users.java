package json;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InvalidObjectException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JOptionPane;

import merrimackutil.json.JSONSerializable;
import merrimackutil.json.JsonIO;
import merrimackutil.json.parser.JSONParser;
import merrimackutil.json.types.JSONArray;
import merrimackutil.json.types.JSONObject;
import merrimackutil.json.types.JSONType;

public class Users implements JSONSerializable {
    private List<User> keys;
    private static final String FILE_PATH = "users.json"; // JSON file to store user data
    public Users() {
        this.keys = new ArrayList<>();
    }

    public List<User> getKeys() {
        return keys;
    }

    public void setKeys(List<User> keys) {
        this.keys = keys;
    }
  // Load users from JSON file
  private void loadExistingUsers() {
    try {
        File file = new File(FILE_PATH);
        if (!file.exists()) return; // If file doesn't exist, there's nothing to load

        JSONType jsonType = JsonIO.readObject(file);
        if (jsonType instanceof JSONObject) {
            this.deserialize(jsonType); // Deserialize into the current Users object
            for (User user : this.getKeys()) {
                // Populate any necessary data, e.g., a HashMap
            }
        }
    } catch (FileNotFoundException e) {
        JOptionPane.showMessageDialog(null, "Error loading users: File not found.");
    } catch (IOException e) {
        JOptionPane.showMessageDialog(null, "Error reading users.json.");
    } catch (Exception e) {
        JOptionPane.showMessageDialog(null, "Error loading users.");
    }
}

    @Override
    public void deserialize(JSONType jsonType) throws InvalidObjectException {
        if (!(jsonType instanceof JSONObject)) {
            throw new InvalidObjectException("JSONObject expected.");
        }

        JSONObject jsonObject = (JSONObject) jsonType;

        if (jsonObject.containsKey("keys")) {
            JSONArray keysArray = jsonObject.getArray("keys");
            for (int i = 0; i < keysArray.size(); i++) {
                User user = new User();
                user.deserialize(keysArray.getObject(i));
                keys.add(user);
            }
        } else {
            throw new InvalidObjectException("Missing keys field -- invalid Users object.");
        }
    }
    public void saveToFile(String filePath) {
    try {
        JSONObject jsonObject = (JSONObject) this.toJSONType();  // Convert to JSON representation
        FileWriter fileWriter = new FileWriter(filePath);
        fileWriter.write(jsonObject.toString());  // Write the JSON to the file
        fileWriter.close();
        System.out.println("User data saved to: " + filePath);
    } catch (IOException e) {
        System.err.println("Error saving users to file: " + e.getMessage());
    }
}
    @Override
    public JSONType toJSONType() {
        JSONObject jsonObject = new JSONObject();
        JSONArray keysArray = new JSONArray();
        for (User user : this.getKeys()) {  // Use the correct list 'keys'
            keysArray.add(user.toJSONType());  // Convert each User to JSON
        }
        jsonObject.put("keys", keysArray);
        return jsonObject;
    }
    


}
