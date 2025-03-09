package com.back.banka.Repository;
import com.back.banka.Model.VerificationCode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface IVerificationCodeRepository extends JpaRepository<VerificationCode,Long> {
    Optional<VerificationCode> findByCodeAndUserIdAndPurposeAndUsedFalse(
            String code, Long userId, String purpose);
    List<VerificationCode> findByUserIdAndPurposeAndUsedFalse(Long userId, String purpose);
}
