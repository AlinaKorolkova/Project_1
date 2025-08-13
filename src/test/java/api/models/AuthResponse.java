package api.models;

import lombok.Data;

@Data
public class AuthResponse {
    private boolean success;
    private String accessToken;
    private String refreshToken;
    private User user;
}