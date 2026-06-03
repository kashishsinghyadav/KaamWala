package com.kaamwala.service;

import com.kaamwala.dto.request.UpdateWorkerProfileRequest;
import com.kaamwala.dto.response.NearbyWorkerResponse;
import com.kaamwala.dto.response.PagedResponse;
import com.kaamwala.dto.response.PortfolioResponse;
import com.kaamwala.dto.response.WorkerProfileResponse;
import com.kaamwala.entity.PortfolioItem;
import com.kaamwala.entity.User;
import com.kaamwala.entity.WorkerProfile;
import com.kaamwala.exception.ResourceNotFoundException;
import com.kaamwala.repository.PortfolioItemRepository;
import com.kaamwala.repository.UserRepository;
import com.kaamwala.repository.WorkerProfileRepository;
import com.kaamwala.repository.BookingRepository;
import com.kaamwala.repository.ReviewRepository;
import com.kaamwala.entity.Booking;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.UUID;

/**
 * Service for managing worker profiles, nearby searches, and portfolio items.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class WorkerService {

    private final WorkerProfileRepository workerProfileRepository;
    private final UserRepository userRepository;
    private final PortfolioItemRepository portfolioItemRepository;
    private final BookingRepository bookingRepository;
    private final ReviewRepository reviewRepository;

    /**
     * Get a worker's full profile by user ID.
     *
     * @param userId the user UUID
     * @return the worker profile response
     */
    @Transactional(readOnly = true)
    public WorkerProfileResponse getProfile(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));

        WorkerProfile profile = workerProfileRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("WorkerProfile", "userId", userId));

        return mapToProfileResponse(user, profile);
    }

    /**
     * Update a worker's profile.
     *
     * @param userId  the authenticated worker's UUID
     * @param request the profile update request
     * @return the updated profile response
     */
    @Transactional
    public WorkerProfileResponse updateProfile(UUID userId, UpdateWorkerProfileRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));

        WorkerProfile profile = workerProfileRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("WorkerProfile", "userId", userId));

        // Update user fields
        if (request.getName() != null) {
            user.setName(request.getName());
        }
        if (request.getEmail() != null) {
            user.setEmail(request.getEmail());
        }
        if (request.getLatitude() != null) {
            user.setLatitude(request.getLatitude());
        }
        if (request.getLongitude() != null) {
            user.setLongitude(request.getLongitude());
        }
        userRepository.save(user);

        // Update profile fields
        if (request.getBio() != null) {
            profile.setBio(request.getBio());
        }
        if (request.getSkills() != null) {
            profile.setSkills(request.getSkills());
        }
        if (request.getServiceAreas() != null) {
            profile.setServiceAreas(request.getServiceAreas());
        }
        if (request.getStartingPrice() != null) {
            profile.setStartingPrice(request.getStartingPrice());
        }
        if (request.getAvailabilityStatus() != null) {
            profile.setAvailabilityStatus(request.getAvailabilityStatus());
        }
        workerProfileRepository.save(profile);

        log.info("Worker profile updated for user {}", userId);

        return mapToProfileResponse(user, profile);
    }

    /**
     * Find nearby available workers.
     *
     * @param latitude  the search latitude
     * @param longitude the search longitude
     * @param radiusKm  the search radius in kilometres
     * @param page      the page number (0-based)
     * @param size      the page size
     * @return paged list of nearby workers
     */
    @Transactional(readOnly = true)
    public PagedResponse<NearbyWorkerResponse> getNearbyWorkers(double latitude, double longitude,
                                                                  double radiusKm, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<WorkerProfile> workerPage = workerProfileRepository.findNearbyWorkers(
                latitude, longitude, radiusKm, pageable);

        List<NearbyWorkerResponse> content = workerPage.getContent().stream()
                .map(wp -> {
                    User user = wp.getUser();
                    double distanceKm = calculateDistance(latitude, longitude,
                            user.getLatitude(), user.getLongitude());

                    return NearbyWorkerResponse.builder()
                            .userId(user.getId())
                            .name(user.getName())
                            .avatarUrl(user.getAvatarUrl())
                            .skills(wp.getSkills())
                            .startingPrice(wp.getStartingPrice())
                            .ratingAvg(wp.getRatingAvg())
                            .totalJobs(wp.getTotalJobs())
                            .isVerified(wp.getIsVerified())
                            .bio(wp.getBio())
                            .latitude(user.getLatitude())
                            .longitude(user.getLongitude())
                            .distanceKm(Math.round(distanceKm * 100.0) / 100.0)
                            .build();
                })
                .toList();

        return PagedResponse.<NearbyWorkerResponse>builder()
                .content(content)
                .page(workerPage.getNumber())
                .size(workerPage.getSize())
                .totalElements(workerPage.getTotalElements())
                .totalPages(workerPage.getTotalPages())
                .last(workerPage.isLast())
                .build();
    }

    /**
     * Get a worker's portfolio items.
     *
     * @param workerId the worker's UUID
     * @return list of portfolio responses
     */
    @Transactional(readOnly = true)
    public List<PortfolioResponse> getWorkerPortfolio(UUID workerId) {
        return portfolioItemRepository.findByWorkerIdOrderByCreatedAtDesc(workerId).stream()
                .map(this::mapToPortfolioResponse)
                .toList();
    }

    /**
     * Update a worker's availability status.
     *
     * @param userId the worker's UUID
     * @param status the new availability status
     * @return the updated profile response
     */
    @Transactional
    public WorkerProfileResponse updateAvailability(UUID userId,
                                                     WorkerProfile.AvailabilityStatus status) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));

        WorkerProfile profile = workerProfileRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("WorkerProfile", "userId", userId));

        profile.setAvailabilityStatus(status);
        workerProfileRepository.save(profile);

        log.info("Worker {} availability updated to {}", userId, status);

        return mapToProfileResponse(user, profile);
    }

    /**
     * Get worker statistics: total jobs, total earnings, average rating.
     *
     * @param workerId the worker's UUID
     * @return a map-like response embedded in WorkerProfileResponse
     */
    @Transactional(readOnly = true)
    public WorkerProfileResponse getWorkerStats(UUID workerId) {
        return getProfile(workerId);
    }

    /**
     * Calculate distance between two lat/lng points using Haversine formula.
     */
    private double calculateDistance(double lat1, double lng1, double lat2, double lng2) {
        final double R = 6371; // Earth's radius in km
        double dLat = Math.toRadians(lat2 - lat1);
        double dLng = Math.toRadians(lng2 - lng1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(dLng / 2) * Math.sin(dLng / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return R * c;
    }

    private WorkerProfileResponse mapToProfileResponse(User user, WorkerProfile profile) {
        return WorkerProfileResponse.builder()
                .userId(user.getId())
                .name(user.getName())
                .phone(user.getPhone())
                .email(user.getEmail())
                .avatarUrl(user.getAvatarUrl())
                .latitude(user.getLatitude())
                .longitude(user.getLongitude())
                .skills(profile.getSkills())
                .serviceAreas(profile.getServiceAreas())
                .startingPrice(profile.getStartingPrice())
                .isVerified(profile.getIsVerified())
                .aadhaarVerified(profile.getAadhaarVerified())
                .panVerified(profile.getPanVerified())
                .selfieVerified(profile.getSelfieVerified())
                .ratingAvg(profile.getRatingAvg())
                .totalJobs(profile.getTotalJobs())
                .totalEarnings(profile.getTotalEarnings())
                .bio(profile.getBio())
                .subscriptionTier(profile.getSubscriptionTier())
                .availabilityStatus(profile.getAvailabilityStatus())
                .build();
    }

    private PortfolioResponse mapToPortfolioResponse(PortfolioItem item) {
        return PortfolioResponse.builder()
                .id(item.getId())
                .workerId(item.getWorker().getId())
                .title(item.getTitle())
                .description(item.getDescription())
                .beforeImageUrl(item.getBeforeImageUrl())
                .afterImageUrl(item.getAfterImageUrl())
                .videoUrl(item.getVideoUrl())
                .category(item.getCategory())
                .createdAt(item.getCreatedAt())
                .build();
    }
}
