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
import org.springframework.security.access.prepost.PreAuthorize;
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

    @PreAuthorize("#id == authentication.details['userId'] or hasRole('ADMIN')")
    @GetMapping("/{id}")
    public ResponseEntity<UserResponseDto> getUserById(@PathVariable Long id) {
        UserResponseDto userResponseDto = userService.getUserById(id);
        return ResponseEntity.ok(userResponseDto);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public ResponseEntity<Page<OnlyUserResponseDto>> getAllUsers(UserFilter userFilter, Pageable pageable) {
        Page<OnlyUserResponseDto> onlyUserResponseDtos = userService.getAllUsers(userFilter, pageable);
        return ResponseEntity.ok(onlyUserResponseDtos);
    }

    @PatchMapping("/{id}")
    @PreAuthorize("#id == authentication.details['userId'] or hasRole('ADMIN')")
    public ResponseEntity<OnlyUserResponseDto> updateUserById(@PathVariable Long id,
                                                          @Valid @RequestBody OnlyUserRequestDto onlyUserRequestDto) {
        OnlyUserResponseDto onlyUserResponseDto = userService.updateUser(id, onlyUserRequestDto);
        return ResponseEntity.ok(onlyUserResponseDto);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/activate/{id}")
    public ResponseEntity activateUserById(@PathVariable Long id) {
        userService.activateUserById(id);
        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/deactivate/{id}")
    public ResponseEntity deactivateUserById(@PathVariable Long id) {
        userService.deactivateUserById(id);
        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("#id == authentication.details['userId'] or hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity deleteUserById(@PathVariable Long id) {
        userService.deleteUserById(id);
        return ResponseEntity.noContent().build();
    }
}
