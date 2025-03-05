package com.back.banka.Dtos.ResponseDto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class ProfileResponseDto {
    private String username;
    private String name;
    private String birthday;
    private String country;
    private String role;
    private int age;
    private String DNI;
}
