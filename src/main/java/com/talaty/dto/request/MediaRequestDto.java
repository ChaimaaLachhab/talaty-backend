package com.talaty.dto.request;

import com.talaty.enums.MediaType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MediaRequestDto {
    private String mediaUrl;
    private String mediaId;
    private MediaType mediaType;
    private Long productId;
    private Long userId;
}
