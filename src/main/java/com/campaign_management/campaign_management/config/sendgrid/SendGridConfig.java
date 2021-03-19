package com.campaign_management.campaign_management.config.sendgrid;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@PropertySource("classpath:application.properties")
public class SendGridConfig {

	@Value("${sendgrid.api.key}") String sendGridAPIKey;

	public String getSendGridAPIKey() {
		return sendGridAPIKey;
	}	
}
