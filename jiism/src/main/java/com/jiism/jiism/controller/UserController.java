 package com.jiism.jiism.controller;

import com.jiism.jiism.model.entities.Utilisateur;
import com.jiism.jiism.repository.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import org.springframework.dao.DataIntegrityViolationException;

@RestController
@RequestMapping("/admin/users")
public class UserController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserController(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder) {

        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // =========================================================
    // CONSULTER TOUS LES UTILISATEURS
    // =========================================================

    @GetMapping
    public ResponseEntity<List<Utilisateur>> getAllUsers() {
        return ResponseEntity.ok(userRepository.findAll());
    }


    // =========================================================
    // CONSULTER UN UTILISATEUR
    // =========================================================

    @GetMapping("/{id}")
    public ResponseEntity<?> getUserById(@PathVariable Long id) {

        Utilisateur user = userRepository.findById(id).orElse(null);

        if (user == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(user);
    }


    // =========================================================
    // AJOUTER UN UTILISATEUR
    // =========================================================

    @PostMapping
    public ResponseEntity<?> createUser(@RequestBody Utilisateur newUser) {

        // Vérification des champs obligatoires
        if (newUser.getUsername() == null
                || newUser.getUsername().isBlank()
                || newUser.getEmail() == null
                || newUser.getEmail().isBlank()
                || newUser.getPassword() == null
                || newUser.getPassword().isBlank()) {

            return ResponseEntity.badRequest()
                    .body("Username, email et mot de passe sont obligatoires.");
        }

        // Vérification du username
        if (userRepository.findByUsername(newUser.getUsername()).isPresent()) {
            return ResponseEntity.badRequest()
                    .body("Ce username existe déjà.");
        }

        // Vérification de l'email
        if (userRepository.findByEmail(newUser.getEmail()).isPresent()) {
            return ResponseEntity.badRequest()
                    .body("Cet email existe déjà.");
        }

        // Hash du mot de passe
        newUser.setPassword(
                passwordEncoder.encode(newUser.getPassword())
        );

        // Créé par l'administrateur => compte directement vérifié
        newUser.setEmailVerified(true);

        // Rôle par défaut
        if (newUser.getRoles() == null || newUser.getRoles().isEmpty()) {

            newUser.setRoles(new ArrayList<>());
            newUser.getRoles().add("AUTEUR");

        } else {

            String role = newUser.getRoles().get(0).toUpperCase();

            if (!role.equals("ADMIN")
                    && !role.equals("EDITEUR")
                    && !role.equals("AUTEUR")
                    && !role.equals("PUBLIC")) {

                return ResponseEntity.badRequest()
                        .body("Rôle invalide. Utilisez ADMIN, EDITEUR, AUTEUR ou PUBLIC.");
            }

            newUser.getRoles().clear();
            newUser.getRoles().add(role);
        }

        Utilisateur savedUser = userRepository.save(newUser);

        return ResponseEntity.ok(savedUser);
    }


    // =========================================================
    // MODIFIER UN UTILISATEUR
    // =========================================================

    @PutMapping("/{id}")
    public ResponseEntity<?> updateUser(
            @PathVariable Long id,
            @RequestBody Utilisateur updatedUser) {

        Utilisateur user = userRepository.findById(id).orElse(null);

        if (user == null) {
            return ResponseEntity.notFound().build();
        }

        // Vérification username
        if (updatedUser.getUsername() != null
                && !updatedUser.getUsername().equals(user.getUsername())) {

            if (userRepository.findByUsername(updatedUser.getUsername()).isPresent()) {
                return ResponseEntity.badRequest()
                        .body("Ce username existe déjà.");
            }

            user.setUsername(updatedUser.getUsername());
        }

        // Vérification email
        if (updatedUser.getEmail() != null
                && !updatedUser.getEmail().equals(user.getEmail())) {

            if (userRepository.findByEmail(updatedUser.getEmail()).isPresent()) {
                return ResponseEntity.badRequest()
                        .body("Cet email existe déjà.");
            }

            user.setEmail(updatedUser.getEmail());
        }

        user.setFirstName(updatedUser.getFirstName());
        user.setLastName(updatedUser.getLastName());
        user.setPhone(updatedUser.getPhone());

        // Si un nouveau mot de passe est fourni,
        // on le hash avant de l'enregistrer.
        if (updatedUser.getPassword() != null
                && !updatedUser.getPassword().isBlank()) {

            user.setPassword(
                    passwordEncoder.encode(updatedUser.getPassword())
            );
        }

        return ResponseEntity.ok(userRepository.save(user));
    }


    // =========================================================
    // CHANGER LE ROLE
    // =========================================================

    @PutMapping("/{id}/role")
    public ResponseEntity<?> updateUserRole(
            @PathVariable Long id,
            @RequestParam String role) {

        Utilisateur user = userRepository.findById(id).orElse(null);

        if (user == null) {
            return ResponseEntity.notFound().build();
        }

        role = role.toUpperCase();

        if (!role.equals("ADMIN")
                && !role.equals("EDITEUR")
                && !role.equals("AUTEUR")
                && !role.equals("PUBLIC")) {

            return ResponseEntity.badRequest()
                    .body("Rôle invalide. Utilisez ADMIN, EDITEUR, AUTEUR ou PUBLIC.");
        }

        user.getRoles().clear();
        user.getRoles().add(role);

        return ResponseEntity.ok(userRepository.save(user));
    }


    // =========================================================
    // SUPPRIMER UN UTILISATEUR
    // =========================================================

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable Long id) {
        Utilisateur user = userRepository.findById(id).orElse(null);

        if (user == null) {
            return ResponseEntity.notFound().build();
        }

        try {
            userRepository.delete(user);
            userRepository.flush();

            return ResponseEntity.ok("Utilisateur supprimé avec succès");

        } catch (org.springframework.dao.DataIntegrityViolationException e) {

            return ResponseEntity.status(409)
                    .body("Impossible de supprimer cet utilisateur car il est encore affecté à un ou plusieurs articles.");
        }
    }
}
