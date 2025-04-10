package com.odp.walled.controller;

import com.odp.walled.dto.AuthRequest;
import com.odp.walled.dto.AuthResponse;
import com.odp.walled.dto.RegisterRequest;
import com.odp.walled.dto.UserResponse;
import com.odp.walled.service.UserService;
import com.odp.walled.util.JwtUtil;
import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
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
    public ResponseEntity<String> register(@RequestBody RegisterRequest request) {
        userService.createUser(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body("Registration successful. Please log in.");
    }

    @PostMapping("/login")
    public ResponseEntity<UserResponse> login(@RequestBody AuthRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(), request.getPassword()));
        // System.out.println(authentication.getName() + "jasdjsd");
        // System.out.println(request.getPassword());
        // System.out.println(authentication);
        // SecurityContextHolder.getContext().setAuthentication(authentication);

        // String token = jwtUtil.generateToken(authentication.getName());
        // System.out.println("1233423hk" + token);
        UserResponse user = userService.getUserByEmail(authentication.getName());
        // System.out.println(user);

        return ResponseEntity.ok(user);
    }
}
