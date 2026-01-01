package com.petconnect.backend.utils;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

/**
 * Utility class for date and time operations.
 * Provides common date/time formatting and calculations.
 */
public final class DateTimeUtils {

    private DateTimeUtils() {
        // Utility class
    }

    /**
     * Common date/time formatters.
     */
    public static final DateTimeFormatter ISO_DATE_TIME = DateTimeFormatter.ISO_DATE_TIME;
    public static final DateTimeFormatter HUMAN_READABLE = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    public static final DateTimeFormatter DATE_ONLY = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    /**
     * Get current date/time in UTC.
     */
    public static LocalDateTime nowUtc() {
        return ZonedDateTime.now(ZoneId.of("UTC")).toLocalDateTime();
    }

    /**
     * Format LocalDateTime to ISO string.
     */
    public static String formatIso(LocalDateTime dateTime) {
        return dateTime != null ? dateTime.format(ISO_DATE_TIME) : null;
    }

    /**
     * Format LocalDateTime to human readable string.
     */
    public static String formatHumanReadable(LocalDateTime dateTime) {
        return dateTime != null ? dateTime.format(HUMAN_READABLE) : null;
    }

    /**
     * Parse ISO date/time string.
     */
    public static LocalDateTime parseIso(String dateTimeString) {
        return dateTimeString != null ? LocalDateTime.parse(dateTimeString, ISO_DATE_TIME) : null;
    }

    /**
     * Check if a date/time is in the past.
     */
    public static boolean isPast(LocalDateTime dateTime) {
        return dateTime != null && dateTime.isBefore(nowUtc());
    }

    /**
     * Check if a date/time is in the future.
     */
    public static boolean isFuture(LocalDateTime dateTime) {
        return dateTime != null && dateTime.isAfter(nowUtc());
    }

    /**
     * Calculate minutes between two date/times.
     */
    public static long minutesBetween(LocalDateTime start, LocalDateTime end) {
        ValidationUtils.requireNotNull(start, "Start date/time");
        ValidationUtils.requireNotNull(end, "End date/time");
        return ChronoUnit.MINUTES.between(start, end);
    }

    /**
     * Calculate hours between two date/times.
     */
    public static long hoursBetween(LocalDateTime start, LocalDateTime end) {
        ValidationUtils.requireNotNull(start, "Start date/time");
        ValidationUtils.requireNotNull(end, "End date/time");
        return ChronoUnit.HOURS.between(start, end);
    }

    /**
     * Add minutes to a date/time.
     */
    public static LocalDateTime addMinutes(LocalDateTime dateTime, long minutes) {
        ValidationUtils.requireNotNull(dateTime, "Date/time");
        return dateTime.plusMinutes(minutes);
    }

    /**
     * Add hours to a date/time.
     */
    public static LocalDateTime addHours(LocalDateTime dateTime, long hours) {
        ValidationUtils.requireNotNull(dateTime, "Date/time");
        return dateTime.plusHours(hours);
    }

    /**
     * Get relative time description (e.g., "2 hours ago").
     */
    public static String getRelativeTime(LocalDateTime dateTime) {
        if (dateTime == null) {
            return null;
        }

        LocalDateTime now = nowUtc();
        long minutes = minutesBetween(dateTime, now);

        if (minutes < 1) {
            return "just now";
        } else if (minutes < 60) {
            return minutes + " minute" + (minutes == 1 ? "" : "s") + " ago";
        } else if (minutes < 1440) { // 24 hours
            long hours = minutes / 60;
            return hours + " hour" + (hours == 1 ? "" : "s") + " ago";
        } else {
            long days = minutes / 1440;
            return days + " day" + (days == 1 ? "" : "s") + " ago";
        }
    }
}