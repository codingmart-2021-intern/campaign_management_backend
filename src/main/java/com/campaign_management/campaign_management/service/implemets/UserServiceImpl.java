package com.campaign_management.campaign_management.service.implemets;

import java.util.Random;
import java.util.concurrent.TimeUnit;
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

    @Override
    public User findById(int id) {
        return userRepository.findById(id).orElse(null);
    }

    @Override
    public User addData(User user) throws Exception {

        User existData = userRepository.findByEmail(user.getEmail());

        if (existData != null) {
            throw new Exception("Email already exist please try with new mail");
        } else {
            String code = generateVerficationCode();
            user.setVerificationCode(code);
            user.setEnabled(false);
            user.setMbverify(false);
            user.setPassword(new BCryptPasswordEncoder().encode(user.getPassword()));
            return userRepository.save(user);
        }

    }

    @Override
    public User updateData(User user, int id) {
        User exist = userRepository.findById(id).orElse(null);
        if (exist != null) {
            exist.setImage(user.getImage());
            exist.setName(user.getName());
            exist.setPhone(user.getPhone());
            exist.setRole(user.getRole());
            exist.setDOB(user.getDOB());
            exist.setGender(user.getGender());
            exist.setPassword(exist.getPassword());

            userRepository.save(exist);
        }
        return exist;
    }

    @Override
    public String deleteData(int id) {
        JSONObject jsonObject = new JSONObject();
        try {
            userRepository.deleteById(id);
            jsonObject.put("message", "user data deleted");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return jsonObject.toString();
    }

    @Override
    public String checkEmailVerification(String code) throws JSONException {
        User res_data = userRepository.findByVerificationCode(code);
        if (res_data != null) {
            res_data.setEnabled(true);
            userRepository.save(res_data);
            return returnJsonString(true, "verified");
        }
        return returnJsonString(false, "Not verified sorry!!");
    }

    @Override
    public String forgotPassword(ForgotPassword data) throws Exception {
        String email = data.getEmail();
        User res_data = userRepository.findByEmail(email);
        if (res_data == null || !res_data.getEnabled()) {
            throw new Exception("Enter email Id not found in our database");
        }

        String otp = generateOtp();
        res_data.setOtp(otp);
        res_data.setTimestamp(System.currentTimeMillis());
        userRepository.save(res_data);
        sendForgotPasswordEmail(email, otp);

        return returnJsonString(true, "OTP send !!");
    }

    @Override
    public String otpVerification(OtpVefication data) throws Exception {
        User res_data = userRepository.findByOtp(data.getOtp());
        if (res_data == null || !res_data.getOtp().equals(data.getOtp()) || !res_data.getEnabled()) {
            throw new Exception("enter otp was wrong!!");
        }
        verifyOtpValidation(res_data.getTimestamp());

        res_data.setPassword(new BCryptPasswordEncoder().encode(data.getPassword()));

        userRepository.save(res_data);

        return returnJsonString(true, "Otp verified successfully!!");
    }

    @Override
    public String changeNewPassword(SetNewPassword data) throws Exception {
        User res_data = userRepository.findByEmail(data.getEmail());


        System.out.println();

        if (!new BCryptPasswordEncoder().matches(data.getOldPassword(), res_data.getPassword())) {
            throw new Exception("old password was wrong");
        }
        res_data.setPassword(new BCryptPasswordEncoder().encode(data.getNewPassword()));
        userRepository.save(res_data);

        return returnJsonString(true, "password changed successfully!!");
    }

    // Helper functions
    public String generateVerficationCode() {
        String code = RandomString.make(64);
        return code;
    }

    // call from controller for signup Email verification
    public void sendVerificationEmail(User user, String siteURL) throws Exception {
        String toAddress = user.getEmail();
        String fromAddress = "campaignmanagement.noreply@gmail.com";
        String senderName = "CAMPAIGN_MANAGEMENT";
        String subject = "Please verify your registration";
        String content = "Dear [[name]],<br>" + "Please click the link below to verify your registration:<br>"
                + "<h2><a href=\"[[URL]]\" target=\"_self\">VERIFY</a></h2> <br>" + "Thank you,<br>";

        content = content.replace("[[name]]", user.getName());
        String verifyURL = "https://campaign-management-sb-backend.herokuapp.com" + siteURL
                + "/api/v1/user/verify?code=" + user.getVerificationCode();
        content = content.replace("[[URL]]", verifyURL);

        JSONObject obj = new JSONObject();
        obj.put("toAddress", toAddress);
        obj.put("fromAddress", fromAddress);
        obj.put("senderName", senderName);
        obj.put("subject", subject);
        obj.put("content", content);

        sendMailer(obj);
    }

    public void sendForgotPasswordEmail(String email, String otp) throws Exception {
        String toAddress = email;
        String fromAddress = "campaignmanagement.noreply@gmail.com";
        String senderName = "CAMPAIGN_MANAGEMENT";
        String subject = "Otp for Forgot password";
        String content = "Dear user,<br>" + "Please take the below otp for change new password <br>" + otp + "<br>"
                + "Thank you";

        JSONObject obj = new JSONObject();
        obj.put("toAddress", toAddress);
        obj.put("fromAddress", fromAddress);
        obj.put("senderName", senderName);
        obj.put("subject", subject);
        obj.put("content", content);

        sendMailer(obj);
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

    public void verifyOtpValidation(Long timestamp) throws Exception {

        Long minutes = TimeUnit.MILLISECONDS.toMinutes(System.currentTimeMillis() - timestamp);
        if (minutes > 3) {
            throw new Exception("otp was expired");
        }
        return;
    }

    public String returnJsonString(boolean status, String response) throws JSONException {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("status", status);
        jsonObject.put("message", response);
        return jsonObject.toString();
    }

}
