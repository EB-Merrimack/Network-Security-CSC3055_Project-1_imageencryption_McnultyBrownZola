package json;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.type.TypeReference;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.util.Base64;

public class keyring {
    public List<User> keys;

    public keyring() {
        this.keys = new ArrayList<>();
    }

    public void load(String filePath) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            this.keys = objectMapper.readValue(new File(filePath), new TypeReference<List<User>>(){});
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void save(String filePath) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            objectMapper.writeValue(new File(filePath), this.keys);
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

    // Generate and add a new user with a secure public key
    public void addSecureUser(String id) {
        try {
            KeyPairGenerator keyGen = KeyPairGenerator.getInstance("ElGamal");
            keyGen.initialize(2048);
            KeyPair pair = keyGen.generateKeyPair();
            String publicKey = Base64.getEncoder().encodeToString(pair.getPublic().getEncoded());
            addUser(id, publicKey);
        } catch (Exception e) {
            e.printStackTrace();
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
