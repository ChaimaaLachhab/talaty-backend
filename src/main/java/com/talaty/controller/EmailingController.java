package com.talaty.controller;

import com.talaty.dto.request.MailRequest;
import com.talaty.service.EmailService;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/mail")
public class EmailingController {
    private final EmailService emailingService;
    
//    @PostMapping
//    @ResponseStatus(HttpStatus.OK)
//    public void sendMail(@RequestBody MailRequest request) throws MessagingException {
//        emailingService.sendMail(request);
//    }
}