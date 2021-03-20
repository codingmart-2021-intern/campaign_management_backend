package com.campaign_management.campaign_management;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.io.IOException;

@SpringBootApplication
public class CampaignManagementApplication {

	public static void main(String[] args) throws IOException{
		SpringApplication.run(CampaignManagementApplication.class, args);
		
		System.out.println("All method executed successfully!!..");
	}
}

@Configuration
@EnableScheduling
@ConditionalOnProperty(name="scheduling.enabled", matchIfMissing=true)
class ScheduleingConfiguration{
	
}
