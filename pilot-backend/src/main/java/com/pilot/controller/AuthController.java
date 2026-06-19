package com.pilot.controller;

import com.pilot.dto.*;
import com.pilot.model.User;
import com.pilot.repository.RefreshTokenRepository;
import com.pilot.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    /**
     * POST /api/auth/register
     * Telo: { fullName, email, password, confirmPassword }
     * Vraca: access token + refresh token + user info
     */
    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(authService.register(request));
    }

    /**
     * POST /api/auth/login
     * Telo: { email, password }
     * Vraca: access token + refresh token + user info
     */
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }

    /**
     * POST /api/auth/refresh
     * Telo: { refreshToken }
     * Vraca: novi access token + novi refresh token (rotacija)
     */
    @PostMapping("/refresh")
    public ResponseEntity<AuthResponse> refresh(@Valid @RequestBody RefreshTokenRequest request) {
        return ResponseEntity.ok(authService.refresh(request));
    }

    /**
     * POST /api/auth/logout
     * Header: Authorization: Bearer <accessToken>
     * Opoziva sve refresh tokene trenutnog korisnika.
     */
    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@AuthenticationPrincipal User currentUser) {
        authService.logout(currentUser.getId());
        return ResponseEntity.noContent().build();
    }
}
