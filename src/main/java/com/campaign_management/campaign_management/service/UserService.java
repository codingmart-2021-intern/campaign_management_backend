package com.campaign_management.campaign_management.service;

import java.util.*;

import com.campaign_management.campaign_management.model.ForgotPassword;
import com.campaign_management.campaign_management.model.OtpVefication;
import com.campaign_management.campaign_management.model.SetNewPassword;
import com.campaign_management.campaign_management.model.User;

import org.codehaus.jettison.json.JSONException;


public interface UserService {

    User findById(int id);

    User addData(User user) throws Exception;

    User updateData(User role, int id);

    String deleteData(int id);
    
    String checkEmailVerification(String code) throws JSONException;

    String forgotPassword(ForgotPassword data) throws Exception;

    String otpVerification(OtpVefication data) throws Exception;

    String changeNewPassword(SetNewPassword data) throws Exception;

}
