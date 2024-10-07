package com.rajsubhod.authservice.repository;

import com.rajsubhod.authservice.entities.UserInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserInfoRepository extends JpaRepository<UserInfo,Long> {

    Optional<UserInfo> findByUsername(String username);
    boolean existsByEmail(String email);
}
