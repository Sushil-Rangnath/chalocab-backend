package com.cab.chaloCab.util;

import org.springframework.stereotype.Component;

import java.security.SecureRandom;

@Component
public class OtpUtil {

    private final SecureRandom random = new SecureRandom();

    public String generateOtp(int length) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length; i++) {
            sb.append(random.nextInt(10)); // digits 0-9
        }
        return sb.toString();
    }
}
