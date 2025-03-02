package com.back.banka.Dtos.RequestDto;
import jakarta.persistence.Column;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
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
    @Min(value = 18, message = "Debes ser mayor de 18 años para poder registrarte.")
    @Max(value = 120, message = "La edad máxima permitida es 120 años.")
    private int age;
    @NotBlank(message = "Campo obligatorio")
    @Email(message = "Formato inválido")
    private String email;
    @NotBlank(message = "Campo obligatorio")
    @Size(min = 8, max = 20, message = "La contraseña debe tener entre 8 y 20 caracteres")
    @Pattern(
            regexp = "^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$",
            message = "La contraseña debe contener al menos una mayúscula, una minúscula, un número y un carácter especial"
    )
    private String password;
    @NotBlank(message = "Campo obligatorio")
    private String country;
    @NotBlank(message = "Campo obligatorio")
    @Size(min = 8, max = 8, message = "El DNI debe tener exactamente 8 caracteres")
    @Pattern(regexp = "\\d{8}", message = "El DNI debe contener solo números")
    @Column(name = "dni", nullable = false, unique = true)
    private String DNI;

}
