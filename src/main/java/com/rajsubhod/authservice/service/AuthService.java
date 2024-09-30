package com.rajsubhod.authservice.service;

import com.rajsubhod.authservice.dto.UserInfoDto;
import com.rajsubhod.authservice.entities.UserInfo;
import com.rajsubhod.authservice.entities.UserRole;
import com.rajsubhod.authservice.message.UserInfoProducer;
import com.rajsubhod.authservice.repository.UserInfoRepository;
import com.rajsubhod.authservice.repository.UserRoleRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class AuthService {

    private final UserInfoRepository userInfoRepository;
    private final UserRoleRepository userRoleRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserInfoProducer userInfoProducer;

    public AuthService(UserInfoRepository userInfoRepository, UserRoleRepository userRoleRepository, PasswordEncoder passwordEncoder, UserInfoProducer userInfoProducer) {
        this.userInfoRepository = userInfoRepository;
        this.userRoleRepository = userRoleRepository;
        this.passwordEncoder = passwordEncoder;
        this.userInfoProducer = userInfoProducer;
    }

    public UserInfo checkIfUserAlreadyExists(UserInfoDto userInfoDto) {
        return userInfoRepository.findByUsername(userInfoDto.getUsername()).orElse(null);
    }

    public Boolean singupUser(UserInfoDto userInfoDto) {
//        TODO: implement UserInfoDTO validation
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
        userInfoProducer.sendEventTOQueue(userInfoDto);
        return true;

    }
}
