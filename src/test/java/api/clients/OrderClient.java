package api.clients;

import api.models.Order;
import io.qameta.allure.Step;
import io.restassured.response.Response;

import static io.restassured.RestAssured.given;

public class OrderClient {
    private static final String BASE_URL = "https://stellarburgers.nomoreparties.site/api";

    @Step("Создание заказа")
    public Response createOrder(Order order, String accessToken) {
        return given()
                .header("Content-type", "application/json")
                .header("Authorization", accessToken)
                .body(order)
                .when()
                .post(BASE_URL + "/orders");
    }

    @Step("Создание заказа без авторизации")
    public Response createOrderWithoutAuth(Order order) {
        return given()
                .header("Content-type", "application/json")
                .body(order)
                .when()
                .post(BASE_URL + "/orders");
    }

    @Step("Получение заказов от пользователей")
    public Response getUserOrders(String accessToken) {
        return given()
                .header("Authorization", accessToken)
                .when()
                .get(BASE_URL + "/orders");
    }

    @Step("Получение всех ингредиентов")
    public Response getAllIngredients() {
        return given()
                .when()
                .get(BASE_URL + "/ingredients");
    }
}
