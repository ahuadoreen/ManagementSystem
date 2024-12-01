package com.example.ms.config;

import com.example.ms.component.JWTTokenAuthenticationFilter;
//import com.example.ms.component.MyAuthenticationEntryPoint;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
//import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
//import org.springframework.security.config.annotation.web.builders.HttpSecurity;
//import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
//import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
//import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
//import org.springframework.security.web.SecurityFilterChain;
//import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

//@EnableWebSecurity
//@Configuration
//@EnableMethodSecurity()
public class WebSecurityConfig {
//    @Autowired
//    private MyAuthenticationEntryPoint myAuthenticationEntryPoint;
    @Autowired
    private JWTTokenAuthenticationFilter jwtTokenAuthenticationFilter;

//    @Bean
//    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
//        http.csrf(AbstractHttpConfigurer::disable)//前后端分离提供接口需要关闭
//                //添加过滤器并且指定在用户密码认证过滤器前
//                .addFilterBefore(jwtTokenAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
//                .sessionManagement(AbstractHttpConfigurer::disable)//无状态 这里使用的jwt代替session
//                .authorizeHttpRequests(auth -> auth
//                        .requestMatchers(HttpMethod.POST, "/user/login").permitAll()
//                        .requestMatchers(HttpMethod.GET, "/images/**",
//                                "/swagger*/**", "/api-docs/**", "/*/api-docs/**").permitAll()
//                        .requestMatchers(HttpMethod.OPTIONS).permitAll()
//                        .anyRequest().authenticated())
//                .exceptionHandling(exception -> exception.authenticationEntryPoint(myAuthenticationEntryPoint))
//                .headers(headers -> headers.frameOptions(HeadersConfigurer.FrameOptionsConfig::disable));
//        return http.build();
//    }
}
