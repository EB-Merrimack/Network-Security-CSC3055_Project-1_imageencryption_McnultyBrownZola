package json;

import java.util.ArrayList;
import java.util.List;

public class Photos {
    private String imgdir;
    private List<Photo> photos;

    // Default constructor
    public Photos() {
        this.photos = new ArrayList<>();
    }

    // Getters and Setters
    public String getImgdir() {
        return imgdir;
    }

    public void setImgdir(String imgdir) {
        this.imgdir = imgdir;
    }

    public List<Photo> getPhotos() {
        return photos;
    }

    public void setPhotos(List<Photo> photos) {
        this.photos = photos;
    }
}
