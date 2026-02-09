package com.github.birulazena.UserService.exception;

public class PaymentCardLimitExceededException extends RuntimeException {
    public PaymentCardLimitExceededException(String message) {
        super(message);
    }
}
