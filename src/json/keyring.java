package json;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.SecureRandom;
import java.security.Security;
import java.util.Base64;
import java.util.List;
import java.util.ArrayList;

public class keyring {
    public List<User> keys;

    static {
        Security.addProvider(new BouncyCastleProvider());
    }

    public keyring() {
        this.keys = new ArrayList<>();
    }

    public void load(String filePath) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            Users users = objectMapper.readValue(new File(filePath), Users.class);
            this.keys = users.getUsers();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void save(String filePath) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            Users users = new Users();
            users.setUsers(this.keys);
            objectMapper.writeValue(new File(filePath), users);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void addUser(String id, String publicKey) {
        User newUser = new User();
        newUser.setId(id);
        newUser.setPublicKey(publicKey);
        this.keys.add(newUser);
    }

    // Generate and add a new user with a secure public key using Bouncy Castle and SecureRandom
    public void addSecureUser(String id) {
        try {
            if (findUserById(id) == null) {
                KeyPairGenerator keyGen = KeyPairGenerator.getInstance("ElGamal", "BC");
                SecureRandom secureRandom = SecureRandom.getInstanceStrong();
                keyGen.initialize(2048, secureRandom);
                KeyPair pair = keyGen.generateKeyPair();

                // Encode the public key as a random bit string
                byte[] publicKeyBitString = pair.getPublic().getEncoded();
                secureRandom.nextBytes(publicKeyBitString);
                String publicKey = Base64.getEncoder().encodeToString(publicKeyBitString);

                addUser(id, publicKey);

                // Encode the private key as a random bit string
                byte[] privateKeyBitString = pair.getPrivate().getEncoded();
                secureRandom.nextBytes(privateKeyBitString);
                savePrivateKeyToFile(id, privateKeyBitString);

                // Log the generated public and private keys to verify uniqueness
                System.out.println("Generated Public Key for " + id + ": " + publicKey);
            } else {
                System.out.println("User ID already exists: " + id);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Save private key to a single file
    public void savePrivateKeyToFile(String userId, byte[] privateKey) throws IOException {
        String privateKeyEncoded = Base64.getEncoder().encodeToString(privateKey);
        String fileName = "private_keys.txt";
        File file = new File(fileName);

        if (!file.exists()) {
            file.createNewFile();
            System.out.println("Created file: " + fileName);
        }

        try (FileWriter fw = new FileWriter(file, true)) {
            fw.write(userId + ":" + privateKeyEncoded + System.lineSeparator());
            System.out.println("Private key saved successfully for user: " + userId);
        } catch (IOException e) {
            throw new IOException("Error saving private key for user: " + userId + " - " + e.getMessage(), e);
        }
    }

    // Additional method to find user by id if needed
    public User findUserById(String id) {
        for (User user : keys) {
            if (user.getId().equals(id)) {
                return user;
            }
        }
        return null;
    }
}
