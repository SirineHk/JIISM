package com.jiism.jiism.repository;

import com.jiism.jiism.model.entities.ArticleAssignment;
import com.jiism.jiism.model.entities.Article;
import com.jiism.jiism.model.entities.Utilisateur;
import com.jiism.jiism.model.enums.ArticleAssignmentStatus;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface ArticleAssignmentRepository
        extends JpaRepository<ArticleAssignment, Long> {

    List<ArticleAssignment> findByEditor(Utilisateur editor);

    List<ArticleAssignment> findByEditorAndStatus(
            Utilisateur editor,
            ArticleAssignmentStatus status
    );

    List<ArticleAssignment> findByArticle(Article article);

    List<ArticleAssignment> findByEditorAndDueDateBefore(
            Utilisateur editor,
            LocalDateTime date
    );

    List<ArticleAssignment> findByEditorAndDueDateBetween(
            Utilisateur editor,
            LocalDateTime start,
            LocalDateTime end
    );

    @Query("""
    SELECT a
    FROM ArticleAssignment a
    WHERE a.editor = :editor
      AND a.dueDate < :now
      AND a.status <> :completedStatus
""")
    List<ArticleAssignment> findOverdueAssignments(
            @Param("editor") Utilisateur editor,
            @Param("now") LocalDateTime now,
            @Param("completedStatus") ArticleAssignmentStatus completedStatus
    );

    List<ArticleAssignment> findByEditorAndDueDateBetweenAndStatusNot(
            Utilisateur editor,
            LocalDateTime start,
            LocalDateTime end,
            ArticleAssignmentStatus status
    );
}