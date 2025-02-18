package json;

import merrimackutil.json.JSONSerializable;
import merrimackutil.json.types.JSONObject;
import merrimackutil.json.types.JSONType;

public class User implements JSONSerializable {
    private String id;
    private String publicKey;

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
    public void deserialize(JSONType jsonType) {
        if (!(jsonType instanceof JSONObject)) {
            throw new IllegalArgumentException("Expected JSONObject");
        }

        JSONObject jsonObject = (JSONObject) jsonType;
        this.id = jsonObject.getString("user");
        this.publicKey = jsonObject.getString("keyData");
    }

    @Override
    public JSONType toJSONType() {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("user", this.id);
        jsonObject.put("keyData", this.publicKey);
        return jsonObject;
    }
}
