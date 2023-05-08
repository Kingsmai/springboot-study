package com.example.springbootstudybackend.config;

import com.alibaba.fastjson2.JSON;
import com.example.springbootstudybackend.entity.RestBean;
import com.example.springbootstudybackend.service.AuthorizeService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.rememberme.JdbcTokenRepositoryImpl;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import javax.sql.DataSource;
import java.io.IOException;

@Configuration
@EnableWebSecurity
public class SecurityConfiguration {
    @Resource
    AuthorizeService authorizeService;

    @Resource
    DataSource dataSource;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http,
                                           PersistentTokenRepository repository) throws Exception {
        return http
                .authorizeHttpRequests()                // 对所有 HTTP 请求进行授权认证
                .requestMatchers("/api/auth/**").permitAll() // 放行验证 URL
                .anyRequest().authenticated()           // 表示所有的请求必须被认证
                .and()
                .formLogin()                            // 启用表单登录认证方式
                .loginProcessingUrl("/api/auth/login")  // 设置登录认证的请求 URL
                .successHandler(this::onAuthenticationSuccess) // 登录成功执行的代码
                .failureHandler(this::onAuthenticationFailure) // 登录失败执行的代码
                .and()
                .logout()                               // 启用退出登录功能
                .logoutUrl("/api/auth/logout")          // 设置退出登录的请求 URL
                .logoutSuccessHandler(this::onAuthenticationSuccess) // 登出成功执行代码
                .and()
                .rememberMe()                           // 设置记住我
                .rememberMeParameter("remember")        // 更改记住我参数
                .tokenRepository(repository)            // 设置 token 仓库
                .tokenValiditySeconds(60 * 60 * 24 * 7) // 七天内免登录
                .and()
                .csrf().disable()                       // 禁用 CSRF 防护，因为在前后端分离的架构中，通常使用 token 来进行防护
                .cors()                                 // 设置跨域
                .configurationSource(this.corsConfigurationSource()) // 配置跨域
                .and()
                .exceptionHandling()                    // 配置异常处理
                .authenticationEntryPoint(this::onAuthenticationFailure) // 设置身份验证失败后的处理方式
                .and()
                .build();                               // 创建 SecurityFilterChain 对象
    }

    @Bean
    // 返回持久化 Token 仓库
    public PersistentTokenRepository tokenRepository() {
        JdbcTokenRepositoryImpl jdbcTokenRepository = new JdbcTokenRepositoryImpl();
        // 设定数据库数据源
        jdbcTokenRepository.setDataSource(dataSource);
        // 首次在一个新的数据库中运行时，创建 token 表，创建好之后，改为 false
        jdbcTokenRepository.setCreateTableOnStartup(false);
        return jdbcTokenRepository;
    }

    // 配置 CORS
    private CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration cors = new CorsConfiguration();
        // 允许所有的跨域请求。【上线的时候，需要具体到某一个】
        cors.addAllowedOriginPattern("*");
        cors.setAllowCredentials(true);
        cors.addAllowedHeader("*");
        cors.addAllowedMethod("*");
        cors.addExposedHeader("*");
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", cors);
        return source;
    }

    @Bean
    public AuthenticationManager authenticationManager(HttpSecurity security) throws Exception {
        return security.getSharedObject(AuthenticationManagerBuilder.class)
                .userDetailsService(authorizeService)
                .and().build();
    }

    // 定义密码编码器
    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        // 因为和登录共用，所以要判断是哪个操作
        String json = "";
        if (request.getRequestURI().contains("/login")) {
            json = JSON.toJSONString(RestBean.success("登陆成功"));
        } else if (request.getRequestURI().contains("/logout")) {
            json = JSON.toJSONString(RestBean.success("退出登录成功"));
        }
        response.getWriter().write(json);
    }

    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        String json = JSON.toJSONString(RestBean.failure(401, exception.getMessage()));
        response.getWriter().write(json);
    }
}
