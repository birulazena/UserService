package com.github.birulazena.UserService.dto.response;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record OnlyUserResponseDto(Long id,
                                  String name,
                                  String surname,
                                  LocalDate birthDate,
                                  String email,
                                  Boolean active,
                                  LocalDateTime createdAt,
                                  LocalDateTime updatedAt) {
}
