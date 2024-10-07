package com.rajsubhod.authservice.controller;

import com.rajsubhod.authservice.dto.Response.JwtResponse;
import com.rajsubhod.authservice.dto.UserInfoDto;
import com.rajsubhod.authservice.entities.RefreshToken;
import com.rajsubhod.authservice.service.AuthService;
import com.rajsubhod.authservice.service.JwtService;
import com.rajsubhod.authservice.service.RefreshTokenService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

    @Mock
    private JwtService jwtService;
    @Mock
    private RefreshTokenService refreshTokenService;
    @Mock
    private AuthService authService;

    @InjectMocks
    private AuthController authController;

    private UserInfoDto userInfoDto;
    private RefreshToken refreshToken;

    @BeforeEach
    void setUp() {
        userInfoDto = UserInfoDto.builder()
                .username("testuser")
                .password("password")
                .email("testuser@example.com")
                .build();

        refreshToken = new RefreshToken();
        refreshToken.setToken("sampleRefreshToken");
    }

    @Test
    void signUp_UserAlreadyExists() {
        when(authService.singupUser(any(UserInfoDto.class))).thenReturn(false);

        ResponseEntity<?> response = authController.signUp(userInfoDto);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("User already exists", response.getBody());
    }

    @Test
    void signUp_Success() {
        when(authService.singupUser(any(UserInfoDto.class))).thenReturn(true);
        when(refreshTokenService.createRefreshToken(anyString())).thenReturn(refreshToken);
        when(jwtService.generateToken(anyString())).thenReturn("sampleJwtToken");

        ResponseEntity<?> response = authController.signUp(userInfoDto);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        JwtResponse jwtResponse = (JwtResponse) response.getBody();
        assertNotNull(jwtResponse);
        assertEquals("sampleJwtToken", jwtResponse.getAccessToken());
        assertEquals("sampleRefreshToken", jwtResponse.getRefreshToken());
    }

    @Test
    void signUp_Exception() {
        when(authService.singupUser(any(UserInfoDto.class))).thenThrow(new RuntimeException("Error"));

        ResponseEntity<?> response = authController.signUp(userInfoDto);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Error in signing up", response.getBody());
    }
}