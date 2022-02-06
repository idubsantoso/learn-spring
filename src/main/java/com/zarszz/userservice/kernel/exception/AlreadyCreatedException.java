package com.zarszz.userservice.kernel.exception;

public class AlreadyCreatedException extends RuntimeException {
    public AlreadyCreatedException(String message) {
        super(message);
    }
}
