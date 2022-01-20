package com.zarszz.userservice.repository;

import com.zarszz.userservice.domain.SecretCode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.NoSuchElementException;
import java.util.Optional;

@Repository
public interface SecretCodeRepository extends JpaRepository<SecretCode, Long> {
    public Optional<SecretCode> findByCodeAndEmail(String code, String email) throws NoSuchElementException;
}
