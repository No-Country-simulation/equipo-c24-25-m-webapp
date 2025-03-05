package com.back.banka.Services.Impl;

import com.back.banka.Dtos.RequestDto.ActiveAccountRequestDto;
import com.back.banka.Dtos.ResponseDto.ActiveAccountResponseDto;
import com.back.banka.Dtos.ResponseDto.DeactivateAccountResponseDto;
import com.back.banka.Dtos.ResponseDto.GetAllAccountDto;
import com.back.banka.Dtos.ResponseDto.ReactivateAccountResponseDto;
import com.back.banka.Enums.AccountStatus;
import com.back.banka.Enums.AccountType;
import com.back.banka.Enums.TokenType;
import com.back.banka.Exceptions.Custom.*;
import com.back.banka.Model.AccountBank;
import com.back.banka.Model.Tokens;
import com.back.banka.Model.User;
import com.back.banka.Repository.IAccountBankRepository;
import com.back.banka.Repository.ITokenRepository;
import com.back.banka.Repository.UserRepository;
import com.back.banka.Services.IServices.IAccountBankService;
import com.back.banka.Services.IServices.IEmailService;
import com.back.banka.Utils.IUtilsService;
import com.back.banka.Utils.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import javax.naming.ServiceUnavailableException;
import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AccountBankServiceImpl implements IAccountBankService {

    private static final Logger logger = LoggerFactory.getLogger(AccountBankServiceImpl.class);

    private final IAccountBankRepository accountBankRepository;
    private final UserRepository userRepository;
    private final IUtilsService utilsService;




    /**
     * Activa una nueva cuenta bancaria para un usuario.
     * - Si el usuario no existe, lanza una excepción.
     * - Si el usuario ya tiene 3 cuentas bancarias, no permite crear más.
     * - Si es la primera cuenta del usuario, valida la información antes de crearla.
     * - Guarda la nueva cuenta en la base de datos y devuelve la información de la cuenta creada.
     * envia un mensaje al correo electronico del usuario confirmando la creacion de su cuenta bancaria
     *
     * @param requestDto Datos de la solicitud de activación de cuenta.
     * @return ActiveAccountResponseDto con los detalles de la cuenta creada.
     * @throws BadRequestExceptions si el usuario no existe, ya tiene 3 cuentas o los datos no son válidos.
     */

    @Override
    public ActiveAccountResponseDto activeAccount(ActiveAccountRequestDto requestDto) {


            String auth =  this.utilsService.getAuthenticatedUser();

            if (auth == null) {
                throw new InvalidCredentialExceptions("Usuario no Autenticado");
            }

            logger.error("activando cuenta {} documento usuario", requestDto.getDocument());

            User user = this.userRepository.findByDNI(requestDto.getDocument()).orElseThrow(()

                    -> new BadRequestExceptions(" usuario con documento" + requestDto.getDocument() + " no existe"));

           int accountCount = validateAccountCount(user);

            confirmData(requestDto);

            if (user.getDateBirthDay() == null) {
                user.setDateBirthDay(requestDto.getBirthDate());
                this.userRepository.save(user);
            }

            AccountBank create = createBankAccount(user, requestDto);
            AccountBank savedAccount = this.accountBankRepository.save(create);

          try{  this.utilsService.sendAccountNotification(
                    user,
                    "¡Confirmación de proceso de activacion de cuenta!",
                    "email-template",
                    "Tu cuenta ha sido activada . recibiste un bono de 50000000. Gracias por acceder a nuestros servicios bancarios");
        } catch (Exception e) {
            logger.error("Error inesperado al enviar correo: {}", e.getMessage(), e);
        }
        return buildAccountResponseDto(savedAccount);

    }

    private void confirmData(ActiveAccountRequestDto requestDto) {


        int age = Period.between(requestDto.getBirthDate(), LocalDate.now()).getYears();

        if (age < 18 || age > 120) {
            throw new BadRequestExceptions("Edad inválida. Debe ser mayor de edad.");
        }


    }

    /**
     * Este metodo es opcional, se puede usar
     * para generar mas de una cuenta bancaria
     */

    private int validateAccountCount(User user) {


        int accountCount = this.accountBankRepository.countByUser(user);

        if (accountCount >=1) {
            throw new BadRequestExceptions("por ahora solo se puede activar une cuenta");
        }

        return accountCount;
    }

    private AccountBank createBankAccount(User user, ActiveAccountRequestDto requestDto) {

        return AccountBank.builder()
                .accountStatus(AccountStatus.ACTIVE)
                .balance(new BigDecimal("50000000"))
                .type(AccountType.SAVINGS_ACCOUNT)
                .permissionPhrase(requestDto.getSecurityPhrase())
                .number(generateAccountNumber())
                .user(user)
                .dateOfActivation(LocalDate.now())
                .build();
    }

    private String generateAccountNumber() {
        String prefix = "58";
        ThreadLocalRandom random = ThreadLocalRandom.current();
        String accountNumber;

        int attempts = 0;
        final int MAX_ATTEMPTS = 6;

        do {
            String randomPart = String.format("%010d", random.nextLong(10_000_000_000L));
            accountNumber = prefix + randomPart;
            attempts++;

            if (attempts >= MAX_ATTEMPTS) {
                throw new RuntimeException("No se pudo generar un número de cuenta único después de " + MAX_ATTEMPTS + " intentos");
            }
        } while (accountBankRepository.existsByNumber(accountNumber));

        return accountNumber;
    }

    private ActiveAccountResponseDto buildAccountResponseDto(AccountBank accountBank) {
        return ActiveAccountResponseDto.builder()
                .accountNumber(accountBank.getNumber())
                .accountType(accountBank.getType().name())
                .accountStatus(accountBank.getAccountStatus().name())
                .balance(accountBank.getBalance())
                .dateOfActivation(accountBank.getDateOfActivation().toString())
                .build();
    }

    /**
     * Desactiva una cuenta bancaria si cumple con los criterios de validación.
     * <p>
     * Este método obtiene el usuario autenticado y verifica que tenga permiso
     * para desactivar la cuenta asociada al id proporcionado.
     * realiza validaciones sobre el estado de la cuenta y el balance .
     * <p>
     * Si la cuenta es válida para desactivación, cambia su estado a INACTIVE,
     * registra la fecha guarda datos.
     *

     * @return Un objeto DeactivateAccountResponseDto con los detalles de la cuenta desactivada.
     * @throws InvalidCredentialExceptions
     * @throws BadRequestExceptions
     * @throws CustomAuthenticationException
     */
    @Transactional
    @Override
    public DeactivateAccountResponseDto deactivateAccount() {

        Long userId = this.utilsService.getAuthenticatedUserId();
        if (userId == null) {
            throw new CustomAuthenticationException("usuario no autenticado");
        }
           AccountBank accountBank = this.accountBankRepository.findByUserId(userId).orElseThrow(()
                   -> new BadRequestExceptions(" Cuenta no encontrada"));
           this.utilsService.validateUserAuthorization(accountBank, userId);

           this.utilsService.validateAccountStatus(accountBank);
           this.utilsService.validateBalanceAccount(accountBank);
           accountBank.setAccountStatus(AccountStatus.INACTIVE);
           accountBank.setDateOfDeactivation(LocalDate.now());
           this.accountBankRepository.save(accountBank);
        try { this.utilsService.sendAccountNotification(
                   accountBank.getUser(),
                   "¡Tu cuenta ha sido Desactivada!",
                   "email-template",
                   "Tu cuenta bancaria ha sido desactivada con éxito. debes esperar 5 minutos habiles para activarla");

       } catch (Exception e){
            logger.error("error al enviar correo " + e.getMessage());
       }
        return buildDeactivateAccountResponseDto(accountBank);

    }
@Transactional(readOnly = true)
    @Override
    public List<GetAllAccountDto> getAllAccounts() {
        try {

            List<AccountBank> accountBanks = this.accountBankRepository.findAll();

            return accountBanks.stream()
                    .map(accounts -> GetAllAccountDto.builder()
                            .id(accounts.getId())
                            .balanceAccount(accounts.getBalance())
                            .statusAccount(accounts.getAccountStatus().name())
                            .userId(accounts.getUser().getId())
                            .numberAccount(accounts.getNumber())
                            .dateOfCreation(accounts.getDateOfActivation() != null ? accounts.getDateOfActivation().toString() : "Fecha no disponible")
                            .build()
                    )
                    .collect(Collectors.toList());
        } catch (Exception e) {
            throw new RuntimeException("Error al traer cuentas bancarias" + e);
        }

    }

    /**
     * Reactiva una cuenta bancaria si cumple con los criterios de validación.
     * <p>
     * Este método obtiene el usuario autenticado y verifica que tenga permiso
     * para reactivar la cuenta asociada al id proporcionado.
     * realiza validaciones sobre el estado de la cuenta  .
     * <p>
     * Si la cuenta es válida para react9car, cambia su estado a ACTIVE,
     * registra la fecha guarda datos.
     *
     * @param
     * @return Un objeto DeactivateAccountResponseDto con los detalles de la cuenta desactivada.
     * @throws InvalidCredentialExceptions
     * @throws BadRequestExceptions
     * @throws CustomAuthenticationException
     */
    @Transactional
    @Override
    public ReactivateAccountResponseDto reactiveAccount() {

        Long userId = this.utilsService.getAuthenticatedUserId();
        if (userId == null) {
            throw new CustomAuthenticationException("usuario no autenticado");
        }

            AccountBank accountBank = this.accountBankRepository.findByUserId(userId).orElseThrow(()
                    -> new CustomAuthenticationException("Error: la cuenta no fue encontrada"));

            this.utilsService.validateUserAuthorization(accountBank, userId);

            if (!accountBank.getAccountStatus().equals(AccountStatus.INACTIVE)) {
                throw new BadRequestExceptions("Solo se puede activar cuentas inactivas");
            }

            if (accountBank.getDateOfDeactivation() != null) {
                LocalDateTime deactivationDateTime = accountBank.getDateOfDeactivation().atTime(23, 59, 59);

                Duration timeSinceDeactivation = Duration.between(deactivationDateTime, LocalDateTime.now());

                if (timeSinceDeactivation.toMinutes() < 5) {
                    throw new BadRequestExceptions("Debe esperar al menos 5 minutos antes de reactivar la cuenta.");
                }
            }

            accountBank.setAccountStatus(AccountStatus.ACTIVE);
            accountBank.setDateOfReactivation(LocalDate.now());
        try {
            this.utilsService.sendAccountNotification(
                    accountBank.getUser(),
                    "¡Tu cuenta ha sido reactivada!",
                    "email-template",
                    "Tu cuenta bancaria ha sido reactivada con éxito.");
            this.accountBankRepository.save(accountBank);
        } catch (InvalidCredentialExceptions | CustomAuthenticationException | BadRequestExceptions e) {
            throw e;

        } catch (Exception e){
            logger.error("error al enviar correo {} ", e.getMessage());
        }
        return buildReactivateAccountResponseDto(accountBank);
    }

    private DeactivateAccountResponseDto buildDeactivateAccountResponseDto(AccountBank accountBank) {
        return DeactivateAccountResponseDto.
                builder()
                .UserId(accountBank.getUser().getId())
                .numberAccount(accountBank.getNumber())
                .dateDeactivated(accountBank.getDateOfDeactivation() != null ? accountBank.getDateOfDeactivation().toString() : "Fecha no disponible")
                .statusAccount(accountBank.getAccountStatus().name())
                .build();

    }


    private ReactivateAccountResponseDto buildReactivateAccountResponseDto(AccountBank accountBank) {
        return ReactivateAccountResponseDto.
                builder()
                .id(accountBank.getId())
                .UserId(accountBank.getUser().getId())
                .numberAccount(accountBank.getNumber())
                .dateOfReactivation(accountBank.getDateOfReactivation() != null ? accountBank.getDateOfReactivation().toString() : "Fecha no disponible")
                .message("Cuenta reactivada exitosamente")
                .build();

    }


    //Muestra el saldo en la cuenta
    @Transactional(readOnly = true)
    @Override
    public ActiveAccountResponseDto getBalance() {
        {

            Long userId = this.utilsService.getAuthenticatedUserId();
            if (userId == null) {
                throw new CustomAuthenticationException("usuario no autenticado");
            }

            AccountBank account = accountBankRepository.findByUserIdAndAccountStatus(userId,AccountStatus.ACTIVE)
                    .orElseThrow(() -> new ModelNotFoundException("Cuenta no Encontrada"));

            if (account.getAccountStatus() != AccountStatus.ACTIVE) {
                throw new BadRequestExceptions("La cuenta está inactiva");
            }
            return new ActiveAccountResponseDto(
                    account.getNumber(),
                    account.getType().name(),
                    account.getAccountStatus().name(),
                    account.getBalance(),
                    account.getDateOfActivation() != null ? account.getDateOfActivation().toString() : "Fecha no disponible");
        }
    }




}

