package com.odp.walled.controller;

import com.odp.walled.dto.TransactionRequest;
import com.odp.walled.dto.TransactionResponse;
import com.odp.walled.service.TransactionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import java.io.ByteArrayInputStream;
import java.util.List;

import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/transactions")
@RequiredArgsConstructor
public class TransactionController {
    private final TransactionService transactionService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public TransactionResponse createTransaction(@Valid @RequestBody TransactionRequest request) {
        return transactionService.processTransaction(request);
    }

    @GetMapping
    public List<TransactionResponse> getTransactionsByWallet(
            @RequestParam Long walletId) {
        return transactionService.getTransactionsByWallet(walletId);
    }

    @GetMapping("/export/pdf")
    public ResponseEntity<InputStreamResource> exportPdf(@RequestParam Long walletId) {
        ByteArrayInputStream pdf = transactionService.exportTransactionsAsPdf(walletId);

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "inline; filename=transactions.pdf");

        return ResponseEntity.ok()
                .headers(headers)
                .contentType(MediaType.APPLICATION_PDF)
                .body(new InputStreamResource(pdf));
    }
}