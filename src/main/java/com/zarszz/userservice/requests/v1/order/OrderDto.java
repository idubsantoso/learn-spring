package com.zarszz.userservice.requests.v1.order;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import java.util.List;

@Data
public class OrderDto {
    @NotEmpty(message = "Order Item should not empty")
    @JsonProperty(value = "products_items")
    List<OrderItemDto> productsItems;

    @NotNull(message = "Address should not empty")
    @JsonProperty(value = "address_id")
    Long orderId;

    String comments;
}
