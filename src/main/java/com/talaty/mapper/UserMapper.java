package com.talaty.mapper;

import com.talaty.dto.request.AdminRequestDto;
import com.talaty.dto.request.CustomerRequestDto;
import com.talaty.dto.response.AdminResponseDto;
import com.talaty.dto.response.CustomerResponseDto;
import com.talaty.model.*;
import org.mapstruct.*;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING)
public interface UserMapper {

    // Admin Mappings
    AdminResponseDto toAdminResponseDto(Admin admin);
    Admin toAdminEntity(AdminRequestDto dto);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    Admin partialUpdateAdmin(AdminRequestDto adminRequestDto, @MappingTarget Admin admin);

    // Customer Mappings
    CustomerResponseDto toCustomerResponseDto(Customer customer);
    Customer toCustomerEntity(CustomerRequestDto dto);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    Customer partialUpdateCustomer(CustomerRequestDto customerRequestDto, @MappingTarget Customer customer);
}
