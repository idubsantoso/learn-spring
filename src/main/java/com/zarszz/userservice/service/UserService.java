package com.zarszz.userservice.service;

import com.zarszz.userservice.domain.Role;
import com.zarszz.userservice.domain.User;

import org.springframework.web.server.ResponseStatusException;

import java.util.List;

public interface UserService {
    User saveUser(User user);
    Role saveRole(Role role);
    void addRoleToUser(String username, String roleName) throws ResponseStatusException;
    User getUser(String username);
    List<User> getUsers();
}
