package com.campaign_management.campaign_management.service;
import com.sendgrid.helpers.mail.*;
import com.sendgrid.helpers.mail.objects.Content;
import com.sendgrid.helpers.mail.objects.Email;

import org.springframework.stereotype.Service;

import com.sendgrid.*;
import com.sendgrid.helpers.*;
import java.io.IOException;
/**
 * MailService
 */
@Service
public class MailService {
    
      public static Boolean sendMail(String args) throws IOException {
        Email from = new Email("balaji@codingmart.com");
        String subject = "Sending with SendGrid to balaji";
        Email to = new Email("balaji@codingmart.com");
        Content content = new Content("text/plain", "and easy to do anywhere, even with Java");
        Mail mail = new Mail(from, subject, to, content);
    
        SendGrid sg = new SendGrid("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");
        System.out.println("EMAIL ___");
        System.out.println(System.getenv("SENDGRID_API_KEY"));
        Request request = new Request();
          request.setMethod(Method.POST);
          request.setEndpoint("mail/send");
          request.setBody(mail.build());
          Response response = sg.api(request);
          System.out.println(response.getStatusCode());
          System.out.println(response.getBody());
          System.out.println(response.getHeaders());
          return true;
      }
    
};