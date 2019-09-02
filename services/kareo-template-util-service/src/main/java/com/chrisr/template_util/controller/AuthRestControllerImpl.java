package com.chrisr.template_util.controller;

import com.chrisr.template_util.repository.entity.User;
import com.chrisr.template_util.request.LoginRequest;
import com.chrisr.template_util.request.SignUpRequest;
import com.chrisr.template_util.response.ApiResponse;
import com.chrisr.template_util.response.JwtAuthResponse;
import com.chrisr.template_util.security.JwtTokenProvider;
import com.chrisr.template_util.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
public class AuthRestControllerImpl implements AuthRestController {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;
    private final UserService userService;

    @Autowired
    public AuthRestControllerImpl(AuthenticationManager authenticationManager,
                                  JwtTokenProvider jwtTokenProvider,
                                  UserService userService) {
        this.authenticationManager = authenticationManager;
        this.jwtTokenProvider = jwtTokenProvider;
        this.userService = userService;
    }

    @Override
    public ResponseEntity<ApiResponse> registerUser(@Valid @RequestBody SignUpRequest signUpRequest) {

        User user = userService.registerUser(signUpRequest);
        return ResponseEntity.ok().body(new ApiResponse(true, "User (" + user.getUsername() + ") registered successfully."));

//		URI location = ServletUriComponentsBuilder.fromCurrentContextPath().path("/api/users/{username}").buildAndExpand(result.getUsername()).toUri();
//		return ResponseEntity.created(location).body(new ApiResponse(true, "User registered successfully"));
    }

    @Override
    public ResponseEntity<JwtAuthResponse> authenticateUserAndCreateJWT(@Valid @RequestBody LoginRequest loginRequest) {

        // TODO: I could do my own custom auth here instead of relying/using Spring-specific AuthenticationManager??
        //  I could do the user lookup myself here,
        //  but will not be able to set SecurityContextHolder?

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getUsername(),
                        loginRequest.getPassword()
                )
        );

        // SecurityContext used by the framework to hold the currently logged-in user
        // After setting the Authentication in the context,
        // we’ll now be able to check if the current user is authenticated – using securityContext.getAuthentication().isAuthenticated()
        SecurityContextHolder.getContext().setAuthentication(authentication);

        String jwt = jwtTokenProvider.generateToken(authentication);
        return ResponseEntity.ok().body(new JwtAuthResponse(jwt));
    }
}
