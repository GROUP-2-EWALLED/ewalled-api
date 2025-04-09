package com.odp.walled.dto;

import lombok.Data;

@Data
public class RegisterRequest {
    private String email;
    private String fullname;
    private String password;
    private String phoneNumber;
    private String avatarUrl;
}
