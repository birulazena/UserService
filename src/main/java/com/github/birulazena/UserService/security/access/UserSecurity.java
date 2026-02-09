package com.github.birulazena.UserService.security.access;

import com.github.birulazena.UserService.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserSecurity {

    private final UserRepository userRepository;

    public boolean belongsToUser(Long userId, Long cardId) {
        return userRepository.findById(userId)
                .map(user -> user.getCards().stream()
                        .anyMatch(card -> card.getId().equals(cardId)))
                .orElse(false);
    }
}
