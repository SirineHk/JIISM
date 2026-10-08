package com.jiism.jiism.controller;

import com.jiism.jiism.model.entities.Article;
import com.jiism.jiism.model.entities.ArticleAssignment;
import com.jiism.jiism.model.entities.Utilisateur;
import com.jiism.jiism.model.enums.ArticleAssignmentStatus;
import com.jiism.jiism.model.enums.ArticleStatus;
import com.jiism.jiism.repository.ArticleAssignmentRepository;
import com.jiism.jiism.repository.ArticleRepository;
import com.jiism.jiism.repository.UserRepository;
import com.jiism.jiism.services.NotificationService;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/article-assignments")
public class ArticleAssignmentController {

    private final ArticleAssignmentRepository assignmentRepository;
    private final ArticleRepository articleRepository;
    private final UserRepository userRepository;
    private final NotificationService notificationService;

    public ArticleAssignmentController(
            ArticleAssignmentRepository assignmentRepository,
            ArticleRepository articleRepository,
            UserRepository userRepository,
            NotificationService notificationService) {

        this.assignmentRepository = assignmentRepository;
        this.articleRepository = articleRepository;
        this.userRepository = userRepository;
        this.notificationService = notificationService;
    }

    // ==========================================
    // ASSIGNER UN ARTICLE A UN EDITEUR
    // ==========================================

    @PostMapping
    public ResponseEntity<?> assignArticle(
            @RequestParam Long articleId,
            @RequestParam Long editorId,
            @RequestParam(required = false) LocalDateTime dueDate,
            Authentication authentication) {

        // ------------------------------------------
        // 1. Récupérer l'ADMIN connecté
        // ------------------------------------------

        String username = authentication.getName();

        Utilisateur admin = userRepository
                .findByUsername(username)
                .orElse(null);

        if (admin == null) {
            return ResponseEntity
                    .badRequest()
                    .body("Administrateur introuvable");
        }

        // ------------------------------------------
        // 2. Vérifier que l'utilisateur est ADMIN
        // ------------------------------------------

        if (admin.getRoles() == null
                || !admin.getRoles().contains("ADMIN")) {

            return ResponseEntity
                    .status(403)
                    .body("Seul un administrateur peut assigner un article");
        }

        // ------------------------------------------
        // 3. Récupérer l'article
        // ------------------------------------------

        Article article = articleRepository
                .findById(articleId)
                .orElse(null);

        if (article == null) {
            return ResponseEntity
                    .badRequest()
                    .body("Article introuvable");
        }

        // ------------------------------------------
        // 4. Récupérer l'éditeur
        // ------------------------------------------

        Utilisateur editor = userRepository
                .findById(editorId)
                .orElse(null);

        if (editor == null) {
            return ResponseEntity
                    .badRequest()
                    .body("Éditeur introuvable");
        }

        // ------------------------------------------
        // 5. Vérifier le rôle EDITEUR
        // ------------------------------------------

        if (editor.getRoles() == null
                || !editor.getRoles().contains("EDITEUR")) {

            return ResponseEntity
                    .badRequest()
                    .body("L'utilisateur sélectionné n'est pas un éditeur");
        }

        // ------------------------------------------
        // 6. Vérifier le statut de l'article
        // ------------------------------------------

        if (article.getStatus() != ArticleStatus.PENDING) {

            return ResponseEntity
                    .badRequest()
                    .body("Seuls les articles en attente peuvent être assignés");
        }

        // ------------------------------------------
        // 7. Créer l'affectation
        // ------------------------------------------

        ArticleAssignment assignment = new ArticleAssignment();

        assignment.setArticle(article);
        assignment.setEditor(editor);
        assignment.setAssignedBy(admin);
        assignment.setAssignedAt(LocalDateTime.now());
        assignment.setDueDate(dueDate);
        assignment.setStatus(ArticleAssignmentStatus.ASSIGNED);

        // ------------------------------------------
        // 8. Modifier le statut de l'article
        // ------------------------------------------

        article.setStatus(ArticleStatus.UNDER_REVIEW);
        article.setLastModified(LocalDateTime.now());

        articleRepository.save(article);

        // ------------------------------------------
        // 9. Sauvegarder l'affectation
        // ------------------------------------------

        ArticleAssignment savedAssignment =
                assignmentRepository.save(assignment);

        // ------------------------------------------
        // 10. NOTIFICATION POUR L'EDITEUR
        // ------------------------------------------

        notificationService.createNotification(
                editor,
                article,
                "Nouvel article à évaluer",
                "Un nouvel article intitulé \""
                        + article.getTitle()
                        + "\" vous a été assigné pour évaluation."
        );

        return ResponseEntity.ok(savedAssignment);
    }

