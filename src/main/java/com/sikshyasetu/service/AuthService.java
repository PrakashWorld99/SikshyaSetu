package com.sikshyasetu.service;

import java.util.Map;

import com.sikshyasetu.dto.SignupRequest;
import com.sikshyasetu.entity.User;

public interface AuthService {
	Map<String, Object> signup(SignupRequest req);
    Map<String, Object> authenticate(String email, String password);
}