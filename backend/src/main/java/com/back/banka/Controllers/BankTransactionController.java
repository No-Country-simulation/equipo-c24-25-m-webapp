package com.back.banka.Controllers;

import com.back.banka.Model.BankTransaction;
import com.back.banka.Services.Impl.BankTransactionServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;

@RestController
@RequestMapping("/transactions")
@RequiredArgsConstructor
public class BankTransactionController {

    private final BankTransactionServiceImpl transactionService;

    @PostMapping("/transfer")
    public ResponseEntity<BankTransaction> transferMoney(@RequestParam Long senderId,
                                                         @RequestParam Long receiverId,
                                                         @RequestParam BigDecimal amount){
        BankTransaction transaction = transactionService.transferMoney(senderId,receiverId,amount);
        return ResponseEntity.ok(transaction);
    }

}



