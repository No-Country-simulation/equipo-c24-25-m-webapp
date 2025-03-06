package com.back.banka.Model;

import com.back.banka.Enums.AccountStatus;
import com.back.banka.Enums.AccountType;
import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Builder
@Table(name = "account")
public class AccountBank {


        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;

        @Column(nullable = false,unique = true)
        private String number;

        @Column(nullable = false)
        @Enumerated(EnumType.STRING)
        private AccountType type;

        @Column(precision = 15, scale = 2,nullable = false)
        private BigDecimal balance;
        @Column(name = "daily_transfer_limit",nullable = false, precision = 15, scale = 2)
        private BigDecimal dailyTransferLimit = new BigDecimal("50000000.00");

        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "user_id")
        private User user;

        @OneToMany(mappedBy = "accountSend",cascade = CascadeType.ALL,orphanRemoval = true)
        private List<BankTransaction> sendTransactions;

        @OneToMany(mappedBy = "accountReceiving",cascade = CascadeType.ALL,orphanRemoval = true)
        private List<BankTransaction> receivingTransactions;

        @Enumerated(EnumType.STRING)
        private AccountStatus accountStatus;
        private String permissionPhrase;
        private LocalDate dateOfActivation;
        private LocalDate dateOfDeactivation;
        private LocalDate dateOfReactivation;
        private LocalDate dateOfBlocked;

}
