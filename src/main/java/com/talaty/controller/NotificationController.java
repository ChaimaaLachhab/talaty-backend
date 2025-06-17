package com.talaty.controller;

import com.talaty.dto.ApiResponse;
import com.talaty.dto.response.NotificationResponseDto;
import com.talaty.model.User;
import com.talaty.service.NotificationService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notifications")
@PreAuthorize("hasRole('USER')")
public class NotificationController {

    private final NotificationService notificationService;

    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<NotificationResponseDto>>> getMyNotifications(
            Authentication authentication,
            @RequestParam(value = "unreadOnly", defaultValue = "false") boolean unreadOnly) {

        Long userId = ((User) authentication.getPrincipal()).getId();
        ApiResponse<List<NotificationResponseDto>> response = notificationService.getUserNotifications(userId, unreadOnly);

        return response.isSuccess()
                ? ResponseEntity.ok(response)
                : ResponseEntity.badRequest().body(response);
    }

    @GetMapping("/unread")
    public ResponseEntity<ApiResponse<List<NotificationResponseDto>>> getUnreadNotifications(Authentication authentication) {
        Long userId = ((User) authentication.getPrincipal()).getId();
        ApiResponse<List<NotificationResponseDto>> response = notificationService.getUserNotifications(userId, true);

        return response.isSuccess()
                ? ResponseEntity.ok(response)
                : ResponseEntity.badRequest().body(response);
    }

    @PutMapping("/{notificationId}/read")
    public ResponseEntity<ApiResponse<String>> markAsRead(
            Authentication authentication,
            @PathVariable Long notificationId) {

        Long userId = ((User) authentication.getPrincipal()).getId();
        ApiResponse<String> response = notificationService.markAsRead(notificationId, userId);

        return response.isSuccess()
                ? ResponseEntity.ok(response)
                : ResponseEntity.badRequest().body(response);
    }

    @PutMapping("/mark-all-read")
    public ResponseEntity<ApiResponse<String>> markAllAsRead(Authentication authentication) {
        Long userId = ((User) authentication.getPrincipal()).getId();
        ApiResponse<String> response = notificationService.markAllAsRead(userId);

        return response.isSuccess()
                ? ResponseEntity.ok(response)
                : ResponseEntity.badRequest().body(response);
    }

    @GetMapping("/unread/count")
    public ResponseEntity<ApiResponse<Long>> getUnreadCount(Authentication authentication) {
        Long userId = ((User) authentication.getPrincipal()).getId();
        ApiResponse<Long> response = notificationService.getUnreadCount(userId);

        return response.isSuccess()
                ? ResponseEntity.ok(response)
                : ResponseEntity.badRequest().body(response);
    }
}
