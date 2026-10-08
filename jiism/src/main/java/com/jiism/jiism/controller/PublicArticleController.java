package com.jiism.jiism.controller;

import com.jiism.jiism.model.entities.Article;
import com.jiism.jiism.model.enums.ArticleStatus;
import com.jiism.jiism.repository.ArticleRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/public/articles")
public class PublicArticleController {

    private final ArticleRepository articleRepository;

    public PublicArticleController(ArticleRepository articleRepository) {
        this.articleRepository = articleRepository;
    }

    // Tous les articles publiés
    @GetMapping
    public ResponseEntity<List<Article>> getPublishedArticles() {

        return ResponseEntity.ok(
                articleRepository.findByStatus(ArticleStatus.PUBLISHED)
        );
    }

    // Consulter un article publié
    @GetMapping("/{id}")
    public ResponseEntity<?> getPublishedArticle(@PathVariable Long id) {

        Article article = articleRepository.findById(id).orElse(null);

        if (article == null) {
            return ResponseEntity.notFound().build();
        }

        if (article.getStatus() != ArticleStatus.PUBLISHED) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(article);
    }

    // Rechercher parmi les articles publiés
    @GetMapping("/search")
    public ResponseEntity<List<Article>> searchPublishedArticles(
            @RequestParam String title) {

        return ResponseEntity.ok(
                articleRepository
                        .findByStatusAndTitleContainingIgnoreCase(
                                ArticleStatus.PUBLISHED,
                                title
                        )
        );
    }
}