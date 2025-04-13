package com.odp.walled.service;

import com.odp.walled.dto.FinancialSummaryDto;
import com.odp.walled.dto.FinancialSummaryResponse;
import com.odp.walled.dto.TransactionRequest;
import com.odp.walled.dto.TransactionResponse;
import com.odp.walled.exception.InsufficientBalanceException;
import com.odp.walled.exception.ResourceNotFound;
import com.odp.walled.mapper.TransactionMapper;
import com.odp.walled.model.Transaction;
import com.odp.walled.model.Transaction.TransactionType;
import com.odp.walled.model.Wallet;
import com.odp.walled.repository.TransactionRepository;
import com.odp.walled.repository.WalletRepository;
import com.odp.walled.util.FinancialSummaryUtil;

import lombok.RequiredArgsConstructor;

import java.util.List;
import java.time.LocalDateTime;
import java.time.temporal.WeekFields;
import java.util.*;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.crossstore.ChangeSetPersister.NotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class TransactionService {
    private final WalletRepository walletRepository;
    private final TransactionRepository transactionRepository;
    private final TransactionMapper transactionMapper;

    @Transactional
    public TransactionResponse processTransaction(TransactionRequest request) {
        Wallet wallet = walletRepository.findById(request.getWalletId())
                .orElseThrow(() -> new ResourceNotFound("Wallet not found"));

        Transaction transaction = new Transaction();
        transaction.setWallet(wallet);
        transaction.setTransactionType(request.getTransactionType());
        transaction.setAmount(request.getAmount());
        transaction.setDescription(request.getDescription());

        if (request.getTransactionType() == TransactionType.TRANSFER) {
            Wallet recipient = walletRepository.findByAccountNumber(request.getRecipientAccountNumber())
                    .orElseThrow(() -> new ResourceNotFound("Recipient wallet not found"));

            if (wallet.getBalance().compareTo(request.getAmount()) < 0) {
                throw new InsufficientBalanceException("Insufficient balance");
            }

            wallet.setBalance(wallet.getBalance().subtract(request.getAmount()));
            recipient.setBalance(recipient.getBalance().add(request.getAmount()));
            walletRepository.save(recipient);

            // Set the recipient wallet
            transaction.setRecipientWallet(recipient);
        } else {
            // TOP_UP
            wallet.setBalance(wallet.getBalance().add(request.getAmount()));
        }

        walletRepository.save(wallet);
        return transactionMapper.toResponse(transactionRepository.save(transaction));
    }

    public List<TransactionResponse> getTransactionsByWalletId(Long walletId) {
        List<Transaction> transactions = transactionRepository
                .findAllByWalletIdOrRecipientWalletId(walletId);

        return transactions.stream().map(transaction -> {
            TransactionResponse response = transactionMapper.toResponse(transaction);

            if (transaction.getTransactionType() == TransactionType.TOP_UP) {
                response.setFromTo("-");
            } else if (transaction.getWallet().getId().equals(walletId)) {
                // User is the sender
                String recipientName = transaction.getRecipientWallet() != null
                        ? transaction.getRecipientWallet().getUser().getFullname()
                        : "-";
                response.setFromTo("To: " + recipientName);
            } else if (transaction.getRecipientWallet() != null &&
                    transaction.getRecipientWallet().getId().equals(walletId)) {
                // User is the recipient
                String senderName = transaction.getWallet().getUser().getFullname();
                response.setFromTo("From: " + senderName);
            } else {
                response.setFromTo("-");
            }

            return response;
        }).toList();
    }

    public FinancialSummaryResponse getSummary(Long walletId) {
        Wallet wallet = walletRepository.findById(walletId).orElseThrow(() -> new ResourceNotFound("Wallet not found"));

        List<Transaction> allTransactions = transactionRepository
                .findAllByWalletIdOrRecipientWalletId(walletId);

        List<Transaction> incomeTransactions = allTransactions.stream()
                .filter(t -> t.getTransactionType() == Transaction.TransactionType.TOP_UP ||
                        (t.getTransactionType() == Transaction.TransactionType.TRANSFER &&
                                t.getRecipientWallet() != null &&
                                t.getRecipientWallet().getId().equals(walletId)))
                .toList();

        List<Transaction> outcomeTransactions = allTransactions.stream()
                .filter(t -> t.getTransactionType() == Transaction.TransactionType.TRANSFER &&
                        t.getWallet() != null &&
                        t.getWallet().getId().equals(walletId))
                .toList();

        List<FinancialSummaryDto> weekly = FinancialSummaryUtil.mergeIncomeOutcome(
                FinancialSummaryUtil.aggregateByWeek(incomeTransactions, walletId, true),
                FinancialSummaryUtil.aggregateByWeek(outcomeTransactions, walletId, false));

        List<FinancialSummaryDto> monthly = FinancialSummaryUtil.mergeIncomeOutcome(
                FinancialSummaryUtil.aggregateByMonth(incomeTransactions, walletId, true),
                FinancialSummaryUtil.aggregateByMonth(outcomeTransactions, walletId, false));

        List<FinancialSummaryDto> quarterly = FinancialSummaryUtil.mergeIncomeOutcome(
                FinancialSummaryUtil.aggregateByQuarter(incomeTransactions, walletId, true),
                FinancialSummaryUtil.aggregateByQuarter(outcomeTransactions, walletId, false));

        return new FinancialSummaryResponse(weekly, monthly, quarterly);
    }

}