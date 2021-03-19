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
    
      public static Boolean sendGridMail(JSONObject mailData) throws IOException, JSONException {
        
        Email from = new Email("balaji@codingmart.com");
        String subject = mailData.get("subject").toString();
        Email to = new Email(mailData.get("toAddress").toString());
        String bodyContent = mailData.get("content").toString();
        Content content = new Content("text/html",bodyContent);
        Mail mail = new Mail(from, subject, to, content);
    
//        SendGrid sg = new SendGrid("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");
        SendGrid sg = new SendGrid("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");
        Request request = new Request();
          request.setMethod(Method.POST);
          request.setEndpoint("mail/send");
          request.setBody(mail.build());
          Response response = sg.api(request);
          if (response.getStatusCode() >= 200 && response.getStatusCode() < 300) {
            return true;
          }
          return false;
      }
    
};