package json;

import java.io.InvalidObjectException;
import merrimackutil.json.JSONSerializable;
import merrimackutil.json.types.JSONObject;
import merrimackutil.json.types.JSONType;

public class User implements JSONSerializable {
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

    @Override
    public JSONType toJSONType() {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("id", id);
        jsonObject.put("publicKey", publicKey);
        return jsonObject;
    }

    @Override
    public void deserialize(JSONType jsonType) throws InvalidObjectException {
        if (!(jsonType instanceof JSONObject)) {
            throw new InvalidObjectException("JSONObject expected.");
        }

        JSONObject jsonObject = (JSONObject) jsonType;

        if (jsonObject.containsKey("id")) {
            id = jsonObject.getString("id");
        } else {
            throw new InvalidObjectException("Missing id field -- invalid User object.");
        }

        if (jsonObject.containsKey("publicKey")) {
            publicKey = jsonObject.getString("publicKey");
        } else {
            throw new InvalidObjectException("Missing publicKey field -- invalid User object.");
        }
    }

    public JSONObject getPublicKeyJSON(User user) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("user", user.getId());
        jsonObject.put("keyData", user.getPublicKey());
        return jsonObject;
    }
}
