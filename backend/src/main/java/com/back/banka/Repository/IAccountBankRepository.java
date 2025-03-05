package com.back.banka.Repository;

import com.back.banka.Enums.AccountStatus;
import com.back.banka.Model.AccountBank;
import com.back.banka.Model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface IAccountBankRepository extends JpaRepository<AccountBank, Long> {
    boolean existsByNumber(String number);
    boolean existsByUser(User user);
    int countByUser(User user);
    Optional<AccountBank> findByUserId(Long userId);
    Optional<AccountBank> findByNumber(String number);
    Optional<AccountBank> findById(Long id);
    Optional<AccountBank> findByIdAndAccountStatus(Long id, AccountStatus accountStatus);
    Optional<AccountBank> findByUserIdAndAccountStatus(Long userId, AccountStatus accountStatus);
}
