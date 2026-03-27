package com.github.birulazena.UserService.security.access;

import com.github.birulazena.UserService.repository.PaymentCardRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserSecurity {

    private final PaymentCardRepository paymentCardRepository;

    public boolean belongsToUser(Long userId, Long cardId) {

        return paymentCardRepository.existsByIdAndUserId(cardId, userId);
    }
}
