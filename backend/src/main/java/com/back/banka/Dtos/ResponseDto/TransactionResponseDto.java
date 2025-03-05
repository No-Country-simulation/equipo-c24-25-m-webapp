package com.back.banka.Dtos.ResponseDto;


import com.back.banka.Enums.StatusTransactions;
import com.back.banka.Enums.TransactionType;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class TransactionResponseDto {
    private Long senderId;
    private Long receiverId;
    private BigDecimal amount;
    private LocalDateTime date;
    private String numberAccountSender;
    private String numberAccountReceiving;
    private StatusTransactions status;
    private TransactionType transactionType;

}

