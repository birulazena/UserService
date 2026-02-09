package com.github.birulazena.UserService.controller;

import com.github.birulazena.UserService.dto.filter.UserFilter;
import com.github.birulazena.UserService.dto.request.PaymentCardRequestDto;
import com.github.birulazena.UserService.dto.response.PaymentCardResponseDto;
import com.github.birulazena.UserService.service.PaymentCardService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/v1/payment-cards")
@RequiredArgsConstructor
public class PaymentCardController {

    private final PaymentCardService paymentCardService;

    @PreAuthorize("#id == authentication.details['userId'] or hasRole('ADMIN')")
    @PostMapping("/{userId}")
    public ResponseEntity<PaymentCardResponseDto> createCard(@PathVariable Long userId,
                                                             @Valid @RequestBody PaymentCardRequestDto paymentCardRequestDto){
        PaymentCardResponseDto paymentCardResponseDto = paymentCardService
                .createPaymentCardForUser(userId, paymentCardRequestDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(paymentCardResponseDto);
    }

    @PreAuthorize("#id == authentication.details['userId'] or hasRole('ADMIN')")
    @GetMapping("/{id}")
    public ResponseEntity<PaymentCardResponseDto> getCardById(@PathVariable Long id) {
        PaymentCardResponseDto paymentCardResponseDto = paymentCardService.getCardById(id);
        return ResponseEntity.ok(paymentCardResponseDto);
    }

    @GetMapping
    public ResponseEntity<Page<PaymentCardResponseDto>> getAllCards(UserFilter userFilter, Pageable pageable) {
        Page<PaymentCardResponseDto> paymentCardResponseDtos = paymentCardService
                .getAllCards(userFilter, pageable);
        return ResponseEntity.ok(paymentCardResponseDtos);
    }

    @PreAuthorize("#id == authentication.details['userId'] or hasRole('ADMIN')")
    @GetMapping("/user/{id}")
    public ResponseEntity<List<PaymentCardResponseDto>> getAllCardsByUserId(@PathVariable Long id) {
        List<PaymentCardResponseDto> paymentCardResponseDtos = paymentCardService
                .getAllCardByUserId(id);
        return ResponseEntity.ok(paymentCardResponseDtos);
    }

    @PreAuthorize("#id == authentication.details['userId'] or hasRole('ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<PaymentCardResponseDto> updateCardById(@PathVariable Long id,
                                                                 @Valid @RequestBody PaymentCardRequestDto paymentCardRequestDto) {
        PaymentCardResponseDto paymentCardResponseDto = paymentCardService
                .updateCardById(id, paymentCardRequestDto);
        return ResponseEntity.ok(paymentCardResponseDto);
    }

    @PreAuthorize("#id == authentication.details['userId'] or hasRole('ADMIN')")
    @PatchMapping("/activate/{id}")
    public ResponseEntity activateCardById(@PathVariable Long id) {
        paymentCardService.activateCardById(id);
        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("#id == authentication.details['userId'] or hasRole('ADMIN')")
    @PatchMapping("/deactivate/{id}")
    public ResponseEntity deactivateCardById(@PathVariable Long id) {
        paymentCardService.deactivateCardById(id);
        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("#id == authentication.details['userId'] or hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity deleteCardById(@PathVariable Long id) {
        paymentCardService.deleteCardById(id);
        return ResponseEntity.noContent().build();
    }
}
