package com.back.banka.Services.Impl;

import com.back.banka.Dtos.RequestDto.ActiveAccountRequestDto;
import com.back.banka.Dtos.ResponseDto.ActiveAccountResponseDto;
import com.back.banka.Dtos.ResponseDto.DeactivateAccountResponseDto;
import com.back.banka.Dtos.ResponseDto.GetAllAccountDto;
import com.back.banka.Dtos.ResponseDto.ReactivateAccountResponseDto;
import com.back.banka.Enums.AccountStatus;
import com.back.banka.Enums.AccountType;
import com.back.banka.Exceptions.Custom.BadRequestExceptions;
import com.back.banka.Exceptions.Custom.CustomAuthenticationException;
import com.back.banka.Exceptions.Custom.InvalidCredentialExceptions;
import com.back.banka.Model.AccountBank;
import com.back.banka.Model.User;
import com.back.banka.Repository.IAccountBankRepository;
import com.back.banka.Repository.UserRepository;
import com.back.banka.Services.IServices.IAccountBankService;
import com.back.banka.Services.IServices.IEmailService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Period;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

@Service
public class AccountBankServiceImpl implements IAccountBankService {

    private static final Logger logger = LoggerFactory.getLogger(AccountBankServiceImpl.class);

    private final IAccountBankRepository accountBankRepository;
    private final UserRepository userRepository;
    private final IEmailService emailService;

    public AccountBankServiceImpl(IAccountBankRepository accountBankRepository, UserRepository userRepository, IEmailService emailService) {
        this.accountBankRepository = accountBankRepository;
        this.userRepository = userRepository;
        this.emailService = emailService;
    }

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
        logger.error("activando cuenta {} documento usuario", requestDto.getDocument());

        User user = this.userRepository.findByDNI(requestDto.getDocument()).orElseThrow(()

                -> new BadRequestExceptions(" usuario con documento" + requestDto.getDocument() + " no existe"));


        confirmData(requestDto);

        if (user.getDateBirthDay() == null) {
            user.setDateBirthDay(requestDto.getBirthDate());
            this.userRepository.save(user);
        }

