package api.models;

import lombok.Data;

@Data
public class User {
    private String email;
    private String password;
    private String name;

    @Override
    public String toString() {
        return String.format(
                "User[name='%s', email='%s', password='****']",
                name != null ? name : "null",
                email != null ? email : "null"
        );
    }
    public static User createTestUser(String email, String password, String name) {
        User user = new User();
        user.setEmail(email);
        user.setPassword(password);
        user.setName(name);
        return user;
    }
}