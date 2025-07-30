package com.sikshyasetu.service;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.sikshyasetu.dto.SignupRequest;
import com.sikshyasetu.entity.InstitutionMaster;
import com.sikshyasetu.entity.RefreshToken;
import com.sikshyasetu.entity.User;
import com.sikshyasetu.enums.Role;
import com.sikshyasetu.repository.InstitutionRepository;
import com.sikshyasetu.repository.RefreshTokenRepository;
import com.sikshyasetu.repository.UserRepository;
import com.sikshyasetu.security.JwtUtil;
//import lombok.RequiredArgsConstructor;


@Service
//@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

	@Autowired
    private  InstitutionRepository institutionRepository;
	@Autowired
    private UserRepository userRepository;
	@Autowired
    private PasswordEncoder passwordEncoder;
	@Autowired
    private JwtUtil jwtUtil;
	@Autowired
	private RefreshTokenRepository refreshTokenRepository;

	public Map<String, Object> signup(SignupRequest req) {
	    if (userRepository.existsByEmail(req.getEmail())) {
	        throw new RuntimeException("Email already exists");
	    }

	    String institutionId = UUID.randomUUID().toString();

	    InstitutionMaster inst = new InstitutionMaster();
	    inst.setInstitutionId(institutionId);
	    inst.setInstitutionName(req.getInstitutionName());
	    inst.setSignupDate(LocalDateTime.now());
	    inst.setTrialExpiryDate(LocalDateTime.now().plusDays(7));
	    institutionRepository.save(inst);

	    User user = new User();
	    user.setName(req.getAdminName());
	    user.setEmail(req.getEmail());
	    user.setPasswordHash(passwordEncoder.encode(req.getPassword()));
	    user.setRole(Role.SUPER_ADMIN);
	    user.setInstitutionId(institutionId);
	    userRepository.save(user);

	    String accessToken = jwtUtil.generateToken(user.getEmail(), user.getRole());
	    String refreshToken = jwtUtil.generateRefreshToken(user.getEmail());

	    // Save refresh token
	    refreshTokenRepository.deleteByUserEmail(user.getEmail());
	    RefreshToken tokenEntity = new RefreshToken();
	    tokenEntity.setToken(refreshToken);
	    tokenEntity.setUserEmail(user.getEmail());
	    tokenEntity.setExpiryDate(LocalDateTime.now().plusDays(7));
	    refreshTokenRepository.save(tokenEntity);

	    return Map.of(
	        "accessToken", accessToken,
	        "refreshToken", refreshToken,
	        "role", user.getRole(),
	        "institutionId", institutionId
	    );
	}


	@Override
	public Map<String, Object> authenticate(String email, String password) {
	    User user = userRepository.findByEmail(email)
	        .orElseThrow(() -> new RuntimeException("Invalid email or password"));

	    if (!passwordEncoder.matches(password, user.getPasswordHash())) {
	        throw new RuntimeException("Invalid email or password");
	    }

	    String accessToken = jwtUtil.generateToken(user.getEmail(), user.getRole());
	    String refreshToken = jwtUtil.generateRefreshToken(user.getEmail());

	    // Clean old token
	    refreshTokenRepository.deleteByUserEmail(user.getEmail());

	    RefreshToken tokenEntity = new RefreshToken();
	    tokenEntity.setToken(refreshToken);
	    tokenEntity.setUserEmail(user.getEmail());
	    tokenEntity.setExpiryDate(LocalDateTime.now().plusDays(7));
	    refreshTokenRepository.save(tokenEntity);

	    return Map.of(
	        "accessToken", accessToken,
	        "refreshToken", refreshToken,
	        "name", user.getName(),
	        "role", user.getRole()
	    );
	}

   
}

