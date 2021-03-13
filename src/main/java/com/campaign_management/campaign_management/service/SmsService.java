package com.campaign_management.campaign_management.service;

import com.campaign_management.campaign_management.model.SMSOtpVefication;
import com.campaign_management.campaign_management.model.SmsModel;
import org.codehaus.jettison.json.JSONException;

public interface SmsService {
    String sendSms(SmsModel data) throws JSONException;
    String smsOtpVerification(SMSOtpVefication request) throws Exception;
}
