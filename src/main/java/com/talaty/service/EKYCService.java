package com.talaty.service;

import com.talaty.dto.ApiResponse;
import com.talaty.dto.request.EKYCUpdateDto;
import com.talaty.dto.response.EKYCResponseDto;
import com.talaty.enums.ApplicationStatus;
import com.talaty.mapper.EKYCMapper;
import com.talaty.model.Customer;
import com.talaty.model.Document;
import com.talaty.model.EKYC;
import com.talaty.repository.CustomerRepository;
import com.talaty.repository.DocumentRepository;
import com.talaty.repository.EKYCRepository;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
@Slf4j
public class EKYCService {

    @Autowired
    private EKYCRepository ekycRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private DocumentRepository documentRepository;

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private EKYCMapper EKYCMapper;

    public ApiResponse<EKYCResponseDto> submitEKYC(Long userId, EKYCUpdateDto request) {
        try {
            Optional<Customer> customerOpt = customerRepository.findById(userId);
            if (customerOpt.isEmpty()) {
                return ApiResponse.error("Customer not found");
            }

            Customer customer = customerOpt.get();

            // Check if user already has eKYC
            if (customer.getEkyc() != null) {
                return ApiResponse.error("eKYC already submitted for this user");
            }

            // Check for duplicate national ID
            if (request.getNationalId() != null &&
                    ekycRepository.findByNationalId(request.getNationalId()).isPresent()) {
                return ApiResponse.error("National ID already exists");
            }

            // Check for duplicate company registration number
            if (request.getCompanyRegistrationNumber() != null &&
                    ekycRepository.findByCompanyRegistrationNumber(request.getCompanyRegistrationNumber()).isPresent()) {
                return ApiResponse.error("Company registration number already exists");
            }

            EKYC ekyc = EKYCMapper.toEntity(request);

            ekyc.setStatus(ApplicationStatus.PENDING);
            ekyc.setSubmittedAt(LocalDateTime.now());

            ekyc = ekycRepository.save(ekyc);

            // Link eKYC to customer
            customer.setEkyc(ekyc);
            customerRepository.save(customer);

            // Calculate initial score
            calculateAndUpdateScore(ekyc.getId());

            // Create notification
            notificationService.createNotification(
                    userId,
                    "eKYC Submitted",
                    "Your eKYC application has been submitted and is under review.",
                    "EMAIL"
            );

            EKYCResponseDto response = EKYCMapper.toDto(ekyc);
            return ApiResponse.success("eKYC submitted successfully", response);

        } catch (Exception e) {
            log.error("eKYC submission failed", e);
            return ApiResponse.error("eKYC submission failed: " + e.getMessage());
        }
    }

    public ApiResponse<EKYCResponseDto> getEKYCByUserId(Long userId) {
        try {
            Optional<Customer> customerOpt = customerRepository.findById(userId);
            if (customerOpt.isEmpty()) {
                return ApiResponse.error("Customer not found");
            }

            Customer customer = customerOpt.get();
            if (customer.getEkyc() == null) {
                return ApiResponse.error("No eKYC found for this user");
            }

            EKYCResponseDto response = EKYCMapper.toDto(customer.getEkyc());
            return ApiResponse.success(response);

        } catch (Exception e) {
            log.error("Failed to get eKYC", e);
            return ApiResponse.error("Failed to get eKYC: " + e.getMessage());
        }
    }

    public ApiResponse<List<EKYCResponseDto>> getAllEKYCApplications(ApplicationStatus status) {
        try {
            List<EKYC> ekycs;
            if (status != null) {
                ekycs = ekycRepository.findByStatusOrderBySubmittedAtDesc(status);
            } else {
                ekycs = ekycRepository.findAll();
            }

            List<EKYCResponseDto> responses = ekycs.stream()
                    .map(EKYCMapper::toDto)
                    .collect(Collectors.toList());

            return ApiResponse.success(responses);

        } catch (Exception e) {
            log.error("Failed to get eKYC applications", e);
            return ApiResponse.error("Failed to get eKYC applications: " + e.getMessage());
        }
    }

    public void calculateAndUpdateScore(Long ekycId) {
        try {
            Optional<EKYC> ekycOpt = ekycRepository.findById(ekycId);
            if (ekycOpt.isEmpty()) return;

            EKYC ekyc = ekycOpt.get();
            int score = 0;

            // Basic information scoring (30 points) - reduced from 40
            if (ekyc.getNationalId() != null && !ekyc.getNationalId().trim().isEmpty()) score += 10;
            if (ekyc.getCompanyName() != null && !ekyc.getCompanyName().trim().isEmpty()) score += 10;
            if (ekyc.getAddress() != null && !ekyc.getAddress().trim().isEmpty()) score += 5;
            if (ekyc.getCompanyRegistrationNumber() != null && !ekyc.getCompanyRegistrationNumber().trim().isEmpty()) score += 5; // Reduced

            // Business information scoring (25 points) - reduced from 30
            if (ekyc.getCompanyEstablishedDate() != null) score += 9;

            // Financial information scoring (20 points) - unchanged
            if (ekyc.getAnnualRevenue() != null && ekyc.getAnnualRevenue() > 0) score += 10;
            if (ekyc.getNumberOfEmployees() != null && ekyc.getNumberOfEmployees() > 0) score += 10;

            // Document verification scoring (15 points) - increased from 10
            List<Document> documents = documentRepository.findByEkycId(ekycId);
            long verifiedDocs = documents.stream().filter(Document::isVerified).count();
            if (verifiedDocs > 0) {
                score += Math.min(15, (int)(verifiedDocs * 3)); // 3 points per verified document, max 15
            }
            
            ekyc.setScore(score);
            ekycRepository.save(ekyc);

        } catch (Exception e) {
            log.error("Failed to calculate score for eKYC: " + ekycId, e);
        }
    }
}
