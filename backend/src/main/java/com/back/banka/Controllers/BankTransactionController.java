package com.back.banka.Controllers;

import com.back.banka.Dtos.RequestDto.TransactionRequestDto;
import com.back.banka.Dtos.RequestDto.VerifyCodeDto;
import com.back.banka.Dtos.ResponseDto.TransactionResponseDto;
import com.back.banka.Dtos.ResponseDto.VerificationCodeResponseDto;
import com.back.banka.Services.IServices.BankTransactionService;
import com.back.banka.Services.Impl.BankTransactionServiceImpl;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@PreAuthorize("hasRole('CLIENT')")
@SecurityRequirement(name = "bearerAuth")
@RestController
@RequestMapping("/api/banca/transacciones")
@RequiredArgsConstructor
public class BankTransactionController {
    private final BankTransactionService transactionService;


    @PostMapping("/transferir/iniciar")
    public ResponseEntity<VerificationCodeResponseDto> initiateTransfer(@Valid @RequestBody TransactionRequestDto requestDto) {
        VerificationCodeResponseDto response = transactionService.initiateTransfer(requestDto);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PostMapping("/transferir/{verificationId}/verificar")
    public ResponseEntity<TransactionResponseDto> completeTransfer(
            @PathVariable Long verificationId,
            @Valid @RequestBody VerifyCodeDto verifyCodeDto) {
        TransactionResponseDto response = transactionService.completeTransfer(verificationId, verifyCodeDto);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @GetMapping("/obtener-transacciones")
    public ResponseEntity<List<TransactionResponseDto>> getTransactionHistory(){
        List<TransactionResponseDto> transactions = transactionService.getTransactionHistory();
        return ResponseEntity.ok(transactions);
    }

}








