package json;

import java.util.List;

public class Photo {
    private String owner;
    private String fileName;
    private String iv;
    private List<KeyEntry> keyBlock;

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
}
