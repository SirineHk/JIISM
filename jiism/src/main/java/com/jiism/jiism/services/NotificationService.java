package com.jiism.jiism.services;

import com.jiism.jiism.model.entities.Article;
import com.jiism.jiism.model.entities.Notification;
import com.jiism.jiism.model.entities.Utilisateur;
import com.jiism.jiism.repository.NotificationRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class NotificationService {

    private final NotificationRepository notificationRepository;

    public NotificationService(
            NotificationRepository notificationRepository) {

        this.notificationRepository = notificationRepository;
    }

    // ==========================================
    // CREER UNE NOTIFICATION
    // ==========================================

    public Notification createNotification(
            Utilisateur user,
            Article article,
            String title,
            String message) {

        Notification notification = new Notification();

        notification.setUser(user);
        notification.setArticle(article);
        notification.setTitle(title);
        notification.setMessage(message);
        notification.setRead(false);
        notification.setCreatedAt(LocalDateTime.now());

        // Notification classique
        notification.setType("GENERAL");
        notification.setReferenceId(null);

        return notificationRepository.save(notification);
    }

    // ==========================================
    // CREER UNE NOTIFICATION DE DEADLINE
    // ==========================================

    public Notification createDeadlineNotification(
            Utilisateur user,
            Article article,
            String type,
            String title,
            String message,
            Long assignmentId) {

        // Vérifier si cette notification existe déjà
        boolean alreadyExists =
                notificationRepository.existsByUserAndTypeAndReferenceId(
                        user,
                        type,
                        assignmentId
                );

        // Si elle existe déjà, ne rien créer
        if (alreadyExists) {
            return null;
        }

        Notification notification = new Notification();

        notification.setUser(user);
        notification.setArticle(article);
        notification.setTitle(title);
        notification.setMessage(message);
        notification.setRead(false);
        notification.setCreatedAt(LocalDateTime.now());

        notification.setType(type);
        notification.setReferenceId(assignmentId);

        return notificationRepository.save(notification);
    }

    // ==========================================
    // TOUTES LES NOTIFICATIONS D'UN UTILISATEUR
    // ==========================================

    public List<Notification> getUserNotifications(
            Utilisateur user) {

        return notificationRepository
                .findByUserOrderByCreatedAtDesc(user);
    }

    // ==========================================
    // NOTIFICATIONS NON LUES
    // ==========================================

    public List<Notification> getUnreadNotifications(
            Utilisateur user) {

        return notificationRepository
                .findByUserAndReadFalseOrderByCreatedAtDesc(user);
    }

    // ==========================================
    // NOMBRE NON LUES
    // ==========================================

    public long getUnreadCount(Utilisateur user) {

        return notificationRepository
                .countByUserAndReadFalse(user);
    }

    // ==========================================
    // MARQUER COMME LUE
    // ==========================================

    public Notification markAsRead(Notification notification) {

        notification.setRead(true);

        return notificationRepository.save(notification);
    }

    // ==========================================
    // MARQUER TOUTES COMME LUES
    // ==========================================

    public void markAllAsRead(Utilisateur user) {

        List<Notification> notifications =
                notificationRepository
                        .findByUserAndReadFalseOrderByCreatedAtDesc(user);

        for (Notification notification : notifications) {
            notification.setRead(true);
        }

        notificationRepository.saveAll(notifications);
    }
}