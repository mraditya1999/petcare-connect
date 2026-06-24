package com.spring.petcareConnect.services.impl;

import com.spring.petcareConnect.services.OtpRedisService;
import com.spring.petcareConnect.config.AppConstants;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
public class RedisOtpServiceImpl implements OtpRedisService {

    private final StringRedisTemplate redisTemplate;

    public RedisOtpServiceImpl(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Override
    public void storeOtpHash(String phone, String hash, long ttlSeconds) {
        String key = AppConstants.OTP_KEY_PREFIX + phone;
        redisTemplate.opsForValue().set(key, hash, Duration.ofSeconds(ttlSeconds));
        // reset attempts
        redisTemplate.delete(AppConstants.OTP_ATTEMPTS_KEY_PREFIX + phone);
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
        String lastVal = redisTemplate.opsForValue().get(lastKey);
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
        String cnt = redisTemplate.opsForValue().get(hk);
        int count = cnt == null ? 0 : Integer.parseInt(cnt);
        return count < maxPerHour;
    }

    @Override
    public void recordOtpSent(String phone) {
        long now = java.time.Instant.now().getEpochSecond();
        String lastKey = AppConstants.OTP_LAST_SENT_PREFIX + phone;
        redisTemplate.opsForValue().set(lastKey, String.valueOf(now), Duration.ofDays(1));

        String hk = hourlyKey(phone);
        Long v = redisTemplate.opsForValue().increment(hk);
        if (v != null && v == 1L) {
            // set expiry to 3600 seconds from now
            redisTemplate.expire(hk, Duration.ofHours(1));
        }
    }

    @Override
    public int getRemainingCooldownSeconds(String phone, int cooldownSeconds) {
        String lastKey = AppConstants.OTP_LAST_SENT_PREFIX + phone;
        String lastVal = redisTemplate.opsForValue().get(lastKey);
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
        String cnt = redisTemplate.opsForValue().get(hk);
        return cnt == null ? 0 : Integer.parseInt(cnt);
    }

    @Override
    public String getOtpHash(String phone) {
        return redisTemplate.opsForValue().get(AppConstants.OTP_KEY_PREFIX + phone);
    }

    @Override
    public void deleteOtp(String phone) {
        redisTemplate.delete(AppConstants.OTP_KEY_PREFIX + phone);
        redisTemplate.delete(AppConstants.OTP_ATTEMPTS_KEY_PREFIX + phone);
    }

    @Override
    public int increaseAttempts(String phone) {
        String key = AppConstants.OTP_ATTEMPTS_KEY_PREFIX + phone;
        Long v = redisTemplate.opsForValue().increment(key);
        if (v == null) return 1;
        // keep attempts key short-lived (same TTL as OTP approx)
        redisTemplate.expire(key, Duration.ofMinutes(10));
        return v.intValue();
    }

    @Override
    public void blockPhone(String phone, long seconds) {
        redisTemplate.opsForValue().set(AppConstants.OTP_BLOCKED_KEY_PREFIX + phone, "1", Duration.ofSeconds(seconds));
        // cleanup otp and attempts
        deleteOtp(phone);
    }

    @Override
    public boolean isPhoneBlocked(String phone) {
        String val = redisTemplate.opsForValue().get(AppConstants.OTP_BLOCKED_KEY_PREFIX + phone);
        return val != null;
    }
}

