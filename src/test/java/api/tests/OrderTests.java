package api.tests;

import api.clients.OrderClient;
import api.clients.UserClient;
import api.models.*;
import api.utils.DataGenerator;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.Response;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;
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

        userClient.createUser(user);
        AuthResponse authResponse = userClient.login(user).as(AuthResponse.class);
        accessToken = authResponse.getAccessToken();

        IngredientsResponse ingredientsResponse = orderClient.getAllIngredients()
                .then()
                .extract()
                .as(IngredientsResponse.class);

        ingredients = DataGenerator.extractIngredientIds(ingredientsResponse);
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
        Order order = DataGenerator.createValidOrder(ingredients);
        Response response = orderClient.createOrder(order, accessToken);

        OrderResponse orderResponse = response.as(OrderResponse.class);

        response.then()
                .statusCode(200)
                .body("success", equalTo(true))
                .body("name", notNullValue())
                .body("order", notNullValue())
                .body("order.number", notNullValue())
                .body("order.ingredients", notNullValue());

        assertFalse("Список ингредиентов не должен быть пустым",
                orderResponse.getOrder().getIngredients().isEmpty());

        for (OrderResponse.OrderIngredient ingredient : orderResponse.getOrder().getIngredients()) {
            assertNotNull("ID ингредиента не должен быть null", ingredient.get_id());
            assertNotNull("Название ингредиента не должно быть null", ingredient.getName());
            assertNotNull("Тип ингредиента не должен быть null", ingredient.getType());
        }
    }

    @Test
    @DisplayName("Создание заказа без авторизации")
    public void testGetUserOrdersWithoutAuth() {
        OrderResponse.ErrorResponse errorResponse = orderClient.getUserOrders("")
                .then()
                .statusCode(401)
                .extract()
                .as(OrderResponse.ErrorResponse.class);

        assertEquals("Сообщение об ошибке должно соответствовать",
                "You should be authorised", errorResponse.getMessage());
        assertFalse("Флаг success должен быть false", errorResponse.isSuccess());
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
    @DisplayName("Создание заказа с авторизацией и минимальным набором ингредиентов")
    public void testCreateOrderWithAuthAndMinimumIngredients() {
        Order order = DataGenerator.createOrderWithMinimumIngredients(ingredients);

        Response response = orderClient.createOrder(order, accessToken);
        OrderResponse orderResponse = response.as(OrderResponse.class);

        assertEquals(200, response.getStatusCode());
        assertTrue("Order should be created successfully", orderResponse.isSuccess());
        assertNotNull("Order name should not be null", orderResponse.getName());
        assertNotNull("Order number should not be null", orderResponse.getOrder().getNumber());
    }

    @Test
    @DisplayName("Создание заказа с авторизацией и несколькими ингредиентами")
    public void testCreateOrderWithAuthAndMultipleIngredients() {
        Order order = DataGenerator.createOrderWithMultipleIngredients(ingredients);

        Response response = orderClient.createOrder(order, accessToken);
        response.then().statusCode(200);

        OrderResponse orderResponse = response.as(OrderResponse.class);
        assertTrue(orderResponse.isSuccess());
    }

    @Test
    @DisplayName("Получение заказов с авторизацией")
    public void testGetUserOrdersWithAuth() {
        Order testOrder = DataGenerator.createOrderWithMinimumIngredients(ingredients);
        orderClient.createOrder(testOrder, accessToken);

        UserOrdersResponse userOrders = orderClient.getUserOrders(accessToken)
                .then()
                .statusCode(200)
                .extract()
                .as(UserOrdersResponse.class);

        assertTrue("Флаг success должен быть true", userOrders.isSuccess());
        assertNotNull("Список заказов не должен быть null", userOrders.getOrders());
        assertFalse("Список заказов не должен быть пустым", userOrders.getOrders().isEmpty());

        UserOrdersResponse.OrderData lastOrder = userOrders.getOrders().get(0);
        assertEquals("Статус заказа должен быть 'done'", "done", lastOrder.getStatus());
        assertNotNull("Номер заказа не должен быть null", lastOrder.getNumber());
        assertNotNull("Дата создания не должна быть null", lastOrder.getCreatedAt());

        assertNotNull("Список ингредиентов не должен быть null", lastOrder.getIngredients());
        assertFalse("Список ингредиентов не должен быть пустым", lastOrder.getIngredients().isEmpty());

        for (String ingredientId : lastOrder.getIngredients()) {
            assertNotNull("ID ингредиента не должен быть null", ingredientId);
            assertFalse("ID ингредиента не должен быть пустым", ingredientId.trim().isEmpty());
        }

        assertTrue("total должно быть положительным числом", userOrders.getTotal() >= 0);
        assertTrue("totalToday должно быть положительным числом", userOrders.getTotalToday() >= 0);
    }
}