import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

public class Photo {
    private String id;
    private String url;

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}

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
}

public class Photos {
    @JsonProperty("photos")
    private List<Photo> photos;

    // Getter and Setter
    public List<Photo> getPhotos() {
        return photos;
    }

    public void setPhotos(List<Photo> photos) {
        this.photos = photos;
    }
}

public class Users {
    @JsonProperty("users")
    private List<User> users;

    // Getter and Setter
    public List<User> getUsers() {
        return users;
    }

    public void setUsers(List<User> users) {
        this.users = users;
    }
}
