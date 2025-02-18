package EncryptionAndDecryption.Encryption;

import java.security.*;
import java.util.Base64;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import json.Users;
import json.User;
import json.KeyRing;
import merrimackutil.json.JSONSerializable;
import merrimackutil.json.JsonIO;
import merrimackutil.json.types.JSONObject;

public class KeyManager {
    static {
        Security.addProvider(new BouncyCastleProvider());
    }

    public static void generateAndStoreKeys(String username, Users userManager) throws Exception {
        // Step 1: Generate ElGamal Key Pair
        KeyPairGenerator keyGen = KeyPairGenerator.getInstance("ElGamal", "BC");
        keyGen.initialize(512);
        KeyPair keyPair = keyGen.generateKeyPair();
        PublicKey publicKey = keyPair.getPublic();
        PrivateKey privateKey = keyPair.getPrivate();

        // Step 2: Encode Public and Private Keys in Base64
        String publicKeyBase64 = Base64.getEncoder().encodeToString(publicKey.getEncoded());
        String privateKeyBase64 = Base64.getEncoder().encodeToString(privateKey.getEncoded());

        // Step 3: Add the public key to the key ring (users.json)
        addPublicKeyToKeyRing(username, publicKeyBase64);

        // Step 4: Save the private key to a separate file (encoded in base64)
        savePrivateKey(username, privateKeyBase64);

        // Step 5: Add the public key to the user manager (users.json)
        User user = new User();
        user.setId(username);
        user.setPublicKey(publicKeyBase64);
        userManager.getKeys().add(user);
        
    }

    private static void addPublicKeyToKeyRing(String username, String publicKey) {
        try {
            // Load the existing keyring (users.json)
            File keyRingFile = new File("src/json/users.json");
            KeyRing keyRing = new KeyRing();
    
            // Check if the file exists and deserialize
            if (keyRingFile.exists()) {
                // Read the existing JSON object from the file
                JSONObject existingKeyRing = JsonIO.readObject(keyRingFile);
    
                // Deserialize the JSONObject into a KeyRing object
                keyRing.deserialize(existingKeyRing);
            }
    
            // Add the new public key to the key ring
            keyRing.addKey(username, publicKey);
    
            // Write the updated KeyRing object back to the file
            JsonIO.writeFormattedObject(keyRing, keyRingFile);
    
            System.out.println("Public key for " + username + " added to users.json");
    
        } catch (IOException e) {
            System.err.println("Error adding public key to key ring: " + e.getMessage());
        }
    }
    
    private static void savePrivateKey(String username, String privateKey) {
        try {
            // Specify the path to save the private key file under 'src/json'
            File directory = new File("./key_data");
            if (!directory.exists()) {
                directory.mkdirs(); // Create the 'json' directory if it doesn't exist
            }

            // Set the path for the private key file
            File file = new File(directory, username + "_private.key");
            FileWriter writer = new FileWriter(file);
            writer.write(privateKey);
            writer.close();
            System.out.println("Private key saved to: " + file.getAbsolutePath());
        } catch (IOException e) {
            System.err.println("Error saving private key: " + e.getMessage());
        }
    }
}
