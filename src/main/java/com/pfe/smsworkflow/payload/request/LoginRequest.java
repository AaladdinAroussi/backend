package com.pfe.smsworkflow.payload.request;


import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginRequest {
	private String username;
	private String phone;
	@NotBlank
	private String password;



}
