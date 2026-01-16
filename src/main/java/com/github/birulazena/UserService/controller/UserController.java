package com.github.birulazena.UserService.controller;

import com.github.birulazena.UserService.dto.filter.UserFilter;
import com.github.birulazena.UserService.dto.request.OnlyUserRequestDto;
import com.github.birulazena.UserService.dto.request.UserRequestDto;
import com.github.birulazena.UserService.dto.response.OnlyUserResponseDto;
import com.github.birulazena.UserService.dto.response.UserResponseDto;
import com.github.birulazena.UserService.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping()
    public ResponseEntity<UserResponseDto> createUser(@Valid @RequestBody UserRequestDto userRequestDto) {
        UserResponseDto userResponseDto = userService.createUser(userRequestDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(userResponseDto);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserResponseDto> getUserById(@PathVariable Long id) {
        UserResponseDto userResponseDto = userService.getUserById(id);
        return ResponseEntity.ok(userResponseDto);
    }

    @GetMapping
    public ResponseEntity<Page<OnlyUserResponseDto>> getAllUsers(UserFilter userFilter, Pageable pageable) {
        Page<OnlyUserResponseDto> onlyUserResponseDtos = userService.getAllUsers(userFilter, pageable);
        return ResponseEntity.ok(onlyUserResponseDtos);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<OnlyUserResponseDto> updateUserById(@PathVariable Long id,
                                                          @Valid @RequestBody OnlyUserRequestDto onlyUserRequestDto) {
        OnlyUserResponseDto onlyUserResponseDto = userService.updateUser(id, onlyUserRequestDto);
        return ResponseEntity.ok(onlyUserResponseDto);
    }

    @PatchMapping("/activate/{id}")
    public ResponseEntity activateUserById(@PathVariable Long id) {
        userService.activateUserById(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/deactivate/{id}")
    public ResponseEntity deactivateUserById(@PathVariable Long id) {
        userService.deactivateUserById(id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity deleteUserById(@PathVariable Long id) {
        userService.deleteUserById(id);
        return ResponseEntity.noContent().build();
    }
}
