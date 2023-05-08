package com.example.springbootstudybackend.service;

import org.springframework.security.core.userdetails.UserDetailsService;

public interface AuthorizeService extends UserDetailsService {
    // 发送验证码邮件
    String sendValidateEmail(String email, String username, String sessionId);

    // 验证并注册
    String validateAndRegister(String username, String password, String email, String verificationCode, String sessionId);
}
