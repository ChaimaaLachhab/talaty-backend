package com.talaty.service;

import com.talaty.model.User;
import com.talaty.repository.UserRepository;
import jakarta.mail.internet.MimeMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.util.Optional;


@Service
@Slf4j
public class EmailService {

    private final JavaMailSender mailSender;
    private final UserRepository userRepository;

    @Value("${spring.mail.username}")
    private String fromEmail;

    @Value("${app.frontend-url}")
    private String frontendUrl;

    @Autowired
    public EmailService(JavaMailSender mailSender, UserRepository userRepository) {
        this.mailSender = mailSender;
        this.userRepository = userRepository;
    }

    public void sendVerificationEmail(String toEmail, String verificationToken) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);

            helper.setFrom(fromEmail);
            helper.setTo(toEmail);
            helper.setSubject("Verify Your Email - Talaty Apply Now");

            String verificationUrl = frontendUrl + "/verify-email?token=" + verificationToken;
            String htmlContent = buildVerificationEmailContent(verificationUrl);

            helper.setText(htmlContent, true);

            mailSender.send(message);
            log.info("Verification email sent to: {}", toEmail);

        } catch (Exception e) {
            log.error("Failed to send verification email to: {}", toEmail, e);
            throw new RuntimeException("Failed to send verification email", e);
        }
    }

    public void sendOTPEmail(String toEmail, String otpCode) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);

            helper.setFrom(fromEmail);
            helper.setTo(toEmail);
            helper.setSubject("Your OTP Code - Talaty Apply Now");

            String htmlContent = buildOTPEmailContent(otpCode);
            helper.setText(htmlContent, true);

            mailSender.send(message);
            log.info("OTP email sent to: {}", toEmail);

        } catch (Exception e) {
            log.error("Failed to send OTP email to: {}", toEmail, e);
            throw new RuntimeException("Failed to send OTP email", e);
        }
    }

    public void sendNotificationEmail(Long userId, String title, String message) {
        try {
            Optional<User> userOpt = userRepository.findById(userId);
            if (userOpt.isEmpty()) {
                log.warn("User not found for notification email: {}", userId);
                return;
            }

            User user = userOpt.get();

            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);

            helper.setFrom(fromEmail);
            helper.setTo(user.getEmail());
            helper.setSubject(title + " - Talaty Apply Now");

            String htmlContent = buildNotificationEmailContent(user.getFullName(), title, message);
            helper.setText(htmlContent, true);

            mailSender.send(mimeMessage);
            log.info("Notification email sent to: {}", user.getEmail());

        } catch (Exception e) {
            log.error("Failed to send notification email to user: {}", userId, e);
            throw new RuntimeException("Failed to send notification email", e);
        }
    }

    private String buildVerificationEmailContent(String verificationUrl) {
        return """
            <!DOCTYPE html>
            <html>
            <head>
                <meta charset="UTF-8">
                <title>Email Verification</title>
                <style>
                    body { font-family: Arial, sans-serif; line-height: 1.6; color: #333; }
                    .container { max-width: 600px; margin: 0 auto; padding: 20px; }
                    .header { background-color: #007bff; color: white; padding: 20px; text-align: center; }
                    .content { padding: 30px; background-color: #f9f9f9; }
                    .button { display: inline-block; padding: 12px 24px; background-color: #007bff; color: white; text-decoration: none; border-radius: 5px; margin: 20px 0; }
                    .footer { text-align: center; padding: 20px; color: #666; font-size: 12px; }
                </style>
            </head>
            <body>
                <div class="container">
                    <div class="header">
                        <h1>Welcome to Talaty Apply Now!</h1>
                    </div>
                    <div class="content">
                        <h2>Verify Your Email Address</h2>
                        <p>Thank you for registering with Talaty Apply Now. To complete your registration, please verify your email address by clicking the button below:</p>
                        <a href="%s" class="button">Verify Email Address</a>
                        <p>If the button doesn't work, copy and paste this link into your browser:</p>
                        <p><a href="%s">%s</a></p>
                        <p>This verification link will expire in 24 hours.</p>
                        <p>If you didn't create an account with us, please ignore this email.</p>
                    </div>
                    <div class="footer">
                        <p>&copy; 2025 Talaty Apply Now. All rights reserved.</p>
                    </div>
                </div>
            </body>
            </html>
            """.formatted(verificationUrl, verificationUrl, verificationUrl);
    }

    private String buildOTPEmailContent(String otpCode) {
        return """
            <!DOCTYPE html>
            <html>
            <head>
                <meta charset="UTF-8">
                <title>Your OTP Code</title>
                <style>
                    body { font-family: Arial, sans-serif; line-height: 1.6; color: #333; }
                    .container { max-width: 600px; margin: 0 auto; padding: 20px; }
                    .header { background-color: #28a745; color: white; padding: 20px; text-align: center; }
                    .content { padding: 30px; background-color: #f9f9f9; text-align: center; }
                    .otp-code { font-size: 36px; font-weight: bold; color: #28a745; letter-spacing: 5px; margin: 20px 0; padding: 20px; background-color: white; border: 2px dashed #28a745; }
                    .footer { text-align: center; padding: 20px; color: #666; font-size: 12px; }
                </style>
            </head>
            <body>
                <div class="container">
                    <div class="header">
                        <h1>Your OTP Code</h1>
                    </div>
                    <div class="content">
                        <h2>One-Time Password</h2>
                        <p>Use this OTP code to complete your login:</p>
                        <div class="otp-code">%s</div>
                        <p><strong>This code will expire in 5 minutes.</strong></p>
                        <p>If you didn't request this code, please ignore this email or contact support if you have concerns.</p>
                    </div>
                    <div class="footer">
                        <p>&copy; 2025 Talaty Apply Now. All rights reserved.</p>
                    </div>
                </div>
            </body>
            </html>
            """.formatted(otpCode);
    }

    private String buildNotificationEmailContent(String userName, String title, String message) {
        return """
            <!DOCTYPE html>
            <html>
            <head>
                <meta charset="UTF-8">
                <title>%s</title>
                <style>
                    body { font-family: Arial, sans-serif; line-height: 1.6; color: #333; }
                    .container { max-width: 600px; margin: 0 auto; padding: 20px; }
                    .header { background-color: #17a2b8; color: white; padding: 20px; text-align: center; }
                    .content { padding: 30px; background-color: #f9f9f9; }
                    .footer { text-align: center; padding: 20px; color: #666; font-size: 12px; }
                </style>
            </head>
            <body>
                <div class="container">
                    <div class="header">
                        <h1>%s</h1>
                    </div>
                    <div class="content">
                        <p>Dear %s,</p>
                        <p>%s</p>
                        <p>If you have any questions, please don't hesitate to contact our support team.</p>
                        <p>Best regards,<br>The Talaty Apply Now Team</p>
                    </div>
                    <div class="footer">
                        <p>&copy; 2025 Talaty Apply Now. All rights reserved.</p>
                    </div>
                </div>
            </body>
            </html>
            """.formatted(title, title, userName, message);
    }
}
