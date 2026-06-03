package com.kaamwala.controller;

import com.kaamwala.dto.request.CreateBidRequest;
import com.kaamwala.dto.response.ApiResponse;
import com.kaamwala.dto.response.BidResponse;
import com.kaamwala.dto.response.BookingResponse;
import com.kaamwala.dto.response.PagedResponse;
import com.kaamwala.service.BidService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

/**
 * REST controller for bid management on job postings.
 */
@RestController
@RequiredArgsConstructor
@Tag(name = "Bids", description = "Bid management endpoints")
public class BidController {

    private final BidService bidService;

    /**
     * Place a bid on a job post.
     */
    @PostMapping("/api/jobs/{jobId}/bids")
    @Operation(summary = "Create bid", description = "Place a bid on a job posting (workers only)")
    public ResponseEntity<ApiResponse<BidResponse>> createBid(
            @PathVariable UUID jobId,
            @Valid @RequestBody CreateBidRequest request,
            @AuthenticationPrincipal UUID userId) {
        BidResponse bid = bidService.createBid(jobId, request, userId);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(bid, "Bid placed successfully"));
    }

    /**
     * Get all bids for a specific job post.
     */
    @GetMapping("/api/jobs/{jobId}/bids")
    @Operation(summary = "Get bids for job", description = "List all bids on a job posting")
    public ResponseEntity<ApiResponse<List<BidResponse>>> getBidsForJob(@PathVariable UUID jobId) {
        List<BidResponse> bids = bidService.getBidsForJob(jobId);
        return ResponseEntity.ok(ApiResponse.success(bids));
    }

    /**
     * Get all bids placed by the authenticated worker.
     */
    @GetMapping("/api/bids/my-bids")
    @Operation(summary = "My bids", description = "Get all bids placed by the authenticated worker")
    public ResponseEntity<ApiResponse<PagedResponse<BidResponse>>> getMyBids(
            @AuthenticationPrincipal UUID userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        PagedResponse<BidResponse> bids = bidService.getMyBids(userId, page, size);
        return ResponseEntity.ok(ApiResponse.success(bids));
    }

    /**
     * Accept a bid (creates a booking automatically).
     */
    @PatchMapping("/api/bids/{id}/accept")
    @Operation(summary = "Accept bid", description = "Accept a bid and auto-create a booking (customers only)")
    public ResponseEntity<ApiResponse<BookingResponse>> acceptBid(
            @PathVariable UUID id,
            @AuthenticationPrincipal UUID userId) {
        BookingResponse booking = bidService.acceptBid(id, userId);
        return ResponseEntity.ok(ApiResponse.success(booking, "Bid accepted, booking created"));
    }

    /**
     * Reject a bid.
     */
    @PatchMapping("/api/bids/{id}/reject")
    @Operation(summary = "Reject bid", description = "Reject a specific bid (customers only)")
    public ResponseEntity<ApiResponse<Void>> rejectBid(
            @PathVariable UUID id,
            @AuthenticationPrincipal UUID userId) {
        bidService.rejectBid(id, userId);
        return ResponseEntity.ok(ApiResponse.success(null, "Bid rejected"));
    }
}
