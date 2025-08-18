package api.models;

import com.google.gson.annotations.SerializedName;
import lombok.Data;

import java.util.List;

@Data
public class OrderResponse {
    private boolean success;
    private String name;
    private OrderData order;

    @Data
    public static class OrderData {
        private List<OrderIngredient> ingredients; // Теперь это список объектов
        @SerializedName("_id")
        private String id;
        private String status;
        private String number;
        private String createdAt;
        private String updatedAt;
    }

    @Data
    public static class OrderIngredient {
        @SerializedName("_id")
        private String id;
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