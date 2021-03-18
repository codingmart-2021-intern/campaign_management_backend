package com.campaign_management.campaign_management.controller;

import com.campaign_management.campaign_management.model.SMSOtpVefication;
import com.campaign_management.campaign_management.model.SmsModel;
import com.campaign_management.campaign_management.service.SmsService;
import org.codehaus.jettison.json.JSONException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@CrossOrigin
@RestController
@RequestMapping("/api/v1/sms")
public class SmsController {

    @Autowired
    private SmsService smsService;

    @PostMapping(value = "/generateOtp")
    public ResponseEntity<?> sendSms(@RequestBody SmsModel data) throws JSONException {
        return smsService.sendSms(data);
    }

    @PostMapping(value = "/verifyPhoneNumber")
    public ResponseEntity<?> smsOtpVerification(@RequestBody SMSOtpVefication data) throws Exception {
        return smsService.smsOtpVerification(data);
    }

}
