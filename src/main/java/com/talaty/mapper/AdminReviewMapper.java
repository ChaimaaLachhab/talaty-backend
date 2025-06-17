package com.talaty.mapper;

import com.talaty.dto.request.AdminReviewDto;
import com.talaty.model.EKYC;
import org.mapstruct.*;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING)
public interface AdminReviewMapper {

    @Mapping(target = "status", source = "decision")
    @Mapping(target = "reviewComments", source = "comments")
    @Mapping(target = "score", source = "finalScore")
    @Mapping(target = "reviewedAt", expression = "java(java.time.LocalDateTime.now())")
    void updateEKYCFromReview(AdminReviewDto reviewDto, @MappingTarget EKYC ekyc);
}
