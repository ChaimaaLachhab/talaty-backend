package com.talaty.mapper;

import com.talaty.dto.request.EKYCUpdateDto;
import com.talaty.dto.response.EKYCResponseDto;
import com.talaty.model.EKYC;
import org.mapstruct.*;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING)
public interface EKYCMapper {
    EKYC toEntity(EKYCUpdateDto EKYCUpdateDto);
    EKYCResponseDto toDto(EKYC ekyc);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    EKYC partialUpdate(EKYCUpdateDto EKYCUpdateDto, @MappingTarget EKYC ekyc);
}
