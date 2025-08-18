package api.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
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
}