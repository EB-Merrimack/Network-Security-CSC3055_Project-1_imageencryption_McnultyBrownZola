package json;

import java.io.InvalidObjectException;
import merrimackutil.json.JSONSerializable;
import merrimackutil.json.types.JSONObject;
import merrimackutil.json.types.JSONType;

public class Photo implements JSONSerializable {
    private String owner;
    private String fileName;
    private String iv;
    private String encryptedFilePath;

    // Default constructor
    public Photo() {}

    // Parameterized constructor
    public Photo(String owner, String fileName, String iv, String encryptedFilePath) {
        this.owner = owner;
        this.fileName = fileName;
        this.iv = iv;
        this.encryptedFilePath = encryptedFilePath;
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

        if (photoJson.size() > 4) {
            throw new InvalidObjectException("Superfluous fields -- invalid photo object.");
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
        return obj;
    }

    @Override
    public String toString() {
        return "Owner: " + owner + "\nFileName: " + fileName + "\nIV: " + iv + "\nEncryptedFilePath: " + encryptedFilePath;
    }
}
