package com.github.birulazena.UserService.integration;

import com.github.birulazena.UserService.dto.filter.UserFilter;
import com.github.birulazena.UserService.dto.request.OnlyUserRequestDto;
import com.github.birulazena.UserService.dto.request.UserRequestDto;
import com.github.birulazena.UserService.dto.response.OnlyUserResponseDto;
import com.github.birulazena.UserService.dto.response.UserResponseDto;
import com.github.birulazena.UserService.entity.User;
import com.github.birulazena.UserService.factory.TestDataFactory;
import com.github.birulazena.UserService.repository.UserRepository;
import com.github.birulazena.UserService.service.UserService;
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

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;



@SpringBootTest
@Testcontainers
public class UserServiceIntegrationTest {

    @Autowired
    private UserService userService;

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
    void createUserTest() {

        UserRequestDto userRequestDto = TestDataFactory.newUniqueUserRequestDto();

        UserResponseDto userResponseDto = userService.createUser(userRequestDto);
        Optional<User> userDB = userRepository.findById(userResponseDto.id());

        assertTrue(userDB.isPresent());
        assertEquals(userResponseDto.id(), userDB.get().getId());
        assertNotNull(cacheManager.getCache("user_cache").get(userResponseDto.id()).get());

    }

        @Test
        void getUserByIdTest() {

            User user = TestDataFactory.newUniqueUser();
            User saveUser = userRepository.save(user);
            UserResponseDto userResponseDto = userService.getUserById(saveUser.getId());

            assertEquals(saveUser.getId(), userResponseDto.id());
            assertEquals(saveUser.getEmail(), userResponseDto.email());
            assertEquals(saveUser.getCards().size(), userResponseDto.cards().size());

            UserResponseDto userResponseDto1 = userService.getUserById(saveUser.getId());

            assertEquals(userResponseDto, userResponseDto1);
        }

    @Test
    void getAllUsersTest() {

        User user1 = TestDataFactory.newUniqueUser();
        user1.setName("GetAllUsersTest");
        User user2 = TestDataFactory.newUniqueUser();
        user2.setName("GetAllUsersTest");
        User user3 = TestDataFactory.newUniqueUser();
        user3.setName("Maxim");

        userRepository.save(user1);
        userRepository.save(user2);
        userRepository.save(user3);

        UserFilter userFilter = new UserFilter("GetAllUsersTest", null);
        Pageable pageable = PageRequest.of(0, 10);

        Page<OnlyUserResponseDto> result = userService.getAllUsers(userFilter, pageable);

        assertEquals(2, result.getTotalElements());
        assertTrue(result.getContent().stream()
                .allMatch(d -> d.name().equals("GetAllUsersTest")));
    }

    @Test
    void updateUserTest() {

        User user = TestDataFactory.newUniqueUser();
        User savedUser = userRepository.save(user);
        Long id = savedUser.getId();
        OnlyUserRequestDto onlyUserRequestDto = TestDataFactory.newUniqueOnlyUserRequestDto();

        OnlyUserResponseDto onlyUserResponseDto = userService.updateUser(id, onlyUserRequestDto);

        assertEquals(id, onlyUserResponseDto.id());
        assertEquals(onlyUserRequestDto.email(), onlyUserResponseDto.email());
        assertNull(cacheManager.getCache("user_cache").get(id));
    }

    @Test
    void activateUserByIdTest() {
        User user = TestDataFactory.newUniqueUser();
        user.setActive(false);
        User savedUser = userRepository.save(user);

        assertFalse(savedUser.getActive());

        Long id = savedUser.getId();

        userService.activateUserById(id);

        assertNull(cacheManager.getCache("user_cache").get(id));
    }

    @Test
    void deactivateUserByIdTest() {
        User user = TestDataFactory.newUniqueUser();
        user.setActive(true);
        User savedUser = userRepository.save(user);

        assertTrue(savedUser.getActive());

        Long id = savedUser.getId();

        userService.deactivateUserById(id);

        assertNull(cacheManager.getCache("user_cache").get(id));
    }

    @Test
    void deleteUserByIdTest() {
        User user = TestDataFactory.newUniqueUser();
        User savedUser = userRepository.save(user);
        Long id = savedUser.getId();

        userService.deleteUserById(id);

        assertTrue(userRepository.findById(id).isEmpty());
        assertNull(cacheManager.getCache("user_cache").get(id));
    }

}
