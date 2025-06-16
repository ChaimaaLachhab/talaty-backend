package com.talaty.dto;

import com.talaty.enums.ApplicationStatus;
import com.talaty.model.Media;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class LoginResponse {
    private String token;
    private long expiresIn;
    private String userType;
    private Long userId;
    private String fullName;
    private boolean verified;
    private boolean phoneVerified;
    private boolean emailVerified;
//    private boolean identityVerified;
    private Double profileCompletion;
//    private String nextOnboardingStep;
//    private boolean canApplyForCredit;
//    private ApplicationStatus applicationStatus;
}
