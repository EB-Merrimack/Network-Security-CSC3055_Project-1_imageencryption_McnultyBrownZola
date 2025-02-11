package json;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.ArrayList;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Users {
    private List<User> keys;

    // Constructor to initialize the keys list
    public Users() {
        this.keys = new ArrayList<>();
    }

    // Getter
    public List<User> getKeys() {
        return keys;
    }

    // Setter
    public void setKeys(List<User> keys) {
        this.keys = keys;
    }
}
