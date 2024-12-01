package com.example.ms.gateway.config;

import com.example.ms.gateway.component.JwtSecurityContextRepository;
import com.example.ms.gateway.component.MyAuthenticationEntryPoint;
import com.example.ms.gateway.component.MyReactiveAuthorizationManager;
import com.example.ms.gateway.component.MyServerAccessDeniedHandler;
import com.example.tools.utils.Constant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;

@EnableWebFluxSecurity
@EnableReactiveMethodSecurity
@Configuration
public class WebFluxSecurityConfig {
    @Autowired
    private MyAuthenticationEntryPoint myAuthenticationEntryPoint;
    @Autowired
    JwtSecurityContextRepository jwtSecurityContextRepository;
    @Autowired
    private MyServerAccessDeniedHandler myServerAccessDeniedHandler;

    @Bean
    public SecurityWebFilterChain manageSecurityFilterChain(ServerHttpSecurity http) {
        http.csrf(ServerHttpSecurity.CsrfSpec::disable)//前后端分离提供接口需要关闭
                .securityContextRepository(jwtSecurityContextRepository)
                .authorizeExchange( auth -> auth
                        .pathMatchers("/*/user/login", "/*/language/getLangList").permitAll()
                        .pathMatchers(HttpMethod.GET, "/images/**",
                                "/swagger*/**", "/api-docs/**", "/*/api-docs/**", "/*/*/api-docs/**").permitAll()
//                        .pathMatchers("/*/user/getCurrentUserInfo", "/*/menu/getCurrentUserMenus", "/*/dictionary/getDictionariesByKeys").authenticated()
                        .pathMatchers(HttpMethod.POST, "/*/*/search", "/*/*/add", "/*/*/update", "/*/*/delete", "/*/*/export", "/*/*/import").access(new MyReactiveAuthorizationManager(null))
                        .pathMatchers(HttpMethod.POST, "/*/*/downloadTemplate").access(MyReactiveAuthorizationManager.hasAuthority("import"))
                        .pathMatchers(HttpMethod.POST, "/*/*/deleteBatch").access(MyReactiveAuthorizationManager.hasAuthority("delete"))
                        .pathMatchers(HttpMethod.POST, "/*/*/saveRoleAuth").access(MyReactiveAuthorizationManager.hasAuthority("update"))
                        .anyExchange().authenticated())
                .exceptionHandling(exception -> {
                    exception.authenticationEntryPoint(myAuthenticationEntryPoint); // 认证失败
                    exception.accessDeniedHandler(myServerAccessDeniedHandler); // 权限不足
                })
                .headers(headers -> headers.frameOptions(ServerHttpSecurity.HeaderSpec.FrameOptionsSpec::disable));
        return http.build();
    }
}