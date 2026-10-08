package com.jiism.jiism.controller;

import com.jiism.jiism.model.entities.Article;
import com.jiism.jiism.model.entities.ArticleAssignment;
import com.jiism.jiism.model.entities.ArticleReview;
import com.jiism.jiism.model.entities.Utilisateur;
import com.jiism.jiism.model.enums.ArticleStatus;
import com.jiism.jiism.repository.ArticleAssignmentRepository;
import com.jiism.jiism.repository.ArticleRepository;
import com.jiism.jiism.repository.ArticleReviewRepository;
import com.jiism.jiism.services.NotificationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/admin/articles")
public class AdminArticleController {

    private final ArticleRepository articleRepository;
    private final ArticleAssignmentRepository assignmentRepository;
    private final ArticleReviewRepository reviewRepository;
    private final NotificationService   notificationService;

    public AdminArticleController(
            ArticleRepository articleRepository,
            ArticleAssignmentRepository assignmentRepository,
            ArticleReviewRepository reviewRepository, NotificationService notificationService) {

        this.articleRepository = articleRepository;
        this.assignmentRepository = assignmentRepository;
        this.reviewRepository = reviewRepository;
        this.notificationService = notificationService;
    }

    @GetMapping("/{articleId}/reviews")
    public ResponseEntity<?> getArticleReviews(
            @PathVariable Long articleId) {

        Article article = articleRepository
                .findById(articleId)
                .orElse(null);

        if (article == null) {
            return ResponseEntity.badRequest()
                    .body("Article introuvable");
        }

        return ResponseEntity.ok(
                reviewRepository.findByArticle(article)
        );
    }

    @PutMapping("/{articleId}/accept")
    public ResponseEntity<?> acceptArticle(
            @PathVariable Long articleId) {

        Article article = articleRepository
                .findById(articleId)
                .orElse(null);

        if (article == null) {
            return ResponseEntity.badRequest()
                    .body("Article introuvable");
        }

        if (article.getStatus() != ArticleStatus.UNDER_REVIEW) {
            return ResponseEntity.badRequest()
                    .body("L'article doit être en cours de révision");
        }

        List<ArticleReview> reviews =
                reviewRepository.findByArticle(article);

        if (reviews.isEmpty()) {
            return ResponseEntity.badRequest()
                    .body("L'article doit avoir au moins un avis d'éditeur");
        }

        article.setStatus(ArticleStatus.ACCEPTED);
        article.setLastModified(LocalDateTime.now());
        articleRepository.save(article);

// Notification à l'auteur
        Utilisateur author = article.getSubmittedBy();

        if (author != null) {
            notificationService.createNotification(
                    author,
                    article,
                    "Article accepté",
                    "Votre article \"" + article.getTitle()
                            + "\" a été accepté par l'administration."
            );
        }

        return ResponseEntity.ok(article);
    }

    @PutMapping("/{articleId}/reject")
    public ResponseEntity<?> rejectArticle(
            @PathVariable Long articleId) {

        Article article = articleRepository
                .findById(articleId)
                .orElse(null);

        if (article == null) {
            return ResponseEntity.badRequest()
                    .body("Article introuvable");
        }

        if (article.getStatus() != ArticleStatus.UNDER_REVIEW) {
            return ResponseEntity.badRequest()
                    .body("L'article doit être en cours de révision");
        }

        List<ArticleReview> reviews =
                reviewRepository.findByArticle(article);

        if (reviews.isEmpty()) {
            return ResponseEntity.badRequest()
                    .body("L'article doit avoir au moins un avis d'éditeur");
        }

        article.setStatus(ArticleStatus.REJECTED);
        article.setLastModified(LocalDateTime.now());
        articleRepository.save(article);

// Notification à l'auteur
        Utilisateur author = article.getSubmittedBy();

        if (author != null) {
            notificationService.createNotification(
                    author,
                    article,
                    "Article rejeté",
                    "Votre article \"" + article.getTitle()
                            + "\" a été rejeté par l'administration."
            );
        }

        return ResponseEntity.ok(article);
    }

    @PutMapping("/{articleId}/publish")
    public ResponseEntity<?> publishArticle(@PathVariable Long articleId) {

        Article article = articleRepository.findById(articleId).orElse(null);

        if (article == null) {
            return ResponseEntity.badRequest().body("Article introuvable");
        }

        // Uniquement un article accepté peut être publié
        if (article.getStatus() != ArticleStatus.ACCEPTED) {
            return ResponseEntity.badRequest()
                    .body("Seuls les articles acceptés peuvent être publiés");
        }

        article.setStatus(ArticleStatus.PUBLISHED);
        article.setPublishedAt(LocalDateTime.now());
        article.setLastModified(LocalDateTime.now());

        Article savedArticle = articleRepository.save(article);

        Utilisateur author = article.getSubmittedBy();

        if (author != null) {
            notificationService.createNotification(
                    author,
                    article,
                    "Article publié",
                    "Votre article \"" + article.getTitle()
                            + "\" a été publié avec succès."
            );
        }

        return ResponseEntity.ok(savedArticle);
    }
}