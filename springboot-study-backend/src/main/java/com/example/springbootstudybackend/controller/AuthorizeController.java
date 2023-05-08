package com.example.springbootstudybackend.controller;

import com.example.springbootstudybackend.entity.RestBean;
import com.example.springbootstudybackend.service.AuthorizeService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.constraints.Pattern;
import org.hibernate.validator.constraints.Length;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 接口
 */
@Validated // 启用验证
@RestController
@RequestMapping("/api/auth")
public class AuthorizeController {

    private final String EMAIL_REGEX = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";
    private final String USERNAME_REGEX = "^[\\u4e00-\\u9fa5a-zA-Z0-9]+$";

    @Resource
    AuthorizeService service;

    /**
     * 验证邮箱，要求用户发送一个邮箱过来
     */
    @PostMapping("/getVerificationCode")
    public RestBean<String> getVerificationCode(@Pattern(regexp = EMAIL_REGEX) @RequestParam("email") String email,
                                                @RequestParam("username") String username,
                                                HttpSession session) {
        // 验证邮箱
        String errorMessage = service.sendValidateEmail(email, username, session.getId());
        if (errorMessage == null) {
            return RestBean.success("邮件已发送，请注意查收");
        } else {
            /* 一般失败有两种情况：
             * 1. 服务器出问题了
             * 2. 他填写的邮箱地址有问题
             */
            return RestBean.failure(400, errorMessage);
        }
    }

    /**
     * 用户注册
     */
    @PostMapping("/register")
    public RestBean<String> registerUser(@Pattern(regexp = USERNAME_REGEX) @Length(min = 2, max = 8)
                                         @RequestParam("username") String username,
                                         @Length(min = 8, max = 16)
                                         @RequestParam("password") String password,
                                         @Pattern(regexp = EMAIL_REGEX)
                                         @RequestParam("email") String email,
                                         @Length(min = 6, max = 6)
                                         @RequestParam("verificationCode") String verificationCode,
                                         HttpSession session) {
        String errorMessage = service.validateAndRegister(username, password, email, verificationCode, session.getId());
        if (errorMessage == null) {
            return RestBean.success("注册成功");
        } else {
            return RestBean.failure(400, errorMessage);
        }
    }
}
