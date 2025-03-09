package com.back.banka.Services.Impl;

import com.back.banka.Dtos.RequestDto.TransactionRequestDto;
import com.back.banka.Dtos.RequestDto.VerifyCodeDto;
import com.back.banka.Dtos.ResponseDto.TransactionResponseDto;
import com.back.banka.Dtos.ResponseDto.VerificationCodeResponseDto;
import com.back.banka.Enums.AccountStatus;
import com.back.banka.Enums.StatusTransactions;
import com.back.banka.Enums.TransactionType;
import com.back.banka.Exceptions.Custom.*;
import com.back.banka.Model.AccountBank;
import com.back.banka.Model.BankTransaction;
import com.back.banka.Model.User;
import com.back.banka.Model.VerificationCode;
import com.back.banka.Repository.BankTransactionRepository;
import com.back.banka.Repository.IAccountBankRepository;
import com.back.banka.Repository.IVerificationCodeRepository;
import com.back.banka.Repository.UserRepository;
import com.back.banka.Services.IServices.BankTransactionService;
import com.back.banka.Services.IServices.IVerificationCodeService;
import com.back.banka.Utils.IUtilsService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class BankTransactionServiceImpl implements BankTransactionService {
    private final BankTransactionRepository transactionRepository;
    private final IAccountBankRepository accountBankRepository;
    private final IUtilsService utilsService;
    private final IVerificationCodeService verificationCode;
    private final ObjectMapper objectMapper;



    @Transactional
    @Override
    public VerificationCodeResponseDto initiateTransfer(TransactionRequestDto requestDto) {
        Long userId = this.utilsService.getAuthenticatedUserId();
        if (userId == null) {
            throw new CustomAuthenticationException("Error: usuario no autenticado");
        }

        AccountBank senderAccount = accountBankRepository.findByUserIdAndAccountStatus(userId, AccountStatus.ACTIVE)
                .orElseThrow(() -> new ModelNotFoundException("No se encontró una cuenta activa para el usuario"));

        this.utilsService.validateOwnership(senderAccount, senderAccount.getUser().getEmail());

        AccountBank receiverAccount = accountBankRepository.findByNumber(requestDto.getReceiverAccountNumber())
                .orElseThrow(() -> new ModelNotFoundException("Cuenta de destino no encontrada"));

        validateTransferRequirements(senderAccount, receiverAccount, requestDto.getAmount());

        // Crear datos de referencia para la verificación
        Map<String, Object> referenceData = new HashMap<>();
        referenceData.put("senderAccountId", senderAccount.getId());
        referenceData.put("receiverAccountId", receiverAccount.getId());
        referenceData.put("amount", requestDto.getAmount());
        referenceData.put("receiverAccountNumber", receiverAccount.getNumber());
        referenceData.put("receiverName", receiverAccount.getUser().getName());

        String referenceDataJson;
        try {
            referenceDataJson = objectMapper.writeValueAsString(referenceData);
        } catch (Exception e) {
            throw new RuntimeException("Error al serializar los datos de referencia de la transferencia", e);
        }

        // Generar código de verificación
        VerificationCode verificationCode = this.verificationCode.generateCode(
                userId,
                "TRANSFER",
                referenceDataJson,
                "Código de verificación para transferencia",
                "transfer-verification-code"
        );

        return VerificationCodeResponseDto.builder()
                .verificationId(verificationCode.getId())
                .expiresAt(verificationCode.getExpiresAt())
                .message("Se ha enviado un código de verificación a su correo electrónico")
                .build();

    }

    @Override
    public TransactionResponseDto completeTransfer(Long verificationId, VerifyCodeDto verifyCodeDto) {

        Long userId = this.utilsService.getAuthenticatedUserId();
        if (userId == null) {
            throw new CustomAuthenticationException("Error: usuario no autenticado");
        }

        VerificationCode verificationCode = this.verificationCode.validateCode(
                userId,
                verifyCodeDto.getCode(),
                "TRANSFER"
        );
        log.info("Codigo extraido {}",verificationCode.getCode());

        Map<String, Object> referenceData;
        try {
            String refData = verificationCode.getReferenceData();
            if (refData == null || refData.isEmpty()) {
                throw new RuntimeException("Los datos de referencia están vacíos");
            }
            log.info("Datos de referencia: {}", refData);
            referenceData = objectMapper.readValue(refData, new TypeReference<Map<String, Object>>() {});
        } catch (Exception e) {
            log.error("Error al deserializar datos: {} - Contenido: {}", e.getMessage(), verificationCode.getReferenceData());
            throw new RuntimeException("Error al deserializar los datos de referencia de la transferencia", e);
        }

        Long senderAccountId = ((Number) referenceData.get("senderAccountId")).longValue();
        Long receiverAccountId = ((Number) referenceData.get("receiverAccountId")).longValue();
        BigDecimal amount = new BigDecimal(referenceData.get("amount").toString());

        // Recuperar las cuentas nuevamente para asegurar que estén actualizadas
        AccountBank senderAccount = accountBankRepository.findById(senderAccountId)
                .orElseThrow(() -> new ModelNotFoundException("Cuenta emisora no encontrada"));

        AccountBank receiverAccount = accountBankRepository.findById(receiverAccountId)
                .orElseThrow(() -> new ModelNotFoundException("Cuenta receptora no encontrada"));

        validateTransferRequirements(senderAccount, receiverAccount, amount);

        senderAccount.setBalance(senderAccount.getBalance().subtract(amount));
        receiverAccount.setBalance(receiverAccount.getBalance().add(amount));

        List<AccountBank> accountsToUpdate = Arrays.asList(senderAccount, receiverAccount);
        accountBankRepository.saveAll(accountsToUpdate);

        BankTransaction transaction = BankTransaction.builder()
                .accountSend(senderAccount)
                .accountReceiving(receiverAccount)
                .amount(amount)
                .transactionType(TransactionType.SENDING_TRANSACTION)
                .date(LocalDateTime.now())
                .status(StatusTransactions.COMPLETED)
                .build();

        transactionRepository.save(transaction);

        sendTransferNotifications(senderAccount, receiverAccount, amount);

        return new TransactionResponseDto(
                transaction.getAccountSend().getId(),
                transaction.getAccountReceiving().getId(),
                transaction.getAmount(),
                transaction.getDate(),
                transaction.getAccountSend().getNumber(),
                transaction.getAccountReceiving().getNumber(),
                transaction.getAccountSend().getAccountStatus().toString(),
                transaction.getAccountReceiving().getAccountStatus().toString(),
                transaction.getStatus(),
                transaction.getTransactionType()
        );

    }

    private void validateTransferRequirements(AccountBank senderAccount, AccountBank receiverAccount, BigDecimal amount) {
        if (senderAccount.getId().equals(receiverAccount.getId())) {
            throw new InvalidTransactionsException("No puedes transferir dinero a la misma cuenta");
        }

        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new InvalidAmountException("El monto a transferir debe ser mayor a cero");
        }

        if (senderAccount.getBalance().compareTo(amount) < 0) {
            throw new InsufficientFundsException("Saldo insuficiente para realizar la transferencia");
        }

        if (!senderAccount.getAccountStatus().equals(AccountStatus.ACTIVE) ||
                !receiverAccount.getAccountStatus().equals(AccountStatus.ACTIVE)) {
            throw new AccountStatusException("Ambas cuentas deben estar activas");
        }

        BigDecimal dailyLimit = senderAccount.getDailyTransferLimit();
        BigDecimal todayTransferred = transactionRepository.getTotalAmountTransferredToday(
                senderAccount.getId(), LocalDate.now());

        if (todayTransferred.add(amount).compareTo(dailyLimit) > 0) {
            throw new TransferLimitExceededException("La transferencia excede el límite diario permitido");
        }
    }

    @Transactional(readOnly = true)
    public List<TransactionResponseDto> getTransactionHistory() {

        {
            Long userId = this.utilsService.getAuthenticatedUserId();
            if (userId == null) {
                throw new CustomAuthenticationException("Error: usuario no autenticado");
            }


            AccountBank userAccount = accountBankRepository
                    .findByUserIdAndAccountStatus(userId, AccountStatus.ACTIVE)
                    .orElseThrow(() -> new ModelNotFoundException("Cuenta bancaria no encontrada o inactiva"));
            this.utilsService.validateOwnership(userAccount, userAccount.getUser().getEmail());

            List<BankTransaction> transactions = transactionRepository.findAllTransactionsOrderedByMonth(userAccount.getId());

            return transactions.stream()
                    .map(transaction -> {
                        boolean isIncoming = transaction.getAccountReceiving().getId().equals(userAccount.getId());
                        return new TransactionResponseDto(
                                transaction.getAccountSend().getId(),
                                transaction.getAccountReceiving().getId(),
                                transaction.getAmount(),
                                transaction.getDate(),
                                transaction.getAccountSend().getNumber(),
                                transaction.getAccountReceiving().getNumber(),
                                transaction.getAccountSend().getAccountStatus().toString(),
                                transaction.getAccountReceiving().getAccountStatus().toString(),
                                transaction.getStatus(),
                                isIncoming ? TransactionType.RECEIVING_TRANSACTION : TransactionType.SENDING_TRANSACTION


                        );
                    })
                    .collect(Collectors.toList());
        }
    }


    private void sendTransferNotifications(AccountBank senderAccount, AccountBank receiverAccount, BigDecimal amount) {
        CompletableFuture.runAsync(() -> {
            try {
                Map<String, Object> variables = new HashMap<>();
                variables.put("amount", amount);
                variables.put("senderAccountNumber", senderAccount.getNumber());
                variables.put("senderName", senderAccount.getUser().getName());
                variables.put("receiverAccountNumber", receiverAccount.getNumber());
                variables.put("receiverName", receiverAccount.getUser().getName());

                this.utilsService.sendAccountNotificationVariables(
                        receiverAccount.getUser(),
                        "Confirmación de Transferencia",
                        "transfer-confirmation",
                        variables
                );

                this.utilsService.sendAccountNotificationVariables(
                        senderAccount.getUser(),
                        "Confirmación de Transferencia",
                        "sender-transfer-confirmation",
                        variables
                );
            } catch (Exception e) {
                log.error("Error al enviar emails de notificación: {}", e.getMessage(), e);
            }
        });
    }
}





