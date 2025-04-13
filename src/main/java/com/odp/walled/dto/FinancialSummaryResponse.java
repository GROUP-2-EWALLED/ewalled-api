package com.odp.walled.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class FinancialSummaryResponse {
    private FinancialPeriodSummary weekly;
    private FinancialPeriodSummary monthly;
    private FinancialPeriodSummary quarterly;
}
