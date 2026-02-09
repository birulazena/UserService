package com.github.birulazena.UserService.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;

import java.time.LocalDate;
import java.util.List;

public record UserRequestDto(@NotBlank(message = "Name must not be blank")
                             String name,
                             @NotBlank(message = "Surname must not be blank")
                             String surname,
                             @Past(message = "Birth date must be in the past")
                             @NotNull(message = "Birth date is required")
                             LocalDate birthDate,
                             @Email(message = "Email must be valid")
                             @NotNull(message = "Email is required")
                             String email,
                             @Valid List<PaymentCardRequestDto> cards) {
}
