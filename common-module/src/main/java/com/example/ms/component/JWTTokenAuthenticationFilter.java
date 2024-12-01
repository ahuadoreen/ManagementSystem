package com.example.ms.component;

import com.alibaba.fastjson2.JSONObject;
import com.example.tools.utils.Constant;
import com.example.tools.component.IdentityUtils;
import com.example.tools.utils.JWTUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
//import org.springframework.security.core.authority.SimpleGrantedAuthority;
//import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

//@Component
public class JWTTokenAuthenticationFilter extends OncePerRequestFilter { // OncePerRequestFilter表示只走一次这个过滤器
    @Autowired
    IdentityUtils identityUtils;

    @SneakyThrows
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        if (request.getMethod().equals("OPTIONS")) {
            response.setStatus(HttpServletResponse.SC_OK);
            filterChain.doFilter(request, response);
            return;
        }
        response.setCharacterEncoding("utf-8");
        String token = request.getHeader("token");
        //token不存在
        if (null != token) {
            Map<String, String> login = null;
            try {
                login = JWTUtil.verifyToken(token);
            } catch (RuntimeException e) {
                System.out.println(e);
            }
            String username = request.getHeader("username");
            //解密token后的username与用户传来的username不一致，一般都是token过期
            if (null != username && null != login) {
                if (username.equals(login.get("username"))) {
                    JSONObject user = identityUtils.getCurrentUser(username);
                    // 缓存丢失的话就直接判定认证失败，让用户重新登录
                    if (user != null) {
                        List<String> permissions = new ArrayList<>();
                        if (identityUtils.isUserSuperAdminRole(user)) {
                            permissions.add(Constant.ROLE_SUPER_ADMIN);
                        }
                        List<String> authList = identityUtils.getCurrentAuthList(user);
                        if (authList != null) {
                            permissions = authList;
                        }
//                        Collection<SimpleGrantedAuthority> authorities = permissions.stream().map(SimpleGrantedAuthority::new).collect(Collectors.toList());
//                        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(username, null, authorities);
//                        SecurityContextHolder.getContext().setAuthentication(authenticationToken);
                    }
                }
            }
        }
        filterChain.doFilter(request, response);
    }
}
