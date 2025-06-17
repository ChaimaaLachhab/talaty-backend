//package com.talaty;
//
//import com.talaty.dto.request.EKYCRequestDto;
//import com.talaty.dto.request.OTPRequestDto;
//import org.junit.jupiter.api.Order;
//import org.junit.jupiter.api.Test;
//import org.springframework.boot.test.context.SpringBootTest;
//import com.talaty.dto.ApiResponse;
//import com.talaty.dto.request.OTPVerifyDto;
//import com.talaty.model.User;
//import com.talaty.repository.UserRepository;
//import org.junit.jupiter.api.MethodOrderer;
//import org.junit.jupiter.api.TestMethodOrder;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.web.client.TestRestTemplate;
//import org.springframework.http.HttpEntity;
//import org.springframework.http.HttpHeaders;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import com.talaty.dto.LoginRequest;
//
//
//import java.util.Map;
//
//import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
//
//@SpringBootTest
//@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
//class EKYCIntegrationTest {
//
//    @Autowired
//    private TestRestTemplate restTemplate;
//
//    @Autowired
//    private UserRepository userRepository;
//
//    private String jwtToken;
//
//    @Test
//    @Order(1)
//    void testLogin() {
//        LoginRequest request = new LoginRequest("test@talaty.ma", "test123");
//
//        ResponseEntity<ApiResponse> response = restTemplate.postForEntity(
//                "/api/auth/login", request, ApiResponse.class);
//
//        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
//        assertThat(response.getBody().isSuccess()).isTrue();
//
//        // Extraire le token pour les tests suivants
//        Map<String, Object> data = (Map<String, Object>) response.getBody().getData();
//        this.jwtToken = (String) data.get("token");
//    }
//
//    @Test
//    @Order(2)
//    void testCreateEKYC() {
//        HttpHeaders headers = new HttpHeaders();
//        headers.setBearerAuth(jwtToken);
//
//        EKYCRequestDto request = new EKYCRequestDto();
//        request.setCompanyName("Test Company");
//        request.setBusinessSector("Test");
//        request.setBusinessPurpose("Testing");
//
//        HttpEntity<EKYCRequestDto> entity = new HttpEntity<>(request, headers);
//
//        ResponseEntity<ApiResponse> response = restTemplate.postForEntity(
//                "/api/ekyc", entity, ApiResponse.class);
//
//        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
//        assertThat(response.getBody().isSuccess()).isTrue();
//    }
//
//    @Test
//    @Order(3)
//    void testOTPFlow() {
//        // Test envoi OTP
//        OTPRequestDto otpRequest = new OTPRequestDto("+212665858585");
//
//        ResponseEntity<ApiResponse> sendResponse = restTemplate.postForEntity(
//                "/api/auth/otp/send", otpRequest, ApiResponse.class);
//
//        assertThat(sendResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
//
//        // Récupérer le code OTP depuis la base (en test)
//        User user = userRepository.findByPhone("+212665858585").orElseThrow();
//        String otpCode = user.getOtpCode();
//
//        // Test vérification OTP
//        OTPVerifyDto verifyRequest = new OTPVerifyDto("+212665858585", otpCode);
//
//        ResponseEntity<ApiResponse> verifyResponse = restTemplate.postForEntity(
//                "/api/auth/otp/verify", verifyRequest, ApiResponse.class);
//
//        assertThat(verifyResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
//        assertThat(verifyResponse.getBody().isSuccess()).isTrue();
//    }
//}
