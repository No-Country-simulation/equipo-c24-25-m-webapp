package com.back.banka.Dtos.ResponseDto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@AllArgsConstructor
public class VerificationCodeResponseDto {
    private Long verificationId;
    private LocalDateTime expiresAt;
    private String message;
}
