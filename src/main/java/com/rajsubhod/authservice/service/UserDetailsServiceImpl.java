package com.rajsubhod.authservice.service;

import com.rajsubhod.authservice.entities.UserInfo;
import com.rajsubhod.authservice.repository.UserInfoRepository;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;


@Component
@AllArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private static final Logger logger = org.slf4j.LoggerFactory.getLogger(UserDetailsServiceImpl.class);
    private final UserInfoRepository userInfoRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        logger.info("Inside loadUserByUsername of UserDetailsServiceImpl");
        UserInfo user = userInfoRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));
        return new UserDetailsMapper(user);
    }
}
