package com.github.birulazena.UserService.dto.response;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public record UserResponseDto(Long id,
                              String name,
                              String surname,
                              LocalDate birthDate,
                              Boolean active,
                              List<PaymentCardResponseDto> cards,
                              LocalDateTime createdAt,
                              LocalDateTime updatedAt) {
}
