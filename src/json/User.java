package json;

import java.util.Objects;

public class User {
    private String id;
    private String publicKey;

    // Default Constructor
    public User() {
    }

    // Parameterized Constructor
    public User(String id, String publicKey) {
        this.id = id;
        this.publicKey = publicKey;
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPublicKey() {
        return publicKey;
    }

    public void setPublicKey(String publicKey) {
        this.publicKey = publicKey;
    }

    // Override toString for better logging
    @Override
    public String toString() {
        return "User{id='" + id + "', publicKey='" + publicKey + "'}";
    }

    // Override equals & hashCode for better comparisons
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(id, user.id) && Objects.equals(publicKey, user.publicKey);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, publicKey);
    }
}
