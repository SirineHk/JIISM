package com.jiism.jiism.model.entities;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.jiism.jiism.model.enums.ArticleAssignmentStatus;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "article_assignments")
public class ArticleAssignment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // ==========================================
    // ARTICLE
    // ==========================================

    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "article_id", nullable = false)
    private Article article;

    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "editor_id", nullable = false)
    private Utilisateur editor;

    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assigned_by", nullable = false)
    private Utilisateur assignedBy;

    // ==========================================
    // DATES
    // ==========================================

    private LocalDateTime assignedAt;

    private LocalDateTime dueDate;

    // ==========================================
    // STATUT
    // ==========================================

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ArticleAssignmentStatus status =
            ArticleAssignmentStatus.ASSIGNED;

    // ==========================================
    // CONSTRUCTEUR
    // ==========================================

    public ArticleAssignment() {
    }

    // ==========================================
    // GETTERS / SETTERS
    // ==========================================

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Article getArticle() {
        return article;
    }

    public void setArticle(Article article) {
        this.article = article;
    }

    public Utilisateur getEditor() {
        return editor;
    }

    public void setEditor(Utilisateur editor) {
        this.editor = editor;
    }

    public Utilisateur getAssignedBy() {
        return assignedBy;
    }

    public void setAssignedBy(Utilisateur assignedBy) {
        this.assignedBy = assignedBy;
    }

    public LocalDateTime getAssignedAt() {
        return assignedAt;
    }

    public void setAssignedAt(LocalDateTime assignedAt) {
        this.assignedAt = assignedAt;
    }

    public LocalDateTime getDueDate() {
        return dueDate;
    }

    public void setDueDate(LocalDateTime dueDate) {
        this.dueDate = dueDate;
    }

    public ArticleAssignmentStatus getStatus() {
        return status;
    }

    public void setStatus(ArticleAssignmentStatus status) {
        this.status = status;
    }
}