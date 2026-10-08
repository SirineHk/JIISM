package com.jiism.jiism.controller;

import com.jiism.jiism.model.dto.LoginRequest;
import com.jiism.jiism.model.entities.Utilisateur;
import com.jiism.jiism.services.AuthService;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.UnsupportedEncodingException;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
        return authService.loginService(loginRequest);
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody Utilisateur user)
            throws MessagingException, UnsupportedEncodingException {
        System.out.println("registerrrrrrrr");

        return authService.registerService(user);
    }

    @GetMapping("/verify")
    public ResponseEntity<?> verifyAccount(@RequestParam("token") String token)
            throws MessagingException, UnsupportedEncodingException {

        return authService.verifyAccountService(token);
    }
}