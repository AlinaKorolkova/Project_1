package api.models;

import lombok.Data;

@Data
public class OrderResponse {
    private boolean success;
    private String name;
    private OrderData order;

    @Data
    public static class OrderData {
        private int number;
    }
}