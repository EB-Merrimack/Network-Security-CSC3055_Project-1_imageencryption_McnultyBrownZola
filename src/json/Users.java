package json;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Users {
    private List<User> keys;

    // Constructor to initialize the keys list
    public Users() {
        this.keys = new ArrayList<>();
    }

    // Getter
    public List<User> getKeys() {
        return keys;
    }

    // Setter
    public void setKeys(List<User> keys) {
        this.keys = keys;
    }

    // Method to add a user and save private key
    public void addUser(String userId) throws IOException {
        KeyPair keyPair = generateRSAKeyPair();
        String publicKeyEncoded = Base64.getEncoder().encodeToString(keyPair.getPublic().getEncoded());

        // Create new user and add to key ring
        User user = new User();
        user.setId(userId);
        user.setPublicKey(publicKeyEncoded);
        keys.add(user);

        // Save private key to a separate file
        savePrivateKeyToFile(userId, keyPair.getPrivate());
    }

    // Generate RSA key pair
    private KeyPair generateRSAKeyPair() {
        try {
            KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
            keyGen.initialize(2048);
            return keyGen.generateKeyPair();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    // Save private key to file
    private void savePrivateKeyToFile(String userId, PrivateKey privateKey) throws IOException {
        String privateKeyEncoded = Base64.getEncoder().encodeToString(privateKey.getEncoded());
        String fileName = userId + "_private_key.txt";
        File file = new File(fileName);

        if (!file.exists()) {
            file.createNewFile();
            System.out.println("Created file: " + fileName);
        }

        try (FileOutputStream fos = new FileOutputStream(file, false)) {
            fos.write(privateKeyEncoded.getBytes());
            System.out.println("Private key saved successfully for user: " + userId);
        }
    }
}
