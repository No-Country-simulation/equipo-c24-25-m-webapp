package com.back.banka.Utils;

import com.back.banka.Enums.TokenType;
import com.back.banka.Exceptions.Custom.BadRequestExceptions;
import com.back.banka.Exceptions.Custom.CustomAuthenticationException;
import com.back.banka.Model.AccountBank;
import com.back.banka.Model.SecurityUser;
import com.back.banka.Model.Tokens;
import com.back.banka.Model.User;
import com.back.banka.Repository.ITokenRepository;
import com.back.banka.Services.IServices.IEmailService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class UtilsServiceImpl implements IUtilsService {

    private final IEmailService emailService;
    private final ITokenRepository tokenRepository;


    @Override
    public void validateOwnership(AccountBank accountBank,
                                  String username) {
        if (!accountBank.getUser().getEmail().equals(username)) {
            throw new CustomAuthenticationException("Error: no esta autorizado para hacer movimientos en esta cuenta");

        }
    }

    @Override
    public void validateAccountStatus(AccountBank accountBank) {
        switch (accountBank.getAccountStatus()) {

            case INACTIVE -> throw new BadRequestExceptions("Error: La cuenta ya ha sido desactivada");
            case BLOCKED -> throw new BadRequestExceptions("Error: Su cuenta esta bloqueda no puede ser desactivada");
            default -> {
            }
        }
    }

    @Override
    public void validateBalanceAccount(AccountBank accountBank) {
        if (accountBank.getBalance().compareTo(BigDecimal.ZERO) > 0) {
            throw new BadRequestExceptions("Error: su cuenta debe estar en cero");
        }
    }

    @Override
    public String getAuthenticatedUser() {
        Logger logger = LoggerFactory.getLogger(getClass());

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) {
            logger.error("No hay autenticación en el contexto de seguridad.");
            return null;
        }

        Object principal = authentication.getPrincipal();
        if (principal instanceof UserDetails) {
            logger.info(" Usuario autenticado: " + ((UserDetails) principal).getUsername());
            return ((UserDetails) principal).getUsername();
        } else {
            logger.error(" El usuario no está autenticado correctamente.");
            return null;
        }
    }

    public Long getAuthenticatedUserId() {
        Logger logger = LoggerFactory.getLogger(getClass());

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) {
            logger.error("No hay autenticación en el contexto de seguridad.");
            return null;
        }

        Object principal = authentication.getPrincipal();
        if (principal instanceof SecurityUser) {
            User user = ((SecurityUser) principal).getUser();
            if (user != null) {
                Long userId = user.getId();
                logger.info("Usuario autenticado ID: " + userId);
                return userId;
            }
        }

        logger.error("No se pudo obtener el ID del usuario autenticado.");
        return null;
    }


    @Override
    public void sendAccountNotification(User user,
                                        String subject,
                                        String templateName,
                                        String message) {
        Map<String, Object> emailVariables = new HashMap<>();
        emailVariables.put("name", user.getName());
        emailVariables.put("message", message);

        emailService.sendEmailTemplate(
                user.getEmail(),
                subject,
                templateName,
                emailVariables
        );
    }

    @Override
    public void saveUserToken(User user, String token) {
        Tokens tokenSaved = Tokens.builder()
                .user(user)
                .expired(false)
                .revoked(false)
                .token(token)
                .tokenType(TokenType.BEARER)
                .build();
        this.tokenRepository.save(tokenSaved);
    }

    public void validateUserAuthorization(AccountBank accountBank, Long authenticatedUserId) {
        if (!accountBank.getUser().getId().equals(authenticatedUserId)) {
            throw new CustomAuthenticationException("No autorizado para reactivar esta cuenta");
        }
    }

    public void revokedUsersToken(User user) {
        List<Tokens> validations =
                this.tokenRepository.findAllIExpiredIsFalseOrRevokedIsFalseByUserId(user.getId());
        if (!validations.isEmpty()) {
            validations.forEach(token -> {
                token.setRevoked(true);
                token.setExpired(true);
            });
            tokenRepository.saveAll(validations);
        }
    }

    @Override
    public void sendAccountNotificationVariables(User user,
                                        String subject,
                                        String templateName,
                                        Map<String, Object> emailVariables) {
        emailVariables.put("name", user.getName()); // Agregar el nombre del usuario
        emailService.sendEmailTemplate(
                user.getEmail(),
                subject,
                templateName,
                emailVariables
        );
    }
}
