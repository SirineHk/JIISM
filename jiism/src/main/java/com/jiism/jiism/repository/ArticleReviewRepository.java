package com.jiism.jiism.repository;

import com.jiism.jiism.model.entities.Article;
import com.jiism.jiism.model.entities.ArticleReview;
import com.jiism.jiism.model.entities.Utilisateur;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ArticleReviewRepository extends JpaRepository<ArticleReview, Long> {

    List<ArticleReview> findByArticle(Article article);

    List<ArticleReview> findByReviewer(Utilisateur reviewer);
}