package json;

import java.io.InvalidObjectException;
import java.util.List;
import java.util.ArrayList;

import merrimackutil.json.JSONSerializable;
import merrimackutil.json.types.JSONObject;
import merrimackutil.json.types.JSONType;
import merrimackutil.json.types.JSONArray;

public class Photo implements JSONSerializable {
    private String owner;
    private String fileName;
    private String iv;
    private String encryptedFilePath;
    private List<JSONObject> keyBlock; // List of key entry objects

    // Default constructor
    public Photo() {
        this.keyBlock = new ArrayList<>();
    }

    // Parameterized constructor
    public Photo(String owner, String fileName, String iv, String encryptedFilePath, List<JSONObject> keyBlock) {
        this.owner = owner;
        this.fileName = fileName;
        this.iv = iv;
        this.encryptedFilePath = encryptedFilePath;
        this.keyBlock = keyBlock;
    }

    // Getters and Setters
    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getIv() {
        return iv;
    }

    public void setIv(String iv) {
        this.iv = iv;
    }

    public String getEncryptedFilePath() {
        return encryptedFilePath;
    }

    public void setEncryptedFilePath(String encryptedFilePath) {
        this.encryptedFilePath = encryptedFilePath;
    }

    public List<JSONObject> getKeyBlock() {
        return keyBlock;
    }

    public void setKeyBlock(List<JSONObject> keyBlock) {
        this.keyBlock = keyBlock;
    }

    // Deserialize JSONType to this object
    @Override
    public void deserialize(JSONType obj) throws InvalidObjectException {
        if (!(obj instanceof JSONObject)) {
            throw new InvalidObjectException("JSONObject expected.");
        }

        JSONObject photoJson = (JSONObject) obj;

        if (photoJson.containsKey("owner")) {
            owner = photoJson.getString("owner");
        } else {
            throw new InvalidObjectException("Missing owner field -- invalid photo object.");
        }

        if (photoJson.containsKey("fileName")) {
            fileName = photoJson.getString("fileName");
        } else {
            throw new InvalidObjectException("Missing fileName field -- invalid photo object.");
        }

        if (photoJson.containsKey("iv")) {
            iv = photoJson.getString("iv");
        } else {
            throw new InvalidObjectException("Missing iv field -- invalid photo object.");
        }

        if (photoJson.containsKey("encryptedFilePath")) {
            encryptedFilePath = photoJson.getString("encryptedFilePath");
        } else {
            throw new InvalidObjectException("Missing encryptedFilePath field -- invalid photo object.");
        }

        if (photoJson.containsKey("keyBlock")) {
            JSONArray keyBlockArray = photoJson.getArray("keyBlock");
            keyBlock = new ArrayList<>();
            for (int i = 0; i < keyBlockArray.size(); i++) {
                keyBlock.add(keyBlockArray.getObject(i));
            }
        } else {
            throw new InvalidObjectException("Missing keyBlock field -- invalid photo object.");
        }
    }

    // Convert this object to a JSON type
    @Override
    public JSONType toJSONType() {
        JSONObject obj = new JSONObject();
        obj.put("owner", owner);
        obj.put("fileName", fileName);
        obj.put("iv", iv);
        obj.put("encryptedFilePath", encryptedFilePath);

        JSONArray keyBlockArray = new JSONArray();
        for (JSONObject keyEntry : keyBlock) {
            keyBlockArray.add(keyEntry);
        }
        obj.put("keyBlock", keyBlockArray);

        return obj;
    }

    @Override
    public String toString() {
        return "Owner: " + owner + "\nFileName: " + fileName + "\nIV: " + iv + "\nEncryptedFilePath: " + encryptedFilePath;
    }
}
