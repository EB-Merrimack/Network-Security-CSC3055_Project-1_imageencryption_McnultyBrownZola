import com.fasterxml.jackson.databind.ObjectMapper;

import json.Photo;
import json.KeyEntry;
import json.User;
import json.Photos;
import json.Users;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import java.io.File;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.List;
import java.util.ArrayList;

public class Main {
    private static final String PHOTOS_FILE_PATH = "src/json/photos.json";
    private static final String USERS_FILE_PATH = "src/json/users.json";
    private static final ObjectMapper mapper = new ObjectMapper();

    public static void main(String[] args) {
        try {
            Photos photos = loadJsonFile(PHOTOS_FILE_PATH, Photos.class);
            Users users = loadJsonFile(USERS_FILE_PATH, Users.class);

            if (photos == null) {
                photos = new Photos();
                photos.setPhotos(new ArrayList<>());
            }

            if (users == null) {
                users = new Users();
                users.setUsers(new ArrayList<>());
            }

            // Perform operations (example)
            KeyGenerator keyGen = KeyGenerator.getInstance("AES");
            keyGen.init(256); // AES-256
            SecretKey secretKey = keyGen.generateKey();

            IvParameterSpec ivSpec = generateIv();
            String iv = Base64.getEncoder().encodeToString(ivSpec.getIV());
            String keyData = Base64.getEncoder().encodeToString(secretKey.getEncoded());

            List<KeyEntry> keyBlock = new ArrayList<>();
            keyBlock.add(new KeyEntry("Bob", keyData));

            Photo newPhoto = new Photo();
            newPhoto.setOwner("Alice");
            newPhoto.setFileName("photo_123.jpg");
            newPhoto.setIv(iv);
            newPhoto.setKeyBlock(keyBlock);
            photos.getPhotos().add(newPhoto);

            User newUser = new User();
            newUser.setId("user_456");
            newUser.setPublicKey("public_key_example");
            users.getUsers().add(newUser);

            saveJsonFile(PHOTOS_FILE_PATH, photos);
            saveJsonFile(USERS_FILE_PATH, users);

            System.out.println("JSON files updated successfully!");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static IvParameterSpec generateIv() {
        byte[] iv = new byte[16];
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
}
