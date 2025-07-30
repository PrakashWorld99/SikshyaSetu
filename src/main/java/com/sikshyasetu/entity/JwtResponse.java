package com.sikshyasetu.entity;

import com.sikshyasetu.enums.Role;

public class JwtResponse {
	private String token;
    private String name;
    private Role role;
    

	public String getToken() {
		return token;
	}
	public void setToken(String token) {
		this.token = token;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Role getRole() {
		return role;
	}
	public void setRole(Role role) {
		this.role = role;
	}
	public JwtResponse(String token, String name, Role role) {
		super();
		this.token = token;
		this.name = name;
		this.role = role;
	}
		
}
