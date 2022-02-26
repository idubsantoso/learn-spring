package com.zarszz.userservice.utility.rabbitmq.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SendTransactionStatusEmail {
    private String recipientEmail;
    private Long paymentId;
    private String state;
}
