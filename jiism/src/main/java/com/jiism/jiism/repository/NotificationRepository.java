package com.jiism.jiism.repository;

import com.jiism.jiism.model.entities.Notification;
import com.jiism.jiism.model.entities.Utilisateur;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NotificationRepository
        extends JpaRepository<Notification, Long> {

    List<Notification> findByUserOrderByCreatedAtDesc(
            Utilisateur user
    );

    List<Notification> findByUserAndReadFalseOrderByCreatedAtDesc(
            Utilisateur user
    );

    long countByUserAndReadFalse(
            Utilisateur user
    );

    // ==========================================
    // NOTIFICATIONS DE DEADLINE
    // ==========================================

    boolean existsByUserAndTypeAndReferenceId(
            Utilisateur user,
            String type,
            Long referenceId
    );
}