        AccountBank create = createBankAccount(user, requestDto);
        AccountBank savedAccount = this.accountBankRepository.save(create);
        sendNotificationEmail(user, "¡Tu cuenta ha sido Activada!",
                "Tu cuenta bancaria ha sido activada con éxito.");
        return buildAccountResponseDto(savedAccount);
    }

    private void confirmData(ActiveAccountRequestDto requestDto) {


        int age = Period.between(requestDto.getBirthDate(), LocalDate.now()).getYears();

        if (age < 18 || age > 120) {
            throw new BadRequestExceptions("Edad inválida. Debe ser mayor de edad y menor de 120 años.");
        }


    }

    /**
     * Este metodo es opcional, se puede usar
     * para generar mas de una cuenta bancaria
     */

    private boolean validateAccountCount(User user, ActiveAccountRequestDto requestDto) {


        int accountCount = this.accountBankRepository.countByUser(user);

        if (accountCount >= 2) {
            throw new BadRequestExceptions("no puede crear mas de 2 cuentas Bancarias");
        }

        if (accountCount == 0) {
            System.out.println("puede generar una cuenta mas ");
            //llamar a confirmData
        }
        return true;
    }

    private AccountBank createBankAccount(User user, ActiveAccountRequestDto requestDto) {

        return AccountBank.builder()
                .accountStatus(AccountStatus.ACTIVE)
                .balance(BigDecimal.ZERO)
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
     * @param accountId
     * @return Un objeto DeactivateAccountResponseDto con los detalles de la cuenta desactivada.
     * @throws InvalidCredentialExceptions
     * @throws BadRequestExceptions
     * @throws CustomAuthenticationException
     */
    @Override
    public DeactivateAccountResponseDto deactivateAccount(Long accountId) {
        String username = getAuthenticatedUser();
        if (username == null) {
            throw new InvalidCredentialExceptions("Usuario no Autenticado");
        }

        AccountBank accountBank = this.accountBankRepository.findById(accountId).orElseThrow(()
                -> new BadRequestExceptions(" Cuenta no encontrada"));
        validateOwnership(accountBank, username);

        validateAccountStatus(accountBank);
        validateBalanceAccount(accountBank);

        accountBank.setAccountStatus(AccountStatus.INACTIVE);
        accountBank.setDateOfDeactivation(LocalDate.now());
        this.accountBankRepository.save(accountBank);
        sendNotificationEmail(accountBank.getUser(), "¡Tu cuenta ha sido Desactivada!",
                "Tu cuenta bancaria ha sido desactivada con éxito.");
        return buildDeactivateAccountResponseDto(accountBank);
    }

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
     * @param accountId
     * @return Un objeto DeactivateAccountResponseDto con los detalles de la cuenta desactivada.
     * @throws InvalidCredentialExceptions
     * @throws BadRequestExceptions
     * @throws CustomAuthenticationException
     */
    @Override
    public ReactivateAccountResponseDto reactiveAccount(Long accountId) {
        String username = getAuthenticatedUser();
        if (username == null) {
            throw new InvalidCredentialExceptions("Usuario no Autenticado");
        }
        AccountBank accountBank = this.accountBankRepository.findById(accountId).orElseThrow(()
                -> new CustomAuthenticationException("Error: la cuenta no fue encontrada"));
        validateOwnership(accountBank, username);
        if (!accountBank.getAccountStatus().equals(AccountStatus.INACTIVE)) {
            throw new BadRequestExceptions("Solo se puede activar cuentas inactivas");
        }
        accountBank.setAccountStatus(AccountStatus.ACTIVE);
        accountBank.setDateOfReactivation(LocalDate.now());
        sendNotificationEmail(accountBank.getUser(), "¡Tu cuenta ha sido reactivada!",
                "Tu cuenta bancaria ha sido reactivada con éxito.");
        this.accountBankRepository.save(accountBank);
        return buildReactivateAccountResponseDto(accountBank);
    }

    private DeactivateAccountResponseDto buildDeactivateAccountResponseDto(AccountBank accountBank) {
        return DeactivateAccountResponseDto.
                builder()
                .numberAccount(accountBank.getNumber())
                .dateDeactivated(accountBank.getDateOfDeactivation() != null ? accountBank.getDateOfDeactivation().toString() : "Fecha no disponible")
                .statusAccount(accountBank.getAccountStatus().name())
                .build();

    }

    private ReactivateAccountResponseDto buildReactivateAccountResponseDto(AccountBank accountBank) {
        return ReactivateAccountResponseDto.
                builder()
                .numberAccount(accountBank.getNumber())
                .dateOfReactivation(accountBank.getDateOfReactivation() != null ? accountBank.getDateOfReactivation().toString() : "Fecha no disponible")
                .message("Cuenta reactivada exitosamente")
                .build();

    }

    private void validateOwnership(AccountBank accountBank, String username) {
        if (!accountBank.getUser().getEmail().equals(username)) {
            throw new CustomAuthenticationException("Error: no esta autorizado para desactivar esta cuenta");

        }

    }

    private void validateAccountStatus(AccountBank accountBank) {
        switch (accountBank.getAccountStatus()) {

            case INACTIVE -> throw new BadRequestExceptions("Error: La cuenta ya ha sido desactivada");
            case BLOCKED -> throw new BadRequestExceptions("Error: Su cuenta esta bloqueda no puede ser desactivada");
            default -> {
            }
        }
    }

    private void validateBalanceAccount(AccountBank accountBank) {
        if (accountBank.getBalance().compareTo(BigDecimal.ZERO) > 0) {
            throw new BadRequestExceptions("Error: su cuenta debe estar en cero");
        }
    }

    //Muestra el saldo en la cuenta
    @Override
    public ActiveAccountResponseDto getBalance(Long accountId) {
        AccountBank account = accountBankRepository.findById(accountId)
                .orElseThrow(() -> new BadRequestExceptions("Cuenta no encontrada"));

        return new ActiveAccountResponseDto(
                account.getNumber(),
                account.getType().name(),
                account.getAccountStatus().name(),
                account.getBalance(),
                account. getDateOfActivation() != null ? account.getDateOfActivation().toString(): "Fecha no disponible");
    }

    /**
     * metodo para obtener usuario autenticado
     */
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



    private void sendNotificationEmail(User user, String subject, String message) {

        String body = "<h1>Banco XYZ</h1>"


                + "<p>Hola, " + user.getName() + ".</p>"


                + "<p>" + message + "</p>"


                + "<p>Gracias por confiar en nosotros.</p>";

        emailService.sendEmail(user.getEmail(), subject, body);

    }
}

