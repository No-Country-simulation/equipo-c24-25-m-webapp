package com.back.banka.Repository;

import com.back.banka.Model.AccountBank;
import com.back.banka.Model.BankTransaction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BankTransactionRepository extends JpaRepository<BankTransaction, Long> {
    List<BankTransaction> findByAccountSendOrAccountReceiving(AccountBank sender, AccountBank receiver);
}