    // ==========================================
    // LISTE DES AFFECTATIONS
    // ==========================================

    @GetMapping
    public List<ArticleAssignment> getAllAssignments() {
        return assignmentRepository.findAll();
    }

    // ==========================================
    // MES AFFECTATIONS
    // ==========================================

    @GetMapping("/my")
    public ResponseEntity<?> getMyAssignments(
            Authentication authentication) {

        String username = authentication.getName();

        Utilisateur editor = userRepository
                .findByUsername(username)
                .orElse(null);

        if (editor == null) {
            return ResponseEntity
                    .badRequest()
                    .body("Utilisateur introuvable");
        }

        if (editor.getRoles() == null
                || !editor.getRoles().contains("EDITEUR")) {

            return ResponseEntity
                    .status(403)
                    .body("Seuls les éditeurs peuvent consulter leurs affectations");
        }

        List<ArticleAssignment> assignments =
                assignmentRepository.findByEditor(editor);

        return ResponseEntity.ok(assignments);
    }

    // ==========================================
    // COMMENCER UNE REVISION
    // ==========================================

    @PutMapping("/{id}/start")
    public ResponseEntity<?> startReview(
            @PathVariable Long id,
            Authentication authentication) {

        String username = authentication.getName();

        // ==========================================
        // 1. RÉCUPÉRER L'ÉDITEUR CONNECTÉ
        // ==========================================

        Utilisateur editor = userRepository
                .findByUsername(username)
                .orElse(null);

        if (editor == null) {
            return ResponseEntity
                    .badRequest()
                    .body("Utilisateur introuvable");
        }

        // ==========================================
        // 2. VÉRIFIER LE RÔLE
        // ==========================================

        if (editor.getRoles() == null
                || !editor.getRoles().contains("EDITEUR")) {

            return ResponseEntity
                    .status(403)
                    .body("Seuls les éditeurs peuvent commencer une révision");
        }

        // ==========================================
        // 3. RÉCUPÉRER L'AFFECTATION
        // ==========================================

        ArticleAssignment assignment = assignmentRepository
                .findById(id)
                .orElse(null);

        if (assignment == null) {
            return ResponseEntity
                    .badRequest()
                    .body("Affectation introuvable");
        }

        // ==========================================
        // 4. VÉRIFIER QUE L'AFFECTATION APPARTIENT
        //    À L'ÉDITEUR CONNECTÉ
        // ==========================================

        if (!assignment.getEditor().getId().equals(editor.getId())) {

            return ResponseEntity
                    .status(403)
                    .body("Cette affectation ne vous appartient pas");
        }

        // ==========================================
        // 5. VÉRIFIER LE STATUT
        // ==========================================

        if (assignment.getStatus() != ArticleAssignmentStatus.ASSIGNED) {

            return ResponseEntity
                    .badRequest()
                    .body("L'affectation n'est pas dans l'état ASSIGNED");
        }

        // ==========================================
        // 6. RÉCUPÉRER L'ARTICLE
        // ==========================================

        Article article = assignment.getArticle();

        // ==========================================
        // 7. PASSER L'AFFECTATION EN IN_REVIEW
        // ==========================================

        assignment.setStatus(ArticleAssignmentStatus.IN_REVIEW);

        ArticleAssignment savedAssignment =
                assignmentRepository.save(assignment);

        // ==========================================
// 8. NOTIFICATION POUR L'AUTEUR
// ==========================================

        System.out.println("===== DEBUG START REVIEW =====");
        System.out.println("Article ID : " + article.getId());
        System.out.println("Article title : " + article.getTitle());
        System.out.println("SubmittedBy : " + article.getSubmittedBy());
        System.out.println("SubmittedBy ID : "
                + (article.getSubmittedBy() != null
                ? article.getSubmittedBy().getId()
                : "NULL"));
        System.out.println("==============================");

        Utilisateur author = article.getSubmittedBy();

        if (author != null) {

            notificationService.createNotification(
                    author,
                    article,
                    "Article en cours de révision",
                    "Votre article \""
                            + article.getTitle()
                            + "\" est maintenant en cours de révision."
            );
        }

        // ==========================================
        // 9. RETOURNER L'AFFECTATION
        // ==========================================

        return ResponseEntity.ok(savedAssignment);
    }

