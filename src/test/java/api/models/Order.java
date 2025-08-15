package api.models;

import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.util.List;
import java.util.Collections;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Order {
    private List<String> ingredients;

    public List<String> getIngredients() {
        return ingredients != null ? ingredients : Collections.emptyList();
    }

    public boolean isValid() {
        return ingredients != null && ingredients.size() >= 1;
    }

    @Override
    public String toString() {
        int count = getIngredients().size();
        return String.format("Order[ingredients=%d: %s...]",
                count,
                count > 0 ? ingredients.subList(0, Math.min(count, 3)) : "empty");
    }

    public static Order createTestOrder(List<String> ingredients) {
        return Order.builder()
                .ingredients(ingredients)
                .build();
    }

    public boolean containsIngredient(String ingredientId) {
        return getIngredients().contains(ingredientId);
    }
}