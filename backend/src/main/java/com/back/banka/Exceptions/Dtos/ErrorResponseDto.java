package com.back.banka.Exceptions.Dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.Date;

@Builder
@Getter
@Setter
@AllArgsConstructor
public class ErrorResponseDto {
    private String message;
    private int code;
    private LocalDate dateCreation;
}
