package com.example.springbootstudybackend.controller;

import com.example.springbootstudybackend.entity.RestBean;
import com.example.springbootstudybackend.entity.UserAccount;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.SessionAttribute;

@RestController
@RequestMapping("/api/user")
public class UserController {
    @GetMapping("/me")
    public RestBean<UserAccount> me(@SessionAttribute("account") UserAccount user) {
        return RestBean.success(user);
    }
}
