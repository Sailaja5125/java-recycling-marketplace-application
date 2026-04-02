package com.example.application.Service;

import com.example.application.Model.User;
import com.example.application.Repository.UserRepository;
import com.example.application.Utils.JWTUtils;
import com.example.application.Utils.UserDTO;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final JWTUtils jwtUtil;
    private final PasswordEncoder passwordEncoder;
    private final Map<String, String> resetTokens = new HashMap<>();

    public AuthService(UserRepository userRepository, JWTUtils jwtUtil, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.jwtUtil = jwtUtil;
        this.passwordEncoder = passwordEncoder;
    }

    public UserDTO signup(User user) {
        if (userRepository.findByEmail(user.getEmail()).isPresent()) {
            throw new IllegalArgumentException("Email already exists");
        }
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        
        if (user.getRole() == null) {
            user.setRole(com.example.application.Model.Role.USER);
        }
        
        return new UserDTO(userRepository.save(user));
    }

    public Map<String, Object> login(String email, String password) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("Invalid credentials"));

        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new IllegalArgumentException("Invalid credentials");
        }

        // Instead of throwing an exception, just issue a new token and proceed
        if (!user.getisLoggedIn()) {
            user.setLoggedIn(true);
            userRepository.save(user);
        }

        String token = jwtUtil.generateToken(user.getEmail());

        Map<String, Object> res = new HashMap<>();
        res.put("token", token);
        res.put("user", new UserDTO(user));
        res.put("userEntity", user);

        return res;
    }

    public String refreshToken(String refreshToken) {
        if (refreshToken == null || !jwtUtil.isTokenValid(refreshToken)) {
            throw new IllegalArgumentException("Invalid refresh token");
        }
        String email = jwtUtil.getEmailFromToken(refreshToken);
        return jwtUtil.generateToken(email);
    }

    public boolean checkLogin(String email) {
        return userRepository.findByEmail(email).map(User::getisLoggedIn).orElse(false);
    }

    public User getUserDetails(String token) {
        if (token == null || !jwtUtil.isTokenValid(token)) {
            throw new IllegalArgumentException("Invalid or expired token");
        }
        String email = jwtUtil.getEmailFromToken(token);
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
    }

    public Map<String, Object> logout(String token) {
        User user = getUserDetails(token);
        user.setLoggedIn(false);
        userRepository.save(user);

        Map<String, Object> payload = new HashMap<>();
        payload.put("message", "Logged out successfully");
        payload.put("userId", user.getId());
        return payload;
    }

    public Map<String, Object> getRewards(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        Map<String, Object> payload = new HashMap<>();
        payload.put("userId", userId);
        payload.put("rewards", user.getRewards());
        return payload;
    }

    public String forgotPassword(String email) {
        userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        String resetToken = UUID.randomUUID().toString();
        resetTokens.put(email, resetToken);
        return resetToken;
    }

    public void resetPassword(String email, String token, String newPassword) {
        if (!resetTokens.containsKey(email) || !resetTokens.get(email).equals(token)) {
            throw new IllegalArgumentException("Invalid or expired token");
        }
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
        resetTokens.remove(email);
    }
}
