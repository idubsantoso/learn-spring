package com.zarszz.userservice.kernel.exception;

public class PaymentErrorException extends RuntimeException{
    public PaymentErrorException(String message) {
        super(message);
    }
}
