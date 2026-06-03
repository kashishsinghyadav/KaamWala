package com.kaamwala.controller;

import com.kaamwala.dto.request.CreateJobRequest;
import com.kaamwala.dto.response.ApiResponse;
import com.kaamwala.dto.response.JobResponse;
import com.kaamwala.dto.response.PagedResponse;
import com.kaamwala.entity.JobPost;
import com.kaamwala.entity.ServiceCategory;
import com.kaamwala.service.JobService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

/**
 * REST controller for job posting management.
 */
@RestController
@RequestMapping("/api/jobs")
@RequiredArgsConstructor
@Tag(name = "Jobs", description = "Job posting management endpoints")
public class JobController {

    private final JobService jobService;

    /**
     * Create a new job posting.
     */
    @PostMapping
    @Operation(summary = "Create job", description = "Post a new job (customers only)")
    public ResponseEntity<ApiResponse<JobResponse>> createJob(
            @Valid @RequestBody CreateJobRequest request,
            @AuthenticationPrincipal UUID userId) {
        JobResponse job = jobService.createJob(request, userId);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(job, "Job created successfully"));
    }

    /**
     * Get jobs filtered by status and category.
     */
    @GetMapping
    @Operation(summary = "List jobs", description = "Get jobs filtered by status and category")
    public ResponseEntity<ApiResponse<PagedResponse<JobResponse>>> getJobs(
            @RequestParam(defaultValue = "OPEN") JobPost.JobStatus status,
            @RequestParam ServiceCategory category,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        PagedResponse<JobResponse> jobs = jobService.getJobsByCategory(status, category, page, size);
        return ResponseEntity.ok(ApiResponse.success(jobs));
    }

    /**
     * Get a specific job by ID.
     */
    @GetMapping("/{id}")
    @Operation(summary = "Get job", description = "Get a specific job posting by ID")
    public ResponseEntity<ApiResponse<JobResponse>> getJobById(@PathVariable UUID id) {
        JobResponse job = jobService.getJobById(id);
        return ResponseEntity.ok(ApiResponse.success(job));
    }

    /**
     * Find nearby jobs using geolocation.
     */
    @GetMapping("/nearby")
    @Operation(summary = "Nearby jobs", description = "Find jobs near a location within a radius")
    public ResponseEntity<ApiResponse<PagedResponse<JobResponse>>> getNearbyJobs(
            @RequestParam double latitude,
            @RequestParam double longitude,
            @RequestParam(defaultValue = "10") double radiusKm,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        PagedResponse<JobResponse> jobs = jobService.getNearbyJobs(latitude, longitude, radiusKm, page, size);
        return ResponseEntity.ok(ApiResponse.success(jobs));
    }

    /**
     * Get jobs posted by the authenticated customer.
     */
    @GetMapping("/my-posts")
    @Operation(summary = "My posted jobs", description = "Get all jobs posted by the authenticated user")
    public ResponseEntity<ApiResponse<PagedResponse<JobResponse>>> getMyPosts(
            @AuthenticationPrincipal UUID userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        PagedResponse<JobResponse> jobs = jobService.getMyPostedJobs(userId, page, size);
        return ResponseEntity.ok(ApiResponse.success(jobs));
    }

    /**
     * Update the status of a job posting.
     */
    @PatchMapping("/{id}/status")
    @Operation(summary = "Update job status", description = "Update the status of a job posting")
    public ResponseEntity<ApiResponse<JobResponse>> updateJobStatus(
            @PathVariable UUID id,
            @RequestParam JobPost.JobStatus status,
            @AuthenticationPrincipal UUID userId) {
        JobResponse job = jobService.updateJobStatus(id, status, userId);
        return ResponseEntity.ok(ApiResponse.success(job, "Job status updated"));
    }

    /**
     * Delete a job posting (only if OPEN).
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "Delete job", description = "Delete a job posting (only if status is OPEN)")
    public ResponseEntity<ApiResponse<Void>> deleteJob(
            @PathVariable UUID id,
            @AuthenticationPrincipal UUID userId) {
        jobService.deleteJob(id, userId);
        return ResponseEntity.ok(ApiResponse.success(null, "Job deleted successfully"));
    }
}
