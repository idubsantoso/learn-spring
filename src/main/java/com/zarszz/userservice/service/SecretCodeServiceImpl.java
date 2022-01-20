package com.zarszz.userservice.service;

import com.zarszz.userservice.domain.SecretCode;
import com.zarszz.userservice.repo.SecretCodeRepo;
import com.zarszz.userservice.requests.v1.email.SendSecretCodeFromEmail;
import com.zarszz.userservice.requests.v1.email.ValidateSecretCodeDto;
import javassist.NotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import java.util.NoSuchElementException;

@Service
public class SecretCodeServiceImpl implements SecretCodeService {

    @Autowired
    private SecretCodeRepo secretCodeRepo;

    @Override
    public boolean validate(ValidateSecretCodeDto validateSecretCodeDto) throws NoSuchElementException {
        var secretCode = secretCodeRepo.findByCodeAndEmail(validateSecretCodeDto.getSecretCode(), validateSecretCodeDto.getEmail())
                .orElseThrow(() -> new NoSuchElementException("Secret code not found"));
        /**
         * valid if created at is not more than 5 minutes from current time
         */
        var now = Instant.now();
        var createDate = secretCode.getCreatedAt();

        var duration = Duration.between(now, createDate).toMinutes();

        return duration >= -5;
    }

    public SecretCode save(SecretCode secretCode) {
        return this.secretCodeRepo.save(secretCode);
    }

    public SecretCode create(SendSecretCodeFromEmail sendSecretCodeFromEmail) {
        return new SecretCode();
    }
}
