package com.back.banka.Dtos.RequestDto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor

public class RegisterRequestDto {
        @NotBlank(message = "Campo obligatorio")
        private String name;
        @NotNull(message = "Campo obligatorio")
        private int age;
        @NotBlank(message = "Campo obligatorio")
        @Email(message = "Formato inválido")
        private String email;
        @NotBlank(message = "Campo obligatorio")
        private String password;
        @NotBlank(message = "Campo obligatorio")
        private String country;
        @NotBlank(message = "Campo obligatorio")
        private String DNI;

    }
