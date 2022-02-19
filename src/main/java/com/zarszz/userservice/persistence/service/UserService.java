package com.zarszz.userservice.persistence.service;

import com.zarszz.userservice.domain.Role;
import com.zarszz.userservice.domain.User;

import com.zarszz.userservice.domain.projection.CurrentUserProjection;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

public interface UserService {
    User saveUser(User user);
    CurrentUserProjection getCurrentUser();
    Role saveRole(Role role);
    void addRoleToUser(String username, String roleName) throws ResponseStatusException;
    User getUser(String username);
    List<User> getUsers();
}
