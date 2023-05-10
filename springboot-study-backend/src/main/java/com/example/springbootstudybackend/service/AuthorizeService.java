package com.example.springbootstudybackend.service;

import org.springframework.security.core.userdetails.UserDetailsService;

public interface AuthorizeService extends UserDetailsService {
    // 发送验证码邮件
    String sendValidateEmail(String email, String username, String sessionId, boolean hasAccount);

    // 验证并注册
    String validateEmailAndRegister(String username, String password, String email, String verificationCode, String sessionId);

    // 验证邮箱
    String validateEmail(String email, String verificationCode, String sessionId);

    // 重置密码
    boolean resetPassword(String password, String email);
}
