package com.example.application.Controller;

import com.example.application.Model.User;
import com.example.application.Service.AuthService;
import com.example.application.Utils.UserDTO;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/signup")
    public ResponseEntity<?> signup(@RequestBody User user) {
        try {
            return ResponseEntity.ok(authService.signup(user));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(409).body(e.getMessage());
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> request, HttpServletResponse response) {
        try {
            Map<String, Object> res = authService.login(request.get("email"), request.get("password"));
            String token = (String) res.get("token");

            ResponseCookie cookie = ResponseCookie.from("accessToken", token)
                    .httpOnly(true)
                    .secure(true)
                    .sameSite("None")
                    .path("/")
                    .maxAge(3600)
                    .build();

            response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());

            // Remove internal entity before returning to client
            res.remove("userEntity");

            return ResponseEntity.ok(res);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(401).body(e.getMessage());
        }
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> refresh(HttpServletRequest request) {
        String refreshToken = Arrays.stream(Optional.ofNullable(request.getCookies()).orElse(new Cookie[0]))
                .filter(c -> "refreshToken".equals(c.getName()))
                .map(Cookie::getValue)
                .findFirst()
                .orElse(null);

        try {
            String newAccessToken = authService.refreshToken(refreshToken);
            return ResponseEntity.ok(Map.of("accessToken", newAccessToken));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        }
    }

    @PostMapping("/login-check")
    public boolean loginCheck(@RequestBody Map<String, String> request) {
        return authService.checkLogin(request.get("email"));
    }

    @GetMapping("/me")
    public ResponseEntity<?> getUserDetails(
            @RequestHeader(value = "Authorization", required = false) String authHeader,
            @CookieValue(value = "accessToken", required = false) String cookieToken) {

        String token = cookieToken;
        if (token == null && authHeader != null && authHeader.startsWith("Bearer ")) {
            token = authHeader.substring(7);
        }

        if (token == null) {
            return ResponseEntity.status(401).body("Missing or invalid token");
        }

        try {
            User user = authService.getUserDetails(token);
            return ResponseEntity.ok(new UserDTO(user));
        } catch (IllegalArgumentException e) {
            if (e.getMessage().contains("not found")) return ResponseEntity.status(404).body(e.getMessage());
            return ResponseEntity.status(401).body(e.getMessage());
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(
            @RequestHeader(value = "Authorization", required = false) String authHeader,
            @CookieValue(value = "accessToken", required = false) String cookieToken,
            HttpServletResponse response) {

        String token = cookieToken;
        if (token == null && authHeader != null && authHeader.startsWith("Bearer ")) {
            token = authHeader.substring(7);
        }

        if (token == null) {
            return ResponseEntity.status(401).body("Missing or invalid token");
        }

        try {
            Map<String, Object> payload = authService.logout(token);

            ResponseCookie cookie = ResponseCookie.from("accessToken", "")
                    .httpOnly(true)
                    .secure(true)
                    .path("/")
                    .maxAge(0)
                    .sameSite("None")
                    .build();
            response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());

            return ResponseEntity.ok(payload);
        } catch (IllegalArgumentException e) {
            if (e.getMessage().contains("not found")) return ResponseEntity.status(404).body(e.getMessage());
            return ResponseEntity.status(401).body(e.getMessage());
        }
    }

    @GetMapping("/rewards/{userId}")
    public ResponseEntity<?> getRewards(@PathVariable Long userId) {
        try {
            return ResponseEntity.ok(authService.getRewards(userId));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(404).body(e.getMessage());
        }
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@RequestParam String email) {
        try {
            return ResponseEntity.ok(authService.forgotPassword(email));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(404).body(e.getMessage());
        }
    }

    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestBody Map<String, String> request) {
        try {
            authService.resetPassword(request.get("email"), request.get("token"), request.get("newPassword"));
            return ResponseEntity.ok("Password reset successful");
        } catch (IllegalArgumentException e) {
            if (e.getMessage().contains("not found")) return ResponseEntity.status(404).body(e.getMessage());
            return ResponseEntity.status(400).body(e.getMessage());
        }
    }
}