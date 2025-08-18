package api.models;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class AuthResponse {
    private boolean success;
    private String accessToken;
    private String refreshToken;
    private User user;
}