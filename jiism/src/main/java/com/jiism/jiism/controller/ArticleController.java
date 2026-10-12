package com.jiism.jiism.controller;

import com.jiism.jiism.model.entities.Article;
import com.jiism.jiism.model.entities.Utilisateur;
import com.jiism.jiism.model.enums.ArticleStatus;
import com.jiism.jiism.repository.ArticleRepository;
import com.jiism.jiism.repository.UserRepository;
import com.jiism.jiism.services.NotificationService;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/article")

public class ArticleController {

    private final ArticleRepository articleRepository;
    private final UserRepository userRepository;
    private final NotificationService notificationService;

    public ArticleController(
            ArticleRepository articleRepository,
            UserRepository userRepository,
            NotificationService notificationService) {

        this.articleRepository = articleRepository;
        this.userRepository = userRepository;
        this.notificationService = notificationService;
    }

    // ==========================================
    // GET TOUS LES ARTICLES
    // ==========================================

    @GetMapping("/articles")
    public List<Article> getAllArticles() {
        return articleRepository.findAll();
    }

    // ==========================================
    // SOUMETTRE UN ARTICLE
    // ==========================================

    @PostMapping
    public ResponseEntity<?> createArticle(
            @RequestBody Article article,
            Authentication authentication) {

        // Vérifier que l'utilisateur est authentifié
        if (authentication == null) {
            return ResponseEntity
                    .status(401)
                    .body("Utilisateur non authentifié");
        }

        // Récupérer le username de l'utilisateur connecté
        String username = authentication.getName();

        // Chercher l'utilisateur dans la base
        Utilisateur user = userRepository
                .findByUsername(username)
                .orElse(null);

        if (user == null) {
            return ResponseEntity
                    .badRequest()
                    .body("Utilisateur introuvable");
        }

        // ==========================================
        // ASSOCIER L'ARTICLE À L'AUTEUR
        // ==========================================

        article.setSubmittedBy(user);

        // ==========================================
        // STATUT INITIAL
        // ==========================================

        article.setStatus(ArticleStatus.PENDING);

        // ==========================================
        // DATES
        // ==========================================

        article.setSubmittedAt(LocalDateTime.now());
        article.setLastModified(LocalDateTime.now());

        // ==========================================
        // VALEURS INITIALES
        // ==========================================

        if (article.getDownloadCount() == null) {
            article.setDownloadCount(0);
        }

        if (article.getConsultCount() == null) {
            article.setConsultCount(0);
        }

        // ==========================================
        // ENREGISTRER L'ARTICLE
        // ==========================================

        Article savedArticle = articleRepository.save(article);

        // ==========================================
        // CRÉER LA NOTIFICATION POUR L'AUTEUR
        // ==========================================

        notificationService.createNotification(
                user,
                savedArticle,
                "Article soumis",
                "Votre article \""
                        + savedArticle.getTitle()
                        + "\" a été soumis avec succès et est en attente de traitement."
        );

        // ==========================================
        // RETOURNER L'ARTICLE
        // ==========================================

        return ResponseEntity.ok(savedArticle);
    }

    // ==========================================
// GET MES ARTICLES
// ==========================================

    @GetMapping("/my")
    public ResponseEntity<?> getMyArticles(Authentication authentication) {

        // Vérifier que l'utilisateur est authentifié
        if (authentication == null) {
            return ResponseEntity
                    .status(401)
                    .body("Utilisateur non authentifié");
        }

        // Récupérer le username de l'utilisateur connecté
        String username = authentication.getName();

        // Chercher l'utilisateur
        Utilisateur user = userRepository
                .findByUsername(username)
                .orElse(null);

        if (user == null) {
            return ResponseEntity
                    .status(404)
                    .body("Utilisateur introuvable");
        }

        // Récupérer uniquement ses articles
        List<Article> articles =
                articleRepository.findBySubmittedBy(user);

        return ResponseEntity.ok(articles);
    }
}