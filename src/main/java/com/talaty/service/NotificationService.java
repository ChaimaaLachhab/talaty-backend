package com.talaty.service;

import com.talaty.dto.ApiResponse;
import com.talaty.dto.response.NotificationResponseDto;
import com.talaty.mapper.NotificationMapper;
import com.talaty.mapper.UserMapper;
import com.talaty.model.Notification;
import com.talaty.repository.NotificationRepository;
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
public class NotificationService {

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private EmailService emailService;

    @Autowired
    private NotificationMapper notificationMapper;

    public ApiResponse<NotificationResponseDto> createNotification(Long userId, String title, String message, String type) {
        try {
            Notification notification = new Notification();
            notification.setUserId(userId);
            notification.setTitle(title);
            notification.setMessage(message);
            notification.setType(type);

            notification = notificationRepository.save(notification);

            // Send email if type is EMAIL
            if ("EMAIL".equals(type)) {
                try {
                    emailService.sendNotificationEmail(userId, title, message);
                    notification.setSent(true);
                    notification.setSentAt(LocalDateTime.now());
                    notification = notificationRepository.save(notification);
                } catch (Exception e) {
                    log.error("Failed to send email notification", e);
                }
            }

            NotificationResponseDto response = notificationMapper.toDto(notification);
            return ApiResponse.success(response, "Notification created successfully");

        } catch (Exception e) {
            log.error("Notification creation failed", e);
            return ApiResponse.error("Notification creation failed: " + e.getMessage());
        }
    }

    public ApiResponse<List<NotificationResponseDto>> getUserNotifications(Long userId, boolean unreadOnly) {
        try {
            List<Notification> notifications;
            if (unreadOnly) {
                notifications = notificationRepository.findByUserIdAndReadFalseOrderByCreatedAtDesc(userId);
            } else {
                notifications = notificationRepository.findByUserIdOrderByCreatedAtDesc(userId);
            }

            List<NotificationResponseDto> responses = notifications.stream()
                    .map(notificationMapper::toDto)
                    .collect(Collectors.toList());

            return ApiResponse.success(responses);

        } catch (Exception e) {
            log.error("Failed to get notifications", e);
            return ApiResponse.error("Failed to get notifications: " + e.getMessage());
        }
    }

    public ApiResponse<String> markAsRead(Long notificationId, Long userId) {
        try {
            Optional<Notification> notificationOpt = notificationRepository.findById(notificationId);
            if (notificationOpt.isEmpty()) {
                return ApiResponse.error("Notification not found");
            }

            Notification notification = notificationOpt.get();
            if (!notification.getUserId().equals(userId)) {
                return ApiResponse.error("Unauthorized to access this notification");
            }

            notification.setRead(true);
            notification.setReadAt(LocalDateTime.now());
            notificationRepository.save(notification);

            return ApiResponse.success("Notification marked as read");

        } catch (Exception e) {
            log.error("Failed to mark notification as read", e);
            return ApiResponse.error("Failed to mark notification as read: " + e.getMessage());
        }
    }

    public ApiResponse<String> markAllAsRead(Long userId) {
        try {
            notificationRepository.markAllAsReadByUserId(userId, LocalDateTime.now());
            return ApiResponse.success("All notifications marked as read");

        } catch (Exception e) {
            log.error("Failed to mark all notifications as read", e);
            return ApiResponse.error("Failed to mark all notifications as read: " + e.getMessage());
        }
    }

    public ApiResponse<Long> getUnreadCount(Long userId) {
        try {
            Long count = notificationRepository.countUnreadByUserId(userId);
            return ApiResponse.success(count);

        } catch (Exception e) {
            log.error("Failed to get unread count", e);
            return ApiResponse.error("Failed to get unread count: " + e.getMessage());
        }
    }
}
