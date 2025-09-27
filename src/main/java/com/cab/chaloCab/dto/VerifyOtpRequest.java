package com.cab.chaloCab.dto;


import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VerifyOtpRequest {
    private String phone;
    private String name;
    private String otp;

}
