package com.github.birulazena.UserService.dto.response.error;

import java.util.Map;

public record ValidationErrorResponse(String message,
                                      Map<String, String> errors) {
}
