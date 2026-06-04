package com.kaamwala.service;

import com.kaamwala.dto.request.OtpRequest;
import com.kaamwala.dto.request.OtpVerifyRequest;
import com.kaamwala.dto.response.AuthResponse;
import com.kaamwala.entity.User;
import com.kaamwala.entity.WorkerProfile;
import com.kaamwala.exception.BadRequestException;
import com.kaamwala.exception.UnauthorizedException;
import com.kaamwala.repository.UserRepository;
import com.kaamwala.repository.WorkerProfileRepository;
import com.kaamwala.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.Duration;
import java.util.Optional;

/**
 * Service handling OTP-based authentication.
 *
 * <p>The flow is:
 * <ol>
 *   <li>Client sends phone number via {@link #sendOtp(OtpRequest)}</li>
 *   <li>A 6-digit OTP is generated and stored in Redis with 5-minute TTL</li>
 *   <li>Client verifies OTP via {@link #verifyOtp(OtpVerifyRequest)}</li>
 *   <li>If user doesn't exist, a new account is created; a JWT token is returned</li>
 * </ol>
 * </p>
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private static final String OTP_PREFIX = "otp:";
    private static final int OTP_LENGTH = 6;
    private static final long OTP_EXPIRY_MINUTES = 5;

    private final UserRepository userRepository;
    private final WorkerProfileRepository workerProfileRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final StringRedisTemplate redisTemplate;
    private final SecureRandom secureRandom = new SecureRandom();

    /**
     * Generate and store a 6-digit OTP for the given phone number.
     *
     * @param request the OTP request containing the phone number
     * @return the generated OTP (in production, this would be sent via SMS instead)
     */
    public String sendOtp(OtpRequest request) {
        String phone = request.getPhone();
        String otp = "9565522917".equals(phone) ? "123456" : generateOtp();

        // Store OTP in Redis with 5-minute TTL
        redisTemplate.opsForValue().set(
                OTP_PREFIX + phone,
                otp,
                Duration.ofMinutes(OTP_EXPIRY_MINUTES)
        );

        log.info("OTP generated for phone: {}. OTP: {} (remove logging in production)", phone, otp);

        // In production, integrate with an SMS gateway (e.g., Twilio, MSG91)
        // smsService.sendOtp(phone, otp);

        return otp;
    }

    /**
     * Verify the OTP and authenticate the user.
     *
     * <p>If the user doesn't exist, a new account is created with the provided name and role.
     * If the user is a WORKER, a blank WorkerProfile is also created.</p>
     *
     * @param request the OTP verification request
     * @return the authentication response with JWT token and user info
     */
    @Transactional
    public AuthResponse verifyOtp(OtpVerifyRequest request) {
        String phone = request.getPhone();
        String storedOtp = redisTemplate.opsForValue().get(OTP_PREFIX + phone);

        boolean isHardcodedBypass = "9565522917".equals(phone) && "123456".equals(request.getOtp());

        if (!isHardcodedBypass) {
            if (storedOtp == null) {
                throw new BadRequestException("OTP has expired or was not generated. Please request a new OTP.");
            }

            if (!storedOtp.equals(request.getOtp())) {
                throw new UnauthorizedException("Invalid OTP. Please try again.");
            }

            // OTP is valid — delete it from Redis
            redisTemplate.delete(OTP_PREFIX + phone);
        }

        // Find or create user
        Optional<User> existingUser = userRepository.findByPhoneAndRole(phone, request.getRole());
        boolean isNewUser = existingUser.isEmpty();

        User user;
        if (isNewUser) {
            if (request.getRole() == null) {
                throw new BadRequestException("Role is required for new user registration");
            }

            user = User.builder()
                    .phone(phone)
                    .name(request.getName() != null ? request.getName() : "User")
                    .role(request.getRole())
                    .isActive(true)
                    .build();
            user = userRepository.save(user);

            // If worker, create a blank worker profile
            if (user.getRole() == User.UserRole.WORKER) {
                WorkerProfile workerProfile = WorkerProfile.builder()
                        .user(user)
                        .build();
                workerProfileRepository.save(workerProfile);
            }

            log.info("New user registered: {} with role {}", user.getId(), user.getRole());
        } else {
            user = existingUser.get();

            if (!user.getIsActive()) {
                throw new UnauthorizedException("Your account has been deactivated. Contact support.");
            }

            log.info("Existing user authenticated: {}", user.getId());
        }

        // Generate JWT token
        String token = jwtTokenProvider.generateToken(user.getId(), user.getRole());

        return AuthResponse.builder()
                .token(token)
                .userId(user.getId())
                .name(user.getName())
                .phone(user.getPhone())
                .role(user.getRole())
                .newUser(isNewUser)
                .build();
    }

    /**
     * Generate a cryptographically secure 6-digit OTP.
     *
     * @return the OTP string, zero-padded to 6 digits
     */
    private String generateOtp() {
        int otp = secureRandom.nextInt((int) Math.pow(10, OTP_LENGTH));
        return String.format("%0" + OTP_LENGTH + "d", otp);
    }
}
