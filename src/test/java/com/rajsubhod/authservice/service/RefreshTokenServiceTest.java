package com.rajsubhod.authservice.service;

import com.rajsubhod.authservice.entities.RefreshToken;
import com.rajsubhod.authservice.entities.UserInfo;
import com.rajsubhod.authservice.repository.RefreshTokenRepository;
import com.rajsubhod.authservice.repository.UserInfoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class RefreshTokenServiceTest {

    @Mock
    private RefreshTokenRepository refreshTokenRepository;

    @Mock
    private UserInfoRepository userInfoRepository;

    @InjectMocks
    private RefreshTokenService refreshTokenService;

    private UserInfo userInfo;
    private RefreshToken refreshToken;

    @BeforeEach
    public void setUp() {
        userInfo = new UserInfo();
        userInfo.setUsername("testuser");

        refreshToken = new RefreshToken();
        refreshToken.setToken(UUID.randomUUID().toString());
        refreshToken.setUser(userInfo);
        refreshToken.setExpiryDate(Instant.now().plusMillis(600000));
    }

    @Test
    public void testCreateRefreshToken_UserExists() {
        when(userInfoRepository.findByUsername(anyString())).thenReturn(Optional.of(userInfo));
        when(refreshTokenRepository.save(any(RefreshToken.class))).thenReturn(refreshToken);

        RefreshToken createdToken = refreshTokenService.createRefreshToken("testuser");

        assertNotNull(createdToken);
        assertEquals(refreshToken.getToken(), createdToken.getToken());
        verify(refreshTokenRepository, times(1)).save(any(RefreshToken.class));
    }

    @Test
    public void testCreateRefreshToken_UserNotFound() {
        when(userInfoRepository.findByUsername(anyString())).thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class, () -> {
            refreshTokenService.createRefreshToken("testuser");
        });
    }

    @Test
    public void testVerifyExpiration_TokenValid() {
        RefreshToken verifiedToken = refreshTokenService.verifyExpiration(refreshToken);

        assertNotNull(verifiedToken);
        assertEquals(refreshToken.getToken(), verifiedToken.getToken());
    }

    @Test
    public void testVerifyExpiration_TokenExpired() {
        refreshToken.setExpiryDate(Instant.now().minusMillis(600000));

        assertThrows(RuntimeException.class, () -> {
            refreshTokenService.verifyExpiration(refreshToken);
        });
        verify(refreshTokenRepository, times(1)).delete(refreshToken);
    }

    @Test
    public void testFindByToken_TokenExists() {
        when(refreshTokenRepository.findByToken(anyString())).thenReturn(Optional.of(refreshToken));

        RefreshToken foundToken = refreshTokenService.findByToken(refreshToken.getToken());

        assertNotNull(foundToken);
        assertEquals(refreshToken.getToken(), foundToken.getToken());
    }

    @Test
    public void testFindByToken_TokenNotFound() {
        when(refreshTokenRepository.findByToken(anyString())).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> {
            refreshTokenService.findByToken(refreshToken.getToken());
        });
    }
}