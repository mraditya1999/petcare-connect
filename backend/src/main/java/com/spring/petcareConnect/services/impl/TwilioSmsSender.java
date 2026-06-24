package com.spring.petcareConnect.services.impl;

import com.spring.petcareConnect.services.SmsSender;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.twilio.Twilio;
import com.twilio.exception.ApiException;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;

@Service
public class TwilioSmsSender implements SmsSender {

    private static final Logger logger = LoggerFactory.getLogger(TwilioSmsSender.class);

    public TwilioSmsSender(@Value("${twilio.accountSid:}") String accountSid,
                           @Value("${twilio.authToken:}") String authToken,
                           @Value("${twilio.fromNumber:}") String fromNumber) {
        this.fromNumber = fromNumber;
        if (accountSid != null && !accountSid.isBlank() && authToken != null && !authToken.isBlank()) {
            Twilio.init(accountSid, authToken);
        } else {
            logger.warn("Twilio not configured (missing accountSid/authToken). SMS sending will fail if used.");
        }
    }

    private final String fromNumber;

    @Override
    public void sendSms(String toE164, String message) {
        try {
            Message.creator(new PhoneNumber(toE164), new PhoneNumber(fromNumber), message).create();
            logger.debug("SMS sent to {}", toE164);
        } catch (ApiException ex) {
            logger.error("Twilio SMS failed for to={}, statusCode={}, twilioCode={}, message={}, moreInfo={}",
                    toE164, ex.getStatusCode(), ex.getCode(), ex.getMessage(), ex.getMoreInfo());
            throw new RuntimeException("Failed to send SMS", ex);
        } catch (Exception ex) {
            logger.error("Failed to send SMS to {}: {}", toE164, ex.getMessage());
            throw new RuntimeException("Failed to send SMS", ex);
        }
    }
}

