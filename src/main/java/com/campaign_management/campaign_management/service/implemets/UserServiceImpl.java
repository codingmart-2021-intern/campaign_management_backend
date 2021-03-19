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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import net.bytebuddy.utility.RandomString;
// import com.campaign_management.campaign_management.service.MailService;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JavaMailSender mailSender;

    // find user by d
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
    public ResponseEntity<?> checkEmailVerification(String code) throws JSONException {
        User res_data = userRepository.findByVerificationCode(code);
        if (res_data != null) {
            res_data.setEnabled(true);
            userRepository.save(res_data);
            return new ResponseEntity<>(returnJsonString(true, "verified"), HttpStatus.OK);
        }
        return new ResponseEntity<>(returnJsonString(false, "Not verified sorry!!"), HttpStatus.FORBIDDEN);
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

        return new ResponseEntity<>(returnJsonString(true, "OTP send to the Email please check spam too !!"), HttpStatus.OK);
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
        String senderName = "CAMPAIGN_MANAGEMENT";
        String subject = "Please verify your registration";
        String content = "Dear [[name]],<br>" + "Please click the link below to verify your registration:<br>"
                + "<h2><a href=\"[[URL]]\" target=\"_self\">VERIFY</a></h2> <br>" + "Thank you,<br>";

        content = content.replace("[[name]]", user.getName());
        String verifyURL = "https://campaign-management-sb-backend.herokuapp.com/rest" + "/api/v1/user/verify?code="
                + user.getVerificationCode();
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
        String senderName = "CAMPAIGN_MANAGEMENT";
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

}
