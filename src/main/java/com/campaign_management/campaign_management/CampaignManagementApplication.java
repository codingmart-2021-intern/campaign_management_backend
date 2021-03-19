package com.campaign_management.campaign_management;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

//import com.sendgrid.*;
//import com.sendgrid.helpers.mail.Mail;
//import com.sendgrid.helpers.mail.objects.Content;
//import com.sendgrid.helpers.mail.objects.Email;

import java.io.IOException;

@SpringBootApplication
public class CampaignManagementApplication {

	public static void main(String[] args) throws IOException {
		SpringApplication.run(CampaignManagementApplication.class, args);
		
		System.out.println("All method executed successfully!!..");
		
//		Email from = new Email("balaji@codingmart.com");
//	    String subject = "Sending with SendGrid is Fun";
//	    Email to = new Email("1999bala10@gmail.com");
//	    Content content = new Content("text/plain", "and easy to do anywhere, even with Java");
//	    Mail mail = new Mail(from, subject, to, content);
//
//	    SendGrid sg = new SendGrid(System.getenv("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"));
//	    Request request = new Request();
		
//		try {
//
//	      request.setMethod(Method.POST);
//	      request.setEndpoint("mail/send");
//	      request.setBody(mail.build());
//	      request.addHeader("Access-Control-Allow-Origin", "*");
//	      request.addHeader("Content-type", "application/json");
//	      request.addHeader("Access-Control-Allow-Credentials", "true");
//	      request.addHeader("Authorization", "Bearer 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");
//	      System.out.println(request.getHeaders()+" "+request.getBody());
//	      Response response = sg.api(request);
//	      System.out.println(response.getStatusCode());
//	      System.out.println(response.getBody());
//	      System.out.println(response.getHeaders());
//	    } catch (IOException ex) {
//	      throw ex;
//	    }
	}

}
