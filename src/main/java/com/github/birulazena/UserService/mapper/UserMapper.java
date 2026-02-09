package com.github.birulazena.UserService.mapper;

import com.github.birulazena.UserService.dto.request.OnlyUserRequestDto;
import com.github.birulazena.UserService.dto.request.UserRequestDto;
import com.github.birulazena.UserService.dto.response.OnlyUserResponseDto;
import com.github.birulazena.UserService.dto.response.UserResponseDto;
import com.github.birulazena.UserService.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedSourcePolicy = ReportingPolicy.IGNORE,
        uses = PaymentCardMapper.class)
public interface UserMapper {

    UserResponseDto toDto(User user);

    OnlyUserResponseDto toOnlyUserResponseDto(User user);

    User toEntity(UserRequestDto userRequestDto);

    User toEntityFromOnlyUserRequestDto(OnlyUserRequestDto onlyUserRequestDto);
}
