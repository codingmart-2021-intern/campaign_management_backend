package com.campaign_management.campaign_management.controller;

import java.util.*;
import javax.servlet.http.HttpServletRequest;
import com.campaign_management.campaign_management.config.JwtTokenProvider;
import com.campaign_management.campaign_management.model.ForgotPassword;
import com.campaign_management.campaign_management.model.OtpVefication;
import com.campaign_management.campaign_management.model.SetNewPassword;
import com.campaign_management.campaign_management.model.User;
import com.campaign_management.campaign_management.repository.UserRepository;
import com.campaign_management.campaign_management.service.UserService;
import com.campaign_management.campaign_management.service.Utility;
import com.campaign_management.campaign_management.service.implemets.UserServiceImpl;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
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

    // AUTHENTICATE..

    @PostMapping(value = "/authenticate", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<String> authenticate(@RequestBody User user) throws Exception {
        log.info("UserResourceImpl : authenticate");

        User res_data = userRepository.findByEmail(user.getEmail());
        if (res_data != null) {
            if (!res_data.getEnabled()) {
                throw new Exception("Please verify the email to continue");
            }
        }

        authenticationManager
                .authenticate(new UsernamePasswordAuthenticationToken(user.getEmail(), user.getPassword()));
        JSONObject jsonObject = new JSONObject();
        try {
            String email = user.getEmail();

            User user_data = userRepository.findByEmail(email);
            jsonObject.put("name", user_data.getName());
            jsonObject.put("email", user_data.getEmail());
            jsonObject.put("phoneNumber", user_data.getPhone());
            jsonObject.put("role", user_data.getRole());
            jsonObject.put("token", tokenProvider.createToken(email, user_data.getRole()));

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return new ResponseEntity<String>(jsonObject.toString(), HttpStatus.OK);
    }

    @GetMapping
    @PreAuthorize("hasRole('ROLE_ADMIN') ")
    public ResponseEntity<List<User>> findAll() {
        return new ResponseEntity<>(userService.findAll(), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_USER')")
    public ResponseEntity<User> findById(@PathVariable int id) {
        return new ResponseEntity<>(userService.findById(id), HttpStatus.OK);
    }

    @PostMapping(value = "/signup", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<User> addData(@RequestBody User user, HttpServletRequest request) throws Exception {

        User responseData = userService.addData(user);

        String url = Utility.getSiteURL(request);
        userServiceImpl.sendVerificationEmail(user, url);

        return new ResponseEntity<>(responseData, HttpStatus.CREATED);
    }

    @PutMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @PreAuthorize("hasRole('ROLE_ADMIN') or  hasRole('ROLE_USER')")
    public ResponseEntity<User> updateData(@RequestBody User role, @PathVariable int id) {
        return new ResponseEntity<>(userService.updateData(role, id), HttpStatus.OK);
    }

    @DeleteMapping(value = "/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<String> deleteData(@PathVariable int id) {
        return new ResponseEntity<>(userService.deleteData(id), HttpStatus.OK);
    }

    // validation

    @GetMapping(value = "/validate/{token}")
    public ResponseEntity<Boolean> validate(@PathVariable String token) {
        return new ResponseEntity<>(tokenProvider.validateToken(token), HttpStatus.OK);
    }

    @GetMapping(value = "/verify")
    public ResponseEntity<String> verifyMail(@Param("code") String code) {
        return new ResponseEntity<>(userService.checkEmailVerification(code), HttpStatus.OK);
    }

    // reset forgot password

    @PostMapping(value = "/forgotpassword/generate/otp", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<String> forgotPassword(@RequestBody ForgotPassword data) throws Exception {
        return new ResponseEntity<>(userService.forgotPassword(data), HttpStatus.OK);
    }

    @PostMapping(value = "/forgotpassword/reset", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<String> resetPassword(@RequestBody OtpVefication data) throws Exception {
        return new ResponseEntity<>(userService.otpVerification(data), HttpStatus.OK);
    }

    // changing new password..

    @PostMapping(value = "/newpassword", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<String> changeNewPassword(@RequestBody SetNewPassword data) throws Exception {
        return new ResponseEntity<>(userService.changeNewPassword(data), HttpStatus.OK);
    }

    @GetMapping("/invalid")
    public String invalid() {
        log.info("executing invalid");
        return "{'message', 'SOMETHING WENT WRONG'}";
    }

}
