package com.jiism.jiism.controller;

import com.jiism.jiism.model.dto.ArticleReviewRequest;
import com.jiism.jiism.model.entities.Article;
import com.jiism.jiism.model.entities.ArticleAssignment;
import com.jiism.jiism.model.entities.ArticleReview;
import com.jiism.jiism.model.entities.Utilisateur;
import com.jiism.jiism.model.enums.ArticleAssignmentStatus;
import com.jiism.jiism.repository.ArticleAssignmentRepository;
import com.jiism.jiism.repository.ArticleRepository;
import com.jiism.jiism.repository.ArticleReviewRepository;
import com.jiism.jiism.repository.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/article-reviews")
public class ArticleReviewController {

    private final ArticleReviewRepository reviewRepository;
    private final ArticleRepository articleRepository;
    private final ArticleAssignmentRepository assignmentRepository;
    private final UserRepository userRepository;

    public ArticleReviewController(
            ArticleReviewRepository reviewRepository,
            ArticleRepository articleRepository,
            ArticleAssignmentRepository assignmentRepository,
            UserRepository userRepository) {

        this.reviewRepository = reviewRepository;
        this.articleRepository = articleRepository;
        this.assignmentRepository = assignmentRepository;
        this.userRepository = userRepository;
    }

    @PostMapping
    public ResponseEntity<?> createReview(
            @RequestBody ArticleReviewRequest request,
            Authentication authentication) {

        // 1. Vérifier l'authentification
        if (authentication == null) {
            return ResponseEntity.status(401)
                    .body("Utilisateur non authentifié");
        }

        String username = authentication.getName();

        // 2. Récupérer l'éditeur connecté
        Utilisateur editor = userRepository
                .findByUsername(username)
                .orElse(null);

        if (editor == null) {
            return ResponseEntity.badRequest()
                    .body("Utilisateur introuvable");
        }

        // 3. Vérifier le rôle
        if (editor.getRoles() == null ||
                !editor.getRoles().contains("EDITEUR")) {

            return ResponseEntity.status(403)
                    .body("Seuls les éditeurs peuvent évaluer les articles");
        }

        // 4. Vérifier l'article
        Article article = articleRepository
                .findById(request.getArticleId())
                .orElse(null);

        if (article == null) {
            return ResponseEntity.badRequest()
                    .body("Article introuvable");
        }

        // 5. Vérifier l'affectation de cet éditeur
        List<ArticleAssignment> assignments =
                assignmentRepository.findByEditor(editor);

        ArticleAssignment assignment = assignments.stream()
                .filter(a -> a.getArticle().getId().equals(article.getId()))
                .findFirst()
                .orElse(null);

        if (assignment == null) {
            return ResponseEntity.status(403)
                    .body("Cet article ne vous est pas affecté");
        }

        // 6. Vérifier que la révision est terminée
        if (assignment.getStatus() != ArticleAssignmentStatus.COMPLETED) {
            return ResponseEntity.badRequest()
                    .body("La révision doit être terminée avant de donner un avis");
        }

        // 7. Vérifier la recommandation
        if (request.getRecommendation() == null) {
            return ResponseEntity.badRequest()
                    .body("La recommandation est obligatoire");
        }

        // 8. Vérifier si l'éditeur a déjà donné un avis
        List<ArticleReview> existingReviews =
                reviewRepository.findByArticle(article);

        boolean alreadyReviewed = existingReviews.stream()
                .anyMatch(r ->
                        r.getReviewer().getId().equals(editor.getId()));

        if (alreadyReviewed) {
            return ResponseEntity.badRequest()
                    .body("Vous avez déjà donné votre avis sur cet article");
        }

        // 9. Créer la review
        ArticleReview review = new ArticleReview();

        review.setArticle(article);
        review.setReviewer(editor);
        review.setComment(request.getComment());
        review.setRecommendation(request.getRecommendation());
        review.setCreatedAt(LocalDateTime.now());

        // 10. Sauvegarder
        ArticleReview savedReview =
                reviewRepository.save(review);

        return ResponseEntity.ok(savedReview);
    }

    @GetMapping("/article/{articleId}")
    public ResponseEntity<?> getReviewsByArticle(
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
}