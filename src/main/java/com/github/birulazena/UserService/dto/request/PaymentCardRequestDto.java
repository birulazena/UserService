package com.github.birulazena.UserService.dto.request;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record PaymentCardRequestDto(@NotBlank(message = "Card number must not be blank")
                                    String number,
                                    @NotBlank(message = "Card holder name must not be blank")
                                    String holder,
                                    @Future(message = "Expiration date must be in the future")
                                    @NotNull(message = "Expiration date is required")
                                    LocalDate expirationDate) {
}
