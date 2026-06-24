package com.spring.petcareConnect.services;

public interface SmsSender {
    void sendSms(String toE164, String message);
}

