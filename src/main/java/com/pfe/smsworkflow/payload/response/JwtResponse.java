package com.pfe.smsworkflow.payload.response;

import lombok.Getter;
import lombok.Setter;

import java.util.List;
@Getter
@Setter
public class JwtResponse {
	private String token;
	private String type = "Bearer";
	private String refreshToken;
	private Long id;
	private String phone;
	private String email;
	private List<String> roles;

	public JwtResponse(String accessToken, String refreshToken, Long id, String phone, String email, List<String> roles) {
		this.token = accessToken;
		this.refreshToken = refreshToken;
		this.id = id;
		this.phone = phone;
		this.email = email;
		this.roles = roles;
	}

}
