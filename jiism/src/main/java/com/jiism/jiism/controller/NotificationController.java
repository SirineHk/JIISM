package com.jiism.jiism.controller;

import com.jiism.jiism.model.entities.Notification;
import com.jiism.jiism.model.entities.Utilisateur;
import com.jiism.jiism.repository.NotificationRepository;
import com.jiism.jiism.repository.UserRepository;
import com.jiism.jiism.services.NotificationService;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/notifications")
public class NotificationController {

    private final NotificationService notificationService;
    private final UserRepository userRepository;
    private final NotificationRepository notificationRepository;

    public NotificationController(
            NotificationService notificationService,
            UserRepository userRepository,
            NotificationRepository notificationRepository) {

        this.notificationService = notificationService;
        this.userRepository = userRepository;
        this.notificationRepository = notificationRepository;
    }

    // ==========================================
    // MES NOTIFICATIONS
    // ==========================================

    @GetMapping
    public ResponseEntity<?> getMyNotifications(
            Authentication authentication) {

        String username = authentication.getName();

        Utilisateur user = userRepository
                .findByUsername(username)
                .orElse(null);

        if (user == null) {
            return ResponseEntity.badRequest()
                    .body("Utilisateur introuvable");
        }

        return ResponseEntity.ok(
                notificationService.getUserNotifications(user)
        );
    }

    // ==========================================
    // NOTIFICATIONS NON LUES
    // ==========================================

    @GetMapping("/unread")
    public ResponseEntity<?> getUnreadNotifications(
            Authentication authentication) {

        String username = authentication.getName();

        Utilisateur user = userRepository
                .findByUsername(username)
                .orElse(null);

        if (user == null) {
            return ResponseEntity.badRequest()
                    .body("Utilisateur introuvable");
        }

        return ResponseEntity.ok(
                notificationService.getUnreadNotifications(user)
        );
    }

    // ==========================================
    // NOMBRE NON LUES
    // ==========================================

    @GetMapping("/unread/count")
    public ResponseEntity<?> getUnreadCount(
            Authentication authentication) {

        String username = authentication.getName();

        Utilisateur user = userRepository
                .findByUsername(username)
                .orElse(null);

        if (user == null) {
            return ResponseEntity.badRequest()
                    .body("Utilisateur introuvable");
        }

        return ResponseEntity.ok(
                notificationService.getUnreadCount(user)
        );
    }

    // ==========================================
    // MARQUER UNE NOTIFICATION COMME LUE
    // ==========================================

    @PutMapping("/{id}/read")
    public ResponseEntity<?> markAsRead(
            @PathVariable Long id,
            Authentication authentication) {

        String username = authentication.getName();

        Utilisateur user = userRepository
                .findByUsername(username)
                .orElse(null);

        if (user == null) {
            return ResponseEntity.badRequest()
                    .body("Utilisateur introuvable");
        }

        Notification notification =
                notificationRepository.findById(id)
                        .orElse(null);

        if (notification == null) {
            return ResponseEntity.notFound().build();
        }

        if (!notification.getUser().getId().equals(user.getId())) {
            return ResponseEntity.status(403)
                    .body("Cette notification ne vous appartient pas");
        }

        return ResponseEntity.ok(
                notificationService.markAsRead(notification)
        );
    }

    // ==========================================
    // MARQUER TOUTES COMME LUES
    // ==========================================

    @PutMapping("/read-all")
    public ResponseEntity<?> markAllAsRead(
            Authentication authentication) {

        String username = authentication.getName();

        Utilisateur user = userRepository
                .findByUsername(username)
                .orElse(null);

        if (user == null) {
            return ResponseEntity.badRequest()
                    .body("Utilisateur introuvable");
        }

        notificationService.markAllAsRead(user);

        return ResponseEntity.ok(
                "Toutes les notifications ont été marquées comme lues"
        );
    }
}