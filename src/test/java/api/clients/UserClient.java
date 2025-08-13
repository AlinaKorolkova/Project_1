package api.clients;

import api.models.AuthResponse;
import api.models.User;
import io.qameta.allure.Step;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

import static io.restassured.RestAssured.given;

public class UserClient {
    private static final String BASE_URL = "https://stellarburgers.nomoreparties.site/api";


    @Step("Создание пользователя")
    public Response createUser(User user) {
        return given()
                .header("Content-type", "application/json")
                .body(user)
                .when()
                .post(BASE_URL + "/auth/register");
    }

    @Step("Авторизация")
    public Response login(User user) {
        return given()
                .header("Content-type", "application/json")
                .body(user)
                .when()
                .post(BASE_URL + "/auth/login");
    }

    @Step("Успешная авторизация пользователя")
    public AuthResponse loginAndGetAuthResponse(User user) {
        Response response = login(user);
        AuthResponse authResponse = response.as(AuthResponse.class);

        // Проверяем, что токен получен
        if (authResponse.getAccessToken() == null || authResponse.getAccessToken().isEmpty()) {
            throw new IllegalStateException("Access token not received in login response");
        }

        return authResponse;
    }

    @Step("Обновление данных пользователя")
    public Response updateUser(User user, String accessToken) {
        // Создаем базовый запрос
        RequestSpecification request = given()
                .header("Content-type", "application/json")
                .body(user);

        // Добавляем заголовок Authorization только если токен не null и не пустой
        if (accessToken != null && !accessToken.trim().isEmpty()) {
            request.header("Authorization", accessToken);
        }

        return request.when().patch(BASE_URL + "/auth/user");
    }

    @Step("Удаление пользователя")
    public Response deleteUser(String accessToken) {
        // Создаем базовый запрос
        RequestSpecification request = given();

        // Добавляем заголовок Authorization только если токен не null и не пустой
        if (accessToken != null && !accessToken.trim().isEmpty()) {
            request.header("Authorization", accessToken);
        }

        return request.when().delete(BASE_URL + "/auth/user");
    }
}