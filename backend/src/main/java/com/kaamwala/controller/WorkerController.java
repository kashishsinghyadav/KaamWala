package com.kaamwala.controller;

import com.kaamwala.dto.request.UpdateWorkerProfileRequest;
import com.kaamwala.dto.response.*;
import com.kaamwala.entity.WorkerProfile;
import com.kaamwala.service.WorkerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import com.kaamwala.entity.ServiceCategory;
import org.springframework.data.domain.Sort;

import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * REST controller for worker profile and discovery.
 */
@RestController
@RequestMapping("/api/workers")
@RequiredArgsConstructor
@Tag(name = "Workers", description = "Worker profile and discovery endpoints")
public class WorkerController {

    private final WorkerService workerService;

    /**
     * Search and discover workers with filters for category, city, and sorting.
     */
    @GetMapping
    @Operation(summary = "Search workers", description = "Search and filter workers by category, city, and sort by price or ratings")
    public ResponseEntity<ApiResponse<PagedResponse<WorkerProfileResponse>>> searchWorkers(
            @RequestParam(required = false) ServiceCategory category,
            @RequestParam(required = false) String city,
            @RequestParam(defaultValue = "price_asc") String sortBy,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        
        Sort sort;
        if ("price_desc".equalsIgnoreCase(sortBy)) {
            sort = Sort.by(Sort.Direction.DESC, "startingPrice");
        } else if ("rating_desc".equalsIgnoreCase(sortBy)) {
            sort = Sort.by(Sort.Direction.DESC, "ratingAvg");
        } else {
            sort = Sort.by(Sort.Direction.ASC, "startingPrice");
        }

        PagedResponse<WorkerProfileResponse> workers = workerService.searchWorkers(
                category, city, page, size, sort);
        return ResponseEntity.ok(ApiResponse.success(workers));
    }

    /**
     * Find nearby available workers.
     */
    @GetMapping("/nearby")
    @Operation(summary = "Nearby workers", description = "Find available workers near a location")
    public ResponseEntity<ApiResponse<PagedResponse<NearbyWorkerResponse>>> getNearbyWorkers(
            @RequestParam double latitude,
            @RequestParam double longitude,
            @RequestParam(defaultValue = "10") double radiusKm,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        PagedResponse<NearbyWorkerResponse> workers = workerService.getNearbyWorkers(
                latitude, longitude, radiusKm, page, size);
        return ResponseEntity.ok(ApiResponse.success(workers));
    }

    /**
     * Get a worker's full profile by user ID.
     */
    @GetMapping("/{id}")
    @Operation(summary = "Get worker profile", description = "Get a worker's full profile information")
    public ResponseEntity<ApiResponse<WorkerProfileResponse>> getWorkerProfile(@PathVariable UUID id) {
        WorkerProfileResponse profile = workerService.getProfile(id);
        return ResponseEntity.ok(ApiResponse.success(profile));
    }

    /**
     * Get a worker's portfolio items.
     */
    @GetMapping("/{id}/portfolio")
    @Operation(summary = "Get worker portfolio", description = "Get a worker's showcase portfolio")
    public ResponseEntity<ApiResponse<List<PortfolioResponse>>> getWorkerPortfolio(@PathVariable UUID id) {
        List<PortfolioResponse> portfolio = workerService.getWorkerPortfolio(id);
        return ResponseEntity.ok(ApiResponse.success(portfolio));
    }

    /**
     * Inquire a worker/express interest.
     */
    @PostMapping("/{id}/inquire")
    @Operation(summary = "Inquire worker", description = "Express interest/inquire a worker for service")
    public ResponseEntity<ApiResponse<Void>> inquireWorker(
            @PathVariable UUID id,
            @AuthenticationPrincipal UUID customerId) {
        workerService.inquireWorker(customerId, id);
        return ResponseEntity.ok(ApiResponse.success(null, "Inquiry sent to worker"));
    }

    /**
     * Update the authenticated worker's profile.
     */
    @PutMapping("/profile")
    @Operation(summary = "Update profile", description = "Update the authenticated worker's profile")
    public ResponseEntity<ApiResponse<WorkerProfileResponse>> updateProfile(
            @Valid @RequestBody UpdateWorkerProfileRequest request,
            @AuthenticationPrincipal UUID userId) {
        WorkerProfileResponse profile = workerService.updateProfile(userId, request);
        return ResponseEntity.ok(ApiResponse.success(profile, "Profile updated successfully"));
    }

    /**
     * Update the authenticated worker's availability status.
     */
    @PatchMapping("/availability")
    @Operation(summary = "Update availability", description = "Set worker availability status")
    public ResponseEntity<ApiResponse<WorkerProfileResponse>> updateAvailability(
            @RequestBody Map<String, String> body,
            @AuthenticationPrincipal UUID userId) {
        WorkerProfile.AvailabilityStatus status = WorkerProfile.AvailabilityStatus.valueOf(
                body.get("status").toUpperCase());
        WorkerProfileResponse profile = workerService.updateAvailability(userId, status);
        return ResponseEntity.ok(ApiResponse.success(profile, "Availability updated"));
    }
}
