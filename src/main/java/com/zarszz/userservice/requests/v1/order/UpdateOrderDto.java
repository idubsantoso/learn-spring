package com.zarszz.userservice.requests.v1.order;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import java.util.List;

@Data
public class UpdateOrderDto {
    @NotEmpty(message = "Order Item should not empty")
    @JsonProperty(value = "products_items")
    List<OrderItemDto> productsItems;

    String comments;
}
