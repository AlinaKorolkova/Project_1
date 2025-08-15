package api.models;

import lombok.Data;

import java.util.List;

// OrderResponse.java
@Data
public class OrderResponse {
    private boolean success;
    private String name;
    private OrderData order;

    @Data
    public static class OrderData {
        private List<OrderIngredient> ingredients; // Теперь это список объектов
        private String _id;
        private String status;
        private String number;
        private String createdAt;
        private String updatedAt;
    }

    @Data
    public static class OrderIngredient {
        private String _id;
        private String name;
        private String type;
        private int proteins;
        private int fat;
        private int carbohydrates;
        private int calories;
        private int price;
        private String image;
    }

    @Data
    public static class ErrorResponse {
        private boolean success;
        private String message;
    }
}