package com.odp.walled.dto;

import lombok.Data;
import lombok.AllArgsConstructor;

@Data
@AllArgsConstructor
public class AuthResponse {
    private String token;
    private UserResponse user;
    private WalletResponse wallet;
}
