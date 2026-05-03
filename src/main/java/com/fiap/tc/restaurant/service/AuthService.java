package com.fiap.tc.restaurant.service;

import com.fiap.tc.restaurant.domain.BaseUser;
import com.fiap.tc.restaurant.dto.request.LoginRequest;
import com.fiap.tc.restaurant.dto.response.LoginResponse;
import com.fiap.tc.restaurant.enums.UserRole;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    public AuthService(AuthenticationManager authenticationManager, JwtService jwtService) {
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
    }

    public LoginResponse login(LoginRequest request) {
        var authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.login(), request.password())
        );

        BaseUser user = (BaseUser) authentication.getPrincipal();
        String token = jwtService.generateToken(user);
        String userType = user.getRole() == UserRole.ROLE_CUSTOMER ? "CUSTOMER" : "RESTAURANT_OWNER";

        return new LoginResponse(token, user.getId(), userType);
    }
}
