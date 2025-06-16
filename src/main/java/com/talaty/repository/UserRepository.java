package com.talaty.repository;

import com.talaty.enums.Role;
import com.talaty.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    User findByUsernameOrEmail(String username, String email);
    Optional<User> findByUsername(String username);
    Optional<User> findByEmail(String email);
    Optional<User> findByPhone(String phone);
    Optional<User> findByEmailVerificationToken(String token);
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
    boolean existsByPhone(String phone);

    @Query("SELECT u FROM User u WHERE u.username = :username OR u.email = :username")
    Optional<User> findByUsernameOrEmail(@Param("username") String username);

    @Query("SELECT u FROM User u WHERE u.role = :role")
    List<User> findByRole(@Param("role") Role role);

    @Query("SELECT u FROM User u WHERE u.verified = false AND u.createdAt < :cutoffTime")
    List<User> findUnverifiedUsersOlderThan(@Param("cutoffTime") LocalDateTime cutoffTime);
}