package json;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.ArrayList;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Users {
    private List<User> users;

    // Constructor to initialize the list
    public Users() {
        this.users = new ArrayList<>();
    }

    // Getter
    public List<User> getUsers() {
        return users;
    }

    // Setter
    public void setUsers(List<User> users) {
        this.users = users;
    }

    // add a user
    public void addUser(User user) {
        if (user != null) {
            this.users.add(user);
        }
    }

    // remove a user
    public void removeUser(User user) {
        this.users.remove(user);
    }
}
