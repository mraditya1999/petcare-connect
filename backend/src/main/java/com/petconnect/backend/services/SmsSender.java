package com.petconnect.backend.services;

import com.twilio.exception.ApiException;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class SmsSender {

    private static final Logger logger = LoggerFactory.getLogger(AuthService.class);

    private final String accountSid;
    private final String authToken;
    private final String fromPhone;

    public SmsSender(@Value("${twilio.accountSid}") String accountSid,
                     @Value("${twilio.authToken}") String authToken,
                     @Value("${twilio.fromPhone}") String fromPhone) {
        this.accountSid = accountSid;
        this.authToken = authToken;
        this.fromPhone = fromPhone;
        com.twilio.Twilio.init(accountSid, authToken);
    }

    public void sendSms(String to, String body) {
        // Ensure number is in E.164 format
        if (!to.startsWith("+")) {
            throw new IllegalArgumentException("Phone number must be in E.164 format (e.g., +919876543210)");
        }

        logger.info("Sending SMS to: {}", to);

        try {
            Message message = Message.creator(
                    new PhoneNumber(to),           // Destination (user)
                    new PhoneNumber(fromPhone),    // Your Twilio number
                    body
            ).create();

            logger.info("SMS sent. SID: {}", message.getSid());
        } catch (ApiException e) {
            logger.error("Twilio API error: {}", e.getMessage());
            throw new RuntimeException("Failed to send SMS: " + e.getMessage());
        }
    }
}
