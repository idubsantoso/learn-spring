package com.zarszz.userservice.requests.v1.email;

import lombok.Data;
import javax.validation.constraints.NotBlank;

@Data
public class ValidateSecretCodeDto {
    @NotBlank(message = "secretCode is required")
    private String secretCode;

    @NotBlank(message = "email is required")
    private String email;
}
