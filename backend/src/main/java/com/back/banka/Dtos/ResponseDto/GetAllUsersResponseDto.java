package com.back.banka.Dtos.ResponseDto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GetAllUsersResponseDto {
    private Long userId;
    private String name;
    private String country;
    private String DNI;
    private boolean status;
    private String email;
    private String birthday;
}
