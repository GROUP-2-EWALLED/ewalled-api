package com.odp.walled.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import com.odp.walled.dto.FinancialSummaryDto;

import java.util.List;

@Data
@AllArgsConstructor
public class FinancialSummaryResponse {
    private List<FinancialSummaryDto> weekly;
    private List<FinancialSummaryDto> monthly;
    private List<FinancialSummaryDto> quarterly;
}
