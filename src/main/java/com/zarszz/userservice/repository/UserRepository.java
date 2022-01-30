package com.zarszz.userservice.repository;

import com.zarszz.userservice.domain.projection.CurrentUserProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import com.zarszz.userservice.domain.User;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    User findByUsername(String username);

    @Query(value = "SELECT u.name, u.username FROM user u WHERE u.username = :username", nativeQuery = true)
    CurrentUserProjection findCurrentUser(@Param("username") String username);
}
