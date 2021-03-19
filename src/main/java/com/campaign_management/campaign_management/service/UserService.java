package com.campaign_management.campaign_management.service;

import com.campaign_management.campaign_management.model.ForgotPassword;
import com.campaign_management.campaign_management.model.OtpVefication;
import com.campaign_management.campaign_management.model.SetNewPassword;
import com.campaign_management.campaign_management.model.User;

import org.codehaus.jettison.json.JSONException;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;

public interface UserService {

    ResponseEntity<User> findById(int id);

    ResponseEntity<?> addData(User user) throws Exception;

    ResponseEntity<?> updateData(User user, int id) throws JSONException;

    ResponseEntity<?> deleteData(int id) throws JSONException;

    String checkEmailVerification(String code,Model model) throws JSONException;

    ResponseEntity<?> forgotPassword(ForgotPassword data) throws Exception;

    ResponseEntity<?> otpVerification(OtpVefication data) throws Exception;

    ResponseEntity<?> changeNewPassword(SetNewPassword data) throws Exception;
}
