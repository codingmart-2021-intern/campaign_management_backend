package com.campaign_management.campaign_management.service;
import com.sendgrid.helpers.mail.*;
import com.sendgrid.helpers.mail.objects.Content;
import com.sendgrid.helpers.mail.objects.Email;

import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.springframework.stereotype.Service;
import com.sendgrid.*;
import java.io.IOException;
/**
 * MailService
 */
@Service
public class MailService {
	
//	Sending mail using sendGrid	
	public static Boolean sendMail(JSONObject mailData,String apiKey) throws IOException, JSONException {
    	  
//  		Email from = new Email("balajisr2021@srishakthi.ac.in");
		Email from = new Email("balaji@codingmart.com");
        String subject = mailData.get("subject").toString();
        Email to = new Email(mailData.get("toAddress").toString());
        String bodyContent = mailData.get("content").toString();
        Content content = new Content("text/html",bodyContent);
        Mail mail = new Mail(from, subject, to, content);

        SendGrid sg = new SendGrid(apiKey);
        
        Request request = new Request();
  		Request batchRequest = new Request();

        String header = "Bearer "+apiKey;
        
  		batchRequest.setMethod(Method.POST);
		batchRequest.setEndpoint("mail/batch");
		batchRequest.addHeader("Content-type", "application/json");
		batchRequest.addHeader("Authorization", header);
		Response batchResponse = sg.api(batchRequest);
		
		System.out.println(batchResponse.getStatusCode());
		System.out.println(batchResponse.getBody());
		
		JSONObject batchJson = new JSONObject(batchResponse.getBody());

		mail.setBatchId(batchJson.getString("batch_id"));
		request.setMethod(Method.POST);
		request.setEndpoint("mail/send");

        request.setBody(mail.build());
		request.addHeader("Content-type", "application/json");
		request.addHeader("Authorization", header);
		
        Response response = sg.api(request);

		System.out.println(request.getHeaders());
		System.out.println(request.getBody());
        System.out.println(response.getStatusCode());
        
		if (response.getStatusCode() >= 200 && response.getStatusCode() < 300) 
			return true;
		return false;
	}
};