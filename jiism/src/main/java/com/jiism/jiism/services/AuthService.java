package com.jiism.jiism.services;

import com.jiism.jiism.config.JwtResponse;
import com.jiism.jiism.model.dto.LoginRequest;
import com.jiism.jiism.model.entities.Utilisateur;
import com.jiism.jiism.model.entities.VerificationToken;
import com.jiism.jiism.repository.UserRepository;
import com.jiism.jiism.repository.VerificationTokenRepository;
import com.jiism.jiism.security.JwtUtil;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.UnsupportedEncodingException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final VerificationTokenRepository tokenRepository;
    private final JavaMailSender mailSender;
    private final AuthenticationManager authenticationManager;

    public AuthService(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            VerificationTokenRepository tokenRepository,
            JavaMailSender mailSender,
            AuthenticationManager authenticationManager) {

        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.tokenRepository = tokenRepository;
        this.mailSender = mailSender;
        this.authenticationManager = authenticationManager;
    }
    public ResponseEntity<?> loginService(LoginRequest loginRequest){
        try {
            // Authentifier l’utilisateur
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.getUsername(),
                            loginRequest.getPassword()
                    )
            );
            SecurityContextHolder.getContext().setAuthentication(authentication);
            // Récupérer username et rôles
            String username = authentication.getName();
            List<String> roles = authentication.getAuthorities()
                    .stream()
                    .map(GrantedAuthority::getAuthority)
                    .toList();
            // Générer token JWT
            String token = JwtUtil.generateToken(username, roles);
            return ResponseEntity.ok(new JwtResponse(token));
        } catch (Exception e) {
            // Authentification échouée
            return ResponseEntity
                    .status(401)
                    .body("Nom d'utilisateur ou mot de passe invalide");
        }
    }

    public ResponseEntity<?> registerService( Utilisateur user) throws MessagingException, UnsupportedEncodingException {
        if (userRepository.findByUsername(user.getUsername()).isPresent()) {
            return ResponseEntity.badRequest().body("Username already exists");
        }
        if (userRepository.findByEmail(user.getEmail()).isPresent()) {
            return ResponseEntity.badRequest().body("Email already exists");
        }
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        // Rôle par défaut
        if (user.getRoles() == null || user.getRoles().isEmpty()) {
            user.setRoles(List.of("AUTEUR"));
        }
        // Sauvegarde utilisateur avec enabled = false
        user.setEmailVerified(false);
        userRepository.save(user);
        // Génération du token
        String token = UUID.randomUUID().toString();
        VerificationToken vToken = new VerificationToken();
        vToken.setToken(token);
        vToken.setUser(user);
        vToken.setExpiryDate(LocalDateTime.now().plusHours(24)); // valable 24h
        tokenRepository.save(vToken);
        // Envoi email
        String link =
                "http://jiism-backend-jiism.apps.dell64.dottn.com/auth/verify?token="
                        + token;
        sendEmail(user.getEmail(), "Vérification Email",
                "<p>Bonjour " + user.getUsername() + ",</p>" +
                        "<p>Cliquez sur le lien ci-dessous pour activer votre compte :</p>" +
                        "<a href='" + link + "'>Activer mon compte</a>");

        return ResponseEntity.ok("Utilisateur enregistré. Vérifiez votre email pour activer votre compte.");
    }
    public ResponseEntity<?> verifyAccountService( String token) throws MessagingException, UnsupportedEncodingException {
        Optional<VerificationToken> optionalToken =
                tokenRepository.findByToken(token);

        if (optionalToken.isEmpty()) {
            return ResponseEntity.badRequest().body("Token invalide !");
        }

        VerificationToken vToken = optionalToken.get();
        if (vToken.isExpired()) {
            return ResponseEntity.badRequest().body("Lien expiré !");
        }
        Utilisateur user = vToken.getUser();
        user.setEmailVerified(true);
        userRepository.save(user);
        sendEmail(user.getEmail(), "Confirmation d'inscription",
                "Bienvenue " + user.getUsername() + ", votre compte a bien été créé.");
        return ResponseEntity.ok("Votre compte a été activé avec succès !");
    }


    public void sendEmail(String to, String subject, String body) throws MessagingException, UnsupportedEncodingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);
        helper.setFrom("no-reply@jiism-platform.com", "JIISM Platform");
        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(body, true);
        mailSender.send(message);
    }
}
