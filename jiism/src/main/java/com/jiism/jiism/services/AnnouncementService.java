package com.jiism.jiism.services;

import com.jiism.jiism.model.entities.Announcement;
import com.jiism.jiism.repository.AnnouncementRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class AnnouncementService {

    private final AnnouncementRepository announcementRepository;

    public AnnouncementService(AnnouncementRepository announcementRepository) {
        this.announcementRepository = announcementRepository;
    }

    public Announcement createAnnouncement(String title, String content) {

        Announcement announcement = new Announcement();

        announcement.setTitle(title);
        announcement.setContent(content);
        announcement.setCreatedAt(LocalDateTime.now());
        announcement.setPublished(false);

        return announcementRepository.save(announcement);
    }

    public List<Announcement> getAllAnnouncements() {
        return announcementRepository.findAll();
    }

    public List<Announcement> getPublishedAnnouncements() {
        return announcementRepository.findByPublishedTrueOrderByCreatedAtDesc();
    }

    public Announcement publishAnnouncement(Long id) {
        Announcement announcement = announcementRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Announcement not found"));

        announcement.setPublished(true);
        announcement.setPublishedAt(LocalDateTime.now());

        return announcementRepository.save(announcement);
    }

    public Announcement unpublishAnnouncement(Long id) {
        Announcement announcement = announcementRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Annonce introuvable"));

        announcement.setPublished(false);
        announcement.setPublishedAt(null);

        return announcementRepository.save(announcement);
    }

    public void deleteAnnouncement(Long id) {
        if (!announcementRepository.existsById(id)) {
            throw new RuntimeException("Announcement not found");
        }

        announcementRepository.deleteById(id);
    }
}