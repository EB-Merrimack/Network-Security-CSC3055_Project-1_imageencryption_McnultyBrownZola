package EncryptionAndDecryption.Encryption;

import java.security.*;
import java.util.Base64;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import json.Users;
import json.User;

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

        // Step 3: Store Public Key in users.json (Key Ring)
        User user = new User();
        user.setId(username);
        user.setPublicKey(publicKeyBase64);
        userManager.getKeys().add(user);
        userManager.saveToFile("users.json");  // Make sure you have this method in Users.java

        // Step 4: Store Private Key in a Secure File
        savePrivateKey(username, privateKeyBase64);
    }

    private static void savePrivateKey(String username, String privateKey) {
        try {
            File file = new File(username + "_private.key");
            FileWriter writer = new FileWriter(file);
            writer.write(privateKey);
            writer.close();
            System.out.println("Private key saved to: " + file.getAbsolutePath());
        } catch (IOException e) {
            System.err.println("Error saving private key: " + e.getMessage());
        }
    }
}
