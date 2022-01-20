package com.zarszz.userservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.zarszz.userservice.domain.User;

public interface UserRepository extends JpaRepository<User, Long> {
    User findByUsername(String username);
}
