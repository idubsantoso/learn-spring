package com.zarszz.userservice.requests.v1.order;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class OrderItemDto {
    @JsonProperty("product_id")
    private int productId;
    
    private int qty;
}
