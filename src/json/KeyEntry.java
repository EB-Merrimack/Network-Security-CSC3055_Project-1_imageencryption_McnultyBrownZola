package json;

public class KeyEntry {
    private String user;
    private String keyData;

    public KeyEntry() {
    }
    

    public KeyEntry(String user, String keyData) {
        this.user = user;
        this.keyData = keyData;
    }

    // Getters and Setters
    public String getUser() {
        return user;
    }

    /**
     * Sets the user for this key entry.
     * @param user user to set
     */
    public void setUser(String user) {
        this.user = user;
    }

    /**
     * Returns the key data for this key entry. This is the actual encrypted AES
     * key, encoded as a Base64 string.
     * @return key data as a Base64 string
     */
    public String getKeyData() {
        return keyData;
    }

    /**
     * Sets the key data for this key entry. This should be the actual encrypted AES
     * key, encoded as a Base64 string.
     * @param keyData key data as a Base64 string
     */
    public void setKeyData(String keyData) {
        this.keyData = keyData;
    }
}
