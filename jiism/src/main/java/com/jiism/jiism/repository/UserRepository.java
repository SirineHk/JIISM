package com.jiism.jiism.repository;

import com.jiism.jiism.model.entities.Utilisateur;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface UserRepository extends JpaRepository<Utilisateur, Long> {

    Optional<Utilisateur> findByUsername(String username);

    Optional<Utilisateur> findByEmail(String email);

    @Query("""
        SELECT COUNT(DISTINCT u)
        FROM Utilisateur u
        JOIN u.roles r
        WHERE r = :role
    """)
    long countByRole(String role);
}