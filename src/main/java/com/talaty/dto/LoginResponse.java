package com.talaty.dto;

import com.talaty.enums.ApplicationStatus;
import com.talaty.model.Media;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class LoginResponse {
    private String token;
    private long expirationTime;
    private String role;
    private Long userId;
    private String fullName;
    private boolean verified;
    private boolean phoneVerified;
    private boolean emailVerified;
    private Double profileCompletion;
    private List<String> nextSteps;

    public LoginResponse(String token, long expirationTime, String role, Long userId,
                         String fullName, boolean verified, boolean phoneVerified,
                         boolean emailVerified, Double profileCompletion) {
        this.token = token;
        this.expirationTime = expirationTime;
        this.role = role;
        this.userId = userId;
        this.fullName = fullName;
        this.verified = verified;
        this.phoneVerified = phoneVerified;
        this.emailVerified = emailVerified;
        this.profileCompletion = profileCompletion;
        this.nextSteps = new ArrayList<>();
    }
}
