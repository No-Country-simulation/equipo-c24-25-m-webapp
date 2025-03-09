package com.back.banka.Services.Impl;

import com.back.banka.Exceptions.Custom.InvalidVerificationCodeException;
import com.back.banka.Exceptions.Custom.UserNotFoundException;
import com.back.banka.Exceptions.Custom.VerificationCodeExpiredException;
import com.back.banka.Model.User;
import com.back.banka.Model.VerificationCode;
import com.back.banka.Repository.IVerificationCodeRepository;
import com.back.banka.Repository.UserRepository;
import com.back.banka.Services.IServices.IVerificationCodeService;
import com.back.banka.Utils.IUtilsService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Service
@RequiredArgsConstructor
public class VerificationCoderServiceImpl implements IVerificationCodeService {

    private final IVerificationCodeRepository verificationCodeRepository;
    private final IUtilsService utilsService;
    private final UserRepository userRepository;

    @Override
    public VerificationCode generateCode(Long userId, String purpose, String referenceData, String emailSubject, String emailTemplate) {

        User user = this.userRepository.findById(userId).orElseThrow(()
                -> new UserNotFoundException("Usuario no encontrado "));

        //marcar codigo anteriores como usados

        List<VerificationCode> pendingCodes =
                this.verificationCodeRepository.findByUserIdAndPurposeAndUsedFalse(userId, purpose);

        pendingCodes.forEach(
                code -> {
                    code.setUsed(true);
                }
        );


        verificationCodeRepository.saveAll(pendingCodes);
        //genrar codigo nuevo

        String newCode = generateRandomCode(6);
        LocalDateTime now = LocalDateTime.now();

        VerificationCode verificationCode = VerificationCode.builder()
                .code(newCode)
                .user(user)
                .createAt(now)
                .referenceData(referenceData)
                .expiresAt(now.plusMinutes(10))
                .purpose(purpose)
                .used(false)
                .build();
        verificationCode = verificationCodeRepository.save(verificationCode);

        //envair codigo por correo

        sendVerificationEmail(user, verificationCode, emailSubject, emailTemplate);


        return verificationCode;
    }

    @Override
    public VerificationCode validateCode(Long userId, String code, String purpose) {
        log.info("Buscando código: '{}', userId: {}, propósito: '{}'",
                code, userId, purpose);
        VerificationCode verificationCode = this.verificationCodeRepository.findByCodeAndUserIdAndPurposeAndUsedFalse(code, userId, purpose)
                .orElseThrow(() -> new InvalidVerificationCodeException("Codigo no encontrado o invalido"));

        if (LocalDateTime.now().isAfter(verificationCode.getExpiresAt())) {
            verificationCode.setUsed(true);
            verificationCodeRepository.save(verificationCode);
            throw new VerificationCodeExpiredException("El codigo ha expirado");
        }
        verificationCode.setUsed(true);
        return verificationCodeRepository.save(verificationCode);
    }

    private String generateRandomCode(int length) {
        Random random = new Random();
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            sb.append(random.nextInt(10));
        }
        return sb.toString();
    }

    private void sendVerificationEmail(User user, VerificationCode verificationCode, String subject, String template) {
        CompletableFuture.runAsync(() -> {
            try {
                Map<String, Object> variables = new HashMap<>();
                variables.put("userName", user.getName());
                variables.put("verificationCode", verificationCode.getCode());
                variables.put("expirationMinutes", 10);

                // Añadir variables personalizadas si hay referenceData
                if (verificationCode.getReferenceData() != null && !verificationCode.getReferenceData().isEmpty()) {
                    try {
                        ObjectMapper objectMapper = new ObjectMapper();
                        Map<String, Object> referenceDataMap = objectMapper.readValue(verificationCode.getReferenceData(),
                                new TypeReference<Map<String, Object>>() {
                                });
                        variables.putAll(referenceDataMap);
                    } catch (Exception e) {
                        log.error("Error parsing referenceData JSON: {}", e.getMessage());
                    }
                }

                this.utilsService.sendAccountNotificationVariables(
                        user,
                        subject,
                        template,
                        variables
                );
            } catch (Exception e) {
                log.error("Error al enviar email con código de verificación: {}", e.getMessage(), e);
            }
        });
    }
}