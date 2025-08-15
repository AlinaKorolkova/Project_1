package api.config;

public class AppConfig {

    public static final String BASE_URL = "https://stellarburgers.nomoreparties.site/api";

    public static final String AUTH_REGISTER = "/auth/register";
    public static final String AUTH_LOGIN = "/auth/login";
    public static final String AUTH_USER = "/auth/user";

    public static final String ORDERS = "/orders";
    public static final String INGREDIENTS = "/ingredients";

    public static final String CONTENT_TYPE_HEADER = "Content-Type"; // Исправлено: дефис, а не подчёркивание
    public static final String APPLICATION_JSON = "application/json";
    public static final String AUTHORIZATION_HEADER = "Authorization";
}