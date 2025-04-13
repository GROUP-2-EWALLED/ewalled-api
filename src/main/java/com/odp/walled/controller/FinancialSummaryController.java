package com.odp.walled.controller;

import com.odp.walled.dto.FinancialSummaryResponse;
import com.odp.walled.service.TransactionService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class FinancialSummaryController {

    @Autowired
    private TransactionService transactionService;

    @GetMapping("/api/summary/{walletId}")
    public FinancialSummaryResponse getFinancialSummary(@PathVariable Long walletId) {
        return transactionService.getSummary(walletId);
    }
}
