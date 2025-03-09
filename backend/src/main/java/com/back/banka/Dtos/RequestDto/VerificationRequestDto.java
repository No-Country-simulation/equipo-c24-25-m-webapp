package com.back.banka.Dtos.RequestDto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
@Builder
@AllArgsConstructor
public class VerificationRequestDto {
    private String purpose;
    private Map<String, Object> referenceData;
}
