package com.rajsubhod.authservice.service;


import com.rajsubhod.authservice.dto.UserInfoDto;
import com.rajsubhod.authservice.entities.UserInfo;
import com.rajsubhod.authservice.entities.UserRole;
import com.rajsubhod.authservice.message.UserInfoProducer;
import com.rajsubhod.authservice.repository.UserInfoRepository;
import com.rajsubhod.authservice.repository.UserRoleRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AuthServiceTest {

    @Mock
    private UserInfoRepository userInfoRepository;
    @Mock
    private UserRoleRepository userRoleRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private UserInfoProducer userInfoProducer;

    @InjectMocks
    private AuthService authService;

    private  UserInfoDto userInfoDto;
    private UserInfo userInfo;
    private UserRole userRole;

    @BeforeEach
    public void setUp() {
        userInfoDto = UserInfoDto.builder()
                .username("testuser")
                .password("password")
                .email("testuser@example.com")
                .build();

        userRole = new UserRole();
        userRole.setRole("ROLE_USER");

        userInfo = new UserInfo(
                UUID.randomUUID().toString(),
                userInfoDto.getUsername(),
                userInfoDto.getPassword(),
                userInfoDto.getEmail(),
                Set.of(userRole)
        );

    }

    @Test
    public void testCheckIfUserAlreadyExists_UserExists() {
        when(userInfoRepository.findByUsername(anyString())).thenReturn(Optional.of(userInfo));

        UserInfo result = authService.checkIfUserAlreadyExists(userInfoDto);

        assertNotNull(result);
        assertEquals(userInfo.getUsername(), result.getUsername());
    }

    @Test
    public void testCheckIfUserAlreadyExists_UserDoesNotExist() {
        when(userInfoRepository.findByUsername(anyString())).thenReturn(Optional.empty());

        UserInfo result = authService.checkIfUserAlreadyExists(userInfoDto);

        assertNull(result);
    }

    @Test
    public void testGetUserByUsername_UserExists() {
        when(userInfoRepository.findByUsername(anyString())).thenReturn(Optional.of(userInfo));

        String userId = authService.getUserByUsername(userInfoDto.getUsername());

        assertNotNull(userId);
        assertEquals(userInfo.getUserId(), userId);
    }

    @Test
    public void testGetUserByUsername_UserDoesNotExist() {
        when(userInfoRepository.findByUsername(anyString())).thenReturn(Optional.empty());

        String userId = authService.getUserByUsername(userInfoDto.getUsername());

        assertNull(userId);
    }

    @Test
    public void testSignupUser_UserDoesNotExist() {
        when(userInfoRepository.findByUsername(anyString())).thenReturn(Optional.empty());
        when(userRoleRepository.findByRole(anyString())).thenReturn(userRole);
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");

        Boolean result = authService.singupUser(userInfoDto);

        assertTrue(result);
        verify(userInfoRepository, times(1)).save(any(UserInfo.class));
        verify(userInfoProducer, times(1)).sendEventTOQueue(any(UserInfoDto.class));
    }

    @Test
    public void testSignupUser_UserExists() {
        when(userInfoRepository.findByUsername(anyString())).thenReturn(Optional.of(userInfo));

        Boolean result = authService.singupUser(userInfoDto);

        assertFalse(result);
        verify(userInfoRepository, never()).save(any(UserInfo.class));
        verify(userInfoProducer, never()).sendEventTOQueue(any(UserInfoDto.class));
    }
}
