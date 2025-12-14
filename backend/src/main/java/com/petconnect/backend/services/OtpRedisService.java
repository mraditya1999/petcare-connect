package com.petconnect.backend.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import java.util.concurrent.TimeUnit;

@Service
public class OtpRedisService {

    private static final Logger logger = LoggerFactory.getLogger(OtpRedisService.class);
    
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
        if (redisTemplate == null) {
            throw new IllegalArgumentException("RedisTemplate cannot be null");
        }
        if (otpTtlMinutes <= 0) {
            throw new IllegalArgumentException("OTP TTL must be positive");
        }
        if (resendCooldownSeconds <= 0) {
            throw new IllegalArgumentException("Resend cooldown must be positive");
        }
        if (maxSendsPerHour <= 0) {
            throw new IllegalArgumentException("Max sends per hour must be positive");
        }
        
        this.redisTemplate = redisTemplate;
        this.otpTtlMinutes = otpTtlMinutes;
        this.resendCooldownSeconds = resendCooldownSeconds;
        this.maxSendsPerHour = maxSendsPerHour;
    }

    // ---------- Redis Keys ----------
    private String otpKey(String phone) {
        if (phone == null || phone.isBlank()) {
            throw new IllegalArgumentException("Phone cannot be null or blank");
        }
        return "otp:code:" + phone;
    }
    
    private String cooldownKey(String phone) {
        if (phone == null || phone.isBlank()) {
            throw new IllegalArgumentException("Phone cannot be null or blank");
        }
        return "otp:cooldown:" + phone;
    }
    
    private String hourlyCountKey(String phone) {
        if (phone == null || phone.isBlank()) {
            throw new IllegalArgumentException("Phone cannot be null or blank");
        }
        return "otp:rate-limit:hour:" + phone;
    }
    
    private String attemptsKey(String phone) {
        if (phone == null || phone.isBlank()) {
            throw new IllegalArgumentException("Phone cannot be null or blank");
        }
        return "otp:attempts:" + phone;
    }
    
    private String blockKey(String phone) {
        if (phone == null || phone.isBlank()) {
            throw new IllegalArgumentException("Phone cannot be null or blank");
        }
        return "otp:block:" + phone;
    }

    // ---------- Phone Blocking ----------
    /**
     * Blocks a phone number for a specified duration.
     *
     * @param phone the phone number to block (must not be null or blank)
     * @param blockSeconds the duration in seconds to block (must be positive)
     * @throws IllegalArgumentException if phone is null/blank or blockSeconds is not positive
     */
    public void blockPhone(String phone, long blockSeconds) {
        if (phone == null || phone.isBlank()) {
            throw new IllegalArgumentException("Phone cannot be null or blank");
        }
        if (blockSeconds <= 0) {
            throw new IllegalArgumentException("Block seconds must be positive");
        }
        
        try {
            redisTemplate.opsForValue().set(blockKey(phone), "1", blockSeconds, TimeUnit.SECONDS);
            logger.info("Blocked phone: {} for {} seconds", phone, blockSeconds);
        } catch (Exception e) {
            logger.error("Error blocking phone: {}", phone, e);
            throw new RuntimeException("Failed to block phone", e);
        }
    }

    /**
     * Checks if a phone number is currently blocked.
     *
     * @param phone the phone number to check (must not be null or blank)
     * @return true if the phone is blocked, false otherwise
     * @throws IllegalArgumentException if phone is null or blank
     */
    public boolean isPhoneBlocked(String phone) {
        if (phone == null || phone.isBlank()) {
            throw new IllegalArgumentException("Phone cannot be null or blank");
        }
        
        try {
            return Boolean.TRUE.equals(redisTemplate.hasKey(blockKey(phone)));
        } catch (Exception e) {
            logger.error("Error checking if phone is blocked: {}", phone, e);
            throw new RuntimeException("Failed to check phone block status", e);
        }
    }

    // ---------- Cooldown ----------
    /**
     * Checks if a phone number is in cooldown period.
     *
     * @param phone the phone number to check (must not be null or blank)
     * @return true if in cooldown, false otherwise
     * @throws IllegalArgumentException if phone is null or blank
     */
    public boolean isInCooldown(String phone) {
        if (phone == null || phone.isBlank()) {
            throw new IllegalArgumentException("Phone cannot be null or blank");
        }
        
        try {
            return Boolean.TRUE.equals(redisTemplate.hasKey(cooldownKey(phone)));
        } catch (Exception e) {
            logger.error("Error checking cooldown for phone: {}", phone, e);
            throw new RuntimeException("Failed to check cooldown", e);
        }
    }

    /**
     * Sets a cooldown period for a phone number.
     *
     * @param phone the phone number (must not be null or blank)
     * @throws IllegalArgumentException if phone is null or blank
     */
    public void setCooldown(String phone) {
        if (phone == null || phone.isBlank()) {
            throw new IllegalArgumentException("Phone cannot be null or blank");
        }
        
        try {
            redisTemplate.opsForValue().set(cooldownKey(phone), "1", resendCooldownSeconds, TimeUnit.SECONDS);
            logger.debug("Set cooldown for phone: {} for {} seconds", phone, resendCooldownSeconds);
        } catch (Exception e) {
            logger.error("Error setting cooldown for phone: {}", phone, e);
            throw new RuntimeException("Failed to set cooldown", e);
        }
    }

    // ---------- Rate Limit ----------
    /**
     * Increments the hourly OTP count for a phone number.
     *
     * @param phone the phone number (must not be null or blank)
     * @return the incremented count
     * @throws IllegalArgumentException if phone is null or blank
     */
    public int incrementHourlyOtpCount(String phone) {
        if (phone == null || phone.isBlank()) {
            throw new IllegalArgumentException("Phone cannot be null or blank");
        }
        
        try {
            String key = hourlyCountKey(phone);
            Long val = redisTemplate.opsForValue().increment(key);
            if (val != null && val == 1L) {
                redisTemplate.expire(key, 1, TimeUnit.HOURS);
            }
            int count = val != null ? val.intValue() : 0;
            logger.debug("Incremented hourly OTP count for phone: {} to {}", phone, count);
            return count;
        } catch (Exception e) {
            logger.error("Error incrementing hourly OTP count for phone: {}", phone, e);
            throw new RuntimeException("Failed to increment hourly OTP count", e);
        }
    }

    /**
     * Checks if a phone number has exceeded the hourly OTP limit.
     *
     * @param phone the phone number (must not be null or blank)
     * @return true if the limit is exceeded, false otherwise
     * @throws IllegalArgumentException if phone is null or blank
     */
    public boolean exceedsHourlyLimit(String phone) {
        if (phone == null || phone.isBlank()) {
            throw new IllegalArgumentException("Phone cannot be null or blank");
        }
        
        try {
            String val = redisTemplate.opsForValue().get(hourlyCountKey(phone));
            boolean exceeds = val != null && Integer.parseInt(val) >= maxSendsPerHour;
            if (exceeds) {
                logger.warn("Phone: {} has exceeded hourly OTP limit", phone);
            }
            return exceeds;
        } catch (NumberFormatException e) {
            logger.error("Error parsing hourly count for phone: {}", phone, e);
            return false;
        } catch (Exception e) {
            logger.error("Error checking hourly limit for phone: {}", phone, e);
            throw new RuntimeException("Failed to check hourly limit", e);
        }
    }

    // ---------- OTP Storage ----------
    /**
     * Saves an OTP hash for a phone number.
     *
     * @param phone the phone number (must not be null or blank)
     * @param otpHash the hashed OTP (must not be null or blank)
     * @throws IllegalArgumentException if phone or otpHash is null or blank
     */
    public void saveOtp(String phone, String otpHash) {
        if (phone == null || phone.isBlank()) {
            throw new IllegalArgumentException("Phone cannot be null or blank");
        }
        if (otpHash == null || otpHash.isBlank()) {
            throw new IllegalArgumentException("OTP hash cannot be null or blank");
        }
        
        try {
            redisTemplate.opsForValue().set(otpKey(phone), otpHash, otpTtlMinutes, TimeUnit.MINUTES);
            redisTemplate.opsForValue().set(attemptsKey(phone), "0", otpTtlMinutes, TimeUnit.MINUTES);
            logger.debug("Saved OTP for phone: {}", phone);
        } catch (Exception e) {
            logger.error("Error saving OTP for phone: {}", phone, e);
            throw new RuntimeException("Failed to save OTP", e);
        }
    }

    /**
     * Retrieves the OTP hash for a phone number.
     *
     * @param phone the phone number (must not be null or blank)
     * @return the OTP hash if found, null otherwise
     * @throws IllegalArgumentException if phone is null or blank
     */
    public String getOtpHash(String phone) {
        if (phone == null || phone.isBlank()) {
            throw new IllegalArgumentException("Phone cannot be null or blank");
        }
        
        try {
            return redisTemplate.opsForValue().get(otpKey(phone));
        } catch (Exception e) {
            logger.error("Error retrieving OTP hash for phone: {}", phone, e);
            throw new RuntimeException("Failed to retrieve OTP hash", e);
        }
    }

    // ---------- Attempts ----------
    /**
     * Increments the verification attempt count for a phone number.
     *
     * @param phone the phone number (must not be null or blank)
     * @return the incremented attempt count
     * @throws IllegalArgumentException if phone is null or blank
     */
    public int increaseAttempts(String phone) {
        if (phone == null || phone.isBlank()) {
            throw new IllegalArgumentException("Phone cannot be null or blank");
        }
        
        try {
            String key = attemptsKey(phone);
            Long val = redisTemplate.opsForValue().increment(key);
            if (val != null && val == 1L) {
                redisTemplate.expire(key, otpTtlMinutes, TimeUnit.MINUTES);
            }
            int attempts = val != null ? val.intValue() : 0;
            logger.debug("Increased attempts for phone: {} to {}", phone, attempts);
            return attempts;
        } catch (Exception e) {
            logger.error("Error increasing attempts for phone: {}", phone, e);
            throw new RuntimeException("Failed to increase attempts", e);
        }
    }

    // ---------- Cleanup ----------
    /**
     * Deletes all OTP-related data for a phone number.
     *
     * @param phone the phone number (must not be null or blank)
     * @throws IllegalArgumentException if phone is null or blank
     */
    public void deleteOtp(String phone) {
        if (phone == null || phone.isBlank()) {
            throw new IllegalArgumentException("Phone cannot be null or blank");
        }
        
        try {
            redisTemplate.delete(otpKey(phone));
            redisTemplate.delete(attemptsKey(phone));
            redisTemplate.delete(cooldownKey(phone));
            logger.debug("Deleted OTP data for phone: {}", phone);
        } catch (Exception e) {
            logger.error("Error deleting OTP data for phone: {}", phone, e);
            throw new RuntimeException("Failed to delete OTP data", e);
        }
    }
}
