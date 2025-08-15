package api.models;

import lombok.Data;

import java.util.List;

@Data
public class UserOrdersResponse {
    private boolean success;
    private List<OrderData> orders;
    private int total;
    private int totalToday;

    @Data
    public static class OrderData {
        private List<String> ingredients;
        private String _id;
        private String status;
        private String number;
        private String createdAt;
        private String updatedAt;
        private String name;
    }

    @Data
    public static class OrderIngredient {
        private String _id;
        private String name;
        private String type;
    }
}
