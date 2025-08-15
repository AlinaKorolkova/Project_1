package api.tests;

import api.clients.UserClient;
import api.models.AuthResponse;
import api.models.ErrorResponse;
import api.models.User;
import api.utils.DataGenerator;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.Response;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.*;

public class UserTests {
    private UserClient userClient;
    private User user;
    private String accessToken;

    @Before
    public void prepare() {
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
    public void shouldReturnErrorWhenUserWithSameEmailAlreadyExists() {
        // Arrange
        User testUser = DataGenerator.getRandomUser();
        userClient.createUser(testUser)
                .then()
                .statusCode(200)
                .body("success", equalTo(true));

        ErrorResponse expectedError = ErrorResponse.builder()
                .success(false)
                .message("User already exists")
                .build();
        Response response = userClient.createUser(testUser);

        ErrorResponse actualError = response.as(ErrorResponse.class);

        assertThat(actualError)
                .usingRecursiveComparison()
                .ignoringFields("timestamp")
                .isEqualTo(expectedError);

        response.then().statusCode(403);
    }

    @Before
    public void sprepare() {
        userClient = new UserClient();
        user = DataGenerator.getRandomUser();
        userClient.createUser(user).then().statusCode(200);
    }

    @Test
    @DisplayName("Авторизация пользователя с валидными данными")
    public void testLoginWithValidData() {
        Response response = userClient.login(user);
        response.then().statusCode(200);

        AuthResponse authResponse = response.as(AuthResponse.class);
        assertTrue(authResponse.isSuccess());
        assertNotNull(authResponse.getAccessToken());

        accessToken = authResponse.getAccessToken(); // для удаления в @After
    }

    @Test
    @DisplayName("Авторизация пользователя с неверным паролем")
    public void testLoginWithInvalidPassword() {
        userClient.createUser(user);
        user.setPassword("wrongpassword");
        Response response = userClient.login(user);
        response.then().statusCode(401);

        assertEquals("email or password are incorrect", response.path("message"));
        assertFalse(response.path("success"));
    }

    @DisplayName("Обновление данных пользователя с авторизацией")
    public void testUpdateUserWithAuth() {
        AuthResponse authResponse = userClient.loginAndGetAuthResponse(user);
        accessToken = authResponse.getAccessToken();

        String expectedName = "UpdatedUserName";
        String expectedEmail = "updated.user@example.com";

        user.setName(expectedName);
        user.setEmail(expectedEmail);

        Response response = userClient.updateUser(user, accessToken);
        response.then().statusCode(200);

        assertEquals(expectedName, response.path("user.name"));
        assertEquals(expectedEmail, response.path("user.email"));
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

    @Test
    @DisplayName("Создание пользователя без email")
    public void testCreateUserWithoutEmail() {
        User userWithoutEmail = DataGenerator.getRandomUser();
        userWithoutEmail.setEmail(null);

        Response response = userClient.createUser(userWithoutEmail);

        response.then().statusCode(403);

        ErrorResponse errorResponse = response.as(ErrorResponse.class);
        assertEquals("Email, password and name are required fields", errorResponse.getMessage());
        assertFalse(errorResponse.isSuccess());
    }
}