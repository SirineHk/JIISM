package com.jiism.jiism.services;

import com.jiism.jiism.model.entities.ArticleAssignment;
import com.jiism.jiism.model.enums.ArticleAssignmentStatus;
import com.jiism.jiism.repository.ArticleAssignmentRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class DeadlineNotificationScheduler {

    private final ArticleAssignmentRepository assignmentRepository;
    private final NotificationService notificationService;

    public DeadlineNotificationScheduler(
            ArticleAssignmentRepository assignmentRepository,
            NotificationService notificationService) {

        this.assignmentRepository = assignmentRepository;
        this.notificationService = notificationService;
    }

    // ==========================================
    // VERIFICATION AUTOMATIQUE DES DEADLINES
    // ==========================================

    @Scheduled(fixedRate = 3600000)
    @Transactional
    public void checkDeadlines() {

        LocalDateTime now = LocalDateTime.now();

        System.out.println("==========================================");
        System.out.println("VERIFICATION AUTOMATIQUE DES DEADLINES");
        System.out.println("Date : " + now);
        System.out.println("==========================================");

        checkUpcomingDeadlines(now);
        checkOverdueDeadlines(now);
    }

    // ==========================================
    // DEADLINES PROCHES
    // ==========================================

    private void checkUpcomingDeadlines(LocalDateTime now) {

        LocalDateTime threeDaysLater = now.plusDays(3);

        List<ArticleAssignment> assignments =
                assignmentRepository.findAll();

        for (ArticleAssignment assignment : assignments) {

            if (assignment.getDueDate() == null) {
                continue;
            }

            if (assignment.getStatus() ==
                    ArticleAssignmentStatus.COMPLETED) {
                continue;
            }

            LocalDateTime dueDate = assignment.getDueDate();

            // Deadline entre maintenant et dans 3 jours
            if (!dueDate.isBefore(now)
                    && !dueDate.isAfter(threeDaysLater)) {

                notificationService.createDeadlineNotification(
                        assignment.getEditor(),
                        assignment.getArticle(),
                        "DEADLINE_UPCOMING",
                        "Deadline proche",
                        "La deadline pour l'évaluation de l'article \""
                                + assignment.getArticle().getTitle()
                                + "\" approche. Il vous reste moins de 3 jours.",
                        assignment.getId()
                );
            }
        }
    }

    // ==========================================
    // DEADLINES DEPASSEES
    // ==========================================

    private void checkOverdueDeadlines(LocalDateTime now) {

        List<ArticleAssignment> assignments =
                assignmentRepository.findAll();

        for (ArticleAssignment assignment : assignments) {

            if (assignment.getDueDate() == null) {
                continue;
            }

            if (assignment.getStatus() ==
                    ArticleAssignmentStatus.COMPLETED) {
                continue;
            }

            // Deadline dépassée
            if (assignment.getDueDate().isBefore(now)) {

                notificationService.createDeadlineNotification(
                        assignment.getEditor(),
                        assignment.getArticle(),
                        "DEADLINE_OVERDUE",
                        "Deadline dépassée",
                        "La deadline pour l'évaluation de l'article \""
                                + assignment.getArticle().getTitle()
                                + "\" est dépassée. Merci de terminer l'évaluation.",
                        assignment.getId()
                );
            }
        }
    }
}