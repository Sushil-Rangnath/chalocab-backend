package com.cab.chaloCab.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OtpConfig {

    @Value("${otp.length:6}")
    private int length;

    @Value("${otp.expiration.minutes:5}")
    private int expirationMinutes;

    public int getLength() {
        return length;
    }

    public int getExpirationMinutes() {
        return expirationMinutes;
    }
}
