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
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
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
    StringRedisTemplate stringRedisTemplate;

    BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

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
     * <h1>获取验证码</h1>
     * <h2>校验验证码流程</h2>
     * <ol>
     *     <li>先生成对应的验证码</li>
     *     <li>发送验证码到指定邮箱</li>
     *     <li>
     *         把邮箱和对应的验证码直接放到 Redis 里面</br>
     *         （过期时间 3 分钟，如果此时重新要求发邮件，那么只要剩余时间低于 2 分钟，就可以重新发一次，重复此流程）
     *     </li>
     *     <li>如果发送失败，把 Redis 里面刚刚插入的删除</li>
     *     <li>用户在注册时，再从 Redis 里面取出键值对，然后看验证码是否一致</li>
     * </ol>
     *
     * @param username   如果为 null，会通过 email 查询此用户名
     * @param hasAccount 要求判断数据库里有没有这个账户
     */
    @Override
    public String sendValidateEmail(String email, String username, String sessionId, boolean hasAccount) {
        // 加上 hasAccount，区分用户获取到的验证码
        String key = "email:" + sessionId + ":" + email + ":" + hasAccount;

        // 只要剩余时间低于 2 分钟，可以重新发送一次
        if (Boolean.TRUE.equals(stringRedisTemplate.hasKey(key))) {
            Long expire = Optional.ofNullable(stringRedisTemplate.getExpire(key, TimeUnit.SECONDS)).orElse(0L);
            if (expire > 120) {
                return "请求频繁，清稍后再试";
            }
        }

        Account accountInfo = mapper.findAccountByEmail(email);

        if (hasAccount && accountInfo == null) {
            return "此邮箱未在本系统中注册";
        }

        if (!hasAccount && accountInfo != null) {
            return "此邮箱已被注册，请使用密码登录";
        }

        // 1. 先生成对应的验证码
        Random random = new Random();
        int validationCode = random.nextInt(899999) + 100000; // 生成六位数验证码

        // 2. 发送邮件
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(from);
        message.setTo(email);
        message.setSubject("【学习平台】：您的验证码");
        // 当用户名为 null，从数据库查找这个账号的用户名
        if (username == null && accountInfo != null) {
            username = accountInfo.getUsername();
        } else if (username == null) {
            return "无法获取用户名";
        }
        message.setText("尊敬的" + username + " 您的邮箱验证码为：" + validationCode);
        try {
            mailSender.send(message);
            // 3. 发送成功后，把邮箱和对应的验证码直接放到 Redis 里面
            // 包含 sessionId，防止用户绕过 60 秒冷却时间
            // TODO：可以锁 IP 地址，但是有时候客户是校园网。又不能这么做
            // 设置键值对，过期时间 3 分钟
            stringRedisTemplate.opsForValue().set(key, String.valueOf(validationCode), 3, TimeUnit.MINUTES);
            return null;
        } catch (MailException e) {
            e.printStackTrace();
            return "邮件发送失败，请检查邮件地址是否有效";
        }
    }

    /**
     * 在 Redis 里验证验证码
     *
     * @param key              redis 的键
     * @param verificationCode 验证码
     * @return 错误信息
     */
    private String verifyVerificationCode(String key, String verificationCode) {
        if (Boolean.FALSE.equals(stringRedisTemplate.hasKey(key))) {
            return "请先请求一封验证码邮件";
        }

        String savedVerificationCode = stringRedisTemplate.opsForValue().get(key);
        if (savedVerificationCode == null) {
            return "验证码失效，请重新请求";
        }

        if (!savedVerificationCode.equals(verificationCode)) {
            return "验证码错误，请检查后再提交";
        }

        // 用掉了，清掉这行数据
        stringRedisTemplate.delete(key);
        return null;
    }

    @Override
    public String validateEmailAndRegister(String username, String password, String email, String verificationCode, String sessionId) {
        String key = "email:" + sessionId + ":" + email + ":false";
        String errorMessage = verifyVerificationCode(key, verificationCode);
        if (errorMessage != null) {
            return errorMessage;
        }
        password = encoder.encode(password);
        return (mapper.createAccount(username, password, email) > 0) ? null : "内部错误，请联系管理员";
    }

    @Override
    public String validateEmail(String email, String verificationCode, String sessionId) {
        String key = "email:" + sessionId + ":" + email + ":true";
        return verifyVerificationCode(key, verificationCode);
    }

    @Override
    public boolean resetPassword(String password, String email) {
        password = encoder.encode(password);
        return mapper.resetPasswordByEmail(password, email) > 0;
    }
}
