package api.clients;

import api.config.AppConfig;
import api.models.AuthResponse;
import api.models.User;
import io.qameta.allure.Step;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.junit.After;

import static io.restassured.RestAssured.given;
import static org.junit.Assert.assertEquals;

public class UserClient {
    private UserClient userClient;
    private String accessToken;

    @Step("Создание пользователя: {0}")
    public Response createUser(User user) {
        return given()
                .header(AppConfig.CONTENT_TYPE_HEADER, AppConfig.APPLICATION_JSON)
                .body(user)
                .when()
                .post(AppConfig.BASE_URL + AppConfig.AUTH_REGISTER);
    }

    @Step("Авторизация пользователя: {0}")
    public Response login(User user) {
        return given()
                .header(AppConfig.CONTENT_TYPE_HEADER, AppConfig.APPLICATION_JSON)
                .body(user)
                .when()
                .post(AppConfig.BASE_URL + AppConfig.AUTH_LOGIN);
    }

    @Step("Успешная авторизация пользователя: {0}")
    public AuthResponse loginAndGetAuthResponse(User user) {
        Response response = login(user);
        AuthResponse authResponse = response.as(AuthResponse.class);

        return authResponse;
    }

    @Step("Обновление данных пользователя: {0}")
    public Response updateUser(User user, String accessToken) {
        RequestSpecification request = given()
                .header(AppConfig.CONTENT_TYPE_HEADER, AppConfig.APPLICATION_JSON)
                .body(user);

        request.header(AppConfig.AUTHORIZATION_HEADER, accessToken);

        return request.when().patch(AppConfig.BASE_URL + AppConfig.AUTH_USER);
    }

    @Step("Удаление пользователя (токен: {0})")
    public Response deleteUser(String accessToken) {
        RequestSpecification request = given()
                .header(AppConfig.AUTHORIZATION_HEADER, accessToken);

        Response response = request.when().delete(AppConfig.BASE_URL + AppConfig.AUTH_USER);

        if (response.statusCode() != 200) {
            System.err.println("Удаление пользователя не удалось. Код: " + response.statusCode() +
                    ", Тело: " + response.body().asString());
        } else {
            System.out.println("Пользователь успешно удалён.");
        }

        return response;
    }
        @After
        public void tearDown() {
            if (accessToken != null) {
                Response response = userClient.deleteUser(accessToken);
                assertEquals("Пользователь должен быть удалён", 200, response.statusCode());
            }
        }
}