    // ==========================================
    // TERMINER UNE REVISION
    // ==========================================

    @PutMapping("/{id}/complete")
    public ResponseEntity<?> completeReview(
            @PathVariable Long id,
            Authentication authentication) {

        String username = authentication.getName();

        Utilisateur editor = userRepository
                .findByUsername(username)
                .orElse(null);

        if (editor == null) {
            return ResponseEntity
                    .badRequest()
                    .body("Utilisateur introuvable");
        }

        // Vérifier que l'utilisateur est éditeur
        if (editor.getRoles() == null
                || !editor.getRoles().contains("EDITEUR")) {

            return ResponseEntity
                    .status(403)
                    .body("Seuls les éditeurs peuvent terminer une révision");
        }

        // Rechercher l'affectation
        ArticleAssignment assignment = assignmentRepository
                .findById(id)
                .orElse(null);

        if (assignment == null) {
            return ResponseEntity
                    .badRequest()
                    .body("Affectation introuvable");
        }

        // Vérifier que l'affectation appartient à cet éditeur
        if (!assignment.getEditor().getId().equals(editor.getId())) {
            return ResponseEntity
                    .status(403)
                    .body("Cette affectation ne vous appartient pas");
        }

        // Vérifier le statut actuel
        if (assignment.getStatus() != ArticleAssignmentStatus.IN_REVIEW) {
            return ResponseEntity
                    .badRequest()
                    .body("L'affectation doit être en cours de révision");
        }

        // Terminer la révision
        assignment.setStatus(ArticleAssignmentStatus.COMPLETED);
        ArticleAssignment savedAssignment = assignmentRepository.save(assignment);

// Notification à l'administrateur qui a effectué l'affectation
        Utilisateur admin = assignment.getAssignedBy();

        if (admin != null) {
            notificationService.createNotification(
                    admin,
                    assignment.getArticle(),
                    "Révision terminée",
                    "La révision de l'article \""
                            + assignment.getArticle().getTitle()
                            + "\" est terminée. Vous pouvez maintenant consulter l'évaluation."
            );
        }

        return ResponseEntity.ok(savedAssignment);
    }

// ==========================================
// ACCEPTER UN ARTICLE
// ==========================================

    @PutMapping("/{id}/accept")
    public ResponseEntity<?> acceptArticle(
            @PathVariable Long id,
            Authentication authentication) {

        String username = authentication.getName();

        // 1. Récupérer l'éditeur connecté
        Utilisateur editor = userRepository
                .findByUsername(username)
                .orElse(null);

        if (editor == null) {
            return ResponseEntity
                    .badRequest()
                    .body("Utilisateur introuvable");
        }

        // 2. Vérifier le rôle
        if (editor.getRoles() == null
                || !editor.getRoles().contains("EDITEUR")) {

            return ResponseEntity
                    .status(403)
                    .body("Seuls les éditeurs peuvent accepter un article");
        }

        // 3. Récupérer l'affectation
        ArticleAssignment assignment = assignmentRepository
                .findById(id)
                .orElse(null);

        if (assignment == null) {
            return ResponseEntity
                    .badRequest()
                    .body("Affectation introuvable");
        }

        // 4. Vérifier que l'affectation appartient à l'éditeur
        if (!assignment.getEditor().getId().equals(editor.getId())) {

            return ResponseEntity
                    .status(403)
                    .body("Cette affectation ne vous appartient pas");
        }

        // 5. Vérifier que la révision est terminée
        if (assignment.getStatus() != ArticleAssignmentStatus.COMPLETED) {

            return ResponseEntity
                    .badRequest()
                    .body("La révision doit être terminée avant de prendre une décision");
        }

        // 6. Récupérer l'article
        Article article = assignment.getArticle();

        // 7. Vérifier que l'article est bien dans l'état attendu
        if (article.getStatus() != ArticleStatus.UNDER_REVIEW) {

            return ResponseEntity
                    .badRequest()
                    .body("L'article ne peut plus être accepté dans son état actuel");
        }

        // 8. Accepter l'article
        article.setStatus(ArticleStatus.ACCEPTED);
        article.setLastModified(LocalDateTime.now());

        articleRepository.save(article);

        // 9. Notification à l'auteur
        Utilisateur author = article.getSubmittedBy();

        if (author != null) {
            notificationService.createNotification(
                    author,
                    article,
                    "Article accepté",
                    "Votre article \"" + article.getTitle()
                            + "\" a été accepté par l'éditeur."
            );
        }

        return ResponseEntity.ok(assignment);
    }


// ==========================================
// REJETER UN ARTICLE
// ==========================================

