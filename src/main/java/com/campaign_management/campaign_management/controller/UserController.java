package com.campaign_management.campaign_management.controller;

import java.io.IOException;
import java.util.*;
import javax.servlet.http.HttpServletRequest;
import com.campaign_management.campaign_management.config.jwt_configure.JwtTokenProvider;
import com.campaign_management.campaign_management.config.sendgrid.SendGridConfig;
import com.campaign_management.campaign_management.model.EmailModel;
import com.campaign_management.campaign_management.model.ForgotPassword;
import com.campaign_management.campaign_management.model.OtpVefication;
import com.campaign_management.campaign_management.model.SetNewPassword;
import com.campaign_management.campaign_management.model.User;
import com.campaign_management.campaign_management.repository.UserRepository;
import com.campaign_management.campaign_management.service.UserService;
import com.campaign_management.campaign_management.service.implemets.UserServiceImpl;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.campaign_management.campaign_management.service.MailService;

@CrossOrigin
@RestController
@RequestMapping("/api/v1/user")
public class UserController {

    private static final Logger log = LoggerFactory.getLogger(UserController.class);

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtTokenProvider tokenProvider;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private UserServiceImpl userServiceImpl;

	@Autowired
	private SendGridConfig sendGridConfig;

    // Login AUTHENTICATE..
    @PostMapping(value = "/authenticate")
    public ResponseEntity<String> authenticate(@RequestBody User user, HttpServletRequest request) throws Exception {
        log.info("UserResourceImpl : authenticate");

        JSONObject jsonObject = new JSONObject();

        User res_data = userRepository.findByEmail(user.getEmail());

        if (res_data == null) {
            return new ResponseEntity<String>(
                    userServiceImpl.returnJsonString(false, "your data is not found in database"),
                    HttpStatus.NOT_FOUND);
        }
        if (!res_data.getEnabled()) {
            return new ResponseEntity<String>(
                    userServiceImpl.returnJsonString(false, "please verify the email to continue"),
                    HttpStatus.NOT_ACCEPTABLE);
        }

        authenticationManager
                .authenticate(new UsernamePasswordAuthenticationToken(user.getEmail(), user.getPassword()));
        try {
            String email = user.getEmail();

            User user_data = userRepository.findByEmail(email);
            jsonObject.put("id", user_data.getId());
            jsonObject.put("image", user_data.getImage());
            jsonObject.put("name", user_data.getName());
            jsonObject.put("email", user_data.getEmail());
            jsonObject.put("phone", user_data.getPhone());
            jsonObject.put("role", user_data.getRole());
            jsonObject.put("mbverify", user_data.getMbVerify());
            jsonObject.put("dob", user_data.getDOB());
            jsonObject.put("gender", user_data.getGender());
            jsonObject.put("token", tokenProvider.createToken(email, user_data.getRole()));

            String deviceDetails = request.getHeader("User-Agent");
            userServiceImpl.securityAlert(deviceDetails, user_data);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return new ResponseEntity<String>(jsonObject.toString(), HttpStatus.OK);
    }

    // get user details by id
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_USER')")
    public ResponseEntity<?> findById(@PathVariable int id) {
        return userService.findById(id);
    }

    // signup
    @PostMapping(value = "/signup")
    public ResponseEntity<?> addData(@RequestBody User user) throws Exception {
        return userService.addData(user);
    }

    // validation (checking token expires or not)
    @GetMapping(value = "/validate/{token}")
    public ResponseEntity<?> validate(@PathVariable String token) throws JSONException {

        boolean res_data = tokenProvider.validateToken(token);
        if (res_data) {
            return new ResponseEntity<>(userServiceImpl.returnJsonString(true, "validation success"), HttpStatus.OK);
        }

        return new ResponseEntity<>(userServiceImpl.returnJsonString(false, "validation error"), HttpStatus.FORBIDDEN);
    }

   

    // reset forgot password (requesting for otp)
    @PostMapping(value = "/forgotpassword/generate/otp")
    public ResponseEntity<?> forgotPassword(@RequestBody ForgotPassword data) throws Exception {
        return userService.forgotPassword(data);
    }

    // otp verification
    @PostMapping(value = "/forgotpassword/reset")
    public ResponseEntity<?> resetPassword(@RequestBody OtpVefication data) throws Exception {
        return userService.otpVerification(data);
    }

    // changing new password..
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_USER')")
    @PostMapping(value = "/newpassword")
    public ResponseEntity<?> changeNewPassword(@RequestBody SetNewPassword data) throws Exception {
        return userService.changeNewPassword(data);
    }

    /* Role Assign */

    // Change User details By users (profile page)
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_USER')")
    @PutMapping(value = "/{id}")
    public ResponseEntity<?> updateData(@RequestBody User user, @PathVariable int id) throws JSONException {
        return userService.updateData(user, id);
    }

    // Change User role by admin
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping(value = "/admin")
    public ResponseEntity<String> updateData(@RequestBody List<User> users, HttpServletRequest request)
            throws Exception {

        users.forEach((user) -> {
            User userDb = userRepository.findByEmail(user.getEmail());

            if (!userDb.role.equals(user.role)) {
                // Notify User via Email
                String toAddress = user.getEmail();
                String fromAddress = "campaignmanagement.noreply@gmail.com";
                String senderName = "Campaign Management";
                String subject = "Your Role has been changed";
                String content = "<!DOCTYPE html> <html lang='en'> <head> <meta charset='UTF-8'> <meta http-equiv='X-UA-Compatible' content='IE=edge'> <meta name='viewport' content='width=device-width, initial-scale=1.0'> <title>Account Deleted</title> <style> * { margin: auto; padding: 0; box-sizing: border-box; font-family: Arial, Helvetica, sans-serif; } a { text-decoration: none; } li { list-style: none; } .main_container { background-color: #292c3d; padding-bottom: 5rem; text-align: center; } .container { width: 80%; margin: auto; } .logo { width: 3rem; margin: 1rem auto; } .white_box { width: 90%; min-height: 5rem; background-color: #fff; border-top-left-radius: 1rem; border-top-right-radius: 1rem; margin: auto; } .img_fluid { width: 100%; border-radius: 1rem; } .title_container { background-color: rgb(219, 3, 75); padding-top: 2rem; } .content_container { width: 90%; margin: auto; background-color: #fff; border-bottom-left-radius: 1rem; border-bottom-right-radius: 1rem; padding: 0 2rem; padding-bottom: 2rem; } .btn { padding: 1rem; color: #fff; background-color: #1E39D1; font-size: 1rem; border-radius: 0.5rem; width: 6rem; text-align: center; } .text_container { padding: 1rem 0rem; display: grid; } .title_Wrapper { width: 90%; margin: auto; } h1,h3,h4,h5 { margin: 1rem 0; } .role { color: #1E39D1; font-weight: 600; font-size: 1.5rem; } </style> </head> <body> <div class='main_container'> <div class='title_container'> <div class='title_wrapper'> <h1 align='center' style='color:#fff'>Campaign Management</h1> <div class='logo'> <img class='img_fluid' src='https://campaign-management-frontend.vercel.app/assets/images/logo1.png' alt='campaign_management'> </div> <div class='white_box'> </div> </div> </div> <div class='content_container'> <h2>Hi " + user.name + " !</h2> <div class='text_container'> <h3>Your role has been changed by Admin</h3> <h4>Current Role: <span class='role'>" + user.role.toUpperCase() + " Creation</span></h4> </div> <a href='https://campaign-management-frontend.vercel.app'> <div class='btn'> Visit site </div> </a> </div> </div> </body> </html>";
                JSONObject obj = new JSONObject();
                try {
                    obj.put("fromAddress", fromAddress);
                    obj.put("toAddress", toAddress);
                    obj.put("senderName", senderName);
                    obj.put("subject", subject);
                    obj.put("content", content);
                    userServiceImpl.sendMailer(obj);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            userRepository.save(user);
        });
        return new ResponseEntity<String>(userServiceImpl.returnJsonString(true, "User Role Updated"), HttpStatus.OK);
    }

    // get only user not admin details
    @GetMapping("/admin")
    @PreAuthorize("hasRole('ROLE_ADMIN') ")
    public List<User> getAllUsers() {
        return userRepository.findAllUsers();
    }

    // deleting user details by admin
    @DeleteMapping(value = "/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<String> deleteData(@PathVariable int id) throws JSONException {
        User user = userRepository.findByUserId(id);
        
        // Send Email to deleted User
        String toAddress = user.getEmail();
        String fromAddress = "campaignmanagement.noreply@gmail.com";
        String senderName = "Campaign Management";
        String subject = "Account has been deleted";
        String content = "<!DOCTYPE html> <html lang='en'> <head> <meta charset='UTF-8'> <meta http-equiv='X-UA-Compatible' content='IE=edge'> <meta name='viewport' content='width=device-width, initial-scale=1.0'> <title>Account Deleted</title> <style> * { margin: auto; padding: 0; box-sizing: border-box; } a { text-decoration: none; } li { list-style: none; } .main_container { background-color: #292c3d; padding-bottom: 5rem; text-align: center; } .container { width: 80%; margin: auto; } .logo { width: 3rem; margin: 1rem auto; } .white_box { width: 90%; min-height: 5rem; background-color: #fff; border-top-left-radius: 1rem; border-top-right-radius: 1rem; margin: auto; } .img_fluid { width: 100%; border-radius: 1rem; } .title_container { background-color: rgb(210, 2, 72); padding-top: 2rem; } .content_container { width: 90%; margin: auto; background-color: #fff; border-bottom-left-radius: 1rem; border-bottom-right-radius: 1rem; padding: 0 2rem; padding-bottom: 2rem; } .btn { padding: 1rem; color: #fff; background-color: #1E39D1; font-size: 1rem; border-radius: 0.5rem; width: 6rem; text-align: center; } .text_container { padding: 1rem 0rem; text-align: start; margin: auto; } .title_Wrapper { width: 90%; margin: auto; } </style> </head> <body> <div class='main_container'> <div class='title_container'> <div class='title_wrapper'> <h1 align='center'>Campaign Management</h1> <div class='logo'> <img class='img_fluid' src='https://campaign-management-frontend.vercel.app/assets/images/logo1.png' alt='campaign_management'> </div> <div class='white_box'> </div> </div> </div> <div class='content_container'> <h2>Hi " + user.name + " !</h2> <div class='text_container'> <h2>Your account has been deleted by Admin</h2> <p>Contact admin for any queries...</p> </div> <a href='https://campaign-management-frontend.vercel.app'> <div class='btn'> Visit site </div> </a> </div> </div> </body> </html>";
        JSONObject obj = new JSONObject();
        try {
            obj.put("fromAddress", fromAddress);
            obj.put("toAddress", toAddress);
            obj.put("senderName", senderName);
            obj.put("subject", subject);
            obj.put("content", content);
            userServiceImpl.sendMailer(obj);
        } catch ( Exception e) {
            e.printStackTrace();
        }
        return userService.deleteData(id);
    }

    /* SEND EMAIL bt sendgrid */
    @PostMapping("/send-mail")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<String> sendNewMail(@RequestBody EmailModel mailData) throws IOException, JSONException {

        JSONObject emailObj = new JSONObject();

        emailObj.put("toAddress", mailData.getToAddress());
        emailObj.put("subject", mailData.getSubject());
        emailObj.put("content", mailData.getContent());
        emailObj.put("senderName", mailData.getSenderName());

        Boolean status = MailService.sendMail(emailObj,sendGridConfig.getSendGridAPIKey());
        
        if (status == true) {
            return new ResponseEntity<>(userServiceImpl.returnJsonString(true, "Email Sent Sucessfully"),
                    HttpStatus.OK);
        } else {
            return new ResponseEntity<>(userServiceImpl.returnJsonString(false, "Unable to send email to this address"),
                    HttpStatus.NOT_ACCEPTABLE);
        }
    }

    // invalid exception
    @GetMapping("/invalid")
    public String invalid() throws JSONException {
        log.info("executing invalid");
        return userServiceImpl.returnJsonString(false, "Somthing went wrong");
    }

}
