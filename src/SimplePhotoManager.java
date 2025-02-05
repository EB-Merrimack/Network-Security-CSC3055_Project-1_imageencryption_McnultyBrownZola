import javax.json.*;
import java.io.*;
import java.util.Base64;

public class SimplePhotoManager {
    private static final String FILE_NAME = "photos.json";
    private JsonArray photosArray;

    public SimplePhotoManager() {
        loadPhotos();
    }

    private void loadPhotos() {
        File file = new File(FILE_NAME);
        if (file.exists()) {
            try (InputStream is = new FileInputStream(file);
                 JsonReader reader = Json.createReader(is)) {
                photosArray = reader.readArray();
            } catch (Exception e) {
                e.printStackTrace();
                photosArray = Json.createArrayBuilder().build();
            }
        } else {
            photosArray = Json.createArrayBuilder().build();
            savePhotos();
        }
    }

    private void savePhotos() {
        try (FileWriter file = new FileWriter(FILE_NAME);
             JsonWriter writer = Json.createWriter(file)) {
            writer.writeArray(photosArray);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void addPhoto(String owner, String fileName, String iv, String user, String keyData) {
        JsonObject photoObject = Json.createObjectBuilder()
                .add("owner", owner)
                .add("fileName", fileName)
                .add("iv", iv)
                .add("keyBlock", Json.createArrayBuilder()
                        .add(Json.createObjectBuilder()
                                .add("user", user)
                                .add("keyData", keyData)
                        )
                )
                .build();

        JsonArrayBuilder arrayBuilder = Json.createArrayBuilder(photosArray);
        arrayBuilder.add(photoObject);
        photosArray = arrayBuilder.build();

        savePhotos();
    }

    public String getPhoto(String fileName) {
        for (JsonValue photo : photosArray) {
            JsonObject obj = (JsonObject) photo;
            if (obj.getString("fileName").equals(fileName)) {
                return obj.toString();
            }
        }
        return "Photo not found";
    }

    public static void main(String[] args) {
        SimplePhotoManager manager = new SimplePhotoManager();

        // Example usage
        manager.addPhoto("Alice", "photo1.jpg", Base64.getEncoder().encodeToString("RandomIV".getBytes()),
                "Bob", Base64.getEncoder().encodeToString("SecretKey".getBytes()));

        System.out.println(manager.getPhoto("photo1.jpg"));
    }
}
