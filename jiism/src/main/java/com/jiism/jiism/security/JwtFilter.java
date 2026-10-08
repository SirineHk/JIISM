package com.jiism.jiism.security;

import com.jiism.jiism.model.entities.Utilisateur;
import com.jiism.jiism.repository.UserRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class JwtFilter extends OncePerRequestFilter {

    private final UserRepository userRepository;

    public JwtFilter(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        String authHeader = request.getHeader("Authorization");

        // Vérifier la présence du header Authorization
        if (authHeader != null && authHeader.startsWith("Bearer ")) {

            String token = authHeader.substring(7);

            try {
                // Extraire le username du JWT
                String username = JwtUtil.extractUsername(token);

                // Vérifier le JWT avant de faire confiance au username
                if (username != null
                        && JwtUtil.validateToken(token, username)
                        && SecurityContextHolder.getContext().getAuthentication() == null) {

                    // Rechercher l'utilisateur
                    Utilisateur user = userRepository
                            .findByUsername(username)
                            .orElse(null);

                    // Vérifier que l'utilisateur existe
                    // et que son email est vérifié
                    if (user != null && user.isEmailVerified()) {

                        List<SimpleGrantedAuthority> authorities =
                                user.getRoles()
                                        .stream()
                                        .map(role -> new SimpleGrantedAuthority(
                                                role.startsWith("ROLE_")
                                                        ? role
                                                        : "ROLE_" + role
                                        ))
                                        .collect(Collectors.toList());

                        // Créer l'authentification Spring Security
                        UsernamePasswordAuthenticationToken authToken =
                                new UsernamePasswordAuthenticationToken(
                                        username,
                                        null,
                                        authorities
                                );

                        // Enregistrer l'utilisateur dans le SecurityContext
                        SecurityContextHolder
                                .getContext()
                                .setAuthentication(authToken);
                    }
                }

            } catch (Exception e) {
                // JWT invalide ou expiré :
                // on ne crée pas d'authentification.
                SecurityContextHolder.clearContext();
            }
        }

        // Continuer la chaîne des filtres
        filterChain.doFilter(request, response);
    }
}