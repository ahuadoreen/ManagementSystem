package com.example.ms.gateway.component;

import com.alibaba.fastjson2.JSON;
import com.alicp.jetcache.Cache;
import com.alicp.jetcache.CacheManager;
import com.alicp.jetcache.anno.CacheType;
import com.alicp.jetcache.template.QuickConfig;
import com.example.tools.component.LogUtil;
import com.example.tools.entity.ResponseData;
import com.example.tools.utils.Constant;
import com.example.tools.utils.JWTUtil;
import jakarta.annotation.PostConstruct;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.server.ServerAuthenticationEntryPoint;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
public class MyAuthenticationEntryPoint implements ServerAuthenticationEntryPoint {
    @Value("${token-expire}")
    private int tokenExpire;
    @Value("${token-refresh-expire}")
    private int tokenRefreshExpire;
    @Autowired
    CacheManager cacheManager;
    private Cache<String, Object> tokenCache;
    @Autowired
    LogUtil logUtil;
    @PostConstruct
    public void initCache() {
        QuickConfig tokenCacheConfig = QuickConfig.newBuilder("tokenCache.")
                .expire(Duration.ofMinutes(tokenRefreshExpire * 60L))
                .cacheType(CacheType.REMOTE) // two level cache
                .syncLocal(false) // invalidate local cache in all jvm process after update
                .build();
        tokenCache = cacheManager.getOrCreateCache(tokenCacheConfig);
    }

    @SneakyThrows
    @Override
    public Mono<Void> commence(ServerWebExchange exchange, AuthenticationException ex) {
        ServerHttpRequest request = exchange.getRequest();
        ServerHttpResponse response = exchange.getResponse();
        response.getHeaders().setContentType(MediaType.APPLICATION_JSON);
        ResponseData<String> responseData = new ResponseData<>();
        HttpStatusCode statusCode;
        statusCode = HttpStatus.UNAUTHORIZED;
        responseData.unauthorized();
        String token = request.getHeaders().getFirst(Constant.SECURITY_HEADER_TOKEN);
        String username = request.getHeaders().getFirst(Constant.SECURITY_HEADER_USERNAME);
        String redisCache = (String) tokenCache.get(username);
        if(token != null && token.equals(redisCache)){
            statusCode = HttpStatus.OK;
            Map<String, String> map = new HashMap<>();
            map.put("username", username);
            String newToken = JWTUtil.genToken(map, new Date(System.currentTimeMillis() + 60L* 1000L * tokenExpire));
            tokenCache.put(username, newToken);
            responseData.setCode(100);
            responseData.setMessage("Token expired, a new token generated.");
            responseData.setData(newToken);
            response.setStatusCode(HttpStatusCode.valueOf(200));
        } else {
            logUtil.addLog(LogUtil.LogLevel.Error, LogUtil.LogType.Auth, "gateway", getClass().getName(), null, JSON.toJSONString(responseData), null, exchange);
        }
        response.setStatusCode(statusCode);
        return response.writeWith(Mono.fromSupplier(() -> response.bufferFactory().wrap(JSON.toJSONBytes(responseData))));
    }
}
