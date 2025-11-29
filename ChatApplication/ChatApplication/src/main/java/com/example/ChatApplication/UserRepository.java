package com.example.ChatApplication;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, String> {
    Optional<User> findByEmail(String email);               // find by email
    Optional<User> findByEmailAndOtp(String email, String otp); // find by email and OTP
    List<User> findByUsernameContainingIgnoreCase(String username); // search users by username
}
