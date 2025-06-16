package com.talaty.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PhoneVerificationDto {
    @NotBlank(message = "Le numéro de téléphone est obligatoire")
    @Pattern(regexp = "^\\+212[5-7][0-9]{8}$", message = "Format de numéro marocain invalide")
    private String phone;
}
