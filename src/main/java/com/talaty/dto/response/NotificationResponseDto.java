package com.talaty.dto.response;

import lombok.*;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NotificationResponseDto {
    private Long id;
    private String title;
    private String message;
    private String type;
    private boolean read;
    private LocalDateTime createdAt;
    private LocalDateTime readAt;
}
