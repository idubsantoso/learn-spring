package com.zarszz.userservice.service;

import com.zarszz.userservice.config.Config;
import com.zarszz.userservice.domain.Role;
import com.zarszz.userservice.domain.User;
import com.zarszz.userservice.repo.RoleRepo;
import com.zarszz.userservice.repo.UserRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.http.HttpStatus;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class UserServiceImpl implements UserService, UserDetailsService {

    private final UserRepo userRepo;
    private final RoleRepo roleRepo;
    private final PasswordEncoder passwordEncoder;

    @Override
    public UserDetails loadUserByUsername(String s) throws UsernameNotFoundException {
        User user = userRepo.findByUsername(s);
        if (user == null) {
            log.error("User not found in the database");
            throw new UsernameNotFoundException("User not found in database");
        } else {
            log.info("User found in database : {}", s);
        }
        Collection<SimpleGrantedAuthority> authorities = new ArrayList<>();
        user.getRoles().forEach(role -> authorities.add(new SimpleGrantedAuthority(role.getName())));
        return new org.springframework.security.core.userdetails.User(user.getUsername(), user.getPassword(),
                authorities);
    }

    @Override
    @CacheEvict(value = Config.ALL_USER, allEntries = true)
    public User saveUser(User user) {
        log.info("Saving new user {} to database", user.getName());
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepo.save(user);
    }

    @Override
    @CacheEvict(value = Config.ALL_USER, allEntries = true)
    public Role saveRole(Role role) {
        log.info("Saving new role {} to database", role.getName());
        return roleRepo.save(role);
    }

    @Override
    @Caching(evict = { @CacheEvict(value = Config.ALL_USER, allEntries = true),
            @CacheEvict(value = Config.USER_STRING, key = "#username") })
    public void addRoleToUser(String username, String roleName) throws ResponseStatusException {
        log.info("Adding role {} to user {}", roleName, username);
        User user = userRepo.findByUsername(username);
        Role role = roleRepo.findByName(roleName);
        if (role == null)
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Unable to find this resource");
        user.getRoles().add(role);
    }

    @Override
    @Caching(evict = { @CacheEvict(value = Config.ALL_USER, allEntries = true),
            @CacheEvict(value = Config.USER_STRING, key = "#username") })
    public User getUser(String username) {
        log.info("Fetching user {}", username);
        return userRepo.findByUsername(username);
    }

    @Override
    @Cacheable(value = Config.ALL_USER, keyGenerator = Config.GENERATOR_CACHE_KEY)
    public List<User> getUsers() {
        List<User> users = userRepo.findAll();
        return users;
    }
}
