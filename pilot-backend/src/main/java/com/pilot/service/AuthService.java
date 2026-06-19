package com.pilot.service;

import com.pilot.dto.*;
import com.pilot.exception.EmailAlreadyExistsException;
import com.pilot.exception.InvalidTokenException;
import com.pilot.model.RefreshToken;
import com.pilot.model.StudentProfile;
import com.pilot.model.User;
import com.pilot.repository.RefreshTokenRepository;
import com.pilot.repository.StudentProfileRepository;
import com.pilot.repository.UserRepository;
import com.pilot.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private final UserRepository userRepository;
    private final StudentProfileRepository profileRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final AuthenticationManager authenticationManager;

    @Value("${pilot.jwt.access-token-expiration-ms}")
    private long accessTokenExpirationMs;

    @Value("${pilot.jwt.refresh-token-expiration-ms}")
    private long refreshTokenExpirationMs;

    /**
     * Registracija novog korisnika.
     * Kreira User + prazan StudentProfile (popunjava se tokom onboardinga).
     */
    @Transactional
    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new EmailAlreadyExistsException(
                "Korisnik sa emailom '" + request.getEmail() + "' vec postoji."
            );
        }

        if (!request.getPassword().equals(request.getConfirmPassword())) {
            throw new IllegalArgumentException("Lozinke se ne poklapaju.");
        }

        User user = User.builder()
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .fullName(request.getFullName())
                .build();
        user = userRepository.save(user);

        // Odmah kreiramo prazan profil — popunjava se tokom onboardinga
        StudentProfile profile = StudentProfile.builder()
                .user(user)
                .profileComplete(false)
                .build();
        profileRepository.save(profile);

        log.info("Novi korisnik registrovan: {}", user.getEmail());

        Authentication auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );

        return buildAuthResponse(auth, user, false);
    }

    /**
     * Login postojeceg korisnika.
     */
    @Transactional
    public AuthResponse login(LoginRequest request) {
        Authentication auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );

        User user = userRepository.findByEmail(request.getEmail()).orElseThrow();
        boolean profileComplete = profileRepository.findByUserId(user.getId())
                .map(p -> Boolean.TRUE.equals(p.getProfileComplete()))
                .orElse(false);

        log.info("Korisnik se prijavio: {}", user.getEmail());
        return buildAuthResponse(auth, user, profileComplete);
    }

    /**
     * Obnavljanje access tokena pomocu refresh tokena.
     */
    @Transactional
    public AuthResponse refresh(RefreshTokenRequest request) {
        RefreshToken refreshToken = refreshTokenRepository
                .findByToken(request.getRefreshToken())
                .orElseThrow(() -> new InvalidTokenException("Refresh token nije pronadjen."));

        if (!refreshToken.isValid()) {
            throw new InvalidTokenException("Refresh token je nevazeci ili istekao.");
        }

        // Rotacija refresh tokena — stari se opoziva, novi se izdaje
        refreshToken.setRevoked(true);
        refreshTokenRepository.save(refreshToken);

        User user = refreshToken.getUser();
        String newAccessToken = jwtTokenProvider.generateAccessTokenFromEmail(user.getEmail());
        String newRefreshTokenValue = createRefreshToken(user);

        boolean profileComplete = profileRepository.findByUserId(user.getId())
                .map(p -> Boolean.TRUE.equals(p.getProfileComplete()))
                .orElse(false);

        return AuthResponse.builder()
                .accessToken(newAccessToken)
                .refreshToken(newRefreshTokenValue)
                .tokenType("Bearer")
                .expiresIn(accessTokenExpirationMs / 1000)
                .user(mapToUserResponse(user, profileComplete))
                .build();
    }

    /**
     * Odjava — opoziva sve refresh tokene korisnika.
     */
    @Transactional
    public void logout(Long userId) {
        refreshTokenRepository.revokeAllUserTokens(userId);
        log.info("Korisnik {} se odjavio, svi refresh tokeni opozvani.", userId);
    }

    // ---- Pomocne metode ----

    private AuthResponse buildAuthResponse(Authentication auth, User user, boolean profileComplete) {
        // Opozivamo stare refresh tokene pre nego sto izdamo novi
        refreshTokenRepository.revokeAllUserTokens(user.getId());

        String accessToken = jwtTokenProvider.generateAccessToken(auth);
        String refreshTokenValue = createRefreshToken(user);

        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshTokenValue)
                .tokenType("Bearer")
                .expiresIn(accessTokenExpirationMs / 1000)
                .user(mapToUserResponse(user, profileComplete))
                .build();
    }

    private String createRefreshToken(User user) {
        RefreshToken token = RefreshToken.builder()
                .user(user)
                .token(UUID.randomUUID().toString())
                .expiresAt(LocalDateTime.now().plusSeconds(refreshTokenExpirationMs / 1000))
                .build();
        return refreshTokenRepository.save(token).getToken();
    }

    private UserResponse mapToUserResponse(User user, boolean profileComplete) {
        UserResponse response = new UserResponse();
        response.setId(user.getId());
        response.setEmail(user.getEmail());
        response.setFullName(user.getFullName());
        response.setProfileComplete(profileComplete);
        return response;
    }
}
