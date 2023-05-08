package com.example.springbootstudybackend.service.impl;

import com.example.springbootstudybackend.entity.Account;
import com.example.springbootstudybackend.mapper.UserMapper;
import com.example.springbootstudybackend.service.AuthorizeService;
import jakarta.annotation.Resource;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * 用于认证用户身份的服务。
 */
@Service
public class AuthorizeServiceImpl implements AuthorizeService {
    @Resource
    UserMapper mapper;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // 非空判断
        if (username.equals("")) {
            throw new UsernameNotFoundException("用户名不能为空");
        }

        // 查询数据库
        Account account = mapper.findAccountByUsernameOrEmail(username);
        if (account == null) {
            throw new UsernameNotFoundException("用户名或密码错误");
        }

        return User
                .withUsername(account.getUsername())
                .password(account.getPassword())
                .roles("user")
                .build();
    }

    @Override
    public boolean sendValidateEmail(String email) {
        /*
         * 1. 先生成对应的验证码
         * 2. 把邮箱和对应的验证码直接放到 Redis 里面
         * （过期时间 3 分钟，如果此时重新要求发邮件，那么只要剩余时间低于 2 分钟，就可以重新发一次，重复此流程）
         * 3. 发送验证码到指定邮箱
         * 4. 如果发送失败，把 Redis 里面刚刚插入的删除
         * 5. 用户在注册时，再从 Redis 里面取出键值对，然后看验证码是否一致
         */
        return false;
    }
}