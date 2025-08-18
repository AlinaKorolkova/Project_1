package api.models;

import com.google.gson.annotations.SerializedName;
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
        @SerializedName("_id")
        private String id;
        private String status;
        private String number;
        private String createdAt;
        private String updatedAt;
        private String name;
    }

    @Data
    public static class OrderIngredient {
        @SerializedName("_id")
        private String id;
        private String name;
        private String type;
    }
}
