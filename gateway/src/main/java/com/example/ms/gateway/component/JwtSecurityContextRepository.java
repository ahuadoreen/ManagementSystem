package com.example.ms.gateway.component;

import com.alibaba.fastjson2.JSONObject;
import com.example.tools.component.IdentityService;
import com.example.tools.component.IdentityUtils;
import com.example.tools.utils.Constant;
import com.example.tools.utils.JWTUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.security.web.server.context.ServerSecurityContextRepository;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Component
@ComponentScan({"com.example.tools.component"})
public class JwtSecurityContextRepository implements ServerSecurityContextRepository {
    @Autowired
    IdentityUtils identityUtils;

    @Autowired
    IdentityService identityService;

    @Override
    public Mono<Void> save(ServerWebExchange exchange, SecurityContext context) {
        return Mono.empty();
    }

    @Override
    public Mono<SecurityContext> load(ServerWebExchange exchange) {
        ServerHttpRequest request = exchange.getRequest();
        request.mutate().header("startTime", Instant.now().toString()).build();
        String token = request.getHeaders().getFirst(Constant.SECURITY_HEADER_TOKEN);
        String username = request.getHeaders().getFirst(Constant.SECURITY_HEADER_USERNAME);
        boolean isAuthenticated = false;
        //token不存在
        if (null != token) {
            Map<String, String> login = null;
            try {
                login = JWTUtil.verifyToken(token);
            } catch (RuntimeException e) {
                System.out.println(token);
                System.out.println(e);
            }
            //解密token后的username与用户传来的username不一致，一般都是token过期
            if (null != username && null != login) {
                if (username.equals(login.get("username"))) {
                    isAuthenticated = true;
                }
            }
        }
        Authentication newAuthentication = new UsernamePasswordAuthenticationToken(username, username);
        boolean finalIsAuthenticated = isAuthenticated;
        return Mono.fromCallable(() -> {
            if (!finalIsAuthenticated) {
                return newAuthentication;
            }
            List<String> permissions = new ArrayList<>();
            JSONObject user = identityUtils.getCurrentUser(username);
            // 缓存丢失的话就直接判定认证失败，让用户重新登录
            if (user == null) {
                user = identityService.getCurrentUser(username);
            }
            if (identityUtils.isUserSuperAdminRole(user)) {
                permissions.add(Constant.ROLE_SUPER_ADMIN);
            }
            List<String> authList = identityUtils.getCurrentAuthList(user);
            if (authList != null) {
                permissions = authList;
            }
            Collection<SimpleGrantedAuthority> authorities = permissions.stream().map(SimpleGrantedAuthority::new).collect(Collectors.toList());
            return new UsernamePasswordAuthenticationToken(username, null, authorities);
        }).map(SecurityContextImpl::new);
    }
}