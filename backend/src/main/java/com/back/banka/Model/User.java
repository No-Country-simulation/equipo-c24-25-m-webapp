package com.back.banka.Model;
import com.back.banka.Enums.Role;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.List;


@Entity
    @Table(name = "users")
    @Getter
    @Setter
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public class User {

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;

        @Column(nullable = false)
        private String name;

        @Column(nullable = false)
        private int age;

        @Column(nullable = false, unique = true)
        private String email;

        @Column(nullable = false)
        private String password;

        @Column(nullable = false)
        private String country;

        @Enumerated(EnumType.STRING)
        @Column(nullable = false)
        private Role role;

        @Column(nullable = false)
        private boolean status;

        @Column(nullable = false,unique = true)
        private String DNI;

        @OneToMany(mappedBy = "user", cascade = CascadeType.ALL,orphanRemoval = true)
        private List<Tokens> tokens;

        @OneToMany(mappedBy = "user",cascade = CascadeType.ALL,orphanRemoval = true)
        private List<AccountBank> accountUser;

         @OneToMany(mappedBy = "user",cascade = CascadeType.ALL,orphanRemoval = true)
         private List<Notifications> notifications;

         private LocalDate dateBirthDay;


    }

