package com.jiism.jiism.model.dto;

import com.jiism.jiism.model.enums.ReviewRecommendation;

public class ArticleReviewRequest {

    private Long articleId;

    private String comment;

    private ReviewRecommendation recommendation;

    public Long getArticleId() {
        return articleId;
    }

    public void setArticleId(Long articleId) {
        this.articleId = articleId;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public ReviewRecommendation getRecommendation() {
        return recommendation;
    }

    public void setRecommendation(ReviewRecommendation recommendation) {
        this.recommendation = recommendation;
    }
}