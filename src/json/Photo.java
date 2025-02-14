package json;

import java.util.List;

public class Photo {
    private String owner;
    private String fileName;
    private String iv;
    private String encryptedFilePath;

    // Default constructor
    public Photo() {}

    // Parameterized constructor
    public Photo(String owner, String fileName, String iv,String encryptedFilePath) {
        this.owner = owner;
        this.fileName = fileName;
        this.iv = iv;
        this.encryptedFilePath = encryptedFilePath;
    }

    // Getters and Setters
    public String getOwner() {
        return owner;
    }

    /**
     * Sets the owner of the photo. This is the user who uploaded the image.
     * @param owner the user ID of the owner
     */
    public void setOwner(String owner) {
        this.owner = owner;
    }

    /**
     * Retrieves the name of the plaintext image file.
     * @return the file name of the plaintext image
     */
    public String getFileName() {
        return fileName;
    }

    /**
     * Sets the name of the plaintext image file.
     * @param fileName the file name of the plaintext image
     */
    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    /**
     * Retrieves the Base64 encoded initialization vector used for AES GCM encryption and decryption of the image.
     * @return the Base64 encoded IV
     */
    public String getIv() {
        return iv;
    }

    /**
     * Sets the initialization vector for the AES GCM encryption of the photo.
     * This IV is stored in the JSON file and used for encryption and decryption.
     * @param iv the Base64 encoded initialization vector
     */
    public void setIv(String iv) {
        this.iv = iv;
    }

   

    /**
     * Retrieves the path to the encrypted image file.
     * 
     * @return the path to the encrypted image file
     */

    public String getEncryptedFilePath() {
        return encryptedFilePath;
    }

    /**
     * Set the path to the encrypted image file.
     * @param encryptedFilePath the path to the encrypted image file
     */
    public void setEncryptedFilePath(String encryptedFilePath) {
        this.encryptedFilePath = encryptedFilePath;
    }
}
