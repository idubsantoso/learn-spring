package com.zarszz.userservice.requests.v1.product;

import javax.validation.constraints.NotBlank;

import lombok.Data;

@Data
public class CreateProductDto {
    @NotBlank(message = "Product name is required")
    private String name;

    @NotBlank(message = "Product description is required")
    private String description;

    @NotBlank(message = "Product price is required")
    private String price;
}
