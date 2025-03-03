package com.back.banka.Services.Impl;

import com.back.banka.Dtos.RequestDto.RegisterRequestDto;
import com.back.banka.Dtos.ResponseDto.RegisterResponseDto;
import com.back.banka.Enums.Role;
import com.back.banka.Exceptions.Custom.DniAlreadyExistsException;
import com.back.banka.Exceptions.Custom.UserAlreadyExistsException;
import com.back.banka.Model.User;
import com.back.banka.Repository.UserRepository;
import com.back.banka.Services.IServices.IRegisterService;
import lombok.AllArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;

@AllArgsConstructor
@Service
public class RegisterServiceImpl implements IRegisterService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailServiceImpl emailService;

    @Transactional
    @Override
    //Registra un usuario solo si el correo aún no está registrado.
    public RegisterResponseDto registerUser(RegisterRequestDto request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new UserAlreadyExistsException("Correo ya registrado. Intenta iniciar sesión o ingresa un correo distinto.");
        }
        if (userRepository.existsByDNI(request.getDNI())) {
            throw new DniAlreadyExistsException("El DNI ya está registrado");
        }

        User user = User.builder()
                .name(request.getName())
                .age(request.getAge())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .country(request.getCountry())
                .DNI(request.getDNI())
                .status(true)
                .role(Role.CLIENT)
                .build();

        User savedUser = userRepository.save(user);

        Map<String, Object> emailVariables = new HashMap<>();
        emailVariables.put("name", savedUser.getName());
        emailVariables.put("message", "Te has registrado con exito");

        try {
            emailService.sendEmailTemplate(
                    savedUser.getEmail(),
                    "¡Bienvenido a Luma!", "register-confirmation",
                    emailVariables);
        } catch (Exception e) {
            System.out.println("Error al enviar el correo: " + e.getMessage());
        }

        return RegisterResponseDto.builder()
                .message("¡Registro Exitoso!")
                .userId(savedUser.getId())
                .build();
    }

}