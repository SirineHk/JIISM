package com.jiism.jiism.controller;

import com.jiism.jiism.model.entities.Announcement;
import com.jiism.jiism.services.AnnouncementService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/announcements/public")
public class AnnouncementPublicController {

    private final AnnouncementService announcementService;

    public AnnouncementPublicController(
            AnnouncementService announcementService) {
        this.announcementService = announcementService;
    }

    @GetMapping
    public ResponseEntity<List<Announcement>> getPublishedAnnouncements() {

        return ResponseEntity.ok(
                announcementService.getPublishedAnnouncements()
        );
    }
}