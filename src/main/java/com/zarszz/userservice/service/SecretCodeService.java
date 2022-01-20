package com.zarszz.userservice.service;

import com.zarszz.userservice.domain.SecretCode;
import com.zarszz.userservice.requests.v1.email.ValidateSecretCodeDto;

public interface SecretCodeService {

    boolean validate(ValidateSecretCodeDto validateSecretCodeDto) throws Exception;
}
