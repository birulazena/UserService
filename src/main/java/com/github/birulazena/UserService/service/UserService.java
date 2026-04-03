package com.github.birulazena.UserService.service;

import com.github.birulazena.UserService.dto.filter.UserFilter;
import com.github.birulazena.UserService.dto.request.OnlyUserRequestDto;
import com.github.birulazena.UserService.dto.request.UserRequestDto;
import com.github.birulazena.UserService.dto.response.OnlyUserResponseDto;
import com.github.birulazena.UserService.dto.response.UserResponseDto;
import com.github.birulazena.UserService.entity.User;
import com.github.birulazena.UserService.exception.EmailAlreadyExistException;
import com.github.birulazena.UserService.exception.PaymentCardLimitExceededException;
import com.github.birulazena.UserService.exception.UserNotFoundException;
import com.github.birulazena.UserService.mapper.UserMapper;
import com.github.birulazena.UserService.repository.UserRepository;
import com.github.birulazena.UserService.specification.UserSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class UserService {

    private final UserRepository userRepository;

    private final UserMapper userMapper;

    @Value("${user.cards.limit}")
    private int cardLimit;

    @CachePut(value = "user_cache", key = "#result.id")
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public UserResponseDto createUser(UserRequestDto userRequestDto) {
        User user = userMapper.toEntity(userRequestDto);

        if(userRepository.existsByEmail(user.getEmail()))
            throw new EmailAlreadyExistException("User with email " + user.getEmail() + "already exist");

        if(user.getCards().size() > cardLimit)
            throw new PaymentCardLimitExceededException("The " + cardLimit + " card limit has been exceeded");

        user.getCards().forEach(c -> c.setUser(user));
        User saveUser = userRepository.save(user);
        return userMapper.toDto(saveUser);
    }

    @Cacheable(value = "user_cache", key = "#id")
    public UserResponseDto getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User with id " + id + " not found"));
        return userMapper.toDto(user);
    }

    public Page<OnlyUserResponseDto> getAllUsers(UserFilter userFilter, Pageable pageable) {
        Specification<User> specification = Specification
                .where(UserSpecification.hasFirstName(userFilter.name()))
                .and(UserSpecification.hasSurname(userFilter.surname()));
        return userRepository.findAll(specification, pageable)
                .map(u -> userMapper.toOnlyUserResponseDto(u));
    }

    @CacheEvict(value = "user_cache", key = "#id")
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public OnlyUserResponseDto updateUser(Long id, OnlyUserRequestDto onlyUserRequestDto) {
        User oldUser = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User with id " + id + " not found"));

        String newEmail = onlyUserRequestDto.email();
        if (!oldUser.getEmail().equals(newEmail) && userRepository.existsByEmail(newEmail)) {
            throw new EmailAlreadyExistException("User with email " + newEmail + " already exists");
        }

        User user = userMapper.toEntityFromOnlyUserRequestDto(onlyUserRequestDto);
        user.setId(id);
        user.setActive(oldUser.getActive());
        User updateUser = userRepository.save(user);
        return userMapper.toOnlyUserResponseDto(updateUser);
    }

    @CacheEvict(value = "user_cache", key = "#id")
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public void activateUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User with id " + id + " not found"));
        if(user.getActive().equals(Boolean.TRUE))
            return;
        userRepository.updateActive(id, true);
    }


    @CacheEvict(value = "user_cache", key = "#id")
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public void deactivateUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User with id " + id + " not found"));
        if(user.getActive().equals(Boolean.FALSE))
            return;
        userRepository.updateActive(id, false);
    }

    @CacheEvict(value = "user_cache", key = "#id")
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public void deleteUserById(Long id) {
        userRepository.deleteById(id);
    }
}
