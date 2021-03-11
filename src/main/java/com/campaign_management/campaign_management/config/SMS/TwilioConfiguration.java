package com.campaign_management.campaign_management.config.SMS;

import org.springframework.context.annotation.Configuration;

@Configuration
public class TwilioConfiguration {

    public String account_sid = "ACeb9125e5ada047b4dffd567708efcd94";
    public String auth_token = "8ee1dfc725a9b2fcf2db3307df84ba06";
    public String trail_number = "+14242066397";

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
