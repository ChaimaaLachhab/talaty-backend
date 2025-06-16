package com.talaty.mapper;

import com.talaty.dto.request.MediaRequestDto;
import com.talaty.dto.response.MediaResponseDto;
import com.talaty.model.Media;
import org.mapstruct.*;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING, uses = {DocumentMapper.class})
public interface MediaMapper {
    Media toEntity(MediaRequestDto mediaRequestDto);
    MediaResponseDto toDto(Media media);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    Media partialUpdate(MediaRequestDto mediaRequestDto, @MappingTarget Media media);
}
