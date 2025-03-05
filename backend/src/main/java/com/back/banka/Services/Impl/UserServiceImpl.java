package com.back.banka.Services.Impl;
import com.back.banka.Dtos.RequestDto.ResetPasswordRequestDto;
import com.back.banka.Dtos.RequestDto.UpdateUserRequestDto;
import com.back.banka.Dtos.ResponseDto.GeneralResponseDto;
import com.back.banka.Dtos.ResponseDto.GetAllUsersResponseDto;
import com.back.banka.Dtos.ResponseDto.ProfileResponseDto;
import com.back.banka.Dtos.ResponseDto.UpdateUserResponseDto;
import com.back.banka.Exceptions.Custom.CustomAuthenticationException;
import com.back.banka.Exceptions.Custom.DniAlreadyExistsException;
import com.back.banka.Exceptions.Custom.ModelNotFoundException;
import com.back.banka.Exceptions.Custom.UserNotFoundException;
import com.back.banka.Model.AccountBank;
import com.back.banka.Model.User;
import com.back.banka.Repository.IAccountBankRepository;
import com.back.banka.Repository.UserRepository;
import com.back.banka.Services.IServices.IEmailService;
import com.back.banka.Services.IServices.IUserService;
import com.back.banka.Utils.IUtilsService;
import com.back.banka.Utils.JwtUtil;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
public class UserServiceImpl implements IUserService {
    private final UserRepository   userRepository;
    private  final IUtilsService utilsService;
    private final IEmailService emailService;
    private final JwtUtil jwtUtil;
    private final IAccountBankRepository accountBankRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserServiceImpl(UserRepository userRepository,
                           IUtilsService utilsService, IEmailService emailService,
                           JwtUtil jwtUtil, IAccountBankRepository accountBankRepository,
                           PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.utilsService = utilsService;
        this.emailService = emailService;
        this.jwtUtil = jwtUtil;
        this.accountBankRepository = accountBankRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     MEtodo para enviar correo para que el usaurio pueda cambiar su contraseña
     el metodo recibe el username del usuario y envia una url al usuario para que pueda resetear su contraseña
     @param email
     **/
    @Override
    public void sendPasswordResetEmail(String email) {
        try {
            User user = this.userRepository.findByEmail(email)
                    .orElseThrow(() -> new UserNotFoundException("Usuario no encontrado"));

            String tokenJwt = this.jwtUtil.generateResetPasswordToken(email);
            String resetUrl = "https://equipo-c24-25-m-webapp-1.onrender.com/recuperar-contrasenia?token=" + tokenJwt;

            this.utilsService.saveUserToken(user, tokenJwt);

            Map<String, Object> emailVariables = new HashMap<>();
            emailVariables.put("name", user.getName());
            emailVariables.put("message", "Hola, " +
                    "a través de este correo podrás configurar tu contraseña.");
            emailVariables.put("resetUrl", resetUrl);
            try {
                this.emailService.sendEmailTemplate(
                        user.getEmail(),
                        "Hola, a través de este correo podrás configurar tu contraseña.",
                        "reset-password",
                        emailVariables
                );
            } catch (Exception e) {
                log.error("Error al enviar el correo de restablecimiento de contraseña a {}: {}", email, e.getMessage(), e);
            }

        } catch (DataAccessException e) {
            log.error("Error de base de datos al procesar la solicitud de restablecimiento de contraseña: {}", e.getMessage(), e);
            throw new RuntimeException("Error al procesar la solicitud. Intente más tarde.");
        }
    }

    /**
MEtodo para actualizar la contraseña de un usuario en la base de datos.
 Valida que el token sea enviado y lo compara con el que esta guardado en la base de datos
 si el token es valido cambia la contraseña
 @param requestDto
 @return string
**/
    @Transactional
    @Override
    public GeneralResponseDto resetPassword(ResetPasswordRequestDto requestDto) {
        try {
            log.info("Iniciando resetPassword para el token: " + requestDto.getToken());

            String username = jwtUtil.extractEmail(requestDto.getToken());
            if (username == null || username.isEmpty()) {
                log.error("El email extraído es nulo o vacío");

                throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Token inválido");
            }
            log.info("Email extraído: " + username);

            if (requestDto.getToken() == null || !jwtUtil.validateToken(requestDto.getToken())) {
                log.error("Token inválido");

                throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Token inválido");
            }

            User user = userRepository.findByEmail(username).orElseThrow(() -> {
                log.error("Usuario no encontrado para el email: " + username);

                return new UsernameNotFoundException("Usuario no encontrado");
            });
            log.info("Usuario encontrado: " + user.getEmail());

            user.setPassword(passwordEncoder.encode(requestDto.getNewPassword()));
            this.userRepository.save(user);
            log.info("Contraseña actualizada correctamente");

            try {
                this.utilsService.sendAccountNotification(
                        user,
                        "Cambio de contraseña",
                        "email-template",
                        "Contraseña tu contraseña ha sido actualizada con exito" +
                                "puedes entrar a tu aplicacion"
                );
            } catch (Exception e) {
                log.error("Error al enviar correo", e);
            }

            return GeneralResponseDto.builder()
                    .message("Contraseña actualizada con exito")
                    .build();
        } catch (Exception e) {
            log.error("Error en resetPassword", e);
            throw e;
        }
    }


    @Transactional(readOnly = true)
    @Override
    public List<GetAllUsersResponseDto> getAllUsers() {
        try {
            List<User> allUsers = this.userRepository.findAll();

            return allUsers.stream()
                    .map(user -> GetAllUsersResponseDto.builder()
                            .userId(user.getId())
                            .name(user.getName())
                            .DNI(user.getDNI())
                            .birthday(String.valueOf(user.getDateBirthDay()))
                            .email(user.getEmail())
                            .country(user.getCountry())
                            .status(user.isStatus())
                            .build())
                    .collect(Collectors.toList());

        } catch (Exception e) {
            throw new RuntimeException("Error al traer usuarios", e);
        }
    }

/**
Método que permite a un usuario modificar su información personal.
 **/
    @Transactional
    @Override
    public UpdateUserResponseDto updateUser( UpdateUserRequestDto request){
        try{
            Long userID = this.utilsService.getAuthenticatedUserId();
            if(userID == null){
                throw new CustomAuthenticationException("Error: usuario no autenticaso");
            }


            User user = this.userRepository.findById(userID).orElseThrow(()
                    ->  new UserNotFoundException("usuario no encontrado"));

            if ( request.getEmail() != null && !request.getEmail().equals(user.getEmail()) &&
                    userRepository.existsByEmailAndIdNot(request.getEmail(), userID)) {
                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST, "El correo electrónico ya está registrado."
                );
            }
             if(!userID.equals(this.utilsService.getAuthenticatedUserId())){
                 throw new CustomAuthenticationException("Error: no esta autorizado para editar datos de este usuarop");
             }
            if (request.getDNI() !=null && !request.getDNI().equals(user.getDNI()) &&
                    userRepository.existsByDNIAndIdNot(request.getDNI(),userID)) {
                throw new DniAlreadyExistsException("El DNI ya está registrado.");
            }




        if(request.getName() != null)
        {user.setName(request.getName());
        }
        if(request.getDNI() != null){user.setDNI(request.getDNI());}
        if(request.getEmail() != null){user.setEmail(request.getEmail());}
            if (request.getDateBirthDay() != null) {
                user.setDateBirthDay(request.getDateBirthDay());
            }


            userRepository.save(user);

            return new UpdateUserResponseDto(
                    user.getName(),
                    user.getDNI(),
                    user.getEmail(),
                    user.getDateBirthDay());
        }catch (UserNotFoundException | DniAlreadyExistsException | ResponseStatusException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("Error interno del servidor", e);
        }
    }

    @Override
    public ProfileResponseDto profileUser() {

        Long userId = this.utilsService.getAuthenticatedUserId();

        if(userId == null){
            throw new CustomAuthenticationException("Usuario no esta autenticado");
        }
        if(!userId.equals(this.utilsService.getAuthenticatedUserId())){
            throw new CustomAuthenticationException("Error: no tiene permiso para ver este  perfil");
        }

        User user = this.userRepository.findById(userId).orElseThrow(()
                -> new UserNotFoundException("Usuario no encontrados"));


        return ProfileResponseDto.builder()
                .username(user.getEmail())
                .role(user.getRole().toString())
                .age(user.getAge())
                .DNI(user.getDNI())
                .name(user.getName())
                .country(user.getCountry())
                .birthday(user.getDateBirthDay() != null ? user.getDateBirthDay().toString(): "Dato no proporcionado")
                .build();
    }
}
