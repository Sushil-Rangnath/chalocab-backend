package com.cab.chaloCab.dto;

import lombok.Data;

@Data
public class FlutterAuthResponse {
    private String accessToken;
    private String refreshToken;
    private String message;
    private String role;
    private String userId;

    public FlutterAuthResponse(String message, String accessToken, String refreshToken,String role,String userId) {
        this.message = message;
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.role=role;
        this.userId=userId;
    }


}
