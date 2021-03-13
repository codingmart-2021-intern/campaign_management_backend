package com.campaign_management.campaign_management.config.SMS;

import com.twilio.Twilio;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TwilioInitializer {

    private final static Logger LOGGER = LoggerFactory.getLogger(TwilioInitializer.class);

    @Autowired
    public TwilioConfiguration twilioConfiguration;

    public TwilioInitializer(TwilioConfiguration twilioConfiguration) {
        this.twilioConfiguration = twilioConfiguration;
        Twilio.init(twilioConfiguration.account_sid, twilioConfiguration.getAuthToken());
        LOGGER.info("LOGGER_FROM_TWILIO_INITIALIZER {} ",twilioConfiguration.getAccountSid());
    }
}
