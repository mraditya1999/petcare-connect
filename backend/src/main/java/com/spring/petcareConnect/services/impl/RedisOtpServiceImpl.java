package com.spring.petcareConnect.services.impl;

import com.spring.petcareConnect.services.OtpRedisService;
import com.spring.petcareConnect.config.AppConstants;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class RedisOtpServiceImpl implements OtpRedisService {

    private final Map<String, String> memoryStore = new ConcurrentHashMap<>();

    public RedisOtpServiceImpl() {
    }

    @Override
    public void storeOtpHash(String phone, String hash, long ttlSeconds) {
        String key = AppConstants.OTP_KEY_PREFIX + phone;
        memoryStore.put(key, hash);
        // reset attempts
        memoryStore.remove(AppConstants.OTP_ATTEMPTS_KEY_PREFIX + phone);
    }

    private String hourlyKey(String phone) {
        java.time.ZonedDateTime now = java.time.ZonedDateTime.now(java.time.ZoneOffset.UTC);
        String hour = now.format(java.time.format.DateTimeFormatter.ofPattern("yyyyMMddHH"));
        return AppConstants.OTP_HOURLY_COUNT_PREFIX + phone + ":" + hour;
    }

    @Override
    public boolean canSendOtp(String phone, int cooldownSeconds, int maxPerHour) {
        // check cooldown
        String lastKey = AppConstants.OTP_LAST_SENT_PREFIX + phone;
        String lastVal = memoryStore.get(lastKey);
        long now = java.time.Instant.now().getEpochSecond();
        if (lastVal != null) {
            try {
                long last = Long.parseLong(lastVal);
                if (now - last < cooldownSeconds) return false;
            } catch (NumberFormatException ex) {
                // ignore and allow
            }
        }

        // check hourly
        String hk = hourlyKey(phone);
        String cnt = memoryStore.get(hk);
        int count = cnt == null ? 0 : Integer.parseInt(cnt);
        return count < maxPerHour;
    }

    @Override
    public void recordOtpSent(String phone) {
        long now = java.time.Instant.now().getEpochSecond();
        String lastKey = AppConstants.OTP_LAST_SENT_PREFIX + phone;
        memoryStore.put(lastKey, String.valueOf(now));

        String hk = hourlyKey(phone);
        long v = parseLong(memoryStore.get(hk), 0L) + 1L;
        memoryStore.put(hk, String.valueOf(v));
    }

    @Override
    public int getRemainingCooldownSeconds(String phone, int cooldownSeconds) {
        String lastKey = AppConstants.OTP_LAST_SENT_PREFIX + phone;
        String lastVal = memoryStore.get(lastKey);
        if (lastVal == null) return 0;
        try {
            long last = Long.parseLong(lastVal);
            long now = java.time.Instant.now().getEpochSecond();
            long rem = cooldownSeconds - (now - last);
            return rem > 0 ? (int) rem : 0;
        } catch (NumberFormatException ex) {
            return 0;
        }
    }

    @Override
    public int getHourlySendCount(String phone) {
        String hk = hourlyKey(phone);
        String cnt = memoryStore.get(hk);
        return cnt == null ? 0 : Integer.parseInt(cnt);
    }

    @Override
    public String getOtpHash(String phone) {
        return memoryStore.get(AppConstants.OTP_KEY_PREFIX + phone);
    }

    @Override
    public void deleteOtp(String phone) {
        memoryStore.remove(AppConstants.OTP_KEY_PREFIX + phone);
        memoryStore.remove(AppConstants.OTP_ATTEMPTS_KEY_PREFIX + phone);
    }

    @Override
    public int increaseAttempts(String phone) {
        String key = AppConstants.OTP_ATTEMPTS_KEY_PREFIX + phone;
        long v = parseLong(memoryStore.get(key), 0L) + 1L;
        memoryStore.put(key, String.valueOf(v));
        return (int) v;
    }

    @Override
    public void blockPhone(String phone, long seconds) {
        memoryStore.put(AppConstants.OTP_BLOCKED_KEY_PREFIX + phone, "1");
        // cleanup otp and attempts
        deleteOtp(phone);
    }

    @Override
    public boolean isPhoneBlocked(String phone) {
        String val = memoryStore.get(AppConstants.OTP_BLOCKED_KEY_PREFIX + phone);
        return val != null;
    }

    private long parseLong(String value, long defaultValue) {
        if (value == null) return defaultValue;
        try {
            return Long.parseLong(value);
        } catch (NumberFormatException ex) {
            return defaultValue;
        }
    }
}

