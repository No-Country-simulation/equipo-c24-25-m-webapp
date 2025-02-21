package com.back.banka.Controllers;


import com.back.banka.Dtos.RequestDto.LoginRequestDto;
import com.back.banka.Dtos.RequestDto.RegisterRequestDto;
import com.back.banka.Dtos.ResponseDto.LoginResponseDto;
import com.back.banka.Dtos.ResponseDto.RegisterResponseDto;
import com.back.banka.Services.IServices.IRegisterService;
import com.back.banka.Services.IServices.IUserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Data
@RestController
@RequestMapping("/api/banca/auth")
public class AuthController {

    private final IUserService userService;
    private final IRegisterService registerService;



    @Operation(summary = "Autenticar usuario", description = "Autentica un usuario con email y contraseña")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Autenticación exitosa"),
            @ApiResponse(responseCode = "401", description = "Credenciales inválidas"),
            @ApiResponse(responseCode = "403", description = "Acceso denegado"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @PostMapping("/login")
    public ResponseEntity<LoginResponseDto> authenticateUser (@Valid  @RequestBody LoginRequestDto requestDto){
        LoginResponseDto loginResponseDto = this.userService.authenticate(requestDto);
        return ResponseEntity.status(HttpStatus.OK).body(loginResponseDto);
    }



    //Request para registrar nuevo usuario
    @Operation(summary = "Registrar usuario", description = "Registra un nuevo usuario en la aplicación")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Usuario registrado exitosamente"),
            @ApiResponse(responseCode = "400", description = "Solicitud incorrecta (datos inválidos)"),
            @ApiResponse(responseCode = "409", description = "El usuario ya está registrado"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @PostMapping("/registrarse")
    public ResponseEntity<RegisterResponseDto> register(@RequestBody RegisterRequestDto request){
        RegisterResponseDto response = registerService.registerUser(request);
        return ResponseEntity.ok(response);
    }


}