    @PutMapping("/{id}/reject")
    public ResponseEntity<?> rejectArticle(
            @PathVariable Long id,
            Authentication authentication) {

        String username = authentication.getName();

        // 1. Récupérer l'éditeur connecté
        Utilisateur editor = userRepository
                .findByUsername(username)
                .orElse(null);

        if (editor == null) {
            return ResponseEntity
                    .badRequest()
                    .body("Utilisateur introuvable");
        }

        // 2. Vérifier le rôle
        if (editor.getRoles() == null
                || !editor.getRoles().contains("EDITEUR")) {

            return ResponseEntity
                    .status(403)
                    .body("Seuls les éditeurs peuvent rejeter un article");
        }

        // 3. Récupérer l'affectation
        ArticleAssignment assignment = assignmentRepository
                .findById(id)
                .orElse(null);

        if (assignment == null) {
            return ResponseEntity
                    .badRequest()
                    .body("Affectation introuvable");
        }

        // 4. Vérifier que l'affectation appartient à l'éditeur
        if (!assignment.getEditor().getId().equals(editor.getId())) {

            return ResponseEntity
                    .status(403)
                    .body("Cette affectation ne vous appartient pas");
        }

        // 5. Vérifier que la révision est terminée
        if (assignment.getStatus() != ArticleAssignmentStatus.COMPLETED) {

            return ResponseEntity
                    .badRequest()
                    .body("La révision doit être terminée avant de prendre une décision");
        }

        // 6. Récupérer l'article
        Article article = assignment.getArticle();

        // 7. Vérifier que l'article est bien dans l'état attendu
        if (article.getStatus() != ArticleStatus.UNDER_REVIEW) {

            return ResponseEntity
                    .badRequest()
                    .body("L'article ne peut plus être rejeté dans son état actuel");
        }

        // 8. Rejeter l'article
        article.setStatus(ArticleStatus.REJECTED);
        article.setLastModified(LocalDateTime.now());

        articleRepository.save(article);

        // 9. Notification à l'auteur
        Utilisateur author = article.getSubmittedBy();

        if (author != null) {
            notificationService.createNotification(
                    author,
                    article,
                    "Article rejeté",
                    "Votre article \"" + article.getTitle()
                            + "\" a été rejeté par l'éditeur."
            );
        }

        return ResponseEntity.ok(assignment);
    }





    @GetMapping("/my/overdue")
    public ResponseEntity<?> getMyOverdueAssignments(
            Authentication authentication) {

        String username = authentication.getName();

        Utilisateur editor =
                userRepository.findByUsername(username).orElse(null);

        if (editor == null) {
            return ResponseEntity.badRequest()
                    .body("Utilisateur introuvable");
        }

        if (editor.getRoles() == null ||
                !editor.getRoles().contains("EDITEUR")) {

            return ResponseEntity.status(403)
                    .body("Seuls les éditeurs peuvent consulter leurs deadlines");
        }

        List<ArticleAssignment> assignments =
                assignmentRepository.findOverdueAssignments(
                        editor,
                        LocalDateTime.now(),
                        ArticleAssignmentStatus.COMPLETED
                );

        return ResponseEntity.ok(assignments);
    }

    @GetMapping("/my/upcoming")
    public ResponseEntity<?> getMyUpcomingAssignments(
            Authentication authentication) {

        String username = authentication.getName();

        Utilisateur editor =
                userRepository.findByUsername(username).orElse(null);

        if (editor == null) {
            return ResponseEntity.badRequest()
                    .body("Utilisateur introuvable");
        }

        if (editor.getRoles() == null
                || !editor.getRoles().contains("EDITEUR")) {

            return ResponseEntity.status(403)
                    .body("Seuls les éditeurs peuvent consulter leurs deadlines");
        }

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime threeDaysLater = now.plusDays(3);

        List<ArticleAssignment> assignments =
                assignmentRepository
                        .findByEditorAndDueDateBetweenAndStatusNot(
                                editor,
                                now,
                                threeDaysLater,
                                ArticleAssignmentStatus.COMPLETED
                        );

        return ResponseEntity.ok(assignments);
    }
}