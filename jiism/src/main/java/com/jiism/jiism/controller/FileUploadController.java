package com.jiism.jiism.controller;

import com.jiism.jiism.model.entities.Article;
import com.jiism.jiism.repository.ArticleRepository;

import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@RestController
@RequestMapping("/files")
public class FileUploadController {

    private final Path uploadDirectory = Paths.get("uploads");

    private final ArticleRepository articleRepository;

    public FileUploadController(ArticleRepository articleRepository) {
        this.articleRepository = articleRepository;
    }

    // ==========================================
    // UPLOAD PDF
    // ==========================================

    @PostMapping("/upload-pdf")
    public ResponseEntity<?> uploadPdf(
            @RequestParam("file") MultipartFile file) {

        try {

            if (file.isEmpty()) {
                return ResponseEntity.badRequest()
                        .body("Le fichier est vide");
            }

            String contentType = file.getContentType();

            if (!"application/pdf".equalsIgnoreCase(contentType)) {
                return ResponseEntity.badRequest()
                        .body("Seuls les fichiers PDF sont autorisés");
            }

            Files.createDirectories(uploadDirectory);

            String fileName = UUID.randomUUID() + ".pdf";

            Path filePath = uploadDirectory.resolve(fileName);

            Files.copy(file.getInputStream(), filePath);

            return ResponseEntity.ok(
                    "uploads/" + fileName
            );

        } catch (IOException e) {

            return ResponseEntity
                    .internalServerError()
                    .body("Erreur lors de l'upload du fichier");
        }
    }

    // ==========================================
    // CONSULTER PDF
    // ==========================================

    @GetMapping("/{fileName:.+}")
    public ResponseEntity<Resource> getFile(
            @PathVariable String fileName) {

        try {

            Path filePath = uploadDirectory
                    .resolve(fileName)
                    .normalize();

            Resource resource =
                    new UrlResource(filePath.toUri());

            if (!resource.exists() || !resource.isReadable()) {
                return ResponseEntity.notFound().build();
            }

            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_PDF)
                    .header(
                            HttpHeaders.CONTENT_DISPOSITION,
                            "inline; filename=\"" +
                                    resource.getFilename() +
                                    "\""
                    )
                    .body(resource);

        } catch (Exception e) {

            return ResponseEntity
                    .internalServerError()
                    .build();
        }
    }

    // ==========================================
    // TELECHARGER PDF
    // ==========================================

    @GetMapping("/download/{fileName:.+}")
    public ResponseEntity<Resource> downloadFile(
            @PathVariable String fileName) {

        try {

            Path filePath = uploadDirectory
                    .resolve(fileName)
                    .normalize();

            Resource resource =
                    new UrlResource(filePath.toUri());

            if (!resource.exists() || !resource.isReadable()) {
                return ResponseEntity.notFound().build();
            }

            String pdfUrl = "uploads/" + fileName;

            Article article = articleRepository
                    .findAll()
                    .stream()
                    .filter(a -> pdfUrl.equals(a.getPdfUrl()))
                    .findFirst()
                    .orElse(null);

            if (article != null) {

                Integer count = article.getDownloadCount();

                if (count == null) {
                    count = 0;
                }

                article.setDownloadCount(count + 1);

                articleRepository.save(article);
            }

            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_PDF)
                    .header(
                            HttpHeaders.CONTENT_DISPOSITION,
                            "attachment; filename=\"" +
                                    resource.getFilename() +
                                    "\""
                    )
                    .body(resource);

        } catch (Exception e) {

            return ResponseEntity
                    .internalServerError()
                    .build();
        }
    }
}