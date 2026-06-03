package com.kaamwala.controller;

import com.kaamwala.dto.request.CreateReviewRequest;
import com.kaamwala.dto.response.ApiResponse;
import com.kaamwala.dto.response.PagedResponse;
import com.kaamwala.dto.response.ReviewResponse;
import com.kaamwala.service.ReviewService;
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
 * REST controller for review management.
 */
@RestController
@RequiredArgsConstructor
@Tag(name = "Reviews", description = "Review management endpoints")
public class ReviewController {

    private final ReviewService reviewService;

    /**
     * Create a review for a completed booking.
     */
    @PostMapping("/api/bookings/{bookingId}/reviews")
    @Operation(summary = "Create review", description = "Leave a review for a completed booking (customers only)")
    public ResponseEntity<ApiResponse<ReviewResponse>> createReview(
            @PathVariable UUID bookingId,
            @Valid @RequestBody CreateReviewRequest request,
            @AuthenticationPrincipal UUID userId) {
        ReviewResponse review = reviewService.createReview(bookingId, request, userId);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(review, "Review submitted successfully"));
    }

    /**
     * Get all reviews for a specific worker.
     */
    @GetMapping("/api/workers/{workerId}/reviews")
    @Operation(summary = "Worker reviews", description = "Get all reviews for a worker")
    public ResponseEntity<ApiResponse<PagedResponse<ReviewResponse>>> getWorkerReviews(
            @PathVariable UUID workerId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        PagedResponse<ReviewResponse> reviews = reviewService.getWorkerReviews(workerId, page, size);
        return ResponseEntity.ok(ApiResponse.success(reviews));
    }
}
