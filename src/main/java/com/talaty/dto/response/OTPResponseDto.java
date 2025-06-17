package com.talaty.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OTPResponseDto {
    private String message;
    private boolean success;
    private LocalDateTime expiryTime;
}
