package com.chrisr.template_util.controller;

import com.chrisr.template_util.request.LoginRequest;
import com.chrisr.template_util.request.SignUpRequest;
import com.chrisr.template_util.response.ApiResponse;
import com.chrisr.template_util.response.JwtAuthResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.validation.Valid;

@RequestMapping("/auth")
public interface AuthRestController {

	@PostMapping("/register")
	ResponseEntity<ApiResponse> registerUser(@Valid @RequestBody SignUpRequest signUpRequest);

	@PostMapping("/login")
	ResponseEntity<JwtAuthResponse> authenticateUserAndCreateJWT(@Valid @RequestBody LoginRequest loginRequest);
}
