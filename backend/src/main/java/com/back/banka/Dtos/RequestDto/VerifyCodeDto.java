package com.back.banka.Dtos.RequestDto;

import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class VerifyCodeDto {
    private String code;
}
