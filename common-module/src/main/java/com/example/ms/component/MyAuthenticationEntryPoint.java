package com.example.ms.component;

import com.alibaba.fastjson2.JSON;
import com.alicp.jetcache.Cache;
import com.alicp.jetcache.CacheManager;
import com.alicp.jetcache.anno.CacheType;
import com.alicp.jetcache.template.QuickConfig;
import com.example.tools.entity.ResponseData;
import com.example.tools.utils.JWTUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
//import org.springframework.security.core.AuthenticationException;
//import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.Duration;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

//@Component
//public class MyAuthenticationEntryPoint implements AuthenticationEntryPoint {
//    @Value("${token-expire}")
//    private int tokenExpire;
//    @Value("${token-refresh-expire}")
//    private int tokenRefreshExpire;
//    @Autowired
//    CacheManager cacheManager;
//    private Cache<String, Object> tokenCache;
//    @PostConstruct
//    public void initCache() {
//        QuickConfig tokenCacheConfig = QuickConfig.newBuilder("tokenCache.")
//                .expire(Duration.ofMinutes(tokenRefreshExpire * 60L))
//                .cacheType(CacheType.BOTH) // two level cache
//                .syncLocal(true) // invalidate local cache in all jvm process after update
//                .build();
//        tokenCache = cacheManager.getOrCreateCache(tokenCacheConfig);
//    }
//
//    @Override
//    public void commence(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, AuthenticationException e) throws IOException {
//        httpServletResponse.setContentType("application/json; charset=utf-8");
//        ResponseData<String> responseData = new ResponseData<>();
//        responseData.unauthorized();
//        String token = httpServletRequest.getHeader("token");
//        String username = httpServletRequest.getHeader("username");
//        String redisCache = (String) tokenCache.get(username);
//        if(token != null && token.equals(redisCache)){
//            Map<String, String> map = new HashMap<>();
//            map.put("username", username);
//            String newToken = JWTUtil.genToken(map, new Date(System.currentTimeMillis() + 60L* 1000L * tokenExpire));
//            tokenCache.put(username, newToken);
//            responseData.setCode(100);
//            responseData.setMessage("Token expired, a new token generated.");
//            responseData.setData(newToken);
//            httpServletResponse.setStatus(HttpStatus.OK.value());
//        }else{
//            httpServletResponse.setStatus(HttpStatus.UNAUTHORIZED.value());
//        }
//        httpServletResponse.getWriter().write(JSON.toJSONString(responseData));
//    }
//}
