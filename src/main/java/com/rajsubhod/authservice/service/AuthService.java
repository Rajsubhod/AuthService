package com.rajsubhod.authservice.service;

import com.rajsubhod.authservice.dto.UserInfoDto;
import com.rajsubhod.authservice.entities.UserInfo;
import com.rajsubhod.authservice.entities.UserRole;
import com.rajsubhod.authservice.message.UserInfoProducer;
import com.rajsubhod.authservice.repository.UserInfoRepository;
import com.rajsubhod.authservice.repository.UserRoleRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class AuthService {

    private static final Logger logger = LoggerFactory.getLogger(AuthService.class);
    private final UserInfoRepository userInfoRepository;
    private final UserRoleRepository userRoleRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserInfoProducer userInfoProducer;

    /**
     * Check if user already exists
     * @param userInfoDto
     * @return UserInfo
     */
    public UserInfo checkIfUserAlreadyExists(UserInfoDto userInfoDto) {
        logger.info("Checking if user already exists");
        return userInfoRepository.findByUsername(userInfoDto.getUsername()).orElse(null);
    }

    /**
     * Get user by username
     * @param userName
     * @return userId
     */
    public String getUserIdByUsername(String userName){
        logger.info("Fetching userInfo by username {}", userName);
        UserInfo userInfo = userInfoRepository.findByUsername(userName).orElse(null);
        return Objects.nonNull(userInfo) ? userInfo.getUserId() : null;
    }

    public String getUserRoleByUserId(String userId){
        logger.info("Fetching user role by userId {}", userId);
        UserInfo userInfo = userInfoRepository.findById(userId).orElseThrow(() -> {
            logger.error("User not found with userId {}", userId);
            return new RuntimeException("User not found with userId " + userId);
        });
        return Objects.nonNull(userInfo) ? userInfo.getRoles().stream().findFirst().get().getRole() : null;

    }

    /**
     * Sign up user to DB and send event to kafka
     * @param userInfoDto
     * @return Boolean
     */
    public Boolean singupUser(UserInfoDto userInfoDto) {
        logger.info("Signing up user {}", userInfoDto.getUsername());
        userInfoDto.setPassword(passwordEncoder.encode(userInfoDto.getPassword()));
        if(Objects.nonNull(checkIfUserAlreadyExists(userInfoDto))){
            return false;
        }

        String userId = UUID.randomUUID().toString();
        UserRole userRole = userRoleRepository.findByRole("ROLE_USER");

        UserInfo userInfo = new UserInfo(
                userId,
                userInfoDto.getUsername(),
                userInfoDto.getPassword(),
                userInfoDto.getEmail(),
                new HashSet<>(Set.of(userRole))
        );

        userInfoDto.setUserId(userId);
        userInfoRepository.save(userInfo);
        // send event to kafka
        logger.info("Sending user info to kafka topic");
        userInfoProducer.sendEventTOQueue(userInfoDto);
        return true;

    }

}
