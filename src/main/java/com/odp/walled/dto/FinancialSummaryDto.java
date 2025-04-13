package com.odp.walled.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
public class FinancialSummaryDto {
    private String period;
    private BigDecimal income;
    private BigDecimal outcome;
}
