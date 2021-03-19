package com.campaign_management.campaign_management.controller;

import java.io.IOException;
import java.util.*;
import javax.servlet.http.HttpServletRequest;
import com.campaign_management.campaign_management.config.jwt_configure.JwtTokenProvider;
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

    // Login AUTHENTICATE..
    @PostMapping(value = "/authenticate")
    public ResponseEntity<String> authenticate(@RequestBody User user) throws Exception {
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
    public String updateData(@RequestBody List<User> users, HttpServletRequest request) throws Exception {

        users.forEach((user) -> userRepository.save(user));
        // User responseData = userRepository.save(user);

        return userServiceImpl.returnJsonString(true, "User role updated");
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
    public ResponseEntity<?> deleteData(@PathVariable int id) throws JSONException {
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

        Boolean status = MailService.sendMail(emailObj);
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
    public String invalid() {
        log.info("executing invalid");
        return "{'message', 'SOMETHING WENT WRONG'}";
    }

}
