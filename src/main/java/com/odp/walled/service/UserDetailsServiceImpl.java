package com.odp.walled.service;

import com.odp.walled.exception.ResourceNotFound;
import com.odp.walled.repository.UserRepository;

import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

        @Autowired
        private UserRepository userRepository;

        @Override
        public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException { // actually loads user by
                                                                                               // email. Do not change
                                                                                               // the
                                                                                               // method name.
                com.odp.walled.model.User user = userRepository.findByEmail(email)
                                .orElseThrow(() -> new ResourceNotFound("User not found with email: " + email));

                return User
                                .withUsername(user.getEmail())
                                .password(user.getPassword())
                                .authorities("ROLE_USER")
                                .build();
        }
}
