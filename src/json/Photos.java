package json;

import java.util.ArrayList;
import java.util.List;

public class Photos {
    private List<Photo> photos;

    // Constructor to initialize the photos list
    public Photos() {
        this.photos = new ArrayList<>();
    }

    // Getter and Setter
    public List<Photo> getPhotos() {
        return photos;
    }

    public void setPhotos(List<Photo> photos) {
        this.photos = photos;
    }
}
