package com.back.banka.Controllers;

import com.back.banka.Dtos.RequestDto.ActiveAccountRequestDto;
import com.back.banka.Dtos.ResponseDto.ActiveAccountResponseDto;
import com.back.banka.Dtos.ResponseDto.DeactivateAccountResponseDto;
import com.back.banka.Dtos.ResponseDto.GetAllAccountDto;
import com.back.banka.Dtos.ResponseDto.ReactivateAccountResponseDto;
import com.back.banka.Repository.ITokenRepository;
import com.back.banka.Services.IServices.IAccountBankService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@SecurityRequirement(name = "bearerAuth")
@RestController
@RequiredArgsConstructor
@PreAuthorize("hasRole('CLIENT')")
@RequestMapping("/api/banca/cuenta-bancaria")
public class AccountBankController {

    private final IAccountBankService accountBankService;


    @Operation(summary = "Activar cuenta bancaria",
            description = "Permite activar una cuenta bancaria para un usuario registrado y con rol CLIENT. "
                    + "Si el usuario activa su primera cuenta, se validan datos como documento, fecha de nacimiento y frase de seguridad. "
                    + "Si el usuario ya tiene una cuenta y desea abrir otra, solo se crea sin necesidad de validar nuevamente.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Cuenta activada exitosamente"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos o faltantes (frase de seguridad, documento o fecha de nacimiento)"),
            @ApiResponse(responseCode = "401", description = "No autorizado (token inválido o sesión expirada)"),
            @ApiResponse(responseCode = "403", description = "Acceso denegado (usuario sin permisos para activar la cuenta)"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })

    @PostMapping("/activar")
    public ResponseEntity<ActiveAccountResponseDto> activeAccount(
            @Valid
            @RequestBody ActiveAccountRequestDto requestDto){
        ActiveAccountResponseDto activeAccount = this.accountBankService.activeAccount(requestDto);

        return ResponseEntity.status(HttpStatus.CREATED).body(activeAccount);
    }

    @Operation(summary = "Desactivar una cuenta bancaria",
            description = "Permite desactivar una cuenta bancaria para un usuario autenticado.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Cuenta desactivada exitosamente."),
            @ApiResponse(responseCode = "400", description = "Solicitud incorrecta."),
            @ApiResponse(responseCode = "401", description = "No autorizado (token inválido o sesión expirada)."),
            @ApiResponse(responseCode = "403", description = "Acceso denegado (usuario sin permisos para desactivar la cuenta)."),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor.")
    })
    @PostMapping("/desactivar")
    public ResponseEntity<DeactivateAccountResponseDto> deactivateAccount(){
         DeactivateAccountResponseDto deactivateAccount = this.accountBankService.deactivateAccount();
         return ResponseEntity.status(HttpStatus.OK).body(deactivateAccount);
    }

    @GetMapping("/cuentas")
    public ResponseEntity<List<GetAllAccountDto>> getAllAccounts(){
        List<GetAllAccountDto> getAllAccountDto = this.accountBankService.getAllAccounts();
        return ResponseEntity.status(HttpStatus.OK).body(getAllAccountDto);

    }

    @Operation(summary = "Reactivar una cuenta bancaria",
            description = "Permite reactivar una cuenta bancaria para un usuario autenticado.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Cuenta reactivada exitosamente."),
            @ApiResponse(responseCode = "400", description = "Solicitud incorrecta."),
            @ApiResponse(responseCode = "401", description = "No autorizado (token inválido o sesión expirada)."),
            @ApiResponse(responseCode = "403", description = "Acceso denegado (usuario sin permisos para reactivar la cuenta)."),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor.")
    })
    @PostMapping("/reactivar")
    public ResponseEntity<ReactivateAccountResponseDto> reactivateAccount(){
        ReactivateAccountResponseDto deactivateAccount = this.accountBankService.reactiveAccount();
        return ResponseEntity.status(HttpStatus.OK).body(deactivateAccount);
    }


    @Operation(summary = "Ver saldo de una cuenta bancaria activa",
            description = "Permite ver el saldo de una cuenta bancaria activa para un usuario autenticado.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Peticion procesada exitosamente."),
            @ApiResponse(responseCode = "400", description = "Solicitud incorrecta."),
            @ApiResponse(responseCode = "401", description = "No autorizado (token inválido o sesión expirada)."),
            @ApiResponse(responseCode = "403", description = "Acceso denegado (usuario sin permisos para ver saldo de  la cuenta)."),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor.")
    })
    @GetMapping("/saldo")
    public ResponseEntity<ActiveAccountResponseDto> getBalance(){
        ActiveAccountResponseDto accountResponseDto = this.accountBankService.getBalance();
        return  ResponseEntity.status(HttpStatus.OK).body(accountResponseDto);
    }


}
