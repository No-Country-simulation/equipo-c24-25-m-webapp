package com.back.banka.Dtos.ResponseDto;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@Data


@Builder
public class LoginResponseDto {
    private final String token;
}
