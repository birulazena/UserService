package com.github.birulazena.UserService.dto.request;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record PaymentCardRequestDto(@NotBlank String number,
                                    @NotBlank String holder,
                                    @Future @NotNull LocalDate expirationDate) {
}
