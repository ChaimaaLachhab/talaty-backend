package com.talaty.mapper;

import com.talaty.dto.request.DocumentUploadDto;
import com.talaty.dto.response.DocumentResponseDto;
import com.talaty.model.Document;
import org.mapstruct.*;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING, uses = {MediaMapper.class})
public interface DocumentMapper {
    Document toEntity(DocumentUploadDto documentUploadDto);
    DocumentResponseDto toDto(Document document);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    Document partialUpdate(DocumentUploadDto documentUploadDto, @MappingTarget Document document);
}
