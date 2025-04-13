package com.odp.walled.repository;

import com.odp.walled.model.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    @Query("SELECT t FROM Transaction t LEFT JOIN FETCH t.wallet LEFT JOIN FETCH t.recipientWallet WHERE t.wallet.id = :walletId OR t.recipientWallet.id = :walletId")
    List<Transaction> findAllByWalletIdOrRecipientWalletId(@Param("walletId") Long walletId);

    List<Transaction> findByWalletId(Long walletId);
}