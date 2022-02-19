package com.zarszz.userservice.persistence.service;

import com.zarszz.userservice.kernel.configs.constant.Cache;
import com.zarszz.userservice.domain.Role;
import com.zarszz.userservice.domain.User;
import com.zarszz.userservice.domain.projection.CurrentUserProjection;
import com.zarszz.userservice.persistence.repository.RoleRepository;
import com.zarszz.userservice.persistence.repository.UserRepository;
import com.zarszz.userservice.security.entity.AuthenticatedUser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
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

    @Autowired
    private final UserRepository userRepo;

    @Autowired
    private final RoleRepository roleRepo;

    @Autowired
    private final PasswordEncoder passwordEncoder;

    @Autowired
    private final AuthenticatedUser authenticatedUser;

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
    @CacheEvict(value = Cache.ALL_USER, allEntries = true)
    public User saveUser(User user) {
        log.info("Saving new user {} to database", user.getName());
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepo.save(user);
    }

    @Override
    public CurrentUserProjection getCurrentUser() {
        // return userRepo.findCurrentUser(authenticatedUser.getUsername()).orElseThrow(() -> new NoSuchElementException("No User Found"));
        return userRepo.findCurrentUser(authenticatedUser.getUsername());
    }

    @Override
    @CacheEvict(value = Cache.ALL_USER, allEntries = true)
    public Role saveRole(Role role) {
        log.info("Saving new role {} to database", role.getName());
        return roleRepo.save(role);
    }

    @Override
    @Caching(evict = {@CacheEvict(value = Cache.ALL_USER, allEntries = true),
            @CacheEvict(value = Cache.USER_STRING, key = "#username")})
    public void addRoleToUser(String username, String roleName) throws ResponseStatusException {
        log.info("Adding role {} to user {}", roleName, username);
        User user = userRepo.findByUsername(username);
        Role role = roleRepo.findByName(roleName);
        if (role == null)
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Unable to find this resource");
        user.getRoles().add(role);
    }

    @Override
    @Caching(evict = {@CacheEvict(value = Cache.ALL_USER, allEntries = true),
            @CacheEvict(value = Cache.USER_STRING, key = "#username")})
    public User getUser(String username) {
        log.info("Fetching user {}", username);
        return userRepo.findByUsername(username);
    }

    @Override
    @Cacheable(value = Cache.ALL_USER, keyGenerator = Cache.GENERATOR_CACHE_KEY)
    public List<User> getUsers() {
        return userRepo.findAll();
    }
}
