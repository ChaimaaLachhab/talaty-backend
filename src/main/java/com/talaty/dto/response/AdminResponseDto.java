package com.talaty.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.talaty.model.Media;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AdminResponseDto {
    private Long id;
    private String firstName;
    private String lastName;
    private String fullName;
    private String username;
    private String email;
    private String phone;
    private boolean verified;
    private boolean enabled;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private MediaResponseDto userPhoto;
}
