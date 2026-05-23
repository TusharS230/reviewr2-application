package com.reviewr2.reviewr2.repository;

import com.reviewr2.reviewr2.entity.User;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository {
    Optional<User> findByUsername(String username);
    Optional<User> findByEmail(String email);
}
