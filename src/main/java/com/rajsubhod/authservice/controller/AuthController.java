package com.rajsubhod.authservice.controller;

import com.rajsubhod.authservice.dto.Response.JwtResponse;
import com.rajsubhod.authservice.dto.UserInfoDto;
import com.rajsubhod.authservice.entities.RefreshToken;
import com.rajsubhod.authservice.service.AuthService;
import com.rajsubhod.authservice.service.JwtService;

import com.rajsubhod.authservice.service.RefreshTokenService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final JwtService jwtService;

    private final RefreshTokenService refreshTokenService;

    private final AuthService authService;

    public AuthController(JwtService jwtService, RefreshTokenService refreshTokenService, AuthService authService) {
        this.jwtService = jwtService;
        this.refreshTokenService = refreshTokenService;
        this.authService = authService;
    }


    @PostMapping("/v1/signup")
    public ResponseEntity<?> signUp(@RequestBody UserInfoDto userInfoDto) {

        try{
            Boolean isSignedUp = authService.singupUser(userInfoDto);
            if(Boolean.FALSE.equals(isSignedUp)){
                return ResponseEntity.badRequest().body("User already exists");
            }
            RefreshToken refreshToken = refreshTokenService.createRefreshToken(userInfoDto.getUsername());
            String jwtToken = jwtService.generateToken(userInfoDto.getUsername());
            return new ResponseEntity<>(new JwtResponse(jwtToken, refreshToken.getToken()), HttpStatus.OK);
        }
        catch (Exception e){
            return ResponseEntity.badRequest().body("Error in signing up");
        }

    }
}
