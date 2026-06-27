package com.spring.petcareConnect.services;

public interface SmsService {
    void sendSms(String toE164, String message);
}

