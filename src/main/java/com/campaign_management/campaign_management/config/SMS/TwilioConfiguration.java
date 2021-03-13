package com.campaign_management.campaign_management.config.SMS;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TwilioConfiguration {

    @Value("${twilo-account_sid}")
    public String account_sid;

    @Value("${twilo-auth_token}")
    public String auth_token;

    @Value("${twilo-trail_number}")
    public String trail_number;

    public String getAccountSid() {
        return account_sid;
    }

    public String getAuthToken() {
        return auth_token;
    }

    public String getTrailNumber() {
        return trail_number;
    }
}
