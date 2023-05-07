package com.example.springbootstudybackend.config;

import com.alibaba.fastjson2.JSON;
import com.example.springbootstudybackend.entity.RestBean;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import java.io.IOException;

@Configuration
@EnableWebSecurity
public class SecurityConfiguration {
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http
                .authorizeHttpRequests()                // 对所有 HTTP 请求进行授权认证
                .anyRequest().authenticated()           // 表示所有的请求必须被认证
                .and()
                .formLogin()                            // 启用表单登录认证方式
                .loginProcessingUrl("/api/auth/login")  // 设置登录认证的请求 URL
                .successHandler(this::onAuthenticationSuccess) // 登录成功执行的代码
                .failureHandler(this::onAuthenticationFailure) // 登录失败执行的代码
                .and()
                .logout()                               // 启用退出登录功能
                .logoutUrl("/api/auth/logout")          // 设置退出登录的请求 URL
                .and()
                .csrf().disable()                       // 禁用 CSRF 防护，因为在前后端分离的架构中，通常使用 token 来进行防护
                .exceptionHandling()                    // 配置异常处理
                .authenticationEntryPoint(this::onAuthenticationFailure) // 设置身份验证失败后的处理方式
                .and()
                .build();                               // 创建 SecurityFilterChain 对象
    }

    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        String json = JSON.toJSONString(RestBean.success("登陆成功"));
        response.getWriter().write(json);
    }

    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        String json = JSON.toJSONString(RestBean.failure(401, exception.getMessage()));
        response.getWriter().write(json);
    }
}
