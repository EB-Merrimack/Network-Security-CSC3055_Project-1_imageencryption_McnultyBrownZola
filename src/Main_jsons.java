import com.fasterxml.jackson.databind.ObjectMapper;
import json.Photo;
import json.KeyEntry;
import json.User;
import json.Photos;
import json.Users;
import json.keyring;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.IvParameterSpec;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.*;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.List;
import java.util.ArrayList;

public class Main_jsons {
    static {
        Security.addProvider(new BouncyCastleProvider());
    }

    private static final String PHOTOS_FILE_PATH = "src/json/photos.json";
    private static final String USERS_FILE_PATH = "src/json/users.json";
    private static final String IMG_DIR = "src/encrypted/images";
    private static final ObjectMapper mapper = new ObjectMapper();

    public static void main(String[] args) {
        try {
            createImgDirIfNotExists();
            createJsonFileIfNotExists(PHOTOS_FILE_PATH, Photos.class);
            createJsonFileIfNotExists(USERS_FILE_PATH, Users.class);

            Photos photos = loadJsonFile(PHOTOS_FILE_PATH, Photos.class);
            Users users = loadJsonFile(USERS_FILE_PATH, Users.class);

            if (photos == null) {
                photos = new Photos();
                photos.setImgdir(IMG_DIR);
                photos.setPhotos(new ArrayList<>());
            }

            if (users == null) {
                users = new Users();
                users.setUsers(new ArrayList<>());
            }

            keyring keyRing = new keyring();
            keyRing.load(USERS_FILE_PATH);

            // Register a new test user with a secure public key
            String testUserId = "testUser" + System.currentTimeMillis();
            keyRing.addSecureUser(testUserId);

            // Save the updated keyring
            keyRing.save(USERS_FILE_PATH);

            // Generate ephemeral AES key
            KeyGenerator keyGen = KeyGenerator.getInstance("AES");
            keyGen.init(128); // AES-128
            SecretKey ephemeralKey = keyGen.generateKey();

            // Encrypt the image
            IvParameterSpec ivSpec = generateIv();
            String encryptedFilePath = encryptFile("test.jpg", ephemeralKey, ivSpec);

            // Encrypt the AES key using ElGamal for each user in the key block
            List<KeyEntry> keyBlock = new ArrayList<>();
            for (User user : keyRing.keys) {
                PublicKey publicKey = getPublicKeyFromUser(user);
                if (publicKey != null) {
                    byte[] encapsulatedKey = encryptEphemeralKey(ephemeralKey, publicKey);
                    keyBlock.add(new KeyEntry(user.getId(), Base64.getEncoder().encodeToString(encapsulatedKey)));
                }
            }

            // Create a new Photo object
            Photo newPhoto = new Photo();
            newPhoto.setOwner("Alice");
            newPhoto.setFileName("test.jpg");
            newPhoto.setIv(Base64.getEncoder().encodeToString(ivSpec.getIV()));
            newPhoto.setKeyBlock(keyBlock);
            newPhoto.setEncryptedFilePath(encryptedFilePath);

            photos.getPhotos().add(newPhoto);

            saveJsonFile(PHOTOS_FILE_PATH, photos);
            saveJsonFile(USERS_FILE_PATH, keyRing);

            System.out.println("JSON files updated successfully!");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void createImgDirIfNotExists() throws Exception {
        Path imgDirPath = Paths.get(IMG_DIR);
        if (!Files.exists(imgDirPath)) {
            Files.createDirectories(imgDirPath);
        }
    }

    private static void createJsonFileIfNotExists(String filePath, Class<?> clazz) throws Exception {
        File file = new File(filePath);
        if (!file.exists()) {
            Object instance = clazz.getDeclaredConstructor().newInstance();
            saveJsonFile(filePath, instance);
        }
    }

    private static IvParameterSpec generateIv() {
        byte[] iv = new byte[12]; // GCM standard IV length
        new SecureRandom().nextBytes(iv);
        return new IvParameterSpec(iv);
    }

    private static <T> T loadJsonFile(String filePath, Class<T> valueType) {
        try {
            File file = new File(filePath);
            if (file.exists()) {
                return mapper.readValue(file, valueType);
            } else {
                return valueType.getDeclaredConstructor().newInstance();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private static void saveJsonFile(String filePath, Object data) {
        try {
            File file = new File(filePath);
            mapper.writerWithDefaultPrettyPrinter().writeValue(file, data);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static String encryptFile(String fileName, SecretKey secretKey, IvParameterSpec ivSpec) throws Exception {
        Path inputFilePath = Paths.get(fileName).toAbsolutePath(); // Path to plain image file in the main directory
        File encryptedFile = new File(IMG_DIR + "/" + fileName + ".enc");

        Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
        cipher.init(Cipher.ENCRYPT_MODE, secretKey, new GCMParameterSpec(128, ivSpec.getIV()));

        try (FileInputStream fis = new FileInputStream(inputFilePath.toFile());
             FileOutputStream fos = new FileOutputStream(encryptedFile)) {

            byte[] buffer = new byte[1024];
            int bytesRead;

            while ((bytesRead = fis.read(buffer)) != -1) {
                byte[] output = cipher.update(buffer, 0, bytesRead);
                if (output != null) {
                    fos.write(output);
                }
            }

            byte[] outputBytes = cipher.doFinal();
            if (outputBytes != null) {
                fos.write(outputBytes);
            }
        }

        // Calculate relative path
        Path currentWorkingDir = Paths.get("").toAbsolutePath();
        Path encryptedFilePath = encryptedFile.toPath().toAbsolutePath();
        return currentWorkingDir.relativize(encryptedFilePath).toString();
    }

    private static PublicKey getPublicKeyFromUser(User user) {
        try {
            byte[] keyBytes = Base64.getDecoder().decode(user.getPublicKey());
            KeyFactory keyFactory = KeyFactory.getInstance("ElGamal", "BC");
            return keyFactory.generatePublic(new X509EncodedKeySpec(keyBytes));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private static byte[] encryptEphemeralKey(SecretKey ephemeralKey, PublicKey publicKey) throws Exception {
        Cipher cipher = Cipher.getInstance("ElGamal/None/NoPadding", "BC");
        cipher.init(Cipher.ENCRYPT_MODE, publicKey);
        return cipher.doFinal(ephemeralKey.getEncoded());
    }
}
