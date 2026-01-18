package com.github.birulazena.UserService.integration;

import com.github.birulazena.UserService.dto.filter.UserFilter;
import com.github.birulazena.UserService.dto.request.PaymentCardRequestDto;
import com.github.birulazena.UserService.dto.response.PaymentCardResponseDto;
import com.github.birulazena.UserService.entity.PaymentCard;
import com.github.birulazena.UserService.entity.User;
import com.github.birulazena.UserService.factory.TestDataFactory;
import com.github.birulazena.UserService.repository.PaymentCardRepository;
import com.github.birulazena.UserService.repository.UserRepository;
import com.github.birulazena.UserService.service.PaymentCardService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cache.CacheManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.postgresql.PostgreSQLContainer;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;


@SpringBootTest
@Testcontainers
public class PaymentCardServiceIntegrationTest {

    @Autowired
    private PaymentCardService paymentCardService;

    @Autowired
    private PaymentCardRepository paymentCardRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CacheManager cacheManager;

    @Container
    static PostgreSQLContainer postgres = new PostgreSQLContainer("postgres:17")
            .withDatabaseName("users")
            .withUsername("postgres")
            .withPassword("postgres");

    @Container
    static GenericContainer<?> redis = new GenericContainer<>("redis:8")
            .withExposedPorts(6379);

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);

        registry.add("spring.data.redis.host", redis::getHost);
        registry.add("spring.data.redis.port", () -> redis.getMappedPort(6379));
    }

    @Test
    void createPaymentCardForUserTest() {
        User user = TestDataFactory.newUniqueUser();
        User savedUser = userRepository.save(user);
        Long userId = savedUser.getId();
        PaymentCardRequestDto paymentCardRequestDto = TestDataFactory.paymentCardRequestDto();

        PaymentCardResponseDto paymentCardResponseDto = paymentCardService
                .createPaymentCardForUser(userId, paymentCardRequestDto);

        assertEquals(paymentCardResponseDto.userId(), userId);
        assertNotNull(paymentCardResponseDto.id());
        assertNull(cacheManager.getCache("user_cache").get(userId));
    }

    @Test
    void getCardByIdTest() {
        User user = TestDataFactory.newUniqueUser();
        User savedUser = userRepository.save(user);
        Long userId = savedUser.getId();
        PaymentCardRequestDto paymentCardRequestDto = TestDataFactory.paymentCardRequestDto();

        PaymentCardResponseDto paymentCardResponseDto = paymentCardService
                .createPaymentCardForUser(userId, paymentCardRequestDto);

        PaymentCardResponseDto paymentCardResponseDto1 = paymentCardService
                .getCardById(paymentCardResponseDto.id());

        assertEquals(paymentCardResponseDto1.id(), paymentCardResponseDto.id());
    }

    @Test
    void getAllCardsTest() {
        User user1 = TestDataFactory.newUniqueUser();
        user1.setName("getAllCardsTest");
        User user2 = TestDataFactory.validNewUser();

        PaymentCard card1 = TestDataFactory.newPaymentCard();
        card1.setUser(user1);
        PaymentCard card2 = TestDataFactory.newPaymentCard();
        card2.setUser(user1);
        PaymentCard card3 = TestDataFactory.newPaymentCard();
        card3.setUser(user2);

        userRepository.save(user1);
        userRepository.save(user2);

        paymentCardRepository.save(card1);
        paymentCardRepository.save(card2);
        paymentCardRepository.save(card3);

        UserFilter userFilter = new UserFilter("getAllCardsTest", null);
        Pageable pageable = PageRequest.of(0, 10);

        Page<PaymentCardResponseDto> cards = paymentCardService
                .getAllCards(userFilter, pageable);

        assertEquals(2, cards.getTotalElements());
    }


    @Test
    void getAllCardByUserIdTest() {
        User user = TestDataFactory.newUniqueUser();
        PaymentCard card1 = TestDataFactory.newPaymentCard();
        card1.setUser(user);
        PaymentCard card2 = TestDataFactory.newPaymentCard();
        card2.setUser(user);

        User savedUser = userRepository.save(user);
        PaymentCard savedCard1 = paymentCardRepository.save(card1);
        PaymentCard savedCard2 = paymentCardRepository.save(card2);

        List<PaymentCardResponseDto> cards = paymentCardService.getAllCardByUserId(savedUser.getId());

        assertEquals(2, cards.size());
        assertTrue(cards.stream()
                .anyMatch(c -> c.id().equals(savedCard1.getId())));
        assertTrue(cards.stream()
                .anyMatch(c -> c.id().equals(savedCard2.getId())));

    }

    @Test
    void updateCardByIdTest() {
        User user = TestDataFactory.newUniqueUser();
        PaymentCard card = TestDataFactory.newPaymentCard();
        card.setUser(user);
        PaymentCardRequestDto paymentCardRequestDto = new PaymentCardRequestDto(
                "112233445566",
                "test",
                LocalDate.now()
        );

        User savedUser = userRepository.save(user);
        PaymentCard savedCard = paymentCardRepository.save(card);
        PaymentCardResponseDto paymentCardResponseDto = paymentCardService
                .updateCardById(savedCard.getId(), paymentCardRequestDto);

        PaymentCard checkCard = paymentCardRepository.findById(savedCard.getId()).get();

        assertEquals(checkCard.getId(), paymentCardResponseDto.id());
        assertEquals(checkCard.getNumber(), paymentCardResponseDto.number());
        assertEquals(checkCard.getHolder(), paymentCardResponseDto.holder());
        assertNull(cacheManager.getCache("user_cache").get(savedUser.getId()));
    }

    @Test
    void activateCardByIdTest() {
        User user = TestDataFactory.newUniqueUser();
        PaymentCard card = TestDataFactory.newPaymentCard();
        card.setActive(false);
        card.setUser(user);

        User savedUser = userRepository.save(user);
        PaymentCard savedCard = paymentCardRepository.save(card);

        paymentCardService.activateCardById(savedCard.getId());

        PaymentCard result = paymentCardRepository.findById(savedCard.getId()).get();

        assertTrue(result.getActive());
        assertNull(cacheManager.getCache("user_cache").get(savedUser.getId()));
    }

    @Test
    void deactivateCardByIdTest() {
        User user = TestDataFactory.newUniqueUser();
        PaymentCard card = TestDataFactory.newPaymentCard();
        card.setActive(true);
        card.setUser(user);

        User savedUser = userRepository.save(user);
        PaymentCard savedCard = paymentCardRepository.save(card);

        paymentCardService.deactivateCardById(savedCard.getId());

        PaymentCard result = paymentCardRepository.findById(savedCard.getId()).get();

        assertFalse(result.getActive());
        assertNull(cacheManager.getCache("user_cache").get(savedUser.getId()));
    }

    @Test
    void deleteCardById() {
        User user = TestDataFactory.newUniqueUser();
        PaymentCard card = TestDataFactory.newPaymentCard();
        card.setUser(user);

        User savedUser = userRepository.save(user);
        PaymentCard savedCard = paymentCardRepository.save(card);

        paymentCardService.deleteCardById(savedCard.getId());

        assertTrue(paymentCardRepository.findById(savedCard.getId()).isEmpty());
        assertNull(cacheManager.getCache("user_cache").get(savedUser.getId()));
    }
}
