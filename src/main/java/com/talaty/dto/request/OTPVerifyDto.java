package com.talaty.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OTPVerifyDto {
    @NotBlank(message = "Le numéro de téléphone est requis")
    private String phone;

    @NotBlank(message = "Le code OTP est requis")
    @Size(min = 6, max = 6, message = "Le code OTP doit contenir 6 chiffres")
    private String otpCode;
}
