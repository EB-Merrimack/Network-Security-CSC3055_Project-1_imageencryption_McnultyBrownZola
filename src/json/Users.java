package json;

import java.util.ArrayList;
import java.util.List;

public class Users {
    private List<User> users;

    // Constructor to initialize the users list
    public Users() {
        this.users = new ArrayList<>();
    }

    // Getter and Setter
    public List<User> getUsers() {
        return users;
    }

    public void setUsers(List<User> users) {
        this.users = users;
    }
}
