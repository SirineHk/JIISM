package com.jiism.jiism.repository;

import com.jiism.jiism.model.entities.Announcement;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AnnouncementRepository
        extends JpaRepository<Announcement, Long> {

    List<Announcement> findByPublishedTrueOrderByPublishedAtDesc();

    List<Announcement> findByPublishedTrueOrderByCreatedAtDesc();
}