package json;

import java.util.List;

public class Photo {
    private String owner;
    private String fileName;
    private String iv;
    private List<KeyEntry> keyBlock;
    private String encryptedFilePath;

    // Default constructor
    public Photo() {}

    // Parameterized constructor
    public Photo(String owner, String fileName, String iv, List<KeyEntry> keyBlock, String encryptedFilePath) {
        this.owner = owner;
        this.fileName = fileName;
        this.iv = iv;
        this.keyBlock = keyBlock;
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

    public List<KeyEntry> getKeyBlock() {
        return keyBlock;
    }

    public void setKeyBlock(List<KeyEntry> keyBlock) {
        this.keyBlock = keyBlock;
    }

    public String getEncryptedFilePath() {
        return encryptedFilePath;
    }

    public void setEncryptedFilePath(String encryptedFilePath) {
        this.encryptedFilePath = encryptedFilePath;
    }
}
