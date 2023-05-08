package com.example.springbootstudybackend.service.impl;

import com.example.springbootstudybackend.entity.Account;
import com.example.springbootstudybackend.mapper.UserMapper;
import com.example.springbootstudybackend.service.AuthorizeService;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.mail.MailException;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.Random;
import java.util.concurrent.TimeUnit;

/**
 * 用于认证用户身份的服务。
 */
@Service
public class AuthorizeServiceImpl implements AuthorizeService {
    @Value("${spring.mail.username}")
    String from;

    @Resource
    UserMapper mapper;

    @Resource
    MailSender mailSender;

    @Resource
    StringRedisTemplate redisTemplate;

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

    /**
     * 1. 先生成对应的验证码
     * 2. 发送验证码到指定邮箱，
     * 3. 把邮箱和对应的验证码直接放到 Redis 里面
     * （过期时间 3 分钟，如果此时重新要求发邮件，那么只要剩余时间低于 2 分钟，就可以重新发一次，重复此流程）
     * 4. 如果发送失败，把 Redis 里面刚刚插入的删除
     * 5. 用户在注册时，再从 Redis 里面取出键值对，然后看验证码是否一致
     */
    @Override
    public boolean sendValidateEmail(String email, String username, String sessionId) {
        String key = "email:" + sessionId + ":" + email;

        // 只要剩余时间低于 2 分钟，可以重新发送一次
        if (Boolean.TRUE.equals(redisTemplate.hasKey(key))) {
            Long expire = Optional.ofNullable(redisTemplate.getExpire(key, TimeUnit.SECONDS)).orElse(0L);
            if (expire > 120) {
                return false;
            }
        }

        // 1. 先生成对应的验证码
        Random random = new Random();
        int validationCode = random.nextInt(899999) + 100000; // 生成六位数验证码
        // 2. 发送邮件
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(from);
        message.setTo(email);
        message.setSubject("【学习平台】：您的验证码");
        message.setText("尊敬的" + username + " 您的验证码是：" + validationCode);
        try {
            mailSender.send(message);
            // 3. 发送成功后，把邮箱和对应的验证码直接放到 Redis 里面
            // 包含 sessionId，防止用户绕过 60 秒冷却时间
            // TODO：可以锁 IP 地址，但是有时候客户是校园网。又不能这么做
            // 设置键值对，过期时间 3 分钟
            redisTemplate.opsForValue().set(key, String.valueOf(validationCode), 3, TimeUnit.MINUTES);
            return true;
        } catch (MailException e) {
            e.printStackTrace();
            return false;
        }
    }
}
