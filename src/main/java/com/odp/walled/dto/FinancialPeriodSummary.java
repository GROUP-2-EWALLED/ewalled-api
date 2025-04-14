package com.odp.walled.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
@AllArgsConstructor
public class FinancialPeriodSummary {
    private BigDecimal totalIncome;
    private BigDecimal totalOutcome;
    private BigDecimal netBalance;
    private List<FinancialSummaryDto> data;
}
