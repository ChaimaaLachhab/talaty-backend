package com.talaty.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OTPRequestDto {
    @NotBlank(message = "Le numéro de téléphone est requis")
    @Pattern(regexp = "^\\+212[5-7][0-9]{8}$", message = "Format numéro marocain invalide")
    private String phone;
}
