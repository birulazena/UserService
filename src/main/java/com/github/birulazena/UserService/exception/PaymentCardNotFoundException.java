package com.github.birulazena.UserService.exception;

public class PaymentCardNotFoundException extends RuntimeException {
    public PaymentCardNotFoundException(String message) {
        super(message);
    }
}
