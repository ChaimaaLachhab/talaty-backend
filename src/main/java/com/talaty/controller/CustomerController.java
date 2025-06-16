package com.talaty.controller;

import com.talaty.dto.request.CustomerRequestDto;
import com.talaty.dto.response.CustomerResponseDto;
import com.talaty.mapper.UserMapper;
import com.talaty.model.Customer;
import com.talaty.service.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Controller for managing artisans.
 */
@RestController
@RequestMapping("/api/customers")
public class CustomerController {

    private final CustomerService customerService;
    private final UserMapper userMapper;

    @Autowired
    public CustomerController(CustomerService customerService, UserMapper userMapper) {
        this.customerService = customerService;
        this.userMapper = userMapper;
    }

    /**
     * Retrieve all artisans.
     *
     * @return List of all artisans.
     */
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/all")
    public ResponseEntity<List<CustomerResponseDto>> getAllCustomers() {
        List<Customer> customers = customerService.getAllCustomers();
        List<CustomerResponseDto> customerResponseDtos = customers.stream()
                .map(userMapper::toCustomerResponseDto)
                .collect(Collectors.toList());
        return new ResponseEntity<>(customerResponseDtos, HttpStatus.OK);
    }

    /**
     * Retrieve an Customer by ID.
     *
     * @return The Customer with the specified ID.
     */
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @GetMapping("/details")
    public ResponseEntity<CustomerResponseDto> getCustomerById(@AuthenticationPrincipal Customer currentCustomer) {
        Customer customer = customerService.getCustomerById(currentCustomer.getId());
        return customer != null
                ? new ResponseEntity<>(userMapper.toCustomerResponseDto(customer), HttpStatus.OK)
                : new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    /**
     * Update an existing Customer.
     *
     * @param artisanDTO The artisan with updated information.
     * @param userPhoto  The photo to be updated, if any.
     * @param customer    The authenticated Customer.
     * @return The updated Customer.
     */
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @PutMapping(value = "/update", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<CustomerResponseDto> updateCustomer(
            @RequestPart("artisan") CustomerRequestDto artisanDTO,
            @RequestPart(value = "userPhoto", required = false) MultipartFile userPhoto,
            @AuthenticationPrincipal Customer customer) {

        Customer updatedCustomer = customerService.updateCustomer(artisanDTO, customer, userPhoto);
        return new ResponseEntity<>(userMapper.toCustomerResponseDto(updatedCustomer), HttpStatus.OK);
    }
}
