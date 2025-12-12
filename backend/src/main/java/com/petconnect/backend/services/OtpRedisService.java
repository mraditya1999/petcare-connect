package com.petconnect.backend.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import java.util.concurrent.TimeUnit;

@Service
public class OtpRedisService {

    private final RedisTemplate<String, String> redisTemplate;
    private final long otpTtlMinutes;
    private final long resendCooldownSeconds;
    private final int maxSendsPerHour;

    @Autowired
    public OtpRedisService(
            RedisTemplate<String, String> redisTemplate,
            @Value("${otp.ttl.minutes:5}") long otpTtlMinutes,
            @Value("${otp.resend.cooldown.seconds:60}") long resendCooldownSeconds,
            @Value("${otp.max.send.per.hour:5}") int maxSendsPerHour
    ) {
        this.redisTemplate = redisTemplate;
        this.otpTtlMinutes = otpTtlMinutes;
        this.resendCooldownSeconds = resendCooldownSeconds;
        this.maxSendsPerHour = maxSendsPerHour;
    }

    // ---------- Redis Keys ----------
    private String otpKey(String phone)         { return "otp:code:" + phone; }
    private String cooldownKey(String phone)    { return "otp:cooldown:" + phone; }
    private String hourlyCountKey(String phone) { return "otp:rate-limit:hour:" + phone; }
    private String attemptsKey(String phone)    { return "otp:attempts:" + phone; }
    private String blockKey(String phone)       { return "otp:block:" + phone; }  // <-- NEW

    // ---------- Phone Blocking ----------
    public void blockPhone(String phone, long blockSeconds) {
        redisTemplate.opsForValue().set(blockKey(phone), "1", blockSeconds, TimeUnit.SECONDS);
    }

    public boolean isPhoneBlocked(String phone) {
        return Boolean.TRUE.equals(redisTemplate.hasKey(blockKey(phone)));
    }

    // ---------- Cooldown ----------
    public boolean isInCooldown(String phone) {
        return Boolean.TRUE.equals(redisTemplate.hasKey(cooldownKey(phone)));
    }

    public void setCooldown(String phone) {
        redisTemplate.opsForValue().set(cooldownKey(phone), "1", resendCooldownSeconds, TimeUnit.SECONDS);
    }

    // ---------- Rate Limit ----------
    public int incrementHourlyOtpCount(String phone) {
        String key = hourlyCountKey(phone);
        Long val = redisTemplate.opsForValue().increment(key);
        if (val != null && val == 1L) {
            redisTemplate.expire(key, 1, TimeUnit.HOURS);
        }
        return val != null ? val.intValue() : 0;
    }

    public boolean exceedsHourlyLimit(String phone) {
        String val = redisTemplate.opsForValue().get(hourlyCountKey(phone));
        return val != null && Integer.parseInt(val) >= maxSendsPerHour;
    }

    // ---------- OTP Storage ----------
    public void saveOtp(String phone, String otpHash) {
        redisTemplate.opsForValue().set(otpKey(phone), otpHash, otpTtlMinutes, TimeUnit.MINUTES);
        redisTemplate.opsForValue().set(attemptsKey(phone), "0", otpTtlMinutes, TimeUnit.MINUTES);
    }

    public String getOtpHash(String phone) {
        return redisTemplate.opsForValue().get(otpKey(phone));
    }

    // ---------- Attempts ----------
    public int increaseAttempts(String phone) {
        String key = attemptsKey(phone);
        Long val = redisTemplate.opsForValue().increment(key);
        if (val != null && val == 1L) {
            redisTemplate.expire(key, otpTtlMinutes, TimeUnit.MINUTES);
        }
        return val != null ? val.intValue() : 0;
    }

    // ---------- Cleanup ----------
    public void deleteOtp(String phone) {
        redisTemplate.delete(otpKey(phone));
        redisTemplate.delete(attemptsKey(phone));
        redisTemplate.delete(cooldownKey(phone));
    }
}
