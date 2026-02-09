package com.github.birulazena.UserService.unit;

import com.github.birulazena.UserService.dto.filter.UserFilter;
import com.github.birulazena.UserService.dto.request.OnlyUserRequestDto;
import com.github.birulazena.UserService.dto.request.UserRequestDto;
import com.github.birulazena.UserService.dto.response.OnlyUserResponseDto;
import com.github.birulazena.UserService.dto.response.UserResponseDto;
import com.github.birulazena.UserService.entity.PaymentCard;
import com.github.birulazena.UserService.entity.User;
import com.github.birulazena.UserService.exception.PaymentCardLimitExceededException;
import com.github.birulazena.UserService.exception.UserNotFoundException;
import com.github.birulazena.UserService.mapper.UserMapper;
import com.github.birulazena.UserService.repository.UserRepository;
import com.github.birulazena.UserService.service.UserService;
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
public class UserServiceUnitTests {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @Mock
    private CacheManager cacheManager;

    @InjectMocks
    private UserService userService;

    @Test
    void createUserCardLimitTest() {

        UserRequestDto userRequestDto = TestDataFactory.userRequestDto();

        User user = new User();
        user.setCards(List.of(
                        new PaymentCard(), new PaymentCard(),
                        new PaymentCard(), new PaymentCard(),
                        new PaymentCard(), new PaymentCard()));

        Mockito.when(userMapper.toEntity(userRequestDto)).thenReturn(user);
        assertThrows(PaymentCardLimitExceededException.class,
                () -> userService.createUser(userRequestDto));
        Mockito.verify(userRepository, Mockito.never()).save(Mockito.any());
    }

    @Test
    void createUserTest() {

        ReflectionTestUtils.setField(userService, "cardLimit", 5);

        UserRequestDto userRequestDto = TestDataFactory.userRequestDto();
        User user = TestDataFactory.validNewUser();
        user.getCards().add(TestDataFactory.validPaymentCard());
        User savedUser = TestDataFactory.validSavedUser();
        UserResponseDto userResponseDto = TestDataFactory.userResponseDto();

        Mockito.when(userMapper.toEntity(userRequestDto)).thenReturn(user);
        Mockito.when(userRepository.save(user)).thenReturn(savedUser);
        Mockito.when(userMapper.toDto(savedUser)).thenReturn(userResponseDto);

        UserResponseDto result = userService.createUser(userRequestDto);
        assertEquals(userResponseDto, result);
    }

    @Test
    void getUserByIdUserNotFoundExceptionTest() {

        Mockito.when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class,
                () -> userService.getUserById(1L));
    }

    @Test
    void getUserByIdTest() {
        User user = TestDataFactory.validSavedUser();
        UserResponseDto userResponseDto = TestDataFactory.userResponseDto();

        Mockito.when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        Mockito.when(userMapper.toDto(user)).thenReturn(userResponseDto);

        UserResponseDto result = userService.getUserById(1L);

        assertEquals(userResponseDto, result);
    }

    @Test
    void getAllUsersTest() {
        UserFilter userFilter = new UserFilter("Zenya", null);
        Pageable pageable = PageRequest.of(0, 10);

        User user1 = TestDataFactory.validSavedUser();
        User user2 = TestDataFactory.validSavedUser();
        User user3 = TestDataFactory.validSavedUser();
        user3.setName("Maxim");

        Page<User> page = new PageImpl<>(List.of(user1, user2));

        OnlyUserResponseDto dto1 = TestDataFactory.onlyUserResponseDto();
        OnlyUserResponseDto dto2 = TestDataFactory.onlyUserResponseDto();

        Mockito.when(userRepository.findAll(Mockito.any(Specification.class), Mockito.eq(pageable)))
                .thenReturn(page);
        Mockito.when(userMapper.toOnlyUserResponseDto(user1)).thenReturn(dto1);
        Mockito.when(userMapper.toOnlyUserResponseDto(user2)).thenReturn(dto2);

        Page<OnlyUserResponseDto> result = userService.getAllUsers(userFilter, pageable);

        assertEquals(2, result.getTotalElements());
        assertEquals(dto1, result.getContent().get(0));
        assertEquals(dto2, result.getContent().get(1));
    }

    @Test
    void updateUserUserNotFoundExceptionTest() {

        OnlyUserRequestDto onlyUserRequestDto = TestDataFactory.newUniqueOnlyUserRequestDto();

        Mockito.when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class,
                () -> userService.updateUser(1L, onlyUserRequestDto));
    }

    @Test
    void updateUserTest() {

        Long id = 1L;
        OnlyUserRequestDto onlyUserRequestDto = TestDataFactory.newUniqueOnlyUserRequestDto();
        User oldUser = TestDataFactory.validSavedUser();
        User user = TestDataFactory.validNewUser();
        User updateUser = TestDataFactory.validSavedUser();
        OnlyUserResponseDto onlyUserResponseDto = TestDataFactory.onlyUserResponseDto();

        Mockito.when(userRepository.findById(id)).thenReturn(Optional.of(oldUser));
        Mockito.when(userMapper.toEntityFromOnlyUserRequestDto(onlyUserRequestDto)).thenReturn(user);
        Mockito.when(userRepository.save(user)).thenReturn(updateUser);
        Mockito.when(userMapper.toOnlyUserResponseDto(updateUser)).thenReturn(onlyUserResponseDto);

        OnlyUserResponseDto result = userService.updateUser(id, onlyUserRequestDto);

        assertEquals(onlyUserResponseDto, result);
    }

    @Test
    void activateUserByIdUserNotFoundExceptionTest() {

        UserRequestDto userRequestDto = TestDataFactory.userRequestDto();

        Mockito.when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class,
                () -> userService.activateUserById(1L));
    }

    @Test
    void activateUserByIdWithFalseTest() {

        User user = TestDataFactory.validSavedUser();
        user.setActive(false);

        Mockito.when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        userService.activateUserById(1L);

        Mockito.verify(userRepository).updateActive(1L, true);
    }

    @Test
    void activateUserByIdWithTrueTest() {
        User user = TestDataFactory.validSavedUser();
        user.setActive(true);

        Mockito.when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        userService.activateUserById(1L);

        Mockito.verify(userRepository, Mockito.never()).updateActive(Mockito.anyLong(), Mockito.anyBoolean());
    }

    @Test
    void deactivateUserByIdUserNotFoundExceptionTest() {

        UserRequestDto userRequestDto = TestDataFactory.userRequestDto();

        Mockito.when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class,
                () -> userService.deactivateUserById(1L));
    }

    @Test
    void deactivateUserByIdWithTrueTest() {

        User user = TestDataFactory.validSavedUser();
        user.setActive(true);

        Mockito.when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        userService.deactivateUserById(1L);

        Mockito.verify(userRepository).updateActive(1L, false);
    }

    @Test
    void deactivateUserByIdWithFalseTest() {
        User user = TestDataFactory.validSavedUser();
        user.setActive(false);

        Mockito.when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        userService.deactivateUserById(1L);

        Mockito.verify(userRepository, Mockito.never()).updateActive(Mockito.anyLong(), Mockito.anyBoolean());
    }

    @Test
    void deleteUserById() {

        userService.deleteUserById(1L);

        Mockito.verify(userRepository).deleteById(1L);
    }

}
