package com.odp.walled.repository;

import com.odp.walled.model.User;
import com.odp.walled.model.Wallet;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface WalletRepository extends JpaRepository<Wallet, Long> {
    Optional<Wallet> findByAccountNumber(String accountNumber);

    boolean existsByAccountNumber(String accountNumber);

    Optional<Wallet> findByUser(User user);

    Optional<Wallet> findByUserId(Long userId);
}