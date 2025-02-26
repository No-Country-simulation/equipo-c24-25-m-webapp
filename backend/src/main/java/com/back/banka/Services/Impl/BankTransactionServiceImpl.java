package com.back.banka.Services.Impl;

import com.back.banka.Enums.StatusTransactions;
import com.back.banka.Enums.TransactionType;
import com.back.banka.Model.AccountBank;
import com.back.banka.Model.BankTransaction;
import com.back.banka.Model.Notifications;
import com.back.banka.Repository.BankTransactionRepository;
import com.back.banka.Repository.IAccountBankRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class BankTransactionServiceImpl {

    private final BankTransactionRepository transactionRepository;
    private final IAccountBankRepository accountBankRepository;

    @Transactional
    public BankTransaction transferMoney(Long senderId, Long receiverId, BigDecimal amount){
        AccountBank sender = accountBankRepository.findById(senderId)
                .orElseThrow(() -> new IllegalArgumentException("Cuenta de origen no encontrada"));
        AccountBank receiver = accountBankRepository.findById(receiverId)
                .orElseThrow(() -> new IllegalArgumentException("Cuenta de origen no encontrada"));

        if(sender.getBalance().compareTo(amount) < 0){
            throw new IllegalArgumentException("Saldo insuficiente");

        }
        sender.setBalance(sender.getBalance().subtract(amount));
        receiver.setBalance(receiver.getBalance().add(amount));

        accountBankRepository.save(sender);
        accountBankRepository.save(receiver);

        BankTransaction transaction = BankTransaction.builder()
                .accountSend(sender)
                .accountReceiving(receiver)
                .amount(amount)
                .date(LocalDateTime.now())
                .transactionType(TransactionType.SENDING_TRANSACTION)
                .status(StatusTransactions.COMPLETED)
                .build();

        transactionRepository.save(transaction);

        return transaction;

    }

}



