package com.campaign_management.campaign_management.service.implemets;

import com.campaign_management.campaign_management.config.SMS.TwilioConfiguration;
import com.campaign_management.campaign_management.model.SMSOtpVefication;
import com.campaign_management.campaign_management.model.SmsModel;
import com.campaign_management.campaign_management.model.User;
import com.campaign_management.campaign_management.repository.UserRepository;
import com.campaign_management.campaign_management.service.SmsService;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.rest.api.v2010.account.MessageCreator;
import com.twilio.type.PhoneNumber;
import org.codehaus.jettison.json.JSONException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class SmsServiceImpl implements SmsService {

    @Autowired
    private TwilioConfiguration twilioConfiguration;

    @Autowired
    private UserServiceImpl userServiceImpl;

    @Autowired
    private UserRepository userRepository;

    @Override
    public ResponseEntity<?> sendSms(SmsModel smsRequest) throws JSONException {

        String otp = userServiceImpl.generateOtp();
        String phone = smsRequest.getPhoneNumber().substring(3);
        Long mobileNumber = Long.parseLong(phone);
        User response_data = userRepository.findByPhoneNumber(mobileNumber);

        String textMessage = "Hi " + response_data.getName() + "\nplease check the below OTP for verification\n" + otp
                + "\n" + "Note Otp will expires in 3 minutes";

        response_data.setOtp(otp);
        response_data.setTimestamp(System.currentTimeMillis());
        userRepository.save(response_data);

        try {
            PhoneNumber to = new PhoneNumber(smsRequest.getPhoneNumber());
            PhoneNumber from = new PhoneNumber(twilioConfiguration.getTrailNumber());
            String message = textMessage;
            MessageCreator creator = Message.creator(to, from, message);
            creator.create();
        } catch (Exception e) {
            return new ResponseEntity<>(userServiceImpl.returnJsonString(false, "please enter the valid phone number"),
                    HttpStatus.FORBIDDEN);
        }
        return new ResponseEntity<>(userServiceImpl.returnJsonString(true, "OTP send to the registered phone number"),
                HttpStatus.OK);

    }

    @Override
    public ResponseEntity<?> smsOtpVerification(SMSOtpVefication request) throws Exception {

        User res_data = userRepository.findByOtp(request.getOtp());
        if (res_data == null || !request.getOtp().equals(res_data.getOtp())) {
            return new ResponseEntity<>(userServiceImpl.returnJsonString(false, "Entered Otp was wrong!!"),
                    HttpStatus.FORBIDDEN);
        }
        userServiceImpl.verifyOtpValidation(res_data.getTimestamp());
        res_data.setMbverify(true);
        userRepository.save(res_data);
        return new ResponseEntity<>(
                userServiceImpl.returnJsonString(true, "your Mobile Number verified successfully!!"), HttpStatus.OK);
    }

}
