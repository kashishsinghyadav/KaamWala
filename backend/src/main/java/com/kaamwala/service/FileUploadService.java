package com.kaamwala.service;

import com.kaamwala.exception.BadRequestException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

/**
 * Service for handling file uploads and deletions.
 *
 * <p>Files are stored locally in the configured upload directory.
 * In production, consider migrating to a cloud storage service (S3, GCS, etc.).</p>
 */
@Service
@Slf4j
public class FileUploadService {

    private final Path uploadDir;
    private final List<String> allowedTypes;
    private final long maxSizeMb;

    public FileUploadService(
            @Value("${app.file.upload-dir}") String uploadDirPath,
            @Value("${app.file.allowed-types}") String allowedTypesStr,
            @Value("${app.file.max-size-mb}") long maxSizeMb) {
        this.uploadDir = Paths.get(uploadDirPath).toAbsolutePath().normalize();
        this.allowedTypes = Arrays.asList(allowedTypesStr.split(","));
        this.maxSizeMb = maxSizeMb;

        // Create upload directory if it doesn't exist
        try {
            Files.createDirectories(this.uploadDir);
        } catch (IOException e) {
            throw new RuntimeException("Could not create upload directory: " + uploadDirPath, e);
        }
    }

    /**
     * Upload a file to the local file system.
     *
     * @param file the multipart file to upload
     * @return the filename (UUID-based) of the stored file
     */
    public String uploadFile(MultipartFile file) {
        if (file.isEmpty()) {
            throw new BadRequestException("Cannot upload empty file");
        }

        validateFileType(file);
        validateFileSize(file);

        String originalFilename = file.getOriginalFilename();
        String extension = "";
        if (originalFilename != null && originalFilename.contains(".")) {
            extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        }

        String filename = UUID.randomUUID() + extension;
        Path targetPath = uploadDir.resolve(filename);

        try {
            Files.copy(file.getInputStream(), targetPath, StandardCopyOption.REPLACE_EXISTING);
            log.info("File uploaded: {} (original: {})", filename, originalFilename);
            return filename;
        } catch (IOException e) {
            throw new RuntimeException("Failed to store file: " + filename, e);
        }
    }

    /**
     * Delete a file from the local file system.
     *
     * @param filename the filename to delete
     */
    public void deleteFile(String filename) {
        Path filePath = uploadDir.resolve(filename).normalize();

        // Security check: prevent path traversal
        if (!filePath.startsWith(uploadDir)) {
            throw new BadRequestException("Invalid file path");
        }

        try {
            if (Files.exists(filePath)) {
                Files.delete(filePath);
                log.info("File deleted: {}", filename);
            } else {
                log.warn("File not found for deletion: {}", filename);
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to delete file: " + filename, e);
        }
    }

    /**
     * Validate the file's content type against allowed types.
     *
     * @param file the file to validate
     */
    public void validateFileType(MultipartFile file) {
        String contentType = file.getContentType();
        if (contentType == null || !allowedTypes.contains(contentType)) {
            throw new BadRequestException(
                    "File type not allowed: " + contentType + ". Allowed types: " + allowedTypes);
        }
    }

    /**
     * Validate the file size.
     */
    private void validateFileSize(MultipartFile file) {
        long maxBytes = maxSizeMb * 1024 * 1024;
        if (file.getSize() > maxBytes) {
            throw new BadRequestException(
                    "File size exceeds maximum allowed: " + maxSizeMb + "MB");
        }
    }
}
