package com.zarszz.userservice.persistence.repository;

import com.zarszz.userservice.domain.Role;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepository extends JpaRepository<Role, Long> {
    Role findByName(String name);
}
