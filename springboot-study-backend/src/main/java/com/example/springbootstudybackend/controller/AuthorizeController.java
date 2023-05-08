package com.example.springbootstudybackend.controller;

import com.example.springbootstudybackend.entity.RestBean;
import com.example.springbootstudybackend.service.AuthorizeService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.constraints.Pattern;
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

    @Resource
    AuthorizeService service;

    /**
     * 验证邮箱，要求用户发送一个邮箱过来
     */
    @PostMapping("/validateEmail")
    public RestBean<String> validateEmail(@Pattern(regexp = EMAIL_REGEX) @RequestParam("email") String email,
                                          @RequestParam("username") String username,
                                          HttpSession session) {
        // 验证邮箱
        if (service.sendValidateEmail(email, username, session.getId())) {
            return RestBean.success("邮件已发送，请注意查收");
        } else {
            /* 一般失败有两种情况：
             * 1. 服务器出问题了
             * 2. 他填写的邮箱地址有问题
             */
            return RestBean.failure(400, "邮件发送失败，请联系管理员");
        }
    }
}
