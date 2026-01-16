package com.github.birulazena.UserService.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;

import java.time.LocalDate;
import java.util.List;

public record UserRequestDto(@NotBlank String name,
                             @NotBlank String surname,
                             @Past @NotNull LocalDate birthDate,
                             @Email @NotNull String email,
                             @Valid List<PaymentCardRequestDto> cards) {
}
