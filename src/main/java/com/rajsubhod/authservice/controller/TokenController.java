package com.rajsubhod.authservice.controller;

import com.rajsubhod.authservice.dto.Request.LoginRequest;
import com.rajsubhod.authservice.dto.Request.RefreshTokenRequest;
import com.rajsubhod.authservice.dto.Response.JwtResponse;
import com.rajsubhod.authservice.entities.RefreshToken;
import com.rajsubhod.authservice.service.AuthService;
import com.rajsubhod.authservice.service.JwtService;
import com.rajsubhod.authservice.service.RefreshTokenService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Objects;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class TokenController {

    private static final Logger logger = LoggerFactory.getLogger(TokenController.class);

    private final AuthenticationManager authenticationManager;
    private final RefreshTokenService refreshTokenService;
    private final JwtService jwtService;
    private final AuthService authService;


    /**
     * Login API
     * </p>
     * This method is used to authenticate user and generate token
     * @param loginRequest
     * @return refreshToken and accessToken
     */
    @PostMapping("/v1/login")
    public ResponseEntity<?> AuthenticateAndGetToken(@RequestBody LoginRequest loginRequest) {
        logger.info("Login request By user: {}", loginRequest.getUsername());
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword())
        );
        if (authentication.isAuthenticated()) {
//            TODO: We are creating token without checking if token exist in DB or not after check if its expired or not
            RefreshToken refreshToken = refreshTokenService.createRefreshToken(loginRequest.getUsername());
            JwtResponse token = new JwtResponse();
            token.setAccessToken(jwtService.generateToken(loginRequest.getUsername()));
            token.setRefreshToken(refreshToken.getToken());

            logger.info("Authentication successful for user: {}", loginRequest.getUsername());
            return new ResponseEntity<>(token, HttpStatus.OK);
        } else {
            logger.error("Authentication token failed generation for user: {}", loginRequest.getUsername());
            return ResponseEntity.internalServerError().body("Error in generating token");
        }
    }

    /**
     * Refresh token API
     * </p>
     * @param refreshTokenRequest
     * @return new accessToken and refreshToken
     */
    @PostMapping("/v1/refreshtoken")
    public JwtResponse refreshToken(@RequestBody RefreshTokenRequest refreshTokenRequest) {
        try {
            RefreshToken refreshToken = refreshTokenService.findByToken(refreshTokenRequest.getRefreshToken());
            logger.info("Refresh token request By user: {}", refreshToken.getUser().getUsername());
            String accessToken = jwtService.generateToken(refreshToken.getUser().getUsername());
            return new JwtResponse(accessToken, refreshToken.getToken());
        } catch (Exception e) {
            logger.error("Refresh token failed for user: {}", refreshTokenRequest.getRefreshToken());
            throw new RuntimeException("Token refresh failed");
        }
    }

    /**
     * Ping API
     * </p>
     * This method is used to check if the connection is alive
     * @return userId
     */
    @GetMapping("/v1/ping")
    public ResponseEntity<String> ping() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            String userId = authService.getUserByUsername(authentication.getName());
            logger.info("Connection request by {}", userId);
            if (Objects.nonNull(userId)) {
                logger.info("Connection is alive");
                return ResponseEntity.ok().header("userId", userId).body(userId); //TODO: make the header variable fixed
            }
        }
        logger.error("Unauthorized access");
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized");
    }
}
