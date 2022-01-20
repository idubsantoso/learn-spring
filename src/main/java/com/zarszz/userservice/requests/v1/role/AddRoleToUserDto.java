package com.zarszz.userservice.requests.v1.role;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class AddRoleToUserDto {
    @NotBlank(message = "username required")
    private String username;

    @NotBlank(message = "role namerequired")
    private String roleName;
}
