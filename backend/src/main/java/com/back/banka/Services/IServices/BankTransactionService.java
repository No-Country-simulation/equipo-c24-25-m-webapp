package com.back.banka.Services.IServices;


import com.back.banka.Dtos.RequestDto.VerifyCodeDto;
import com.back.banka.Dtos.ResponseDto.SendMoneyResponseDto;
import com.back.banka.Dtos.RequestDto.TransactionRequestDto;
import com.back.banka.Dtos.ResponseDto.DeactivateAccountResponseDto;
import com.back.banka.Dtos.ResponseDto.TransactionResponseDto;
import com.back.banka.Dtos.ResponseDto.VerificationCodeResponseDto;

import java.util.List;

public interface BankTransactionService {
    List<TransactionResponseDto> getTransactionHistory();
    VerificationCodeResponseDto initiateTransfer(TransactionRequestDto requestDto);
    TransactionResponseDto completeTransfer(Long verificationId, VerifyCodeDto verifyCodeDto);
}
