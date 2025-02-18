package json;

import merrimackutil.json.JSONSerializable;
import merrimackutil.json.types.JSONArray;
import merrimackutil.json.types.JSONObject;
import merrimackutil.json.types.JSONType;

import java.io.InvalidObjectException;
import java.util.ArrayList;
import java.util.List;

public class KeyRing implements JSONSerializable {
    private List<User> keys;

    public KeyRing() {
        this.keys = new ArrayList<>();
    }

    public List<User> getKeys() {
        return keys;
    }

    public void setKeys(List<User> keys) {
        this.keys = keys;
    }

    @Override
    public void deserialize(JSONType jsonType) throws InvalidObjectException {
        if (!(jsonType instanceof JSONObject)) {
            throw new IllegalArgumentException("Expected JSONObject");
        }

        JSONObject jsonObject = (JSONObject) jsonType;
        if (jsonObject.containsKey("keys")) {
            JSONArray keysArray = jsonObject.getArray("keys");
            for (int i = 0; i < keysArray.size(); i++) {
                User user = new User();
                user.deserialize(keysArray.getObject(i));
                keys.add(user);
            }
        }
    }

    @Override
    public JSONType toJSONType() {
        JSONObject jsonObject = new JSONObject();
        JSONArray keysArray = new JSONArray();
        for (User user : keys) {
            keysArray.add(user.toJSONType());
        }
        jsonObject.put("keys", keysArray);
        return jsonObject;
    }

    // Method to add a user to the key ring
    public void addKey(String username, String publicKey) {
        User user = new User();
        user.setId(username);
        user.setPublicKey(publicKey);
        keys.add(user);
    }
}
