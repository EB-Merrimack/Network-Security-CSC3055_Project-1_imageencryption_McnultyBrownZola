package json;

import java.io.InvalidObjectException;
import java.util.ArrayList;
import java.util.List;
import merrimackutil.json.JSONSerializable;
import merrimackutil.json.types.JSONArray;
import merrimackutil.json.types.JSONObject;
import merrimackutil.json.types.JSONType;

public class Photos implements JSONSerializable {
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

    @Override
    public JSONType toJSONType() {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("imgdir", imgdir);
        JSONArray photosArray = new JSONArray();
        for (Photo photo : photos) {
            photosArray.add(photo.toJSONType());
        }
        jsonObject.put("photos", photosArray);
        return jsonObject;
    }

    @Override
    public void deserialize(JSONType jsonType) throws InvalidObjectException {
        if (!(jsonType instanceof JSONObject)) {
            throw new InvalidObjectException("JSONObject expected.");
        }

        JSONObject jsonObject = (JSONObject) jsonType;

        if (jsonObject.containsKey("imgdir")) {
            imgdir = jsonObject.getString("imgdir");
        } else {
            throw new InvalidObjectException("Missing imgdir field -- invalid photos object.");
        }

        if (jsonObject.containsKey("photos")) {
            JSONArray photosArray = jsonObject.getArray("photos");
            for (int i = 0; i < photosArray.size(); i++) {
                Photo photo = new Photo();
                photo.deserialize(photosArray.getObject(i));
                photos.add(photo);
            }
        } else {
            throw new InvalidObjectException("Missing photos field -- invalid photos object.");
        }
    }
}
