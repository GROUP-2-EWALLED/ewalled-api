package com.odp.walled.service;

import com.odp.walled.dto.UserRequest;
import com.odp.walled.dto.UserResponse;
import com.odp.walled.dto.RegisterRequest;
import com.odp.walled.exception.DuplicateException;
import com.odp.walled.exception.ResourceNotFound;
import com.odp.walled.mapper.UserMapper;
import com.odp.walled.model.User;
import com.odp.walled.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final WalletService walletService;

    public UserResponse createUser(RegisterRequest request) {
        if (request.getPhoneNumber() != null &&
                userRepository.existsByPhoneNumber(request.getPhoneNumber())) {
            throw new DuplicateException("Phone number already in use");
        }
        if (userRepository.existsByEmail(request.getEmail()))
            throw new DuplicateException("Email already exists");

        if (userRepository.existsByUsername(request.getUsername()))
            throw new DuplicateException("Username already exists");

        User user = userMapper.toEntity(request);
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        user = userRepository.save(user);

        walletService.createWallet(user.getId());

        return userMapper.toResponse(user);
    }

    public UserResponse getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFound("User not found"));
        return userMapper.toResponse(user);
    }

    public UserResponse getUserByEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFound("User not found"));
        return userMapper.toResponse(user);
    }

    public User getUserObjectByEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFound("User not found"));
        return user;
    }
}