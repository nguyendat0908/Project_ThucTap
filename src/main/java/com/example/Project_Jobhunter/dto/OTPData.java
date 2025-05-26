package com.example.Project_Jobhunter.dto;

import java.time.LocalDateTime;

public class OTPData {

    private String code;
    private LocalDateTime expiryTime;

    public OTPData(String code, int expiryTime) {
        this.code = code;
        this.expiryTime = LocalDateTime.now().plusMinutes(expiryTime);
    }

    public String getCode() {
        return code;
    }

    public boolean isExpired() {
        return LocalDateTime.now().isAfter(expiryTime);
    }
}
