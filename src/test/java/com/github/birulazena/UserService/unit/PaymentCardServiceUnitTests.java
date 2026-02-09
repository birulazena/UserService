package com.github.birulazena.UserService.unit;

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
import com.github.birulazena.UserService.service.PaymentCardService;
import com.github.birulazena.UserService.factory.TestDataFactory;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.*;


import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
public class PaymentCardServiceUnitTests {

    @Mock
    private PaymentCardRepository paymentCardRepository;

    @Mock
    private PaymentCardMapper paymentCardMapper;

    @Mock
    private UserRepository userRepository;

    @Mock
    private CacheManager cacheManager;

    @InjectMocks
    private PaymentCardService paymentCardService;

    @Test
    void createPaymentCardForUserUserNotFoundExceptionTest() {

        PaymentCardRequestDto paymentCardRequestDto = TestDataFactory.paymentCardRequestDto();

        Mockito.when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class,
                () -> paymentCardService.createPaymentCardForUser(1L, paymentCardRequestDto));
    }

    @Test
    void createPaymentCardForUserPaymentCardLimitExceededExceptionTest() {

        User user = TestDataFactory.validSavedUser();
        user.getCards().addAll(List.of(
                new PaymentCard(), new PaymentCard(),
                new PaymentCard(), new PaymentCard(),
                new PaymentCard()));
        PaymentCardRequestDto paymentCardRequestDto = TestDataFactory.paymentCardRequestDto();

        Mockito.when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        assertThrows(PaymentCardLimitExceededException.class,
                () -> paymentCardService.createPaymentCardForUser(1L, paymentCardRequestDto));
    }

    @Test
    void createPaymentCardForUserTest() {

        ReflectionTestUtils.setField(paymentCardService, "cardLimit", 5);

        PaymentCardRequestDto paymentCardRequestDto = TestDataFactory.paymentCardRequestDto();
        User user = TestDataFactory.validSavedUser();
        PaymentCard paymentCard = TestDataFactory.validPaymentCard();
        PaymentCard savePaymentCard = TestDataFactory.validPaymentCard();
        PaymentCardResponseDto paymentCardResponseDto = TestDataFactory.paymentCardResponseDto();

        Mockito.when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        Mockito.when(paymentCardMapper.toEntity(paymentCardRequestDto)).thenReturn(paymentCard);
        Mockito.when(paymentCardRepository.save(paymentCard)).thenReturn(savePaymentCard);
        Mockito.when(paymentCardMapper.toDto(savePaymentCard)).thenReturn(paymentCardResponseDto);

        PaymentCardResponseDto result = paymentCardService
                .createPaymentCardForUser(1L, paymentCardRequestDto);

        assertEquals(result, paymentCardResponseDto);
    }

    @Test
    void getCardByIdPaymentCardNotFoundExceptionTest() {

        PaymentCard paymentCard = TestDataFactory.validPaymentCard();

        Mockito.when(paymentCardRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(PaymentCardNotFoundException.class,
                () -> paymentCardService.getCardById(1L));
    }

    @Test
    void getCardByIdTest() {

        PaymentCard paymentCard = TestDataFactory.validPaymentCard();
        PaymentCardResponseDto paymentCardResponseDto = TestDataFactory.paymentCardResponseDto();

        Mockito.when(paymentCardRepository.findById(1L)).thenReturn(Optional.of(paymentCard));
        Mockito.when(paymentCardMapper.toDto(paymentCard)).thenReturn(paymentCardResponseDto);

        PaymentCardResponseDto result = paymentCardService
                .getCardById(1L);

        assertEquals(result, paymentCardResponseDto);
    }

    @Test
    void getAllCardsTest() {

        UserFilter userFilter = new UserFilter("Zenya", null);
        Pageable pageable = PageRequest.of(0, 10);

        PaymentCard paymentCard1 = TestDataFactory.validPaymentCard();
        PaymentCard paymentCard2 = TestDataFactory.validPaymentCard();

        Page<PaymentCard> page = new PageImpl<>(List.of(paymentCard1, paymentCard2));

        PaymentCardResponseDto dto1 = TestDataFactory.paymentCardResponseDto();
        PaymentCardResponseDto dto2 = TestDataFactory.paymentCardResponseDto();

        Mockito.when(paymentCardRepository.findAll(Mockito.any(Specification.class), Mockito.eq(pageable)))
                .thenReturn(page);
        Mockito.when(paymentCardMapper.toDto(paymentCard1)).thenReturn(dto1);
        Mockito.when(paymentCardMapper.toDto(paymentCard2)).thenReturn(dto2);

        Page<PaymentCardResponseDto> result = paymentCardService.getAllCards(userFilter, pageable);

        assertEquals(2, result.getTotalElements());
        assertEquals(dto1, result.getContent().get(0));
        assertEquals(dto2, result.getContent().get(1));
    }

    @Test
    void getAllCardByUserIdTest() {

        PaymentCard paymentCard1 = TestDataFactory.validPaymentCard();
        PaymentCard paymentCard2 = TestDataFactory.validPaymentCard();

        List<PaymentCard> cards = List.of(paymentCard1, paymentCard2);

        PaymentCardResponseDto dto1 = TestDataFactory.paymentCardResponseDto();
        PaymentCardResponseDto dto2 = TestDataFactory.paymentCardResponseDto();

        List<PaymentCardResponseDto> dtos = List.of(dto1, dto2);

        Mockito.when(paymentCardRepository.findAllByUserId(1L)).thenReturn(cards);
        Mockito.when(paymentCardMapper.toDto(paymentCard1)).thenReturn(dto1);
        Mockito.when(paymentCardMapper.toDto(paymentCard2)).thenReturn(dto2);

        List<PaymentCardResponseDto> result = paymentCardService.getAllCardByUserId(1L);

        assertEquals(dtos, result);
    }

    @Test
    void updateCardByIdPaymentCardNotFoundExceptionTest() {

        PaymentCard paymentCard = TestDataFactory.validPaymentCard();
        PaymentCardRequestDto paymentCardRequestDto = TestDataFactory.paymentCardRequestDto();

        Mockito.when(paymentCardRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(PaymentCardNotFoundException.class,
                () -> paymentCardService.updateCardById(1L, paymentCardRequestDto));
    }

    @Test
    void updateCardByIdTest() {

        PaymentCardRequestDto paymentCardRequestDto = TestDataFactory.paymentCardRequestDto();
        PaymentCard oldPaymentCard = TestDataFactory.validPaymentCard();
        oldPaymentCard.setUser(TestDataFactory.validSavedUser());

        PaymentCard paymentCard = TestDataFactory.validPaymentCard();
        PaymentCard savedPaymentCard = TestDataFactory.validPaymentCard();
        PaymentCardResponseDto paymentCardResponseDto = TestDataFactory.paymentCardResponseDto();
        Cache cache = Mockito.mock(Cache.class);

        Mockito.when(paymentCardRepository.findById(1L)).thenReturn(Optional.of(oldPaymentCard));
        Mockito.when(paymentCardMapper.toEntity(paymentCardRequestDto)).thenReturn(paymentCard);
        Mockito.when(paymentCardRepository.save(paymentCard)).thenReturn(savedPaymentCard);
        Mockito.when(cacheManager.getCache("user_cache")).thenReturn(cache);
        Mockito.when(paymentCardMapper.toDto(savedPaymentCard)).thenReturn(paymentCardResponseDto);

        PaymentCardResponseDto result = paymentCardService.updateCardById(1L, paymentCardRequestDto);

        assertEquals(paymentCardResponseDto, result);

        Mockito.verify(cache, Mockito.times(1)).evict(1L);
    }


    @Test
    void activateCardByIdPaymentCardNotFoundExceptionTest() {

        PaymentCardRequestDto paymentCardRequestDto = TestDataFactory.paymentCardRequestDto();

        Mockito.when(paymentCardRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(PaymentCardNotFoundException.class,
                () -> paymentCardService.activateCardById(1L));
    }

    @Test
    void activateCardByIdWithFalseTest() {

        PaymentCard paymentCard = TestDataFactory.validPaymentCard();
        paymentCard.setUser(TestDataFactory.validSavedUser());
        paymentCard.setActive(false);

        Cache cache = Mockito.mock(Cache.class);

        Mockito.when(paymentCardRepository.findById(1L))
                .thenReturn(Optional.of(paymentCard));
        Mockito.when(cacheManager.getCache("user_cache"))
                .thenReturn(cache);

        paymentCardService.activateCardById(1L);

        Mockito.verify(paymentCardRepository).updateActive(1L, true);
        Mockito.verify(cache, Mockito.times(1)).evict(1L);
    }


    @Test
    void activateCardByIdWithTrueTest() {

        PaymentCard paymentCard = TestDataFactory.validPaymentCard();
        paymentCard.setActive(true);

        Mockito.when(paymentCardRepository.findById(1L)).thenReturn(Optional.of(paymentCard));

        paymentCardService.activateCardById(1L);

        Mockito.verify(paymentCardRepository, Mockito.never()).updateActive(Mockito.anyLong(), Mockito.anyBoolean());
    }

    @Test
    void deactivateCardByIdPaymentCardNotFoundExceptionTest() {

        PaymentCardRequestDto paymentCardRequestDto = TestDataFactory.paymentCardRequestDto();

        Mockito.when(paymentCardRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(PaymentCardNotFoundException.class,
                () -> paymentCardService.deactivateCardById(1L));
    }

    @Test
    void deactivateCardByIdWithTrueTest() {

        PaymentCard paymentCard = TestDataFactory.validPaymentCard();
        paymentCard.setUser(TestDataFactory.validSavedUser());
        paymentCard.setActive(true);

        Cache cache = Mockito.mock(Cache.class);

        Mockito.when(paymentCardRepository.findById(1L))
                .thenReturn(Optional.of(paymentCard));
        Mockito.when(cacheManager.getCache("user_cache"))
                .thenReturn(cache);

        paymentCardService.deactivateCardById(1L);

        Mockito.verify(paymentCardRepository).updateActive(1L, false);
        Mockito.verify(cache, Mockito.times(1)).evict(1L);
    }


    @Test
    void deactivateCardByIdWithFalseTest() {

        PaymentCard paymentCard = TestDataFactory.validPaymentCard();
        paymentCard.setActive(false);

        Mockito.when(paymentCardRepository.findById(1L)).thenReturn(Optional.of(paymentCard));

        paymentCardService.deactivateCardById(1L);

        Mockito.verify(paymentCardRepository, Mockito.never()).updateActive(Mockito.anyLong(), Mockito.anyBoolean());
    }

    @Test void deleteCardById() {
        PaymentCard paymentCard = TestDataFactory.validPaymentCard();
        paymentCard.setUser(TestDataFactory.validSavedUser());
        Cache cache = Mockito.mock(Cache.class);

        Mockito.when(paymentCardRepository.findById(1L)) .thenReturn(Optional.of(paymentCard));
        Mockito.when(cacheManager.getCache("user_cache")) .thenReturn(cache);

        paymentCardService.deleteCardById(1L); Mockito.verify(paymentCardRepository).deleteById(1L);

        Mockito.verify(cache, Mockito.times(1)).evict(1L);
    }

}
