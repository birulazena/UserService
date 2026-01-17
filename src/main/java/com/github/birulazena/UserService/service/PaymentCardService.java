package com.github.birulazena.UserService.service;

import com.github.birulazena.UserService.dto.filter.UserFilter;
import com.github.birulazena.UserService.dto.request.PaymentCardRequestDto;
import com.github.birulazena.UserService.dto.response.PaymentCardResponseDto;
import com.github.birulazena.UserService.entity.PaymentCard;
import com.github.birulazena.UserService.entity.User;
import com.github.birulazena.UserService.exception.PaymentCardLimitExceededException;
import com.github.birulazena.UserService.exception.PaymentCardNotFoundException;
import com.github.birulazena.UserService.exception.UserNotFoundException;
import com.github.birulazena.UserService.mapper.PaymentCardMapper;
import com.github.birulazena.UserService.repository.PaymentCardRepository;
import com.github.birulazena.UserService.repository.UserRepository;
import com.github.birulazena.UserService.specification.PaymentCardSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@RequiredArgsConstructor
@Service
public class PaymentCardService {

    private final PaymentCardRepository paymentCardRepository;

    private final PaymentCardMapper paymentCardMapper;

    private final UserRepository userRepository;

    private final CacheManager cacheManager;

    @Value("${user.cards.limit}")
    private int cardLimit;

    @CacheEvict(value = "user_cache", key = "#userId")
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public PaymentCardResponseDto createPaymentCardForUser(Long userId, PaymentCardRequestDto paymentCardRequestDto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User with id " + userId + " not found"));
        if(user.getCards().size() >= cardLimit)
            throw new PaymentCardLimitExceededException("The " + cardLimit + " card limit has been exceeded");

        PaymentCard paymentCard = paymentCardMapper.toEntity(paymentCardRequestDto);
        paymentCard.setUser(user);
        PaymentCard savePaymentCard = paymentCardRepository.save(paymentCard);
        return paymentCardMapper.toDto(savePaymentCard);
    }

    public PaymentCardResponseDto getCardById(Long id) {
        PaymentCard paymentCard = paymentCardRepository.findById(id)
                .orElseThrow(() -> new PaymentCardNotFoundException("Payment card with id " + id + " not found"));
        return paymentCardMapper.toDto(paymentCard);
    }

    public Page<PaymentCardResponseDto> getAllCards(UserFilter userFilter, Pageable pageable) {
        Specification<PaymentCard> specification = Specification
                .where(PaymentCardSpecification.hasFirstName(userFilter.name()))
                .and(PaymentCardSpecification.hasSurname(userFilter.surname()));
        return paymentCardRepository.findAll(specification, pageable)
                .map(c -> paymentCardMapper.toDto(c));
    }

    public List<PaymentCardResponseDto> getAllCardByUserId(Long id) {
        return paymentCardRepository.findAllByUserId(id).stream()
                .map(c -> paymentCardMapper.toDto(c))
                .toList();
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public PaymentCardResponseDto updateCardById(Long id, PaymentCardRequestDto paymentCardRequestDto) {
        PaymentCard oldPaymentCard = paymentCardRepository.findById(id)
                .orElseThrow(() -> new PaymentCardNotFoundException("Payment card with id " + id + " not found"));
        PaymentCard paymentCard = paymentCardMapper.toEntity(paymentCardRequestDto);
        paymentCard.setUser(oldPaymentCard.getUser());
        paymentCard.setId(id);
        PaymentCard savePaymentCard = paymentCardRepository.save(paymentCard);
        cacheManager.getCache("user_cache").evict(oldPaymentCard.getUser().getId());
        return paymentCardMapper.toDto(savePaymentCard);
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public void activateCardById(Long id) {
        PaymentCard paymentCard = paymentCardRepository.findById(id)
                .orElseThrow(() -> new PaymentCardNotFoundException("Payment card with id " + id + " not found"));
        if(paymentCard.getActive().equals(Boolean.TRUE))
            return;
        paymentCardRepository.updateActive(id, true);
        cacheManager.getCache("user_cache").evict(paymentCard.getUser().getId());
        paymentCardRepository.updateActive(id, true);
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public void deactivateCardById(Long id) {
        PaymentCard paymentCard = paymentCardRepository.findById(id)
                .orElseThrow(() -> new PaymentCardNotFoundException("Payment card with id " + id + " not found"));
        if(paymentCard.getActive().equals(Boolean.FALSE))
            return;
        paymentCardRepository.updateActive(id, false);
        paymentCardRepository.updateActive(id, false);
        cacheManager.getCache("user_cache").evict(paymentCard.getUser().getId());
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public void deleteCardById(Long id) {
        PaymentCard paymentCard = paymentCardRepository.findById(id)
                .orElseThrow(() -> new PaymentCardNotFoundException("Payment card with id " + id + " not found"));
        paymentCardRepository.deleteById(id);
        cacheManager.getCache("user_cache").evict(paymentCard.getUser().getId());
    }

}
