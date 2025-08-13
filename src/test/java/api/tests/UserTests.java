package api.tests;

import api.clients.UserClient;
import api.models.AuthResponse;
import api.models.User;
import api.utils.DataGenerator;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.Response;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class UserTests {
    private UserClient userClient;
    private User user;
    private String accessToken;

    @Before
    public void setUp() {
        userClient = new UserClient();
        user = DataGenerator.getRandomUser();
    }

    @After
    public void tearDown() {
        if (accessToken != null) {
            userClient.deleteUser(accessToken);
        }
    }

    @Test
    @DisplayName("Создание пользователя с валидными данными")
    public void testCreateUserWithValidData() {
        Response response = userClient.createUser(user);
        response.then().statusCode(200);

        AuthResponse authResponse = response.as(AuthResponse.class);
        assertTrue(authResponse.isSuccess());
        assertNotNull(authResponse.getAccessToken());
        assertNotNull(authResponse.getRefreshToken());

        accessToken = authResponse.getAccessToken();
    }

    @Test
    @DisplayName("Создание пользователя с уже существующим email")
    public void testCreateUserWithExistingEmail() {
        User existingUser = DataGenerator.getExistingUser();
        Response response = userClient.createUser(existingUser);
        response.then().statusCode(403);

        assertEquals("User already exists", response.path("message"));
        assertFalse(response.path("success"));
    }

    @Test
    @DisplayName("Создание пользователя без email")
    public void testCreateUserWithoutEmail() {
        user.setEmail(null);
        Response response = userClient.createUser(user);
        response.then().statusCode(403);

        assertEquals("Email, password and name are required fields", response.path("message"));
        assertFalse(response.path("success"));
    }

    @Test
    @DisplayName("Авторизация пользователя с валидными данными")
    public void testLoginWithValidData() {
        userClient.createUser(user);
        Response response = userClient.login(user);
        response.then().statusCode(200);

        AuthResponse authResponse = response.as(AuthResponse.class);
        assertTrue(authResponse.isSuccess());
        assertNotNull(authResponse.getAccessToken());

        accessToken = authResponse.getAccessToken();
    }

    @Test
    @DisplayName("Авторизация с неверным паролем")
    public void testLoginWithInvalidPassword() {
        userClient.createUser(user);
        user.setPassword("wrongpassword");
        Response response = userClient.login(user);
        response.then().statusCode(401);

        assertEquals("email or password are incorrect", response.path("message"));
        assertFalse(response.path("success"));

        accessToken = null; // чтобы не удалять пользователя в tearDown
    }

    @Test
    @DisplayName("Обновление данных пользователя с авторизацией")
    public void testUpdateUserWithAuth() {
        AuthResponse authResponse = userClient.loginAndGetAuthResponse(user);
        accessToken = authResponse.getAccessToken();

        user.setName("NewName");
        user.setEmail("newemail@example.com");

        Response response = userClient.updateUser(user, accessToken);
        response.then().statusCode(200);

        assertEquals("NewName", response.path("user.name"));
        assertEquals("newemail@example.com", response.path("user.email"));
    }

    @Test
    @DisplayName("Обновление данных пользователя без авторизации")
    public void testUpdateUserWithoutAuth() {
        userClient.createUser(user);
        user.setName("NewName");

        Response response = userClient.updateUser(user, "");
        response.then().statusCode(401);

        assertEquals("You should be authorised", response.path("message"));
        assertFalse(response.path("success"));
    }
}