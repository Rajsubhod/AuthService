package com.rajsubhod.authservice.controller;

import com.rajsubhod.authservice.dto.Request.LoginRequest;
import com.rajsubhod.authservice.dto.Request.RefreshTokenRequest;
import com.rajsubhod.authservice.dto.Response.JwtResponse;
import com.rajsubhod.authservice.entities.RefreshToken;
import com.rajsubhod.authservice.service.JwtService;
import com.rajsubhod.authservice.service.RefreshTokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class TokenController {

    private final AuthenticationManager authenticationManager;

    private final RefreshTokenService refreshTokenService;

    private final JwtService jwtService;


    @PostMapping("/v1/login")
    public ResponseEntity<?> AuthenticateAndGetToken(@RequestBody LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword())
        );
        if(authentication.isAuthenticated()){
//            TODO: We are creating token without checking if token excist in DB or not after check if its expired or not
            RefreshToken refreshToken = refreshTokenService.createRefreshToken(loginRequest.getUsername());
            JwtResponse token = new JwtResponse();
            token.setAccessToken(jwtService.generateToken(loginRequest.getUsername()));
            token.setRefreshToken(refreshToken.getToken());

            return new ResponseEntity<>(token, HttpStatus.OK);
        }
        else {
            return ResponseEntity.internalServerError().body("Error in generating token");
        }
    }

    @PostMapping("/v1/refreshtoken")
    public JwtResponse refreshToken(@RequestBody RefreshTokenRequest refreshTokenRequest){
        try {
            RefreshToken refreshToken = refreshTokenService.findByToken(refreshTokenRequest.getRefreshToken());
            String accessToken = jwtService.generateToken(refreshToken.getUser().getUsername());
            return new JwtResponse(accessToken, refreshToken.getToken());
        }
        catch (Exception e){
            throw new RuntimeException("Token refresh failed");
        }
    }
}
