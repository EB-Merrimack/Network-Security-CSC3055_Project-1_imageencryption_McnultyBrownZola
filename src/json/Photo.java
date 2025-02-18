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

    /**
     * Returns the name of the file of the photo.
     *
     * @return the name of the file of the photo
     */
    public String getFileName() {
        return fileName;
    }

    /**
     * Sets the name of the file of the photo.
     *
     * @param fileName the name of the file of the photo to be set
     */
    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    /**
     * Returns the initialization vector (IV) for the photo. This is used
     * during the encryption process.
     *
     * @return the initialization vector (IV) for the photo
     */
    public String getIv() {
        return iv;
    }

    /**
     * Sets the initialization vector (IV) for the photo. This IV is used
     * during the encryption process.
     *
     * @param iv the initialization vector (IV) to be set for the photo
     */
    public void setIv(String iv) {
        this.iv = iv;
    }

    /**
     * Returns the encrypted file path of the photo.
     *
     * @return the encrypted file path of the photo
     */
    public String getEncryptedFilePath() {
        return encryptedFilePath;
    }

    /**
     * Sets the encrypted file path of the photo.
     *
     * @param encryptedFilePath the encrypted file path to be set for the photo
     */
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
