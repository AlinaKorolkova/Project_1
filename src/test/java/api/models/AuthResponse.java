package api.models;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AuthResponse {
    private boolean success;
    private String accessToken;
    private String refreshToken;
    private User user;
}