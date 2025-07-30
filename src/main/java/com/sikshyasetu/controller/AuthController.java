package com.sikshyasetu.controller;

import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sikshyasetu.dto.RefreshRequest;
import com.sikshyasetu.dto.SignupRequest;
import com.sikshyasetu.entity.JwtResponse;
import com.sikshyasetu.entity.LoginRequest;
import com.sikshyasetu.entity.RefreshToken;
import com.sikshyasetu.entity.User;
import com.sikshyasetu.enums.Role;
import com.sikshyasetu.repository.RefreshTokenRepository;
import com.sikshyasetu.security.JwtUtil;
import com.sikshyasetu.service.AuthServiceImpl;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class AuthController {

	@Autowired
    private AuthServiceImpl authService;
	@Autowired
	private JwtUtil jwtUtil;
	@Autowired
	private RefreshTokenRepository refreshTokenRepository;
//    private final AuthService authService;

    @PostMapping("/signup")
    public ResponseEntity<?> signup(@RequestBody SignupRequest req) {
        return ResponseEntity.ok(authService.signup(req));
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        try {
            Map<String, Object> response = authService.authenticate(request.getEmail(), request.getPassword());
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        }
    }

    
    @PostMapping("/refresh-token")
    public ResponseEntity<?> refresh(@RequestBody RefreshRequest request) {
        String refreshToken = request.getRefreshToken();

        Optional<RefreshToken> storedTokenOpt = refreshTokenRepository.findByToken(refreshToken);
        if (storedTokenOpt.isEmpty() || !jwtUtil.validateRefreshToken(refreshToken)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid refresh token");
        }

        RefreshToken storedToken = storedTokenOpt.get();

        // Generate new access token
        String newAccessToken = jwtUtil.generateToken(storedToken.getUserEmail(), Role.SUPER_ADMIN); // or fetch role

        return ResponseEntity.ok(Map.of("accessToken", newAccessToken));
    }



}

