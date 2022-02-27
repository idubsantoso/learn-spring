package com.zarszz.userservice.requests.v1.order;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.zarszz.userservice.domain.enumData.PaymentMethod;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreatePaymentDto {
    @JsonProperty(value = "payment_method")
    private PaymentMethod paymentMethod;
}
