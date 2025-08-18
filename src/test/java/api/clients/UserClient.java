package api.clients;

import api.config.AppConfig;
import api.models.User;
import io.qameta.allure.Step;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

import static io.restassured.RestAssured.given;

public class UserClient {

    @Step("Создание пользователя: {0}")
    public Response createUser(User user) {
        return given()
                .header(AppConfig.CONTENT_TYPE_HEADER, AppConfig.APPLICATION_JSON)
                .body(user)
                .when()
                .post(AppConfig.AUTH_REGISTER);
    }

    @Step("Авторизация пользователя: {0}")
    public Response login(User user) {
        return given()
                .header(AppConfig.CONTENT_TYPE_HEADER, AppConfig.APPLICATION_JSON)
                .body(user)
                .when()
                .post(AppConfig.AUTH_LOGIN);
    }

    @Step("Обновление данных пользователя: {0}")
    public Response updateUser(User user, String accessToken) {
        RequestSpecification request = given()
                .header(AppConfig.CONTENT_TYPE_HEADER, AppConfig.APPLICATION_JSON)
                .body(user);

        request.header(AppConfig.AUTHORIZATION_HEADER, accessToken);

        return request.when().patch(AppConfig.AUTH_USER);
    }

    @Step("Удаление пользователя (токен: {0})")
    public void deleteUser(String accessToken) {
        RequestSpecification request = given()
                .header(AppConfig.AUTHORIZATION_HEADER, accessToken);

        Response response = request.when().delete(AppConfig.AUTH_USER);

        if (response.statusCode() != 200) {
            System.err.println("Удаление пользователя не удалось. Код: " + response.statusCode() +
                    ", Тело: " + response.body().asString());
        }
    }
}