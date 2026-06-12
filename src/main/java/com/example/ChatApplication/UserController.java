package com.example.ChatApplication;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;
import java.util.*;

@RestController
@RequestMapping("/api/users")
@CrossOrigin("*")
public class UserController {

    @Autowired
    private UserRepository ur;

    @Autowired
    private EmailService emailService;

    private BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    // 1️⃣ Register (first-time user)
    @PostMapping("/register")
    public Map<String, String> register(@RequestBody Map<String, String> body) {
        String email = body.get("email");
        String password = body.get("password");
        Map<String, String> res = new HashMap<>();

        if (ur.findByEmail(email).isPresent()) {
            res.put("status", "error");
            res.put("message", "Email already Present!");
            return res;
        }

        String otp = String.valueOf(new Random().nextInt(900000) + 100000);

        User user = new User();
        user.setEmail(email); // email as primary key
        user.setPassword(encoder.encode(password));
        user.setOtp(otp);
        user.setVerified(false);
        ur.save(user);

        try {
            emailService.sendOtpEmail(email, otp);
            res.put("status", "success");
            res.put("message", "OTP sent! Please check your email.");
        } catch (Exception e) {
            res.put("status", "error");
            res.put("message", "Failed to send OTP. Try again later.");
        }
 
        return res;
    }

    // 2️⃣ Verify OTP
    @PostMapping("/verify")
    public Map<String, String> verify(@RequestBody Map<String, String> body) {
        String email = body.get("email");
        String otp = body.get("otp");
        Map<String, String> res = new HashMap<>();

        Optional<User> userOpt = ur.findByEmailAndOtp(email, otp);
        if (userOpt.isPresent()) {
            User u = userOpt.get();
            u.setVerified(true);
            u.setOtp(null);
            ur.save(u);
            res.put("status", "success");
            res.put("message", "Verified successfully!");
        } else {
            res.put("status", "error");
            res.put("message", "Invalid OTP!");
        }
        return res;
    }

    // 3️⃣ Save profile details (username + profilePic)
    @PostMapping("/save")
    public Map<String, String> save(@RequestBody Map<String, String> body) {
        Map<String, String> res = new HashMap<>();
        String email = body.get("email");
        String username = body.get("username");
        String profilePic = body.get("profilePic");

        Optional<User> userOpt = ur.findByEmail(email);
        if (userOpt.isPresent()) {
            User u = userOpt.get();
            u.setUsername(username);
            u.setProfilePic(profilePic);
            ur.save(u);
            res.put("status", "success");
            res.put("message", "User profile updated!");
        } else {
            res.put("status", "error");
            res.put("message", "User not found!");
        }
        return res;
    }

    // 4️⃣ Update profile picture only (for dashboard)
    @PostMapping("/updateProfile")
    public Map<String, String> updateProfile(@RequestBody Map<String, String> body) {
        String email = body.get("email");
        String profilePic = body.get("profilePic");
        Map<String, String> res = new HashMap<>();

        Optional<User> userOpt = ur.findByEmail(email);
        if (userOpt.isPresent()) {
            User u = userOpt.get();
            u.setProfilePic(profilePic);
            ur.save(u);
            res.put("status", "success");
            res.put("message", "Profile updated!");
        } else {
            res.put("status", "error");
            res.put("message", "User not found!");
        }

        return res;
    }

    // 5️⃣ Get user profile (search endpoint / dashboard load)
    @GetMapping("/profile")
    public Map<String, Object> getProfile(@RequestParam String email) {
        Map<String, Object> res = new HashMap<>();
        Optional<User> userOpt = ur.findByEmail(email);

        if (userOpt.isPresent()) {
            User u = userOpt.get();
            res.put("status", "success");
            res.put("email", u.getEmail());
            res.put("username", u.getUsername());
            res.put("profilePic", u.getProfilePic());
            res.put("verified", u.isVerified());
        } else {
            res.put("status", "error");
            res.put("message", "User not found!");
        }

        return res;
    }

    // 6️⃣ Search friends by query
    @PostMapping("/search")
    public List<Map<String, String>> search(@RequestBody Map<String, String> body) {
        String query = body.get("query");
        List<User> users = ur.findByUsernameContainingIgnoreCase(query);
        List<Map<String, String>> result = new ArrayList<>();

        for (User u : users) {
            Map<String, String> map = new HashMap<>();
            map.put("email", u.getEmail());
            map.put("username", u.getUsername());
            map.put("profilePic", u.getProfilePic());
            result.add(map);
        }
        return result;
    }
 // Login endpoint (no OTP)
    @PostMapping("/login")
    public Map<String, Object> login(@RequestBody Map<String, String> body) {
        String email = body.get("email");
        String password = body.get("password");
        Map<String, Object> res = new HashMap<>();

        Optional<User> userOpt = ur.findByEmail(email);
        if (userOpt.isEmpty()) {
            res.put("message", "Invalid credentials");
            return res;
        }

        User user = userOpt.get();

        // Check password
        if (!encoder.matches(password, user.getPassword())) {
            res.put("message", "Invalid credentials");
            return res;
        }

        // Send back profile info
        res.put("message", "Login successful");
        res.put("email", user.getEmail());
        res.put("username", user.getUsername() != null ? user.getUsername() : "User");
        res.put("profilePic", user.getProfilePic() != null ? user.getProfilePic() : "default.png");

        return res;
    }

}
