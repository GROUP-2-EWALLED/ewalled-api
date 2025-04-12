package com.odp.walled.controller;

import com.odp.walled.dto.WalletResponse;
import com.odp.walled.service.WalletService;
import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@RestController
@RequestMapping("/api/wallets")
@RequiredArgsConstructor
public class WalletController {
    private final WalletService walletService;

    @PostMapping("/{userId}")
    @ResponseStatus(HttpStatus.CREATED)
    public WalletResponse createWallet(@PathVariable Long userId) {
        return walletService.createWallet(userId);
    }

    // @GetMapping("/{id}")
    // public WalletResponse getWalletById(@PathVariable Long id) {
    // return walletService.getWalletById(id);
    // }

    @GetMapping("/{userId}")
    public WalletResponse getWalletByUserId(@PathVariable Long userId) {
        return walletService.getWalletByUserId(userId);
    }

}