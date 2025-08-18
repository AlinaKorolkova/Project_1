package api.utils;

import api.models.IngredientsResponse;
import api.models.Order;
import api.models.User;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

public class DataGenerator {
    public static User getRandomUser() {
        int randomNum = ThreadLocalRandom.current().nextInt(1, 10000);
        User user = new User();
        user.setEmail("testuser" + randomNum + "@example.com");
        user.setPassword("password" + randomNum);
        user.setName("User" + randomNum);
        return user;
    }

    public static List<String> extractIngredientIds(IngredientsResponse ingredientsResponse) {
        return ingredientsResponse.getData().stream()
                .map(api.models.Ingredient::getId)
                .collect(Collectors.toList());
    }

    public static Order createOrderWithMinimumIngredients(List<String> ingredientIds) {
        if (ingredientIds.size() < 2) {
            throw new IllegalArgumentException("Need at least 2 ingredients to create valid order");
        }
        return new Order(List.of(ingredientIds.get(0), ingredientIds.get(1)));
    }

    public static Order createOrderWithMultipleIngredients(List<String> ingredientIds) {
        if (ingredientIds.size() < 3) {
            throw new IllegalArgumentException("Need at least 3 ingredients to test multiple ingredients case");
        }
        return new Order(ingredientIds.subList(0, 3));
    }
}
