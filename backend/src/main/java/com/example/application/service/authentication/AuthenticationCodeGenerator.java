package com.example.application.service.authentication;

import com.example.domain.model.authentication.AuthenticationCode;
import com.example.domain.model.account.SecretKey;

public class AuthenticationCodeGenerator {

    private final TimeBasedOneTimePasswordGenerator otpGenerator;

    public AuthenticationCodeGenerator() {
        this.otpGenerator = TimeBasedOneTimePasswordGenerator.builder().algorithm("HmacSHA1").timeStepInSeconds(30)
                .build();
    }

    public AuthenticationCode generate(SecretKey secretKey) {
        int otp = otpGenerator.generate(secretKey.value());
        final String code = String.format("%06d", otp);
        return new AuthenticationCode(code);
    }
}
