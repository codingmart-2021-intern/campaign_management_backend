package com.campaign_management.campaign_management.service.implemets;

import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

import javax.mail.internet.MimeMessage;
import com.campaign_management.campaign_management.model.ForgotPassword;
import com.campaign_management.campaign_management.model.OtpVefication;
import com.campaign_management.campaign_management.model.SetNewPassword;
import com.campaign_management.campaign_management.model.User;
import com.campaign_management.campaign_management.repository.UserRepository;
import com.campaign_management.campaign_management.service.UserService;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import net.bytebuddy.utility.RandomString;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JavaMailSender mailSender;

    // find user by id
    @Override
    public ResponseEntity<User> findById(int id) {
        User res_data = userRepository.findById(id).get();
        if (res_data != null) {
            return new ResponseEntity<>(res_data, HttpStatus.OK);
        }
        return new ResponseEntity<>((User) res_data, HttpStatus.NOT_FOUND);
    }

    // signup
    @Override
    public ResponseEntity<?> addData(User user) throws Exception {

        User existData = userRepository.findByEmail(user.getEmail());

        if (existData != null) {
            return new ResponseEntity<>(returnJsonString(false, "Email already exist please try with new mail"),
                    HttpStatus.FORBIDDEN);
        } else {
            String code = generateVerficationCode();
            user.setVerificationCode(code);
            user.setEnabled(false);
            user.setMbverify(false);
            user.setPassword(new BCryptPasswordEncoder().encode(user.getPassword()));
            sendVerificationEmail(user);
            userRepository.save(user);
            return new ResponseEntity<>(returnJsonString(true, "SignIn success please check the mail for verification"),
                    HttpStatus.CREATED);
        }

    }

    // update user profile

    @Override
    public ResponseEntity<?> updateData(User user, int id) throws JSONException {
        User exist = userRepository.findById(id).orElse(null);
        if (exist != null) {
            exist.setImage(user.getImage());
            exist.setName(user.getName());
            exist.setPhone(user.getPhone());
            exist.setRole(user.getRole());
            exist.setDOB(user.getDOB());
            exist.setGender(user.getGender());
            exist.setPassword(exist.getPassword());
            return new ResponseEntity<>(userRepository.save(exist), HttpStatus.OK);

        }
        return new ResponseEntity<>(returnJsonString(false, "user Not found"), HttpStatus.OK);
    }

    // delete the data
    @Override
    public ResponseEntity<String> deleteData(int id) throws JSONException {
        try {
            userRepository.deleteById(id);
            return new ResponseEntity<String>(returnJsonString(true, "User deleted successfully"), HttpStatus.OK);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return new ResponseEntity<String>(returnJsonString(false, "User deletion failed"), HttpStatus.OK);
    }

    // check email verification
    @Override
    public User checkEmailVerification(String code) throws JSONException {
        User res_data = userRepository.findByVerificationCode(code);

        if (res_data != null) {
            res_data.setEnabled(true);
            userRepository.save(res_data);
            return res_data;
        }
        return null;
    }

    // forgot password
    @Override
    public ResponseEntity<?> forgotPassword(ForgotPassword data) throws Exception {
        String email = data.getEmail();
        User res_data = userRepository.findByEmail(email);
        if (res_data == null) {
            return new ResponseEntity<>(returnJsonString(false, "Enter email Id not found in our database"),
                    HttpStatus.FORBIDDEN);

        }
        if (!res_data.getEnabled()) {
            return new ResponseEntity<>(returnJsonString(false, "Please Verify the email to change password!!"),
                    HttpStatus.FORBIDDEN);
        }

        String otp = generateOtp();
        res_data.setOtp(otp);
        res_data.setTimestamp(System.currentTimeMillis());
        userRepository.save(res_data);
        sendForgotPasswordEmail(email, otp);

        return new ResponseEntity<>(returnJsonString(true, "OTP send to the Email please check spam too !!"),
                HttpStatus.OK);
    }

    // otp verification
    @Override
    public ResponseEntity<?> otpVerification(OtpVefication data) throws Exception {
        User res_data = userRepository.findByOtp(data.getOtp());
        if (res_data == null || !res_data.getOtp().equals(data.getOtp()) || !res_data.getEnabled()) {
            return new ResponseEntity<>(returnJsonString(false, "Entered otp was wrong!!"), HttpStatus.FORBIDDEN);

        }
        if (!verifyOtpValidation(res_data.getTimestamp())) {
            return new ResponseEntity<>(returnJsonString(false, "Otp was expired!!"), HttpStatus.FORBIDDEN);
        }

        res_data.setPassword(new BCryptPasswordEncoder().encode(data.getPassword()));

        userRepository.save(res_data);

        return new ResponseEntity<>(returnJsonString(true, "Password Changed successfully!!"), HttpStatus.OK);
    }

    // change password
    @Override
    public ResponseEntity<?> changeNewPassword(SetNewPassword data) throws Exception {
        User res_data = userRepository.findByEmail(data.getEmail());

        if (!new BCryptPasswordEncoder().matches(data.getOldPassword(), res_data.getPassword())) {
            return new ResponseEntity<>(returnJsonString(false, "old password was wrong"), HttpStatus.FORBIDDEN);

        }
        res_data.setPassword(new BCryptPasswordEncoder().encode(data.getNewPassword()));
        userRepository.save(res_data);

        return new ResponseEntity<>(returnJsonString(true, "password changed successfully!!"), HttpStatus.OK);
    }

    // -----------------------------------------------------

    // Helper functions
    public String generateVerficationCode() {
        String code = RandomString.make(64);
        return code;
    }

    // call from controller for signup Email verification
    public void sendVerificationEmail(User user) throws Exception {
        String toAddress = user.getEmail();
        String fromAddress = "campaignmanagement.noreply@gmail.com";
        String senderName = "Campaign Management";
        String subject = "Please verify your registration";
        String content = "<div style='padding: 10px 20px;'>"
                + "<img src='https://firebasestorage.googleapis.com/v0/b/react-spring-boot-user-profile.appspot.com/o/images%2Flogo1.ico?alt=media&token=511d4ae0-a523-483d-9943-ba4cd9a6227c'  width='25' height='25' alt='logo'> <span style='font-style:15px'>Campaign Management</span>  <br>"
                + "<h1 style='text-transform: capitalize;'>verify your email</h1>"
                + "<p style='color: #676461;margin: 25px 0;'>Hi [[name]] ! Use the link below to verify your email and start enjoying Campaign Management </p>"
                + "<a style='text-decoration: none; background-color: #1B98F5; color: #fff; padding: 12px 22px;border-radius: 5px; font-weight: 750; letter-spacing: 0.1rem;' href=\"[[URL]]\" target=\"_self\">Verify email</a>"
                + "<p style='font-size: 15px; margin-top: 25px;'>Questions ? Email us at <span style='color: #1B98F5;font-style: italic;'>campaignmanagement.noreply@gmail.com</span></p>"
                + "</div>" + "Thank you,<br>";

        content = content.replace("[[name]]", user.getName());
        String verifyURL = "https://campaign-management-sb-backend.herokuapp.com/rest" + "/api/v1/user/verify?code="
                + user.getVerificationCode();

        // String verifyURL = "http://localhost:3001/rest/api/v1/user/verify?code=" +
        // user.getVerificationCode();

        content = content.replace("[[URL]]", verifyURL);

        JSONObject obj = new JSONObject();
        obj.put("fromAddress", fromAddress);
        obj.put("toAddress", toAddress);
        obj.put("senderName", senderName);
        obj.put("subject", subject);
        obj.put("content", content);

        sendMailer(obj);

        // send grid
        // MailService.sendMail(obj);
    }

    public void sendForgotPasswordEmail(String email, String otp) throws Exception {
        String toAddress = email;
        String fromAddress = "campaignmanagement.noreply@gmail.com";
        String senderName = "Campaign Management";
        String subject = "Otp for Forgot password";
        String content = "Dear user,<br>" + "Please take the below otp for change new password <br>" + otp + "<br>"
                + "Thank you";

        JSONObject obj = new JSONObject();
        obj.put("fromAddress", fromAddress);
        obj.put("toAddress", toAddress);
        obj.put("senderName", senderName);
        obj.put("subject", subject);
        obj.put("content", content);

        sendMailer(obj);

        // send grid
        // MailService.sendMail(obj);
    }

    public String generateOtp() {
        Random r = new Random(System.currentTimeMillis());
        return Integer.toString(10000 + r.nextInt(20000));
    }

    public void sendMailer(JSONObject data) throws Exception {

        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message);
        helper.setFrom(data.get("fromAddress").toString(), data.get("senderName").toString());
        helper.setTo(data.get("toAddress").toString());
        helper.setSubject(data.get("subject").toString());
        helper.setText(data.get("content").toString(), true);
        mailSender.send(message);

    }

    public boolean verifyOtpValidation(Long timestamp) throws Exception {

        Long minutes = TimeUnit.MILLISECONDS.toMinutes(System.currentTimeMillis() - timestamp);
        if (minutes > 3) {
            return false;
        }
        return true;
    }

    public String returnJsonString(boolean status, String response) throws JSONException {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("status", status);
        jsonObject.put("message", response);
        return jsonObject.toString();
    }

    public String securityAlert(String deviceDetails, User user) throws JSONException {

        String deviceName = "";

        if (deviceDetails != null) {
            try {
                int i1 = deviceDetails.indexOf("(");
                int i2 = deviceDetails.indexOf(")");
                deviceName = deviceDetails.substring(i1, i2 + 1).split(";")[1].replace(")", "").trim().split(" ")[0];
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
        String toAddress = user.getEmail();
        String fromAddress = "campaignmanagement.noreply@gmail.com";
        String senderName = "Campaign Management";
        String subject = "Security Alert!";
        String content = "<!DOCTYPE html> <html lang='en'> <head> <meta charset='UTF-8'> <meta http-equiv='X-UA-Compatible' content='IE=edge'> <meta name='viewport' content='width=device-width, initial-scale=1.0'> <title>Security Alert</title> <style> * { margin: auto; padding: 0; box-sizing: border-box; font-family: Arial, Helvetica, sans-serif; } a { text-decoration: none; } li { list-style: none; } .main_container { background-color: #292c3d; padding-bottom: 5rem; text-align: center; } .container { width: 80%; margin: auto; } .logo { width: 3rem; margin: 1rem auto; } .white_box { width: 90%; min-height: 5rem; background-color: #fff; border-top-left-radius: 1rem; border-top-right-radius: 1rem; margin: auto; } .img_fluid { width: 100%; border-radius: 1rem; } .title_container { background-color: rgb(219, 3, 75); padding-top: 2rem; } .content_container { width: 90%; margin: auto; background-color: #fff; border-bottom-left-radius: 1rem; border-bottom-right-radius: 1rem; padding: 0 2rem; padding-bottom: 2rem; } .btn { padding: 1rem; color: #fff; background-color: #1E39D1; font-size: 1rem; border-radius: 0.5rem; width: 10rem; text-align: center; } .text_container { padding: 1rem 0rem; display: grid; text-align: start; color: #202020; } .title_Wrapper { width: 90%; margin: auto; } h1,h2,h3,h4,h5 { margin: 1rem 0; } .role { color: #1E39D1; font-weight: 600; font-size: 1.5rem; } p { margin: 1rem 0; } </style> </head> <body> <div class='main_container'> <div class='title_container'> <div class='title_wrapper'> <h1 align='center' style='color:#fff'>Campaign Management</h1> <div class='logo'> <img class='img_fluid' src='https://campaign-management-frontend.vercel.app/assets/images/logo1.png' alt='campaign_management'> </div> <div class='white_box'> </div> </div> </div> <div class='content_container'> <span style='font-size: 2rem;font-weight:700'>New Device signed in to</span style='font-size: large;'> <h3 style='color: #3b3b3c;'>"
                + toAddress + "</h3> <div class='text_container'> <h4>Hi " + user.name
                + ",</h4> <p style='padding-left: 1rem;'>Your Campaign Management Account was just signed in to from a new "
                + deviceName
                + " device. You're getting this email to make sure that it was you.</p> <p style='padding-left: 1rem;'>If it is not you. Kindly Reset your password.</p> <h4>Device Info:</h4> <p style='padding-left: 1rem;'>"
                + deviceDetails
                + "</p> </div> <a href='https://campaign-management-frontend.vercel.app/forgot-password'> <div class='btn'> Reset Password </div> </a> </div> </div> </body> </html>";
        JSONObject obj = new JSONObject();
        try {
            obj.put("fromAddress", fromAddress);
            obj.put("toAddress", toAddress);
            obj.put("senderName", senderName);
            obj.put("subject", subject);
            obj.put("content", content);
            sendMailer(obj);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return returnJsonString(true, "Email sent ! ");
    }

}
