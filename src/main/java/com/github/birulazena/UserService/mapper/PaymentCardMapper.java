package com.github.birulazena.UserService.mapper;

import com.github.birulazena.UserService.dto.request.PaymentCardRequestDto;
import com.github.birulazena.UserService.dto.response.PaymentCardResponseDto;
import com.github.birulazena.UserService.entity.PaymentCard;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedSourcePolicy = ReportingPolicy.IGNORE)
public interface PaymentCardMapper {

    @Mapping(target = "userId", source = "user.id")
    PaymentCardResponseDto toDto(PaymentCard paymentCard);

    PaymentCard toEntity(PaymentCardRequestDto paymentCardRequestDto);
}
