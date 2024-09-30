package com.rajsubhod.authservice.service;

import com.rajsubhod.authservice.entities.RefreshToken;
import com.rajsubhod.authservice.entities.UserInfo;
import com.rajsubhod.authservice.repository.RefreshTokenRepository;
import com.rajsubhod.authservice.repository.UserInfoRepository;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.UUID;

@Service
public class RefreshTokenService {

    public final RefreshTokenRepository refreshTokenRepository;
    public final UserInfoRepository userInfoRepository;
    private final JwtService jwtService;

    public RefreshTokenService(RefreshTokenRepository refreshTokenRepository, UserInfoRepository userInfoRepository, JwtService jwtService) {
        this.refreshTokenRepository = refreshTokenRepository;
        this.userInfoRepository = userInfoRepository;
        this.jwtService = jwtService;
    }

    public RefreshToken createRefreshToken(String username){
        UserInfo userInfo = userInfoRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));
        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setUser(userInfo);
        refreshToken.setToken(UUID.randomUUID().toString());
        refreshToken.setExpiryDate(Instant.now().plusMillis(600000));

        return refreshTokenRepository.save(refreshToken);
    }

    public RefreshToken verifyExpiration(RefreshToken token) {
        if (token.getExpiryDate().compareTo(Instant.now()) < 0) {
            refreshTokenRepository.delete(token);
            throw new RuntimeException("Token has expired. Please make a new signin request");
        }

        return token;
    }

    public RefreshToken findByToken(String refreshToken) {
        return refreshTokenRepository.findByToken(refreshToken)
                .map(this::verifyExpiration)
                .orElseThrow(() -> new RuntimeException("Invalid refresh Token"));
    }
}
