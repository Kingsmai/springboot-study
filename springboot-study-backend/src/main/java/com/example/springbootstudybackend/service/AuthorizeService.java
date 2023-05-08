package com.example.springbootstudybackend.service;

import org.springframework.security.core.userdetails.UserDetailsService;

public interface AuthorizeService extends UserDetailsService {
    // 发送验证码邮件
    boolean sendValidateEmail(String email);
}
