package com.talaty.controller;

import com.talaty.dto.ApiResponse;
import com.talaty.dto.response.NotificationResponseDto;
import com.talaty.model.User;
import com.talaty.service.NotificationService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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
            @AuthenticationPrincipal User user,
            @RequestParam(value = "unreadOnly", defaultValue = "false") boolean unreadOnly) {

        ApiResponse<List<NotificationResponseDto>> response = notificationService.getUserNotifications(user.getId(), unreadOnly);

        return response.isSuccess()
                ? ResponseEntity.ok(response)
                : ResponseEntity.badRequest().body(response);
    }

    @GetMapping("/unread")
    public ResponseEntity<ApiResponse<List<NotificationResponseDto>>> getUnreadNotifications(@AuthenticationPrincipal User user) {
        ApiResponse<List<NotificationResponseDto>> response = notificationService.getUserNotifications(user.getId(), true);

        return response.isSuccess()
                ? ResponseEntity.ok(response)
                : ResponseEntity.badRequest().body(response);
    }

    @PutMapping("/{notificationId}/read")
    public ResponseEntity<ApiResponse<String>> markAsRead(
            @AuthenticationPrincipal User user,
            @PathVariable Long notificationId) {

        ApiResponse<String> response = notificationService.markAsRead(notificationId, user.getId());

        return response.isSuccess()
                ? ResponseEntity.ok(response)
                : ResponseEntity.badRequest().body(response);
    }

    @PutMapping("/mark-all-read")
    public ResponseEntity<ApiResponse<String>> markAllAsRead(@AuthenticationPrincipal User user) {
        ApiResponse<String> response = notificationService.markAllAsRead(user.getId());

        return response.isSuccess()
                ? ResponseEntity.ok(response)
                : ResponseEntity.badRequest().body(response);
    }

    @GetMapping("/unread/count")
    public ResponseEntity<ApiResponse<Long>> getUnreadCount(@AuthenticationPrincipal User user) {
        ApiResponse<Long> response = notificationService.getUnreadCount(user.getId());

        return response.isSuccess()
                ? ResponseEntity.ok(response)
                : ResponseEntity.badRequest().body(response);
    }
}
