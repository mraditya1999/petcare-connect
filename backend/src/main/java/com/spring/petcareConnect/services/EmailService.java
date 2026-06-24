package com.spring.petcareConnect.services;

import com.spring.petcareConnect.entities.User;
import com.spring.petcareConnect.enums.EmailType;
import com.spring.petcareConnect.services.impl.EmailServiceImpl;

public interface EmailService {
    void sendEmail(User user, EmailType type);
}
