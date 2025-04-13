package com.odp.walled.util;

import com.odp.walled.dto.FinancialSummaryDto;
import com.odp.walled.model.Transaction;

import java.math.BigDecimal;
import java.time.*;
import java.time.format.TextStyle;
import java.time.temporal.WeekFields;
import java.util.*;
import java.util.stream.Collectors;

public class FinancialSummaryUtil {

    public static List<FinancialSummaryDto> aggregateByWeek(List<Transaction> transactions, Long walletId,
            boolean isIncome) {
        Map<Integer, BigDecimal> result = new HashMap<>();
        for (Transaction t : transactions) {
            if (isIncome && isIncome(t, walletId)) {
                int week = t.getTransactionDate().get(WeekFields.ISO.weekOfWeekBasedYear());
                result.put(week, result.getOrDefault(week, BigDecimal.ZERO).add(t.getAmount()));
            } else if (!isIncome && isOutcome(t, walletId)) {
                int week = t.getTransactionDate().get(WeekFields.ISO.weekOfWeekBasedYear());
                result.put(week, result.getOrDefault(week, BigDecimal.ZERO).add(t.getAmount()));
            }
        }

        List<Integer> latestWeeks = getLatestWeeks(4);
        return latestWeeks.stream()
                .map(week -> new FinancialSummaryDto("Week " + week, result.getOrDefault(week, BigDecimal.ZERO),
                        BigDecimal.ZERO))
                .toList();
    }

    public static List<FinancialSummaryDto> aggregateByMonth(List<Transaction> transactions, Long walletId,
            boolean isIncome) {
        Map<Month, BigDecimal> result = new HashMap<>();
        for (Transaction t : transactions) {
            if (isIncome && isIncome(t, walletId)) {
                Month month = t.getTransactionDate().getMonth();
                result.put(month, result.getOrDefault(month, BigDecimal.ZERO).add(t.getAmount()));
            } else if (!isIncome && isOutcome(t, walletId)) {
                Month month = t.getTransactionDate().getMonth();
                result.put(month, result.getOrDefault(month, BigDecimal.ZERO).add(t.getAmount()));
            }
        }

        List<Month> latestMonths = getLatestMonths(4);
        return latestMonths.stream()
                .map(month -> new FinancialSummaryDto(
                        month.getDisplayName(TextStyle.FULL, Locale.ENGLISH),
                        result.getOrDefault(month, BigDecimal.ZERO),
                        BigDecimal.ZERO))
                .toList();
    }

    public static List<FinancialSummaryDto> aggregateByQuarter(List<Transaction> transactions, Long walletId,
            boolean isIncome) {
        Map<Integer, BigDecimal> result = new HashMap<>();
        for (Transaction t : transactions) {
            if (isIncome && isIncome(t, walletId)) {
                int quarter = (t.getTransactionDate().getMonthValue() - 1) / 3 + 1;
                result.put(quarter, result.getOrDefault(quarter, BigDecimal.ZERO).add(t.getAmount()));
            } else if (!isIncome && isOutcome(t, walletId)) {
                int quarter = (t.getTransactionDate().getMonthValue() - 1) / 3 + 1;
                result.put(quarter, result.getOrDefault(quarter, BigDecimal.ZERO).add(t.getAmount()));
            }
        }

        List<Integer> latestQuarters = getLatestQuarters(4);
        return latestQuarters.stream()
                .map(q -> new FinancialSummaryDto("Q" + q, result.getOrDefault(q, BigDecimal.ZERO), BigDecimal.ZERO))
                .toList();
    }

    public static List<FinancialSummaryDto> mergeIncomeOutcome(
            List<FinancialSummaryDto> incomeList,
            List<FinancialSummaryDto> outcomeList) {
        Map<String, FinancialSummaryDto> merged = new LinkedHashMap<>();

        for (FinancialSummaryDto income : incomeList) {
            merged.put(income.getPeriod(),
                    new FinancialSummaryDto(income.getPeriod(), income.getIncome(), BigDecimal.ZERO));
        }

        for (FinancialSummaryDto outcome : outcomeList) {
            merged.compute(outcome.getPeriod(), (k, v) -> {
                if (v == null) {
                    return new FinancialSummaryDto(outcome.getPeriod(), BigDecimal.ZERO, outcome.getOutcome());
                } else {
                    v.setOutcome(outcome.getIncome()); // NOTE: outcome list still has income in that field
                    return v;
                }
            });
        }

        return new ArrayList<>(merged.values());
    }

    // --- Helper methods ---

    private static boolean isIncome(Transaction t, Long walletId) {
        return t.getTransactionType() == Transaction.TransactionType.TOP_UP ||
                (t.getTransactionType() == Transaction.TransactionType.TRANSFER &&
                        t.getRecipientWallet() != null &&
                        Objects.equals(t.getRecipientWallet().getId(), walletId));
    }

    private static boolean isOutcome(Transaction t, Long walletId) {
        return t.getTransactionType() == Transaction.TransactionType.TRANSFER &&
                t.getWallet() != null &&
                Objects.equals(t.getWallet().getId(), walletId);
    }

    private static List<Integer> getLatestWeeks(int count) {
        int currentWeek = LocalDate.now().get(WeekFields.ISO.weekOfWeekBasedYear());
        List<Integer> weeks = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            weeks.add(currentWeek - i);
        }
        return weeks;
    }

    private static List<Month> getLatestMonths(int count) {
        Month currentMonth = LocalDate.now().getMonth();
        List<Month> months = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            months.add(currentMonth.minus(i));
        }
        return months;
    }

    private static List<Integer> getLatestQuarters(int count) {
        int currentQuarter = (LocalDate.now().getMonthValue() - 1) / 3 + 1;
        List<Integer> quarters = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            quarters.add(currentQuarter - i);
        }
        return quarters;
    }
}
