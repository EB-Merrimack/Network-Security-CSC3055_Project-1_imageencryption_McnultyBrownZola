package json;

public class KeyEntry {
    private String user;
    private String keyData;

    // Constructor
    public KeyEntry(String user, String keyData) {
        this.user = user;
        this.keyData = keyData;
    }

    // Getters and Setters
    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getKeyData() {
        return keyData;
    }

    public void setKeyData(String keyData) {
        this.keyData = keyData;
    }
}
