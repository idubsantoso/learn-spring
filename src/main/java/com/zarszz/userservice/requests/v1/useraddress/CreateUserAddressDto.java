package com.zarszz.userservice.requests.v1.useraddress;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import javax.validation.constraints.NotBlank;

@Data
public class CreateUserAddressDto {
    @NotBlank(message = "name is required")
    private String name;

    @NotBlank(message = "address is required")
    private String address;

    @NotBlank(message = "city is required")
    private String city;

    @NotBlank(message = "district is required")
    private String district;

    @NotBlank(message = "state is required")
    private String state;

    @NotBlank(message = "zip code is required")
    @JsonProperty(value = "zip_code")
    private String zipCode;
}
