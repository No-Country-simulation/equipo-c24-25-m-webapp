package com.back.banka.Dtos.ResponseDto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class RegisterResponseDto {
    private String message;
    private Long userId;
    @JsonProperty(value = "access_token")
    private  String accesToken;
    @JsonProperty(value = "refresh_token")
    private String refreshToken;
}
