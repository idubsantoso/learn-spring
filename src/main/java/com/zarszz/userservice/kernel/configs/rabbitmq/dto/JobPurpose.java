package com.zarszz.userservice.kernel.configs.rabbitmq.dto;

public enum JobPurpose {
    PROCESS_PAYMENT,
    SEND_EMAIL,
    SEND_TRANSACTION_STATUS_EMAIL,
    SEND_NOTIFICATION,
    CREATE_MIDTRANS_PAYMENT
}
