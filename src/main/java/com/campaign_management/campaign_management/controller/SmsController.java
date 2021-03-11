package com.campaign_management.campaign_management.controller;

import com.campaign_management.campaign_management.model.SMSOtpVefication;
import com.campaign_management.campaign_management.model.SmsModel;
import com.campaign_management.campaign_management.service.SmsService;
import org.codehaus.jettison.json.JSONException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/sms")
public class SmsController {

    @Autowired
    private SmsService smsService;

    @PostMapping(value = "/generateOtp", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<String> sendSms(@RequestBody SmsModel data) throws JSONException {
        return new ResponseEntity<>(smsService.sendSms(data), HttpStatus.OK);
    }

    @PostMapping(value = "/verifyPhoneNumber", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<String> smsOtpVerification(@RequestBody SMSOtpVefication data) throws Exception {
        return new ResponseEntity<>(smsService.smsOtpVerification(data), HttpStatus.OK);
    }

}
