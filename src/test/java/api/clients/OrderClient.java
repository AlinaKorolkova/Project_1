package api.clients;

import api.config.AppConfig;
import api.models.Order;
import io.qameta.allure.Step;
import io.restassured.response.Response;

import static io.restassured.RestAssured.given;

public class OrderClient {

    @Step("Создание заказа: Ингредиенты: {0} (с авторизацией)")
    public Response createOrder(Order order, String accessToken) {
        return given()
                .header(AppConfig.CONTENT_TYPE_HEADER, AppConfig.APPLICATION_JSON)
                .header(AppConfig.AUTHORIZATION_HEADER, accessToken)
                .body(order)
                .when()
                .post(AppConfig.ORDERS);
    }

    @Step("Получение заказов пользователя (токен: {0})")
    public Response getUserOrders(String accessToken) {
        return given()
                .header(AppConfig.AUTHORIZATION_HEADER, accessToken)
                .when()
                .get(AppConfig.ORDERS);
    }

    @Step("Получение списка всех ингредиентов")
    public Response getAllIngredients() {
        return given()
                .when()
                .get(AppConfig.INGREDIENTS);
    }
}
