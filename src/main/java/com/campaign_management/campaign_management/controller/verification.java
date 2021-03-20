package com.campaign_management.campaign_management.controller;

import javax.servlet.http.HttpServletResponse;

import com.campaign_management.campaign_management.model.User;
import com.campaign_management.campaign_management.service.UserService;
import org.codehaus.jettison.json.JSONException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/user")
public class verification {

    @Autowired
    private UserService userService;

    // email verification
    @GetMapping(value = "/verify")
    public String verifyMail(@Param("code") String code, HttpServletResponse httpServletResponse) throws JSONException {
        User res_data = userService.checkEmailVerification(code);

        if (res_data != null) {
            httpServletResponse.setHeader("Location", "https://campaign-management-frontend.vercel.app/emailverification/" + res_data.getName());
            httpServletResponse.setStatus(302);
        }

        return "not verified";

    }
}
