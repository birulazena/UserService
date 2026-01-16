package com.github.birulazena.UserService.service;

import com.github.birulazena.UserService.dto.filter.UserFilter;
import com.github.birulazena.UserService.dto.request.OnlyUserRequestDto;
import com.github.birulazena.UserService.dto.request.UserRequestDto;
import com.github.birulazena.UserService.dto.response.OnlyUserResponseDto;
import com.github.birulazena.UserService.dto.response.UserResponseDto;
import com.github.birulazena.UserService.entity.User;
import com.github.birulazena.UserService.exception.PaymentCardLimitExceededException;
import com.github.birulazena.UserService.exception.UserNotFoundException;
import com.github.birulazena.UserService.mapper.PaymentCardMapper;
import com.github.birulazena.UserService.mapper.UserMapper;
import com.github.birulazena.UserService.repository.PaymentCardRepository;
import com.github.birulazena.UserService.repository.UserRepository;
import com.github.birulazena.UserService.specification.UserSpecification;
import lombok.RequiredArgsConstructor;
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

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public UserResponseDto createUser(UserRequestDto userRequestDto) {
        User user = userMapper.toEntity(userRequestDto);

        if(user.getCards().size() > cardLimit)
            throw new PaymentCardLimitExceededException("The " + cardLimit + " card limit has been exceeded");

        user.getCards().forEach(c -> c.setUser(user));
        User saveUser = userRepository.save(user);
        return userMapper.toDto(saveUser);
    }

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

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public OnlyUserResponseDto updateUser(Long id, OnlyUserRequestDto onlyUserRequestDto) {
        User oldUser = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User with id " + id + " not found"));
        User user = userMapper.toEntityFromOnlyUserRequestDto(onlyUserRequestDto);
        user.setId(id);
        user.setActive(oldUser.getActive());
        User updateUser = userRepository.save(user);
        return userMapper.toOnlyUserResponseDto(updateUser);
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public void activateUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User with id " + id + " not found"));
        if(user.getActive().equals(Boolean.TRUE))
            return;
        userRepository.updateActive(id, true);
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public void deactivateUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User with id " + id + " not found"));
        if(user.getActive().equals(Boolean.FALSE))
            return;
        userRepository.updateActive(id, false);
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public void deleteUserById(Long id) {
        userRepository.deleteById(id);
    }
}
