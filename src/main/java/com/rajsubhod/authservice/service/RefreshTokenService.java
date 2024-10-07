package com.rajsubhod.authservice.service;

import com.rajsubhod.authservice.entities.RefreshToken;
import com.rajsubhod.authservice.entities.UserInfo;
import com.rajsubhod.authservice.repository.RefreshTokenRepository;
import com.rajsubhod.authservice.repository.UserInfoRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {

    private static final Logger logger = LoggerFactory.getLogger(RefreshTokenService.class);
    private final RefreshTokenRepository refreshTokenRepository;
    private final UserInfoRepository userInfoRepository;
    private final JwtService jwtService;


    public RefreshToken createRefreshToken(String username){
        logger.info("Creating refresh token for user: {}", username);
        UserInfo userInfo = userInfoRepository.findByUsername(username)
                .orElseThrow(() -> {
                    logger.error("User not found with username: {}", username);
                    return new UsernameNotFoundException("User not found with username: " + username);
                });
        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setUser(userInfo);
        refreshToken.setToken(UUID.randomUUID().toString());
        refreshToken.setExpiryDate(Instant.now().plusMillis(600000));

        return refreshTokenRepository.save(refreshToken);
    }

    public RefreshToken verifyExpiration(RefreshToken token) {
        logger.info("Verifying expiration of refresh token: {}", token);
        if (token.getExpiryDate().compareTo(Instant.now()) < 0) {
            refreshTokenRepository.delete(token);
            logger.error("Refresh token has expired");
            throw new RuntimeException("Token has expired. Please make a new signin request");
        }
        return token;
    }

    public RefreshToken findByToken(String refreshToken) {
        logger.info("Finding refresh token");
        return refreshTokenRepository.findByToken(refreshToken)
                .map(this::verifyExpiration)
                .orElseThrow(() -> {
                    logger.error("Invalid refresh token");
                    return new RuntimeException("Invalid refresh Token");
                });
    }
}
