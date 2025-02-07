package json;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Base64;

public class User {
    private String id;
    private String publicKey;

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

    // Method to save private key encoded in base64 format to a file
    public void savePrivateKeyToFile(String privateKey, String filename) throws IOException {
        // Encode the private key in base64
        String privateKeyEncoded = Base64.getEncoder().encodeToString(privateKey.getBytes());
        
        // Write the encoded private key to a file
        try (FileOutputStream fos = new FileOutputStream(filename)) {
            fos.write(privateKeyEncoded.getBytes());
        }
    }
}
