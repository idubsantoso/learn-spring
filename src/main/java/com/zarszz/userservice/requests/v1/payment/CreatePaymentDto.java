package com.zarszz.userservice.requests.v1.payment;

import javax.validation.constraints.NotBlank;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.zarszz.userservice.domain.enumData.PaymentMethod;

import lombok.Data;

@Data
public class CreatePaymentDto {
    @NotBlank(message = "Payment method should not empty")
    @JsonProperty("payment_method")
    PaymentMethod paymentMethod;
}
