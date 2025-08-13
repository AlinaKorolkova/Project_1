package api.utils;

import api.models.User;

import java.util.concurrent.ThreadLocalRandom;

public class DataGenerator {
    public static User getRandomUser() {
        int randomNum = ThreadLocalRandom.current().nextInt(1, 10000);
        User user = new User();
        user.setEmail("testuser" + randomNum + "@example.com");
        user.setPassword("password" + randomNum);
        user.setName("User" + randomNum);
        return user;
    }

    public static User getExistingUser() {
        User user = new User();
        user.setEmail("test-data@yandex.ru");
        user.setPassword("password");
        user.setName("Username");
        return user;
    }
}
