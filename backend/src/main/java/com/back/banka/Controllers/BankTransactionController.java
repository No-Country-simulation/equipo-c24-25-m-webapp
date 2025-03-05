package com.back.banka.Controllers;

import com.back.banka.Dtos.RequestDto.TransactionRequestDto;
import com.back.banka.Dtos.ResponseDto.TransactionResponseDto;
import com.back.banka.Services.Impl.BankTransactionServiceImpl;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
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
    private final BankTransactionServiceImpl transactionService;


    @PostMapping("/transferir")
    public ResponseEntity<TransactionResponseDto> transfer(
            @RequestBody TransactionRequestDto requestDto){
        TransactionResponseDto responseDto = transactionService.transfer(requestDto);
        return ResponseEntity.ok(responseDto);
    }

    @GetMapping("/obtener-transacciones")
    public ResponseEntity<List<TransactionResponseDto>> getTransactionHistory(){
        List<TransactionResponseDto> transactions = transactionService.getTransactionHistory();
        return ResponseEntity.ok(transactions);
    }

}








