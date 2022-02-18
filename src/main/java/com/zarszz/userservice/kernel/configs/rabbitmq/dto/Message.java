package com.zarszz.userservice.kernel.configs.rabbitmq.dto;

import lombok.Data;

@Data
public class Message {
    private JobPurpose purpose;
    private String message;
}
