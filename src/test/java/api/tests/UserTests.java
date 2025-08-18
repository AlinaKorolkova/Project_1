package api.tests;

import api.clients.UserClient;
import api.models.AuthResponse;
import api.models.ErrorResponse;
import api.models.User;
import com.github.javafaker.Faker;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static api.config.AppConfig.WRONG_PASSWORD;
import static api.utils.DataGenerator.getRandomUser;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.*;

public class UserTests {
    private final Faker faker = new Faker();
    private UserClient userClient;
    private User user;
    private String accessToken;

    @Before
    public void prepare() {
        RestAssured.baseURI = "https://stellarburgers.nomoreparties.site";
        userClient = new UserClient();
        user = getRandomUser();
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
        User testUser = getRandomUser();
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

    @Test
    @DisplayName("Авторизация пользователя с валидными данными")
    public void testLoginWithValidData() {
        Response response = userClient.login(user);
        response.then().statusCode(200);

        AuthResponse authResponse = response.as(AuthResponse.class);
        assertTrue(authResponse.isSuccess());
        assertNotNull(authResponse.getAccessToken());

        accessToken = authResponse.getAccessToken();
    }

    @Test
    @DisplayName("Авторизация пользователя с неверным паролем")
    public void testLoginWithInvalidPassword() {
        userClient.createUser(user);
        user.setPassword(WRONG_PASSWORD);
        Response response = userClient.login(user);
        response.then().statusCode(401);

        assertEquals("email or password are incorrect", response.path("message"));
        assertFalse(response.path("success"));
    }

    @Test
    @DisplayName("Обновление данных пользователя без авторизации")
    public void testUpdateUserWithoutAuth() {
        userClient.createUser(user);
        user.setName(faker.name().firstName());

        Response response = userClient.updateUser(user, "");
        response.then().statusCode(401);

        assertEquals("You should be authorised", response.path("message"));
        assertFalse(response.path("success"));
    }

    @Test
    @DisplayName("Создание пользователя без email")
    public void testCreateUserWithoutEmail() {
        User userWithoutEmail = getRandomUser();
        userWithoutEmail.setEmail(null);

        Response response = userClient.createUser(userWithoutEmail);

        response.then().statusCode(403);

        ErrorResponse errorResponse = response.as(ErrorResponse.class);
        assertEquals("Email, password and name are required fields", errorResponse.getMessage());
        assertFalse(errorResponse.isSuccess());
    }
}