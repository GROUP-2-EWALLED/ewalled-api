package com.odp.walled.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class LoginResponse {
    private UserResponse user;
    private WalletResponse wallet;
}
