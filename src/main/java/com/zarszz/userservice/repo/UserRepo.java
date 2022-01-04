package com.zarszz.userservice.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import com.zarszz.userservice.domain.User;

public interface UserRepo extends JpaRepository<User, Long> {
    User findByUsername(String username);
}
