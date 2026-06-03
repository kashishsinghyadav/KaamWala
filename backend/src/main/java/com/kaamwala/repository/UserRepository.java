package com.kaamwala.repository;

import com.kaamwala.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

/**
 * Repository for {@link User} entity operations.
 */
@Repository
public interface UserRepository extends JpaRepository<User, UUID> {

    /**
     * Find a user by their phone number.
     *
     * @param phone the phone number to search for
     * @return an optional containing the user if found
     */
    Optional<User> findByPhone(String phone);

    /**
     * Check if a user exists with the given phone number.
     *
     * @param phone the phone number to check
     * @return true if a user exists with this phone
     */
    boolean existsByPhone(String phone);
}
