package com.spring.petcareConnect.services;

public interface OtpRedisService {
    void storeOtpHash(String phone, String hash, long ttlSeconds);

    String getOtpHash(String phone);

    void deleteOtp(String phone);

    int increaseAttempts(String phone);

    void blockPhone(String phone, long seconds);

    boolean isPhoneBlocked(String phone);

    boolean canSendOtp(String phone, int cooldownSeconds, int maxPerHour);

    void recordOtpSent(String phone);

    int getRemainingCooldownSeconds(String phone, int cooldownSeconds);

    int getHourlySendCount(String phone);
}

