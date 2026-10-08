package com.jiism.jiism.services;

import com.jiism.jiism.model.enums.ArticleStatus;
import com.jiism.jiism.repository.ArticleRepository;
import com.jiism.jiism.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class AdminStatisticsService {

    private final ArticleRepository articleRepository;
    private final UserRepository userRepository;

    public AdminStatisticsService(
            ArticleRepository articleRepository,
            UserRepository userRepository) {

        this.articleRepository = articleRepository;
        this.userRepository = userRepository;
    }

    public Map<String, Object> getStatistics() {

        Map<String, Object> statistics = new HashMap<>();

        // Articles
        statistics.put("totalArticles", articleRepository.count());
        statistics.put("pending",
                articleRepository.countByStatus(ArticleStatus.PENDING));
        statistics.put("underReview",
                articleRepository.countByStatus(ArticleStatus.UNDER_REVIEW));
        statistics.put("accepted",
                articleRepository.countByStatus(ArticleStatus.ACCEPTED));
        statistics.put("rejected",
                articleRepository.countByStatus(ArticleStatus.REJECTED));
        statistics.put("published",
                articleRepository.countByStatus(ArticleStatus.PUBLISHED));

        // Utilisateurs
        statistics.put("totalUsers", userRepository.count());

        statistics.put("totalAdmins",
                userRepository.countByRole("ADMIN"));

        statistics.put("totalEditors",
                userRepository.countByRole("EDITEUR"));

        statistics.put("totalAuthors",
                userRepository.countByRole("AUTEUR"));

        statistics.put("totalPublicUsers",
                userRepository.countByRole("PUBLIC"));

        return statistics;
    }
}