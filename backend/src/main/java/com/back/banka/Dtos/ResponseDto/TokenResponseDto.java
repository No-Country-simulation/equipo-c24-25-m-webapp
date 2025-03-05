package com.back.banka.Dtos.ResponseDto;

import com.back.banka.Model.User;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@Builder
public class TokenResponseDto {
    @JsonProperty(value = "access_token")
    private  String token;
    @JsonProperty(value = "refresh_token")
    private String refreshToken;



}
