package com.talaty.mapper;

import com.talaty.dto.request.EKYCRequestDto;
import com.talaty.dto.response.EKYCResponseDto;
import com.talaty.model.EKYC;
import org.mapstruct.*;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING, uses = {DocumentMapper.class})
public interface EKYCMapper {
    EKYC toEntity(EKYCRequestDto EKYCRequestDto);
    EKYCResponseDto toDto(EKYC ekyc);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    EKYC partialUpdate(EKYCRequestDto EKYCRequestDto, @MappingTarget EKYC ekyc);
}
