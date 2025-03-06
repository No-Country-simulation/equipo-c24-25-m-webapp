package com.back.banka.Repository;

import com.back.banka.Model.AccountBank;
import com.back.banka.Model.BankTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;


import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public interface BankTransactionRepository extends JpaRepository<BankTransaction, Long> {

    @Query("SELECT t FROM BankTransaction t WHERE t.accountSend = :account OR t.accountReceiving = :account ORDER BY t.date DESC")

    List<BankTransaction> findTransactionsByAccount(AccountBank account);

    @Query("SELECT bt FROM BankTransaction bt WHERE bt.accountSend.id = :accountId OR bt.accountReceiving.id = :accountId ORDER BY EXTRACT(YEAR FROM bt.date) DESC, EXTRACT(MONTH FROM bt.date) DESC, bt.date DESC")
    List<BankTransaction> findAllTransactionsOrderedByMonth(@Param("accountId") Long accountId);
    @Query("SELECT COALESCE(SUM(t.amount), 0) FROM BankTransaction t " +
            "WHERE t.accountSend.id = :accountId " +
            "AND t.transactionType = 'SENDING_TRANSACTION' " +
            "AND t.status = 'COMPLETED' " +
            "AND CAST(t.date AS LocalDate) = :date")
    BigDecimal getTotalAmountTransferredToday(@Param("accountId") Long accountId, @Param("date") LocalDate date);


}




