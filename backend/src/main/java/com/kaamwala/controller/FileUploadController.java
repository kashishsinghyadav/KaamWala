package com.kaamwala.controller;

import com.kaamwala.dto.response.ApiResponse;
import com.kaamwala.service.FileUploadService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

/**
 * REST controller for file upload and management.
 */
@RestController
@RequestMapping("/api/files")
@RequiredArgsConstructor
@Tag(name = "Files", description = "File upload and management endpoints")
public class FileUploadController {

    private final FileUploadService fileUploadService;

    /**
     * Upload a file.
     *
     * @param file the multipart file to upload
     * @return the filename of the uploaded file
     */
    @PostMapping("/upload")
    @Operation(summary = "Upload file", description = "Upload an image or video file")
    public ResponseEntity<ApiResponse<String>> uploadFile(@RequestParam("file") MultipartFile file) {
        String filename = fileUploadService.uploadFile(file);
        return ResponseEntity.ok(ApiResponse.success(filename, "File uploaded successfully"));
    }

    /**
     * Delete a file.
     *
     * @param filename the filename to delete
     */
    @DeleteMapping("/{filename}")
    @Operation(summary = "Delete file", description = "Delete a previously uploaded file")
    public ResponseEntity<ApiResponse<Void>> deleteFile(@PathVariable String filename) {
        fileUploadService.deleteFile(filename);
        return ResponseEntity.ok(ApiResponse.success(null, "File deleted successfully"));
    }
}
