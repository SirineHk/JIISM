package com.jiism.jiism.repository;

import com.jiism.jiism.model.entities.Article;
import com.jiism.jiism.model.enums.ArticleStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ArticleRepository extends JpaRepository<Article, Long> {

    long countByStatus(ArticleStatus status);

    List<Article> findByStatus(ArticleStatus status);

    List<Article> findByStatusAndTitleContainingIgnoreCase(
            ArticleStatus status,
            String title
    );
}