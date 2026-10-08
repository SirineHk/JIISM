package com.jiism.jiism.controller;

import com.jiism.jiism.model.entities.Announcement;
import com.jiism.jiism.services.AnnouncementService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/announcements/admin")
public class AnnouncementAdminController {

    private final AnnouncementService announcementService;

    public AnnouncementAdminController(
            AnnouncementService announcementService) {
        this.announcementService = announcementService;
    }

    @PostMapping
    public ResponseEntity<Announcement> create(
            @RequestBody Map<String, String> request) {

        System.out.println("REQUEST = " + request);

        String title = request.get("title");
        String content = request.get("content");

        System.out.println("TITLE = " + title);
        System.out.println("CONTENT = " + content);

        if (title == null || title.trim().isEmpty()
                || content == null || content.trim().isEmpty()) {

            return ResponseEntity.badRequest().build();
        }

        return ResponseEntity.ok(
                announcementService.createAnnouncement(title, content)
        );
    }

    @GetMapping
    public ResponseEntity<List<Announcement>> getAll() {
        return ResponseEntity.ok(
                announcementService.getAllAnnouncements()
        );
    }

    @PutMapping("/{id}/publish")
    public ResponseEntity<Announcement> publish(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                announcementService.publishAnnouncement(id)
        );
    }

    @PutMapping("/{id}/unpublish")
    public ResponseEntity<Announcement> unpublish(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                announcementService.unpublishAnnouncement(id)
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @PathVariable Long id) {

        announcementService.deleteAnnouncement(id);
        return ResponseEntity.noContent().build();
    }
}