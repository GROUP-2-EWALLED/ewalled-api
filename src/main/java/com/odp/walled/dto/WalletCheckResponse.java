package com.odp.walled.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor

public class WalletCheckResponse {
    private String accountNumber;
    private String fullName;
}
