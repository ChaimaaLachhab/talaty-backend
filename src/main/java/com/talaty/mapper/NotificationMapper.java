package com.talaty.mapper;

import com.talaty.dto.response.NotificationResponseDto;
import com.talaty.model.Notification;
import org.mapstruct.*;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING, uses = {EKYCMapper.class})
public interface NotificationMapper {
    NotificationResponseDto toDto(Notification notification);
}
