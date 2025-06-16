package com.talaty.dto.response;

import com.talaty.enums.MediaType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MediaResponseDto {
    private Long id;
    private String mediaUrl;
    private String originalFileName;
    private String mimeType;
    private Long fileSize;
    private MediaType mediaType;
    private LocalDateTime uploadedAt;
}
