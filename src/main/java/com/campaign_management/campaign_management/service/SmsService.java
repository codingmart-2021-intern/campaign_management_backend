package com.campaign_management.campaign_management.service;

import com.campaign_management.campaign_management.model.SMSOtpVefication;
import com.campaign_management.campaign_management.model.SmsModel;
import org.codehaus.jettison.json.JSONException;
import org.springframework.http.ResponseEntity;

public interface SmsService {
    ResponseEntity<?> sendSms(SmsModel data) throws JSONException;
    ResponseEntity<?> smsOtpVerification(SMSOtpVefication request) throws Exception;
}
