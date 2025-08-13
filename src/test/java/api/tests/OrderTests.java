package api.tests;

import api.clients.OrderClient;
import api.clients.UserClient;
import api.models.AuthResponse;
import api.models.Order;
import api.models.OrderResponse;
import api.models.User;
import api.utils.DataGenerator;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.Response;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.List;
import static org.junit.Assert.*;

public class OrderTests {
    private OrderClient orderClient;
    private UserClient userClient;
    private User user;
    private String accessToken;
    private List<String> ingredients;

    @Before
    public void setUp() {
        orderClient = new OrderClient();
        userClient = new UserClient();
        user = DataGenerator.getRandomUser();

        // Регистрируем пользователя и получаем токен
        Response response = userClient.createUser(user);
        accessToken = response.as(AuthResponse.class).getAccessToken();

        // Получаем список ингредиентов
        ingredients = orderClient.getAllIngredients().jsonPath().getList("data._id");
    }

    @After
    public void tearDown() {
        if (accessToken != null) {
            userClient.deleteUser(accessToken);
        }
    }

    @Test
    @DisplayName("Создание заказа с авторизацией и валидными ингредиентами")
    public void testCreateOrderWithAuthAndValidIngredients() {
        Order order = new Order();
        order.setIngredients(ingredients.subList(0, 2)); // Берем первые два ингредиента

        Response response = orderClient.createOrder(order, accessToken);
        response.then().statusCode(200);

        OrderResponse orderResponse = response.as(OrderResponse.class);
        assertTrue(orderResponse.isSuccess());
        assertNotNull(orderResponse.getName());
        assertNotNull(orderResponse.getOrder().getNumber());
    }

    @Test
    @DisplayName("Создание заказа без авторизации")
    public void testCreateOrderWithoutAuth() {
        Order order = new Order();
        order.setIngredients(ingredients.subList(0, 2));

        Response response = orderClient.createOrderWithoutAuth(order);
        response.then().statusCode(200); // Документация говорит о редиректе, но API возвращает 200

        OrderResponse orderResponse = response.as(OrderResponse.class);
        assertTrue(orderResponse.isSuccess());
    }

    @Test
    @DisplayName("Создание заказа без ингредиентов")
    public void testCreateOrderWithoutIngredients() {
        Order order = new Order();

        Response response = orderClient.createOrder(order, accessToken);
        response.then().statusCode(400);

        assertEquals("Ingredient ids must be provided", response.path("message"));
        assertFalse(response.path("success"));
    }

    @Test
    @DisplayName("Создание заказа с невалидным хешем ингредиента")
    public void testCreateOrderWithInvalidIngredientHash() {
        Order order = new Order();
        order.setIngredients(List.of("invalid_hash"));

        Response response = orderClient.createOrder(order, accessToken);
        response.then().statusCode(500);
    }

    @Test
    @DisplayName("Получение заказов конкретного пользователя")
    public void testGetUserOrders() {
        // Сначала создаем заказ
        Order order = new Order();
        order.setIngredients(ingredients.subList(0, 2));
        orderClient.createOrder(order, accessToken);

        // Получаем заказы пользователя
        Response response = orderClient.getUserOrders(accessToken);
        response.then().statusCode(200);

        assertTrue(response.path("success"));
        assertNotNull(response.path("orders"));
        assertTrue(response.path("orders.size()") instanceof Integer);
    }

    @Test
    @DisplayName("Получение заказов без авторизации")
    public void testGetUserOrdersWithoutAuth() {
        Response response = orderClient.getUserOrders("");
        response.then().statusCode(401);

        assertEquals("You should be authorised", response.path("message"));
        assertFalse(response.path("success"));
    }
}
