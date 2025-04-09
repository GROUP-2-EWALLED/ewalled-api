package com.odp.walled.controller;

import com.odp.walled.dto.AuthRequest;
import com.odp.walled.dto.AuthResponse;
import com.odp.walled.dto.RegisterRequest;
import com.odp.walled.dto.UserResponse;
import com.odp.walled.service.UserService;
import com.odp.walled.util.JwtUtil;
import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final UserService userService;

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@RequestBody RegisterRequest request) {
        UserResponse user = userService.createUser(request);
        String token = jwtUtil.generateToken(user.getEmail());

        return ResponseEntity.ok(new AuthResponse(token, user));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody AuthRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(), request.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);

        String token = jwtUtil.generateToken(authentication.getName());
        UserResponse user = userService.getUserByEmail(request.getEmail());

        return ResponseEntity.ok(new AuthResponse(token, user));
    }
}
