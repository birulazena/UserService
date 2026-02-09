package com.github.birulazena.UserService.factory;

import com.github.birulazena.UserService.dto.request.OnlyUserRequestDto;
import com.github.birulazena.UserService.dto.request.PaymentCardRequestDto;
import com.github.birulazena.UserService.dto.request.UserRequestDto;
import com.github.birulazena.UserService.dto.response.OnlyUserResponseDto;
import com.github.birulazena.UserService.dto.response.PaymentCardResponseDto;
import com.github.birulazena.UserService.dto.response.UserResponseDto;
import com.github.birulazena.UserService.entity.PaymentCard;
import com.github.birulazena.UserService.entity.User;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class TestDataFactory {

    public static UserRequestDto userRequestDto() {
        return new UserRequestDto("Zenya",
                "Birulya",
                LocalDate.now(),
                "birulazena@gmail.com",
                new ArrayList<>());
    }

    public static User validNewUser() {
        return new User(null,
                "Zenya",
                "Birulya",
                LocalDate.now(),
                "birulazena@gmail.com",
                true,
                new ArrayList<>());
    }

    public static User validSavedUser() {
        return new User(1L,
                "Zenya",
                "Birulya",
                LocalDate.now(),
                "birulazena@gmail.com",
                true,
                new ArrayList<>());
    }

    public static PaymentCard validPaymentCard() {
        return new PaymentCard(1L,
                null,
                "1234567890",
                "Zenya Birulya",
                LocalDate.now(),
                true);
    }

    public static UserResponseDto userResponseDto() {
        return new UserResponseDto(1L,
                "Zenya",
                "Birulya",
                LocalDate.now(),
                "birulazena@gmail.com",
                true,
                new ArrayList<>(),
                LocalDateTime.now(),
                LocalDateTime.now());
    }

    public static OnlyUserResponseDto onlyUserResponseDto() {
        return new OnlyUserResponseDto(1L,
                "Zenya",
                "Birulya",
                LocalDate.now(),
                "birulazena@gmail.com",
                true,
                LocalDateTime.now(),
                LocalDateTime.now());
    }

    public static PaymentCardRequestDto paymentCardRequestDto() {
        return new PaymentCardRequestDto("1234567890",
                "Zenya",
                LocalDate.now());
    }

    public static PaymentCardResponseDto paymentCardResponseDto() {
        return new PaymentCardResponseDto(
                1L,
                1L,
                "1234567890",
                "Zenya Birulya",
                LocalDate.now(),
                true,
                LocalDateTime.now(),
                LocalDateTime.now());
    }

    public static User newUniqueUser() {
        return new User(null,
                "Zenya",
                "Birulya",
                LocalDate.now(),
                UUID.randomUUID() + "@gmail.com",
                true,
                new ArrayList<>());
    }

    public static UserRequestDto newUniqueUserRequestDto() {
        return new UserRequestDto("Zenya",
                "Birulya",
                LocalDate.now(),
                UUID.randomUUID() + "gmail.com",
                new ArrayList<>());
    }

    public static OnlyUserRequestDto newUniqueOnlyUserRequestDto() {
        return new OnlyUserRequestDto("Zenya",
                "Birulya",
                LocalDate.now(),
                UUID.randomUUID() + "gmail.com");
    }

    public static PaymentCard newPaymentCard() {
        return new PaymentCard(null,
                null,
                "1234567890",
                "Zenya Birulya",
                LocalDate.now(),
                true);
    }
}
