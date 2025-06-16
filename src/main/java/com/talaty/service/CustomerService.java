package com.talaty.service;

import com.talaty.dto.request.CustomerRequestDto;
import com.talaty.mapper.UserMapper;
import com.talaty.model.Customer;
import com.talaty.model.Media;
import com.talaty.repository.CustomerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
public class CustomerService {

    private final CustomerRepository customerRepository;
    private final MediaService mediaService;
    private final UserMapper userMapper;

    @Autowired
    public CustomerService(CustomerRepository customerRepository, MediaService mediaService, UserMapper userMapper) {
        this.customerRepository = customerRepository;
        this.mediaService = mediaService;
        this.userMapper = userMapper;
    }

    public List<Customer> getAllCustomers() {
        return customerRepository.findAll();
    }

    public Customer getCustomerById(Long id) {
        return customerRepository.findById(id).orElse(null);
    }

    public void deleteCustomer(Long id) {
        customerRepository.deleteById(id);
    }

    public Customer updateCustomer(CustomerRequestDto customerDto, Customer customer, MultipartFile userPhoto) {
        Customer newCustomer = userMapper.partialUpdateCustomer(customerDto, customer);

        if (userPhoto != null && !userPhoto.isEmpty()) {
            Media media=  mediaService.updateMediaForUser(userPhoto, newCustomer);
            newCustomer.setUserPhoto(media);
            return customerRepository.save(newCustomer);
        }

        return customerRepository.save(newCustomer);
    }
}
