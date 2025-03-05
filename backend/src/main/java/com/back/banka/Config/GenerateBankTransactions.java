package com.back.banka.Config;

import com.back.banka.Enums.*;
import com.back.banka.Model.AccountBank;
import com.back.banka.Model.BankTransaction;
import com.back.banka.Model.User;
import com.back.banka.Repository.BankTransactionRepository;
import com.back.banka.Repository.IAccountBankRepository;
import com.back.banka.Repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

@Slf4j
@Component
public class GenerateBankTransactions implements CommandLineRunner {
    private final BankTransactionRepository transactionRepository;
    private final IAccountBankRepository accountRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${app.generate-transactions:false}")
    private boolean shouldGenerateTransactions;

    @Value("${app.transaction-count:100}")
    private int transactionCount;

    public GenerateBankTransactions(BankTransactionRepository transactionRepository, IAccountBankRepository accountRepository, UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.transactionRepository = transactionRepository;
        this.accountRepository = accountRepository;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }


    @Override
    @Transactional
    public void run(String... args) throws Exception {

        if (!shouldGenerateTransactions) {
            return;
        }

        generateUsersAndBankAccountsIfNeeded();

        long existingCount = transactionRepository.count();
        if (existingCount >= transactionCount) {
            log.error("ya existe {} trasacciones no se haran mas ", existingCount);
            return;
        }

        List<AccountBank> accounts = accountRepository.findAll();
        if (accounts.size() < 2) {
            log.error("se necesitan al menos dos cuentas para transacciones bancarias");
            return;
        }

        log.info("generando transacciones bancarias");

        Random random = new Random();
        List<BankTransaction> transactions = new ArrayList<>();
        int transactionsToGenerate = transactionCount - (int) existingCount;

        for (int i = 0; i < transactionsToGenerate; i++) {
            TransactionType type = random.nextBoolean() ?
                    TransactionType.SENDING_TRANSACTION :
                    TransactionType.RECEIVING_TRANSACTION;

            AccountBank account1 = accounts.get(random.nextInt(accounts.size()));
            AccountBank account2;
            do {
                account2 = accounts.get(random.nextInt(accounts.size()));
            } while (account2.equals(account1));

            BankTransaction transaction = BankTransaction.builder()
                    .date(getRandomDate(random))
                    .transactionType(type)
                    .amount(getRandomAmount(random))
                    .status(getRandomStatus(random))
                    .build();

            if (type == TransactionType.SENDING_TRANSACTION) {
                transaction.setAccountSend(account1);
                transaction.setAccountReceiving(account2);
            } else {
                transaction.setAccountSend(account2);
                transaction.setAccountReceiving(account1);
            }

            transactions.add(transaction);
        }

        transactionRepository.saveAll(transactions);

        log.info("Se generaron {} transacciones exitosamente", transactions.size());
    }

    private StatusTransactions getRandomStatus(Random random) {
        int randomValue = random.nextInt(10);
        if (randomValue < 7) {
            return StatusTransactions.COMPLETED;
        } else if (randomValue < 9) {
            return StatusTransactions.PENDING;
        } else {
            return StatusTransactions.FAILED;
        }
    }

    private LocalDateTime getRandomDate(Random random) {
        return LocalDateTime.now()
                .minusDays(random.nextInt(90))
                .minusHours(random.nextInt(24))
                .minusMinutes(random.nextInt(60));
    }

    private BigDecimal getRandomAmount(Random random) {
        double amount = 50 + (random.nextDouble() * 1950);
        return BigDecimal.valueOf(amount).setScale(2, RoundingMode.HALF_UP);
    }


    /**
     * Genera un número de cuenta bancaria de exactamente 12 dígitos.
     */
    private String generateAccountNumber(Random random) {
        StringBuilder accountNumber = new StringBuilder();
        accountNumber.append(1 + random.nextInt(9));
        for (int i = 0; i < 11; i++) {
            accountNumber.append(random.nextInt(10));
        }

        return accountNumber.toString();
    }

    private void generateUsersAndBankAccountsIfNeeded() {
        long existingUsers = userRepository.count();
        List<User> users;

        if (existingUsers < 3) {
            System.out.println("Generando usuarios de prueba...");




            users = new ArrayList<>();
            for (int i = 0; i < 3; i++) {
                String dni = UUID.randomUUID().toString().replace("-", "").substring(0, 8);                boolean userExists = userRepository.existsByDNI(dni);
                if(!userExists) {
                    User user = User.builder()
                            .name("Usuario" + (i + 1))
                            .email("usuario" + (i + 1) + "@example.com")
                            .DNI(dni)
                            .age(19 + (i + 1))
                            .role(Role.CLIENT)
                            .status(true)
                            .country("Colombia")
                            .password(passwordEncoder.encode("Password1234@"))
                            .build();
                    users.add(user);
                }
            }
            users = userRepository.saveAll(users);
        } else {
            users = userRepository.findAll().subList(0, 3);
        }

        long existingAccounts = accountRepository.count();
        if (existingAccounts >= 3) {
            return;
        }

        log.info("Verificando cuentas bancarias...");

        Random random = new Random();
        List<AccountBank> newAccounts = new ArrayList<>();

        for (User user : users) {
            boolean userHasAccount = accountRepository.existsByUser(user);

            if (!userHasAccount) {
                String accountNumber = generateAccountNumber(random);

                log.info("Generando cuenta bancaria para el usuario: {}", user.getName());

                AccountBank account = AccountBank.builder()
                        .number(accountNumber)
                        .type(AccountType.SAVINGS_ACCOUNT)
                        .balance(BigDecimal.valueOf(500 + random.nextDouble() * 4500).setScale(2, RoundingMode.HALF_UP))
                        .accountStatus(AccountStatus.ACTIVE)
                        .dateOfActivation(LocalDate.now())
                        .user(user)
                        .build();

                newAccounts.add(account);
            }
        }

        if (!newAccounts.isEmpty()) {
            accountRepository.saveAll(newAccounts);
            log.info("Se generaron {} cuentas bancarias", newAccounts.size());
        } else {
            log.info("No se generaron nuevas cuentas bancarias, todos los usuarios ya tienen una cuenta.");
        }
    }
}