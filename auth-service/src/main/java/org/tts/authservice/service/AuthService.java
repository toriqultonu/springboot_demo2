package org.tts.authservice.service;

import org.springframework.stereotype.Service;

@Service
public class AuthService {


    public Optional<String> authenticate(LoginRequestDTO loginRequestDTO) {
        Optional<User> user = userService.findByEmail(loginRequestDTO.getEmail());
    }

}