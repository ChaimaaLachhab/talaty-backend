package com.talaty.mapper;

import com.talaty.dto.response.OTPResponseDto;
import org.mapstruct.*;

import java.time.LocalDateTime;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING)
public interface OTPMapper {

    @Mapping(target = "expiryTime", expression = "java(java.time.LocalDateTime.now().plusMinutes(5))")
    OTPResponseDto toResponseDto(String message, boolean success);

    default OTPResponseDto toSuccessResponse(String message) {
        return new OTPResponseDto(message, true, LocalDateTime.now().plusMinutes(5));
    }

    default OTPResponseDto toErrorResponse(String message) {
        return new OTPResponseDto(message, false, null);
    }
}
